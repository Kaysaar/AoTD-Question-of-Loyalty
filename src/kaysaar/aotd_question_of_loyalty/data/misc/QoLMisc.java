package kaysaar.aotd_question_of_loyalty.data.misc;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.EngagementResultForFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class QoLMisc {
    public static boolean isStringValid(String forCheck,String checkingFor){
        if(checkingFor==null){
            return isStringValid(forCheck);
        }
        return isStringValid(forCheck)&&forCheck.equals(checkingFor);
    }
    public static boolean isStringValid(String forCheck){
        return forCheck!=null &&!forCheck.isEmpty();
    }
    public static boolean isCommissionedBy(String factionID){
        return isStringValid(Misc.getCommissionFactionId(),factionID);
    }
    public static Set<String> loadEntriesSet(String rawMap, String seperator) {
        if(!QoLMisc.isStringValid(rawMap)){
            return  new HashSet<String>();
        }
        String[]splitted = rawMap.split(seperator);

        return new HashSet<String>(Arrays.asList(splitted));
    }
    public static ArrayList<String> loadEntriesArray(String rawMap, String seperator) {
        if(!QoLMisc.isStringValid(rawMap)){
            return  new ArrayList<String>();
        }
        String[]splitted = rawMap.split(seperator);

        return new ArrayList<String>(Arrays.asList(splitted));
    }
    public static EngagementResultForFleetAPI getNonPlayerFleet(EngagementResultAPI resultAPI){
        if(!resultAPI.getLoserResult().isPlayer()){
            return resultAPI.getLoserResult();
        }
        return resultAPI.getWinnerResult();
    }
    public static boolean isInSpaceofCommisionedFaction() {
        if (!QoLMisc.isCommissioned()) return false;
        if (Global.getSector().getPlayerFleet().getStarSystem() == null) return false;
        if (Global.getSector().getPlayerFleet().getStarSystem().isHyperspace()) return false;
        for (SectorEntityToken allEntity : Global.getSector().getPlayerFleet().getStarSystem().getAllEntities()) {
            if (allEntity instanceof CampaignFleetAPI) continue;
            if (allEntity.getMarket() != null && allEntity.getMarket().getFactionId() != null) {
                if (QoLMisc.isCommissionedBy(allEntity.getMarket().getFactionId())) {

                    return true;
                }

            }

        }
        return false;
    }
    public static boolean isCommissioned(){
        return isStringValid(Misc.getCommissionFactionId(),null);
    }
}
