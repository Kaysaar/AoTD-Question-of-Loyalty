package com.fs.starfarer.api.impl.campaign.intel.eventfactors.onetime;

import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseOneTimeFactor;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class AiCoreSellFactor extends BaseOneTimeFactor {
    public AiCoreSellFactor(int points) {
        super(points);
    }
    @Override
    public String getDesc(BaseEventIntel intel) {
        return "Sell of AI Cores";
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
                tooltip.addPara("You've recently sold noticeable amount of AI cores to "+ Misc.getCommissionFaction().getDisplayName(),
                        0f);
            }

        };
    }
}
