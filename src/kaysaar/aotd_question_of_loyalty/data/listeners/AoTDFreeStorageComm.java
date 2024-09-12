package kaysaar.aotd_question_of_loyalty.data.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;
import kaysaar.aotd_question_of_loyalty.data.plugins.ReflectionUtilis;
import kaysaar.aotd_question_of_loyalty.data.tags.AoTDRankTags;

public class AoTDFreeStorageComm implements EconomyTickListener {

    @Override
    public void reportEconomyTick(int iterIndex) {
        float numIter = Global.getSettings().getFloat("economyIterPerMonth");
        float f = 1f / numIter;
        if(QoLMisc.isCommissioned()){

            for (MarketAPI factionMarket : Misc.getFactionMarkets(Misc.getCommissionFaction())) {
                if(factionMarket.hasSubmarket(Submarkets.SUBMARKET_STORAGE)){
                    StoragePlugin plugin = (StoragePlugin) factionMarket.getSubmarket(Submarkets.SUBMARKET_STORAGE).getPlugin();
                    if(AoTDCommIntelPlugin.get().getCurrentRankData().hasTag(AoTDRankTags.FREE_STORAGE)){
                        setSubmarketPayment(factionMarket, plugin);
                    }
                    else{
                        if(factionMarket.getMemory().is("$aotd_free_from_comm",true)){
                            plugin.setPlayerPaidToUnlock(false);
                            factionMarket.getMemory().unset("$aotd_free_from_comm");
                        }
                    }

                }


            }
            MonthlyReport report = SharedData.getData().getCurrentReport();
            MonthlyReport.FDNode storageNode = null;


            float storageFraction = Global.getSettings().getFloat("storageFreeFraction");
            for (MarketAPI market : Misc.getFactionMarkets(Misc.getCommissionFaction())) {
                if (!market.isPlayerOwned() && Misc.playerHasStorageAccess(market)) {
                    float vc = Misc.getStorageCargoValue(market);
                    float vs = Misc.getStorageShipValue(market);

                    float fc = (int) (vc * storageFraction);
                    float fs = (int) (vs * storageFraction);
                    if (fc > 0 || fs > 0) {
                        if (storageNode == null) {
                            storageNode = report.getNode(MonthlyReport.STORAGE);
                            storageNode.name = "Storage";
                            storageNode.custom = MonthlyReport.STORAGE;
                            storageNode.tooltipCreator = report.getMonthlyReportTooltip();
                        }
                        MonthlyReport.FDNode mNode = report.getNode(storageNode, market.getId());
                        String desc = "";
                        if (fc > 0 && fs > 0) {
                            desc = "ships & cargo";
                        } else if (fc > 0) {
                            desc = "cargo";
                        } else {
                            desc = "ships";
                        }
                        mNode.name = market.getName() + " (" + desc + ")";
                        mNode.custom = market;
                        mNode.custom2 = MonthlyReport.STORAGE;
                        //mNode.tooltipCreator = report.getMonthlyReportTooltip();

                        mNode.upkeep -= (fc + fs) * f;
                    }
                }
            }

        }


    }

    private static void setSubmarketPayment(MarketAPI factionMarket, StoragePlugin plugin) {
        if(plugin==null)return;
        boolean havePaid = true;
        if(Global.getSettings().getModManager().isModEnabled("nexerelin")){
            havePaid = (boolean) ReflectionUtilis.getPrivateVariableFromSuperClass("playerPaidToUnlock", plugin);
        }
        else{
            havePaid = (boolean) ReflectionUtilis.getPrivateVariable("playerPaidToUnlock", plugin);
        }

        if(!havePaid){
            factionMarket.getMemory().set("$aotd_free_from_comm",true);
            plugin.setPlayerPaidToUnlock(true);
        }
    }

    @Override
    public void reportEconomyMonthEnd() {

    }
    public static void runCleanUpScript(FactionAPI faction){
        for (MarketAPI factionMarket : Misc.getFactionMarkets(faction)) {
            if(factionMarket.getMemory().is("$aotd_free_from_comm",true)){
                if(factionMarket.hasSubmarket(Submarkets.SUBMARKET_STORAGE)){
                    StoragePlugin plugin = (StoragePlugin) factionMarket.getSubmarket(Submarkets.SUBMARKET_STORAGE).getPlugin();
                    plugin.setPlayerPaidToUnlock(false);
                    factionMarket.getMemory().unset("$aotd_free_from_comm");
                }
            }
        }
    }
}
