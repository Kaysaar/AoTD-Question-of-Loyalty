package kaysaar.aotd_question_of_loyalty.data.intel.secession.eventfactors;

import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.intel.AoTDCommIntelPlugin;
import kaysaar.aotd_question_of_loyalty.data.intel.AoTDSecessionManager;

import java.awt.*;

public class HoldoutEventFactor extends BaseEventFactor {
    public static int BASE_POINTS =20;
    @Override
    public TooltipMakerAPI.TooltipCreator getMainRowTooltip(final BaseEventIntel intel) {
        return new BaseFactorTooltip() {
            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {

                tooltip.addPara("Maintaining control over markets captured from major factions can have significant impacts on rebellion dynamics. " +
                        "Sustained occupation may fuel resistance efforts, but over time, effective governance and stability measures can " +
                        "convince the opposing faction to consider peace talks.", 0f);

                tooltip.addPara("Points earning monthly %s due to holding %s out of %s starting markets",5f,Color.ORANGE,""+getProgress(intel),""+AoTDSecessionManager.get().getMarketsStillOwned(),""+AoTDSecessionManager.get().originalMarkets.size());
            }

        };
    }

    @Override
    public String getDesc(BaseEventIntel intel) {
        return "Holding our ground";
    }

    @Override
    public boolean shouldShow(BaseEventIntel intel) {
        return true;
    }

    @Override
    public int getProgress(BaseEventIntel intel) {
        int originalMarketSize = AoTDSecessionManager.get().getOriginalMarkets().size();
        int marketsStillOwned = AoTDSecessionManager.get().getMarketsStillOwned();

        if (originalMarketSize == 0) {
            return 0;
        }

        float ownershipRatio = (float) marketsStillOwned / (float) originalMarketSize;
        int denominator = (int) ownershipRatio;

        if (denominator == 0) {
            return 0;
        }

        return BASE_POINTS / denominator;
    }
}
