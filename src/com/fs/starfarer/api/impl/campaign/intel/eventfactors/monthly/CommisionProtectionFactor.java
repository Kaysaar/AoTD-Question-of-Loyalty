package com.fs.starfarer.api.impl.campaign.intel.eventfactors.monthly;

import com.fs.starfarer.api.impl.campaign.intel.events.*;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;

import java.awt.*;

public class CommisionProtectionFactor extends BaseEventFactor {
    public CommisionProtectionFactor() {
    }

    @Override
    public TooltipMakerAPI.TooltipCreator getMainRowTooltip(BaseEventIntel intel) {
        return new BaseFactorTooltip() {
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                Color h = Misc.getHighlightColor();
                float opad = 10f;
                tooltip.addPara("We are protected by influence of %s", opad,Color.ORANGE,Misc.getCommissionFaction().getDisplayNameWithArticle());
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
        if (QoLMisc.isCommissioned()) {
            return -40;
        }
        return super.getProgress(intel);
    }

    @Override
    public Color getProgressColor(BaseEventIntel intel) {
        return Misc.getPositiveHighlightColor(); // no text anyway
    }

    @Override
    public String getProgressStr(BaseEventIntel intel) {
        return super.getProgressStr(intel);
    }

    public String getDesc(BaseEventIntel intel) {
        return "Protection from " + Misc.getCommissionFaction().getDisplayName();
    }

    @Override
    public Color getDescColor(BaseEventIntel intel) {
        return Misc.getCommissionFaction().getBaseUIColor();
    }


}
