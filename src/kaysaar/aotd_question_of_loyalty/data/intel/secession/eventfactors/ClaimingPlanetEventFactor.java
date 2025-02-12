package kaysaar.aotd_question_of_loyalty.data.intel.secession.eventfactors;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseOneTimeFactor;

public class ClaimingPlanetEventFactor extends BaseOneTimeFactor {
    protected String claimedMarketName;
    public ClaimingPlanetEventFactor(int points, MarketAPI claimedMarket) {
        super(points);
        claimedMarketName = claimedMarket.getName();
    }

    @Override
    public String getProgressStr(BaseEventIntel intel) {
        return "Claimed "+claimedMarketName;
    }
}
