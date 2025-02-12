package kaysaar.aotd_question_of_loyalty.data.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.LuddicMajority;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.intel.TriTachyonDeal;
import com.fs.starfarer.api.impl.campaign.intel.events.*;
import com.fs.starfarer.api.impl.campaign.rulecmd.AoTDRetirementOption;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.scripts.crisisalt.LuddicMajorityApplier;

import static com.fs.starfarer.api.impl.campaign.intel.events.DiktatFuelBonusScript.FUEL_EXPORT_BONUS;
import static com.fs.starfarer.api.impl.campaign.intel.events.DiktatFuelBonusScript.MOD_ID;

public class AoTDHandleBonusesFromBeingRetired {

    public static void applyTTDeal(InteractionDialogAPI dialogAPI){
        RetirementInfo retirementInfo = (RetirementInfo) Global.getSector().getMemory().get(AoTDRetirementOption.memKey);
        if(retirementInfo!=null&&retirementInfo.getPrevFaction().equals(Factions.TRITACHYON)){
            if(TriTachyonDeal.get()==null){
                new TriTachyonDeal(dialogAPI);

            }
        }
    }
    public static void unapplyTTDeal(InteractionDialogAPI dialogAPI, FactionAPI factionToJoin){
        RetirementInfo retirementInfo = (RetirementInfo) Global.getSector().getMemory().get(AoTDRetirementOption.memKey);
        if(retirementInfo!=null){
            if(TriTachyonDeal.get()!=null){
                if(factionToJoin.getId().equals(Factions.TRITACHYON)){
                    TriTachyonDeal.get().endAgreement(null,dialogAPI);

                }
                else{
                    TriTachyonDeal.get().endAgreement(TriTachyonDeal.AgreementEndingType.BROKEN,dialogAPI);

                }

            }
        }
    }
    public static void applyBonusesFromRetirement(InteractionDialogAPI dialogAPI) {
        RetirementInfo retirementInfo = (RetirementInfo) Global.getSector().getMemory().get(AoTDRetirementOption.memKey);
        if (retirementInfo != null) {
            String factionId = retirementInfo.getPrevFaction();
            if (HostileActivityEventIntel.get() != null) {
                switch (factionId) {
                    case (Factions.HEGEMONY):
                        HostileActivityEventIntel.get().removeActivityCause(HegemonyHostileActivityFactor.class, HegemonyAICoresActivityCause.class);
                        Global.getSector().getListenerManager().removeListenerOfClass(HegemonyHostileActivityFactor.class);
                        break;
                    case (Factions.TRITACHYON):
                        HostileActivityEventIntel.get().removeActivityCause(TriTachyonHostileActivityFactor.class, TriTachyonStandardActivityCause.class);
                        Global.getSector().getListenerManager().removeListenerOfClass(TriTachyonHostileActivityFactor.class);
                        break;
                    case (Factions.LUDDIC_CHURCH):
                        HostileActivityEventIntel.get().removeActivityCause(LuddicChurchHostileActivityFactor.class, LuddicChurchStandardActivityCause.class);
                        Global.getSector().getListenerManager().removeListenerOfClass(LuddicChurchHostileActivityFactor.class);
                        if (!Global.getSector().getListenerManager().hasListenerOfClass(LuddicMajorityApplier.class)) {
                            Global.getSector().getListenerManager().addListener(new LuddicMajorityApplier());
                        }
                        break;
                    case (Factions.PERSEAN):
                        break;
                    case (Factions.DIKTAT):
                        HostileActivityEventIntel.get().removeActivityCause(SindrianDiktatHostileActivityFactor.class, SindrianDiktatStandardActivityCause.class);
                        Global.getSector().getListenerManager().removeListenerOfClass(SindrianDiktatHostileActivityFactor.class);
                        DiktatFuelBonusScript.sendGainedMessage();
                        Global.getSector().getPlayerStats().getDynamic().getStat(
                                Stats.getCommodityExportCreditsMultId(Commodities.FUEL)).modifyMult(MOD_ID, 1f + FUEL_EXPORT_BONUS,
                                "Proven stable source (due to connections in " + Global.getSector().getFaction(retirementInfo.getPrevFaction()).getDisplayName() + ")");
                        break;
                }
            }

        }
    }

    public static void unapplyBonusesFromRetirement(InteractionDialogAPI dialogAPI) {
        RetirementInfo retirementInfo = (RetirementInfo) Global.getSector().getMemory().get(AoTDRetirementOption.memKey);
        if (retirementInfo != null) {
            String factionId = retirementInfo.getPrevFaction();

            switch (factionId) {
                case (Factions.HEGEMONY):
                    break;
                case (Factions.TRITACHYON):
                    if(TriTachyonDeal.get()!=null){
                        TriTachyonDeal.get().endAgreement(TriTachyonDeal.AgreementEndingType.BROKEN, dialogAPI);

                    }

                    break;
                case (Factions.LUDDIC_CHURCH):

                    LuddicChurchHostileActivityFactor.setDefeatedExpedition(false);
                    Global.getSector().getListenerManager().removeListenerOfClass(LuddicMajorityApplier.class);


                    break;
                case (Factions.PERSEAN):
                    break;
                case (Factions.DIKTAT):
                    DiktatFuelBonusScript.sendLostMessage();
                    Global.getSector().getPlayerStats().getDynamic().getStat(
                            Stats.getCommodityExportCreditsMultId(Commodities.FUEL)).unmodifyFlat(MOD_ID);


                    break;

            }


        }
    }
}
