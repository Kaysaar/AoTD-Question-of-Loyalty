package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoPickerListener;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.eventfactors.onetime.AiCoreSellFactor;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.AICores;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;

import java.awt.*;

public class AoTDAiCores extends AICores {
    protected void selectCores() {
        CargoAPI copy = Global.getFactory().createCargo(false);
        //copy.addAll(cargo);
        //copy.setOrigSource(playerCargo);
        for (CargoStackAPI stack : playerCargo.getStacksCopy()) {
            CommoditySpecAPI spec = stack.getResourceIfResource();
            if (spec != null && spec.getDemandClass().equals(Commodities.AI_CORES)) {
                copy.addFromStack(stack);
            }
        }
        copy.sort();

        final float width = 310f;
        dialog.showCargoPickerDialog("Select AI cores to turn in", "Confirm", "Cancel", true, width, copy, new CargoPickerListener() {
            public void pickedCargo(CargoAPI cargo) {
                if (cargo.isEmpty()) {
                    cancelledCargoSelection();
                    return;
                }

                cargo.sort();
                for (CargoStackAPI stack : cargo.getStacksCopy()) {
                    playerCargo.removeItems(stack.getType(), stack.getData(), stack.getSize());
                    if (stack.isCommodityStack()) { // should be always, but just in case
                        int num = (int) stack.getSize();
                        AddRemoveCommodity.addCommodityLossText(stack.getCommodityId(), num, text);

                        String key = "$turnedIn_" + stack.getCommodityId();
                        int turnedIn = faction.getMemoryWithoutUpdate().getInt(key);
                        faction.getMemoryWithoutUpdate().set(key, turnedIn + num);

                        // Also, total of all cores! -dgb
                        String key2 = "$turnedIn_allCores";
                        int turnedIn2 = faction.getMemoryWithoutUpdate().getInt(key2);
                        faction.getMemoryWithoutUpdate().set(key2, turnedIn2 + num);
                    }
                }

                float bounty = computeCoreCreditValue(cargo);
                float repChange = computeCoreReputationValue(cargo);

                if (bounty > 0) {
                    playerCargo.getCredits().add(bounty);
                    AddRemoveCommodity.addCreditsGainText((int) bounty, text);
                }

                if (repChange >= 1f) {
                    CoreReputationPlugin.CustomRepImpact impact = new CoreReputationPlugin.CustomRepImpact();
                    impact.delta = repChange * 0.01f;
                    Global.getSector().adjustPlayerReputation(
                            new CoreReputationPlugin.RepActionEnvelope(CoreReputationPlugin.RepActions.CUSTOM, impact,
                                    null, text, true),
                            faction.getId());

                    impact.delta *= 0.25f;
                    if (impact.delta >= 0.01f) {
                        Global.getSector().adjustPlayerReputation(
                                new CoreReputationPlugin.RepActionEnvelope(CoreReputationPlugin.RepActions.CUSTOM, impact,
                                        null, text, true),
                                person);
                    }
                }
                if(QoLMisc.isCommissionedBy(faction.getId())){
                    int amount = calculateCommissionPoints(cargo);
                    AoTDCommIntelPlugin.get().addFactor(new AiCoreSellFactor(amount),dialog);
                }

                FireBest.fire(null, dialog, memoryMap, "AICoresTurnedIn");
            }

            public void cancelledCargoSelection() {
            }

            public void recreateTextPanel(TooltipMakerAPI panel, CargoAPI cargo, CargoStackAPI pickedUp, boolean pickedUpFromSource, CargoAPI combined) {

                float bounty = computeCoreCreditValue(combined);
                float repChange = computeCoreReputationValue(combined);

                float pad = 3f;
                float small = 5f;
                float opad = 10f;

                panel.setParaFontOrbitron();
                panel.addPara(Misc.ucFirst(faction.getDisplayName()), faction.getBaseUIColor(), 1f);
                //panel.addTitle(Misc.ucFirst(faction.getDisplayName()), faction.getBaseUIColor());
                //panel.addPara(faction.getDisplayNameLong(), faction.getBaseUIColor(), opad);
                //panel.addPara(faction.getDisplayName() + " (" + entity.getMarket().getName() + ")", faction.getBaseUIColor(), opad);
                panel.setParaFontDefault();

                panel.addImage(faction.getLogo(), width * 1f, 3f);


                //panel.setParaFontColor(Misc.getGrayColor());
                //panel.setParaSmallInsignia();
                //panel.setParaInsigniaLarge();
                panel.addPara("Compared to dealing with other factions, turning AI cores in to " +
                        faction.getDisplayNameLongWithArticle() + " " +
                        "will result in:", opad);
                panel.beginGridFlipped(width, 1, 40f, 10f);
                //panel.beginGrid(150f, 1);
                panel.addToGrid(0, 0, "Bounty value", "" + (int) (valueMult * 100f) + "%");
                panel.addToGrid(0, 1, "Reputation gain", "" + (int) (repMult * 100f) + "%");
                panel.addGrid(pad);

                panel.addPara("If you turn in the selected AI cores, you will receive a %s bounty " +
                                "and your standing with " + faction.getDisplayNameWithArticle() + " will improve by %s points.",
                        opad * 1f, Misc.getHighlightColor(),
                        Misc.getWithDGS(bounty) + Strings.C,
                        "" + (int) repChange);
                if (faction!=null&&QoLMisc.isCommissionedBy(faction.getId())) {
                    panel.addPara("This will result in increase of commission points by %s", 10f, Color.ORANGE, "" + calculateCommissionPoints(combined));
                }


                //panel.addPara("Bounty: %s", opad, Misc.getHighlightColor(), Misc.getWithDGS(bounty) + Strings.C);
                //panel.addPara("Reputation: %s", pad, Misc.getHighlightColor(), "+12");
            }
        });
    }

    public int calculateCommissionPoints(CargoAPI cargo) {
        int bounty = 0;
        for (CargoStackAPI stack : cargo.getStacksCopy()) {
            CommoditySpecAPI spec = stack.getResourceIfResource();
            if (spec != null && spec.getDemandClass().equals(Commodities.AI_CORES)) {
                bounty+=getValueOfCore(spec.getId())*stack.getSize();
            }
        }

        return bounty;

    }

    public int getValueOfCore(String id) {
        if (id.equals(Commodities.GAMMA_CORE)) {
            return 4;
        }
        if (id.equals(Commodities.BETA_CORE)) {
            return 8;
        }
        if (id.equals(Commodities.ALPHA_CORE)) {
            return 12;
        }
        if (id.equals(Commodities.OMEGA_CORE)) {
            return 16;
        }
        return 2;
    }
}
