package kaysaar.aotd_question_of_loyalty.data.scripts.effectapplier;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CustomCampaignEntityPlugin;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.SensorArrayEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;

public class AoTDSharedSensorLink implements EveryFrameScript {
    public static String modifier="$aotd_commision_shaared_array";
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        if(Global.getSector().getPlayerFleet().getStarSystem()!=null){
            for (SectorEntityToken allEntity : Global.getSector().getPlayerFleet().getStarSystem().getAllEntities()) {
                CustomCampaignEntityPlugin plugin =allEntity.getCustomPlugin();
                if( plugin instanceof SensorArrayEntityPlugin){
                    if(QoLMisc.isCommissionedBy(allEntity.getFaction().getId())){
                        float bonus = SensorArrayEntityPlugin.SENSOR_BONUS;
                        if(allEntity.hasTag(Tags.MAKESHIFT)){
                            bonus = SensorArrayEntityPlugin.SENSOR_BONUS_MAKESHIFT;
                        }
                        CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
                        MutableStat.StatMod curr =fleet.getStats().getSensorRangeMod().getFlatBonus("sensor_array");
                        if (curr == null || curr.value <= bonus) {
                            fleet.getStats().addTemporaryModFlat(0.1f, "sensor_array",
                                    "Sensor Array ("+allEntity.getFaction().getDisplayName()+")", bonus,
                                    fleet.getStats().getSensorRangeMod());
                        }
                    }
                }
            }
        }
    }
}
