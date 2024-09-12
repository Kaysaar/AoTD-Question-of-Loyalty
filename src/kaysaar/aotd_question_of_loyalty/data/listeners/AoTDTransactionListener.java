package kaysaar.aotd_question_of_loyalty.data.listeners;

import com.fs.starfarer.api.campaign.PlayerMarketTransaction;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ColonyInteractionListener;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.eventfactors.onetime.GoodTradeFactor;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;

public class AoTDTransactionListener implements ColonyInteractionListener {
    @Override
    public void reportPlayerOpenedMarket(MarketAPI market) {

    }

    @Override
    public void reportPlayerClosedMarket(MarketAPI market) {

    }

    @Override
    public void reportPlayerOpenedMarketAndCargoUpdated(MarketAPI market) {

    }

    @Override
    public void reportPlayerMarketTransaction(PlayerMarketTransaction transaction) {
        if(QoLMisc.isCommissionedBy(transaction.getMarket().getFactionId())&&transaction.getSubmarket().getSpecId().equals(Submarkets.SUBMARKET_OPEN)){
            int tran = (int) transaction.getBaseTradeValueImpact();
            int  factor = 50000;
            int points =tran/factor;
            AoTDCommIntelPlugin.addFactorCreateIfNecessary(new GoodTradeFactor(points),null);
        }


    }
}
