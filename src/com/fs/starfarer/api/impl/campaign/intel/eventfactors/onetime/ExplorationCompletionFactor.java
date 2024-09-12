package com.fs.starfarer.api.impl.campaign.intel.eventfactors.onetime;

import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseOneTimeFactor;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;

public class ExplorationCompletionFactor extends BaseOneTimeFactor {
    public ExplorationCompletionFactor(int points) {
        super(points);
    }
    @Override
    public String getDesc(BaseEventIntel intel) {
        return "Complete Exploration Mission";
    }

    @Override
    public Color getDescColor(BaseEventIntel intel) {
        return super.getDescColor(intel);
    }

    @Override
    public TooltipMakerAPI.TooltipCreator getMainRowTooltip(BaseEventIntel intel) {
        return new BaseFactorTooltip() {
            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara("Thanks to our efforts faction have gathered valuable data",0f);
            }

        };
    }
}
