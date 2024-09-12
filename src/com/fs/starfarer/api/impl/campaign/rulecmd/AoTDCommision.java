package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.ReputationActionResponsePlugin;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;

import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.FactionCommissionIntel;
import com.fs.starfarer.api.impl.campaign.rulecmd.missions.Commission;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.models.BaseFactionCommisionData;
import kaysaar.aotd_question_of_loyalty.data.scripts.commision.AoTDCommissionDataManager;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class AoTDCommision extends Commission {
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        this.dialog = dialog;
        this.memoryMap = memoryMap;

        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;
        if (command.equals("canResign")) {
            return canResign();

        }
        if (command.equals("canColonize")) {
            return canColonize();

        }
        memory = getEntityMemory(memoryMap);

        entity = dialog.getInteractionTarget();
        text = dialog.getTextPanel();
        options = dialog.getOptionPanel();

        playerFleet = Global.getSector().getPlayerFleet();
        playerCargo = playerFleet.getCargo();

        playerFaction = Global.getSector().getPlayerFaction();
        entityFaction = entity.getFaction();

        person = dialog.getInteractionTarget().getActivePerson();
        faction = person.getFaction();

        offersCommissions = faction.getCustomBoolean("offersCommissions");


        //printInfo
        //accept

        if (command.equals("printRequirements")) {
            printRequirements();
        } else if (command.equals("playerMeetsCriteria")) {
            return playerMeetsCriteria();
        } else if (command.equals("printInfo")) {
            printInfo();
        } else if (command.equals("hasFactionCommission")) {
            return hasFactionCommission();
        } else if (command.equals("hasOtherCommission")) {
            if (hasOtherCommission()) {
                memory.set("$theOtherCommissionFaction", Misc.getCommissionFaction().getDisplayNameWithArticle(), 0);
                memory.set("$otherCommissionFaction", Misc.getCommissionFaction().getPersonNamePrefix(), 0);
                return true;
            }
            return false;
        } else if (command.equals("accept")) {
            accept();
        } else if (command.equals("resign")) {
            resign(true);
        } else if (command.equals("resignNoPenalty")) {
            resign(false);
        } else if (command.equals("isSteppingIntoNoReturn")) {
            return isSteppingIntoNoReturn();
        } else if (command.equals("personCanGiveCommission")) {
            return personCanGiveCommission();
        } else if (command.equals("commissionFactionIsAtWarWith")) {
            if (hasOtherCommission()) {
                if (params.size() >= 1) {
                    String target_faction_id = params.get(0).getString(memoryMap);
                    FactionAPI target_faction = Global.getSector().getFaction(target_faction_id);
                    if (target_faction != null) {
                        return Misc.getCommissionFaction().isHostileTo(target_faction);
                    }
                }
            }
            return false;
        } else if (command.equals("isCargoPodsScam")) {
            MarketAPI market = dialog.getInteractionTarget().getMarket();
            if (market == null)
                return false;
            //Misc.getStorage(market)
            for (SectorEntityToken entity : market.getContainingLocation().getAllEntities()) {
                if (Entities.CARGO_PODS.equals(entity.getCustomEntityType())) {

                    // use player fleet 'cause it's in market range, right? And therefore scan range.
                    // market is otherwise attached to a station or planet entity (who knows!)
                    float dist = Misc.getDistance(entity.getLocation(), playerFleet.getLocation());
                    if (dist < 500f)
                        if (entity.getCargo().getSupplies() >= 10) {
                            return true;
                        }

                }
            }
            return false;
        } else if (command.equals("recalcFreeSupplyDaysRemaining")) {
            Object obj1 = person.getFaction().getMemoryWithoutUpdate().get("$playerReceivedCommissionResupplyOn");
            Object obj2 = Global.getSector().getMemoryWithoutUpdate().get("$daysSinceStart");
            if (obj1 == null) return false;
            if (obj2 == null) return false;

            float d1 = (Float) obj1;
            float d2 = (Float) obj2;

            faction.getMemoryWithoutUpdate().set("$daysLeft", (int) d1 + 365 - (int) d2, 0);
        } else if (command.equals("doesPlayerFleetNeedRepairs")) {


            float fleetCRcurrent = 0f;
            float fleetCRmax = 0f;
            float fleetHullDamage = 0f;

            //playerFleet.getFleetData().getMembersListCopy()
            for (FleetMemberAPI member : playerFleet.getMembersWithFightersCopy()) {
                if (member.isFighterWing()) continue; // no one cares about fighters.

                //if (member.canBeRepaired()) {
                fleetHullDamage += 1f - member.getStatus().getHullFraction();
                fleetCRcurrent += member.getRepairTracker().getCR();
                fleetCRmax += member.getRepairTracker().getMaxCR();

            }


            //System.out.println("doesPlayerFleetNeedRepairs results:");
            //System.out.println("fleetCRcurrent = " + fleetCRcurrent);
            //System.out.println("fleetCRmax = " + fleetCRmax);
            //System.out.println("fleetHullDamage = " + fleetHullDamage); // ever 1f is about 100% of a ship

            boolean needsSupplies = false;

            if (fleetHullDamage > 0.5) {
                needsSupplies = true;

                Global.getSector().getPlayerMemoryWithoutUpdate().set("$fleetDamaged", true, 0);
                //memory.set("$fleetDamaged", true , 0); // "Looks like you've taken some damage."
            }

            if (fleetHullDamage > 2.5) {
                needsSupplies = true;
                Global.getSector().getPlayerMemoryWithoutUpdate().set("$fleetDamagedLots", true, 0); // "Your fleet is in rough shape, captain."
            }


            // basically, if the CR percent is less than 60% (of max) for the fleet, acknowledge that supplies are needed.
            if (fleetCRcurrent == 0 || (fleetCRcurrent / fleetCRmax < 0.6f)) {
                needsSupplies = true;
                Global.getSector().getPlayerMemoryWithoutUpdate().set("$fleetLowCR", true, 0);
            }

            //memory.set("$fleetLowCR", true , 0);
            //memory.set("$fleetDamaged", true , 0);
            //memory.set("$fleetDamagedLots", true , 0);

            return needsSupplies;
        }

        return true;
    }

    @Override
    protected void accept() {
        if (Misc.getCommissionFactionId() == null) {
            BaseFactionCommisionData data = AoTDCommissionDataManager.getInstance().getCommisionData(faction.getId());
            if(data==null){
                data = BaseFactionCommisionData.getDefaultCommisionData(faction.getId());
            }
            AoTDCommIntelPlugin intel = data.getPlugin();
            intel.initializeFully(data, text, false, data.getFirstDefRank(), data.getFirstOfficialRank());
            intel.sendUpdate(FactionCommissionIntel.UPDATE_PARAM_ACCEPTED, dialog.getTextPanel());
            Global.getSector().getCharacterData().getMemoryWithoutUpdate().set(MemFlags.FCM_FACTION, faction.getId());
            intel.makeRepChanges(dialog);

        }
    }

    @Override
    protected void resign(boolean withPenalty) {
        CoreReputationPlugin.CustomRepImpact impact = new CoreReputationPlugin.CustomRepImpact();
        impact.delta = -1f * AoTDCommIntelPlugin.get().getCurrentRankData().getPenaltyForLeaving();
        ReputationActionResponsePlugin.ReputationAdjustmentResult rep = Global.getSector().adjustPlayerReputation(
                new CoreReputationPlugin.RepActionEnvelope(CoreReputationPlugin.RepActions.CUSTOM,
                        impact, null, dialog != null ? dialog.getTextPanel() : null, false, true),
                faction.getId());

        AoTDCommIntelPlugin.get().unset();
        AoTDCommIntelPlugin.get().endCommision(dialog);
        Global.getSector().getCharacterData().getMemory().unset(MemFlags.FCM_FACTION);
    }

    @Override
    protected void printInfo() {
        TooltipMakerAPI info = dialog.getTextPanel().beginTooltip();

        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        float pad = 3f;
        float opad = 10f;
        FactionCommissionIntel temp = new FactionCommissionIntel(faction);

        info.setParaSmallInsignia();
        BaseFactionCommisionData daten = AoTDCommissionDataManager.getInstance().getCommisionData(faction.getId());
        if (Misc.getPlayerMarkets(true).isEmpty()||daten.getFirstOfficialRank()==null) {

            info.addPara("Given current circumstances you will start with rank of %s and monthly salary of %s", 5f, Color.ORANGE, daten.getRankFromString(daten.getFirstDefRank()).name, Misc.getDGSCredits(daten.getRankFromString(daten.getFirstDefRank()).salary));

        } else {
            info.addPara("Given current circumstances you will start with rank of %s and monthly salary of %s", 5f, Color.ORANGE, daten.getRankFromString(daten.getFirstOfficialRank()).name, Misc.getDGSCredits(daten.getRankFromString(daten.getFirstOfficialRank()).salary));
            info.addPara("We propose such rank due to your colonial holdings", Color.ORANGE, 5f);
        }
        info.addSectionHeading("Commission Progression", Alignment.MID, 10f);
        info.setParaFontDefault();
        info.addPara("To progress through our ranks you need to prove a worthy asset to our faction.You can do by:", 10f);
        info.addPara("Selling AI Cores to our faction", 3f);
        if(Global.getSettings().getModManager().isModEnabled("aotd_vok")){
            info.addPara("Selling Research databanks to our faction", 3f);

        }
        info.addPara("Completing survey missions and analyze missions",3f);
//        info.addPara("Selling colony items to our faction",3f);
        info.addPara("Completing faction bounties", 3f);
        info.addPara("Catching smugglers within faction's star systems", 3f);
        info.addPara("Making profitable trades with our faction", 3f);

        info.setParaSmallInsignia();
        List<FactionAPI> hostile = temp.getHostileFactions();
        if (hostile.isEmpty()) {
            info.addPara(Misc.ucFirst(faction.getDisplayNameWithArticle()) + " is not currently hostile to any major factions.", 0f);
        } else {
            info.addPara(Misc.ucFirst(faction.getDisplayNameWithArticle()) + " is currently hostile to:", opad);

            info.setParaFontDefault();

            info.setBulletedListMode(BaseIntelPlugin.INDENT);
            float initPad = opad;
            for (FactionAPI other : hostile) {
                info.addPara(Misc.ucFirst(other.getDisplayName()), other.getBaseUIColor(), initPad);
                initPad = 3f;
            }
            info.setBulletedListMode(null);
        }
        info.addPara("Should you prove to be burden to our faction, we will terminate this contract", Misc.getNegativeHighlightColor(), 10f);
        dialog.getTextPanel().addTooltip();
    }

    public boolean canResign() {
        if (AoTDCommIntelPlugin.get() == null) return false;
        return AoTDCommIntelPlugin.get().canResign();

    }

    public boolean isSteppingIntoNoReturn() {
        return !Misc.getPlayerMarkets(true).isEmpty()&&AoTDCommissionDataManager.getInstance().getCommisionData(faction.getId()).getFirstOfficialRank()!=null;
    }

    public boolean canColonize() {
        if (AoTDCommIntelPlugin.get() == null) return true;
        return AoTDCommIntelPlugin.get().canColonize();

    }
}
