package com.fs.starfarer.api.impl.campaign.intel.eventfactors.monthly;

import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class MonthlyObligationFactor extends BaseEventFactor {
    public MonthlyObligationFactor() {
    }

    @Override
    public TooltipMakerAPI.TooltipCreator getMainRowTooltip(BaseEventIntel intel) {
        return new BaseFactorTooltip() {
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                Color h = Misc.getHighlightColor();
                float opad = 10f;
                tooltip.addPara("Due to our contract with %s we are obliged to help them.Staying complacent is not an option!", 0f,Misc.getCommissionFaction().getBaseUIColor(),Misc.getCommissionFaction().getDisplayName());
            }
        };
    }


    @Override
    public boolean shouldShow(BaseEventIntel intel) {
        //HAColonyDefenseData data = getDefenseData(intel);
        return Misc.getCommissionFaction()!=null;
    }

    @Override
    public int getProgress(BaseEventIntel intel) {
        return -AoTDCommIntelPlugin.get().getCurrentRankData().getPointsOfObligation();
    }

    @Override
    public Color getProgressColor(BaseEventIntel intel) {
        return Misc.getNegativeHighlightColor(); // no text anyway
    }

    @Override
    public String getProgressStr(BaseEventIntel intel) {
        return super.getProgressStr(intel);
    }

    public String getDesc(BaseEventIntel intel) {
        return "Obligations towards faction";
    }

    @Override
    public Color getDescColor(BaseEventIntel intel) {
        return Misc.getCommissionFaction().getBaseUIColor();
    }
}
