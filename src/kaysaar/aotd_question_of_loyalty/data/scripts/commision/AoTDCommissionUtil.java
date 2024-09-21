package kaysaar.aotd_question_of_loyalty.data.scripts.commision;

import kaysaar.aotd_question_of_loyalty.data.models.RankData;

public class AoTDCommissionUtil {
    public static void  reportPlayerGotNewRank(RankData newRank){
        for (AoTDRankPromotionListener listenersOfClass : AoTDCommissionDataManager.getInstance().getListeners(AoTDRankPromotionListener.class)) {
            listenersOfClass.reportPlayerGotNewRank(newRank);
        }
    }
}
