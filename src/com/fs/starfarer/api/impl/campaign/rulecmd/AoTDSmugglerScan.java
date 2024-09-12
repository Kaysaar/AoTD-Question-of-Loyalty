package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.fleets.EconomyFleetAssignmentAI;
import com.fs.starfarer.api.impl.campaign.fleets.EconomyFleetRouteManager;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.eventfactors.onetime.StoppingSmuggler;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;
import kaysaar.aotd_question_of_loyalty.data.tags.AoTDRankTags;

import java.awt.*;
import java.util.*;
import java.util.List;

import static kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc.isInSpaceofCommisionedFaction;

public class AoTDSmugglerScan extends BaseCommandPlugin {
    protected CampaignFleetAPI playerFleet;
    protected SectorEntityToken entity;
    protected FactionAPI playerFaction;
    protected FactionAPI entityFaction;
    protected TextPanelAPI text;
    protected OptionPanelAPI options;
    protected CampaignFleetAPI potentialSmuggler;
    protected CargoAPI playerCargo;
    protected MemoryAPI memory;
    protected InteractionDialogAPI dialog;
    protected Map<String, MemoryAPI> memoryMap;
    protected PersonAPI person;
    protected FactionAPI faction;

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        this.dialog = dialog;
        this.memoryMap = memoryMap;
        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;
        entity = dialog.getInteractionTarget();
        text = dialog.getTextPanel();
        options = dialog.getOptionPanel();
        person = dialog.getInteractionTarget().getActivePerson();
        faction = person.getFaction();
        potentialSmuggler = (CampaignFleetAPI) entity;

        if (command.equals("canCargoCheck")) {
            boolean can = canCargoCheck();

            if (can && !entity.getMemory().is("$aotd_rand_completed", true)) {
                entity.getMemory().set("$aotd_rand_completed", true);
                Random rand = Misc.getRandom(0, 0);
                if (rand.nextFloat() >= 0.3) {
                    if (rand.nextFloat() >= 0.6) {
                        entity.getMemory().set("$aotd_bribery_check", true);
                    }
                    entity.getMemory().set("$aotd_will_found_cargo", true);
                } else {
                    entity.getMemory().set("$aotd_will_found_cargo", false);

                }


            }
            return can;
        }
        if (command.equals("getResultsOfScan")) {
            dialog.getOptionPanel().clearOptions();
            dialog.getTextPanel().addPara("Calmly laying down the facts you know, you inform the opposing fleet of your intentions to investigate potential smuggling activity in this area and will be boarding their vessels.");
            entity.getMemory().set("$aotd_checked_cargo", true);
            if (entity.getMemory().is("$aotd_will_found_cargo", true)&&!getIllegalCargoAmount(potentialSmuggler,false).isEmpty()) {
                text.addPara("After conducting search through entire fleet a lot of contraband has been found by our officers");
//                HashMap<String, Integer> map = getIllegalCargoAmount((CampaignFleetAPI) entity, false);
//                float cash = 0f;
//                for (Map.Entry<String, Integer> entry : map.entrySet()) {
//                    CommoditySpecAPI spec = Global.getSettings().getCommoditySpec(entry.getKey());
//                    cash += (spec.getBasePrice() * 0.5F) * entry.getValue();
//                }
//                text.addPara("Captain of this fleet came to you, proposing a quote \"gift\" so you can turn blind eye on such transgression");
//                text.addPara("If you accept you will receive %s", Color.ORANGE, Misc.getDGSCredits(cash));
//                dialog.getOptionPanel().addOption("Accept bribe", "aotd_scan_accept_bribery", Color.ORANGE, null);


                dialog.getOptionPanel().addOption("Confiscate entire contraband", "aotd_seize_cargo");
            } else {
                text.addPara("After conducting search through entire fleet we have not found any traces of illegal commodities.");
                dialog.getOptionPanel().addOption("We apologize for inconvenience", "cutCommLink");
            }

        }
        if (command.equals("pillage")) {
            pillage();
        }


