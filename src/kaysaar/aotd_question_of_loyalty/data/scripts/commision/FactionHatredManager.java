package kaysaar.aotd_question_of_loyalty.data.scripts.commision;

import com.fs.starfarer.api.Global;

import java.util.ArrayList;

public class FactionHatredManager {
    public static String memKeyForFactions = "$aotd_faction_wont_comm";
    public ArrayList<String>factionsThatHatePlayer = new ArrayList<>();
    public int rebelions = 0;
    public static int maxRebellions=2;
    public void addRebellionCount(int rebellions) {
        this.rebelions +=rebellions;
    }

    public int getRebellions() {
        return rebelions;
    }

    public static FactionHatredManager get(){
        if(Global.getSector().getMemory().get(memKeyForFactions)==null){
            FactionHatredManager fhm = new FactionHatredManager();
            Global.getSector().getMemory().set(memKeyForFactions, fhm);
        }
        return (FactionHatredManager) Global.getSector().getMemory().get(memKeyForFactions);
    }

    public ArrayList<String> getFactionsThatHatePlayer() {
        return factionsThatHatePlayer;
    }
    public boolean doesFactionHatePlayer(String factionID){
        return factionsThatHatePlayer.contains(factionID)||rebelions>=maxRebellions;
    }
    public void addFactionThatHatePlayer(String factionID){
        factionsThatHatePlayer.add(factionID);
    }

}
