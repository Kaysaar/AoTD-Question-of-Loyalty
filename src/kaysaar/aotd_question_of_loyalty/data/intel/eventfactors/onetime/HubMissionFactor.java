package kaysaar.aotd_question_of_loyalty.data.intel.eventfactors.onetime;

import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseOneTimeFactor;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class HubMissionFactor extends BaseOneTimeFactor {
    public String missionName;
    public HubMissionFactor(int points,String name) {
        super(points);
        this.missionName = name;
    }

    @Override
    public String getDesc(BaseEventIntel intel) {
        if(this.points>=0){
            return "Complete mission - "+missionName;
        }
        else{
            return "Failed mission - "+missionName;
        }

    }

    @Override
    public Color getDescColor(BaseEventIntel intel) {
        if(this.points<0){
            return Misc.getNegativeHighlightColor();
        }
        return super.getDescColor(intel);
    }

}
