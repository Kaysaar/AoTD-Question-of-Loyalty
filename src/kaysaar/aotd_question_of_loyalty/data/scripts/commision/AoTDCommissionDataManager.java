package kaysaar.aotd_question_of_loyalty.data.scripts.commision;

import com.fs.starfarer.api.Global;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;
import kaysaar.aotd_question_of_loyalty.data.models.BaseFactionCommisionData;
import kaysaar.aotd_question_of_loyalty.data.models.RankData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class AoTDCommissionDataManager {


    private static AoTDCommissionDataManager instance;
    private HashMap<String,RankData>availableRanks;
    private HashMap<String, BaseFactionCommisionData> commissionData;


    public static AoTDCommissionDataManager getInstance(){
       if(instance==null){
           instance = new AoTDCommissionDataManager();
           instance.initalizeRanks();
           instance.initalizeData();
       }
       return instance;
    }
    public void initalizeRanks(){
        availableRanks = new HashMap<>();
        try {
            JSONArray array = Global.getSettings().getMergedSpreadsheetData("rank_id","data/campaign/aotd_rank_data.csv");
            for (int i = 0; i < array.length(); i++) {
                JSONObject entry = array.getJSONObject(i);
                String id = entry.getString("rank_id");
                if(!QoLMisc.isStringValid(id,null)||id.contains("#"))continue;
                String name = entry.getString("name");
                Integer salary = entry.getInt("salary");
                boolean canLeave = entry.getBoolean("canLeave");
                int tariffReduction = entry.getInt("tariffReduction");
                int allowedMarketsToOwn = entry.getInt("allowedMarketsToOwn");
                int pointsOfObligation = entry.getInt("pointsOfObligation");
                int rawPenalty = entry.getInt("penaltyForLeavingRep");
                float truePenalty = rawPenalty/100f;
                Set<String> tags = QoLMisc.loadEntriesSet(entry.getString("tags"),",");
                String description = entry.getString("description");
                availableRanks.put(id,new RankData(id,name,canLeave,truePenalty,description,pointsOfObligation,salary,tariffReduction,tags,allowedMarketsToOwn));
            }

        }
        catch (Exception e){
            throw  new RuntimeException(e);
        }


    }
    public void initalizeData(){
        commissionData = new HashMap<>();
        try {
            JSONArray array = Global.getSettings().getMergedSpreadsheetData("faction_id","data/campaign/aotd_commision_data.csv");
            for (int i = 0; i < array.length(); i++) {
                JSONObject entry = array.getJSONObject(i);
                String id = entry.getString("faction_id");
                if(!QoLMisc.isStringValid(id,null)||id.contains("#"))continue;
                ArrayList<String> ranks = QoLMisc.loadEntriesArray(entry.getString("ranks"),",");
                ArrayList<String>rawThreshold =  QoLMisc.loadEntriesArray(entry.getString("rank_threshold"),",");
                LinkedHashMap<String,String> iconMap = null;
                if(QoLMisc.isStringValid(entry.getString("iconMap"))){

                }
                HashMap<String,Integer>ranksOrdered = new HashMap<>();
                for (String rank : rawThreshold) {
                    String []splitted = rank.split(":");
                    ranksOrdered.put(splitted[0].trim(),Integer.parseInt(splitted[1]));
                }
                int maxPoints = entry.getInt("maxPoints");
                String plugin = "";
                if(QoLMisc.isStringValid(entry.getString("commisionPlugin"))){
                    plugin = entry.getString("commisionPlugin");
                }
                else{
                    plugin = "com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin";
                }
                ArrayList<RankData>ranksData = new ArrayList<>();
                for (String rank : ranks) {
                    ranksData.add(getRank(rank.trim()));
                }
                Set<String>tags = QoLMisc.loadEntriesSet(entry.getString("tags"),",");
                commissionData.put(id,new BaseFactionCommisionData(id,iconMap,maxPoints,ranksData,plugin,ranksOrdered,tags));
            }

        }
        catch (Exception e){
            throw  new RuntimeException(e);
        }
    }
    public RankData getRank(String rankID){
        return availableRanks.get(rankID);
    }
    public void addRank(RankData rankData){
         availableRanks.put(rankData.id,rankData);
    }
    public BaseFactionCommisionData getCommisionData(String id){
        if(commissionData.get(id)==null){
            return BaseFactionCommisionData.getDefaultCommisionData(id);
        }
        return commissionData.get(id);
    }


}
