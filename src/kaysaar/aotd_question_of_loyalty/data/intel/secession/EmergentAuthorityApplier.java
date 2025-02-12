package kaysaar.aotd_question_of_loyalty.data.intel.secession;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.util.Misc;

public class EmergentAuthorityApplier implements EconomyTickListener {
    public static String memKey ="$aotd_emergent";
    @Override
    public void reportEconomyTick(int iterIndex) {
        for (MarketAPI marketAPI : Global.getSector().getEconomy().getMarketsCopy()) {
            if(marketAPI.getFaction().isPlayerFaction()&&!marketAPI.hasCondition("aotd_emergent_autonomy")&&Global.getSector().getMemory().getBoolean(memKey)){
                marketAPI.addCondition("aotd_emergent_autonomy");
            }
            if(!marketAPI.getFaction().isPlayerFaction()){
                marketAPI.removeCondition("aotd_emergent_autonomy");
            }
        }
    }

    @Override
    public void reportEconomyMonthEnd() {

    }
}
