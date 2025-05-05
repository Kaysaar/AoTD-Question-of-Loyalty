package kaysaar.aotd_question_of_loyalty.data.scripts.crisisalt;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ColonySizeChangeListener;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.util.Misc;

public class LuddicMajorityApplier implements ColonySizeChangeListener {

    @Override
    public void reportColonySizeChanged(MarketAPI market, int prevSize) {
        if(!market.hasCondition(Conditions.LUDDIC_MAJORITY)&&market.getFaction().isPlayerFaction()&&!market.hasCondition("aotd_domain_majority")){
            market.addCondition(Conditions.LUDDIC_MAJORITY);
        }
    }
}
