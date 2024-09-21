package kaysaar.aotd_question_of_loyalty.data.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import kaysaar.aotd_question_of_loyalty.data.scripts.commision.AoTDCommissionDataManager;
import kaysaar.aotd_question_of_loyalty.data.tags.AoTDRankTags;

import java.util.*;

public class BaseFactionCommisionData {
    //Necessary fields
    public String factionID;
    public LinkedHashMap<String,String>iconMap;
    public ArrayList<RankData>ranks;
    public HashMap<String,Integer>rankValue;
    public int maxProgress;
    public String intelPlugin;
    public Set<String> tags;

    //Optional fields to fill

    public AoTDCommIntelPlugin getPlugin(){
        try {
            AoTDCommIntelPlugin plugin = (AoTDCommIntelPlugin) Global.getSettings().getScriptClassLoader().loadClass(intelPlugin).newInstance();
            plugin.setData(this);
            return plugin;
        }
        catch (Exception e ){
            throw new RuntimeException(e);
        }
    }

    public FactionAPI getFaction(){
        return Global.getSector().getFaction(factionID);
    }
    public BaseFactionCommisionData(String factionID, LinkedHashMap<String,String>iconMap, int maxPoints,ArrayList<RankData>ranks,String plugin,HashMap<String,Integer>rankValue,Set tags){
        this.factionID = factionID;
        this.iconMap = iconMap;
        this.maxProgress = maxPoints;
        this.ranks = ranks;
        this.intelPlugin = plugin;
        this.rankValue = rankValue;
        this.tags = tags;
    }

    public static BaseFactionCommisionData getDefaultCommisionData(String factionID){
        LinkedHashMap<String,String>iconMap = new LinkedHashMap<>();
        ArrayList<RankData> data = new ArrayList<>();
        data.add(AoTDCommissionDataManager.getInstance().getRank("mercenary"));
        data.add(AoTDCommissionDataManager.getInstance().getRank("auxiliary"));
        data.add(AoTDCommissionDataManager.getInstance().getRank("commander"));
        data.add(AoTDCommissionDataManager.getInstance().getRank("admiral"));
        data.add(AoTDCommissionDataManager.getInstance().getRank("planetary_governor"));
        data.add(AoTDCommissionDataManager.getInstance().getRank("system_governor"));
        data.add(AoTDCommissionDataManager.getInstance().getRank("grand_moff"));
        HashMap<String,Integer>rankValue = new HashMap<>();
        rankValue.put("mercenary",0);
        rankValue.put("auxiliary",250);
        rankValue.put("commander",500);
        rankValue.put("admiral",750);
        rankValue.put("planetary_governor",1200);
        rankValue.put("system_governor",1700);
        rankValue.put("grand_moff",2200);
        HashSet<String>tags = new HashSet<>();
        iconMap.put("grand_moff","hyperspace_topography");
        iconMap.put("system_governor","hyperspace_topography");
        iconMap.put("planetary_governor","hyperspace_topography");
        iconMap.put("admiral","hyperspace_topography");
        iconMap.put("commander","hyperspace_topography");
        iconMap.put("auxiliary","hyperspace_topography");
        iconMap.put("mercenary","hyperspace_topography");

        return new BaseFactionCommisionData(factionID,iconMap,2500,data,"com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin",rankValue,tags);
    }
    public String getFirstDefRank(){
        for (RankData rank : ranks) {
            if(rank.isLeavingAnOption){
                return rank.id;
            }
        }
        return null;
    }
    public String getFirstOfficialRank(){
        for (RankData rank : ranks) {
            if(!rank.isLeavingAnOption){
                return rank.id;
            }
        }
        return null;
    }
    public RankData getAdequateRankForColonies(int colonyNumber){
        RankData firstNonRestrictiveRank = null;
        for (RankData rank : ranks) {
            if(rank.hasTag(AoTDRankTags.NONRESTRICTIVE_COLONIZATION)){
                firstNonRestrictiveRank = rank;
            }
            if(rank.getAmountOfColoniesAbleToColonize()>=colonyNumber){

                return rank;
            }
        }

        return firstNonRestrictiveRank;

    }
    public RankData getRankFromString(String rank){
        for (RankData rankData : ranks) {
            if(rankData.id.equals(rank)){
                return rankData;
            }
        }
        return null;
    }
    public boolean hasTag(String tag){
        for (String s : tags) {
            if(s.equals(tag)){
                return true;
            }
        }
        return false;
    }
}
