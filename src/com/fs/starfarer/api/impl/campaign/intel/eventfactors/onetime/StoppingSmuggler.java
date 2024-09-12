package com.fs.starfarer.api.impl.campaign.intel.eventfactors.onetime;

import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseOneTimeFactor;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class StoppingSmuggler extends BaseOneTimeFactor {
    public StoppingSmuggler(int points) {
        super(points);
    }
    @Override
    public String getDesc(BaseEventIntel intel) {
        return "Stopping Smuggler";
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
                tooltip.addPara("Due to our efforts smuggler has been caught and cargo, that would be delivered ended up being thrown to the sun, right?",0f);
            }

        };
    }
}