        return true;
    }

    public void pillage() {
        CargoAPI cargo = Global.getFactory().createCargo(false);
        HashMap<String, Integer> cargoRaw = getIllegalCargoAmount((CampaignFleetAPI) entity, false);
        for (Map.Entry<String, Integer> entry : cargoRaw.entrySet()) {
            cargo.addCommodity(entry.getKey(), entry.getValue());
        }
        dialog.getVisualPanel().showLoot("Contraband", cargo, false, new CoreInteractionListener() {
            public void coreUIDismissed() {
                getIllegalCargoAmount((CampaignFleetAPI) entity, true);
                //For some stupid reason after that fleet fights with i dont fucking know who
                ((CampaignFleetAPI) entity).getBattle().finish(BattleAPI.BattleSide.NO_JOIN);
                dialog.dismiss();
                dialog.hideTextPanel();
                dialog.hideVisualPanel();
                AoTDCommIntelPlugin.addFactorCreateIfNecessary(new StoppingSmuggler(80),null);

            }
        });
        options.clearOptions();

        dialog.setPromptText("");
    }

    public boolean canCargoCheck() {
        return isInSpaceofCommisionedFaction() && !entity.getMemory().is("$aotd_checked_cargo", true) && entity.getMemory().is(MemFlags.MEMORY_KEY_SMUGGLER, true)&& AoTDCommIntelPlugin.get().getCurrentRankData().hasTag(AoTDRankTags.CAN_SCAN_CARGO);
    }

    public ArrayList<CommoditySpecAPI> getIllegalCommoditiesForFaction(FactionAPI faction) {
        ArrayList<CommoditySpecAPI> illegal = new ArrayList<>();
        for (String illegalCommodity : faction.getIllegalCommodities()) {
            illegal.add(Global.getSettings().getCommoditySpec(illegalCommodity));
        }
        return illegal;
    }

    public HashMap<String, Integer> getIllegalCargoAmount(CampaignFleetAPI fleet, boolean removeIt) {
        RouteManager.RouteData data = RouteManager.getInstance().getRoute(EconomyFleetRouteManager.SOURCE_ID, fleet);
        EconomyFleetAssignmentAI.EconomyRouteData daten = (EconomyFleetAssignmentAI.EconomyRouteData) data.getCustom();

        List<EconomyFleetAssignmentAI.CargoQuantityData> listOfCommodities = new ArrayList<>();
        SectorEntityToken where = data.getCurrent().from;
        if (where.getMarket().getId().equals(daten.from.getId())) {
            listOfCommodities = daten.cargoDeliver;
        } else {
            listOfCommodities = daten.cargoReturn;
        }

        HashMap<String, Integer> cargo = new HashMap<>();
        ArrayList<CommoditySpecAPI> illegal = getIllegalCommoditiesForFaction(Misc.getCommissionFaction());
        Iterator<EconomyFleetAssignmentAI.CargoQuantityData> iter = listOfCommodities.iterator();

        while (iter.hasNext()) {
            EconomyFleetAssignmentAI.CargoQuantityData datsa = iter.next();
            for (CommoditySpecAPI commoditySpecAPI : illegal) {
                if (datsa.getCommodity().getId().equals(commoditySpecAPI.getId())) {
                    int units = datsa.units;
                    cargo.put(datsa.getCommodity().getId(), (int) (units * datsa.getCommodity().getEconUnit()));
                    if (removeIt) {
                        iter.remove();

                    }

                }

            }
        }
        if (removeIt) {
            if (listOfCommodities.isEmpty()) {
                data.expire();
                Misc.giveStandardReturnToSourceAssignments(fleet, true);
            } else {
                String text = fleet.getCurrentAssignment().getActionText();
                for (String s : cargo.keySet()) {
                    String name = Global.getSettings().getCommoditySpec(s).getName();

                    // Create a case-insensitive pattern for the name
                    String pattern = "(?i)" + java.util.regex.Pattern.quote(name);

                    // Replace the name in the text while keeping the original case of other parts intact
                    text = text.replaceAll(pattern, "").replaceAll("\\s*,\\s*,", ",").trim();
                }

// Now clean up the sentence for double commas, empty "and", or trailing commas
                text = text.replaceAll("\\s*,\\s*,", ","); // Remove consecutive commas
                text = text.replaceAll(",\\s*and\\s*,", " and "); // Handle "and" followed by commas
                text = text.replaceAll("\\s*,\\s*$", ""); // Remove trailing commas
                text = text.replaceAll("\\s*,\\s*and\\s*$", ""); // Remove trailing "and" if nothing follows


                if (!text.isEmpty()) {
                    text = text.substring(0, 1).toUpperCase() + text.substring(1);
                }
                fleet.getCurrentAssignment().setActionText(text);
            }
        }

        return cargo;
    }


}
