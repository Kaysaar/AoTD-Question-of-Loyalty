package kaysaar.aotd_question_of_loyalty.data.scripts.trackers;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.impl.campaign.intel.AnalyzeEntityMissionIntel;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.BaseMissionIntel;
import com.fs.starfarer.api.impl.campaign.intel.SurveyPlanetMissionIntel;
import com.fs.starfarer.api.impl.campaign.intel.eventfactors.onetime.ExplorationCompletionFactor;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;
import kaysaar.aotd_question_of_loyalty.data.plugins.ReflectionUtilis;

import java.util.ArrayList;
import java.util.Iterator;

public class ExplorationTracker implements EveryFrameScript {
    protected ArrayList<AnalyzeEntityMissionIntel> entityMissionIntels = new ArrayList<>();
    protected ArrayList<SurveyPlanetMissionIntel> surveyPlanetMissionIntels = new ArrayList<>();
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        handleAnalyzingMissions();
        handleSurveyMissions();
    }
    public ArrayList<AnalyzeEntityMissionIntel> getEntityMissions(){
        ArrayList<AnalyzeEntityMissionIntel> bounties = new ArrayList<>();
        for (IntelInfoPlugin intelInfoPlugin : Global.getSector().getIntelManager().getIntel(AnalyzeEntityMissionIntel.class)) {
            if(intelInfoPlugin instanceof AnalyzeEntityMissionIntel){
                bounties.add((AnalyzeEntityMissionIntel) intelInfoPlugin);
            }
        }
        return bounties;
    }
    public boolean isBaseMissionPresent(AnalyzeEntityMissionIntel bounty){
        for (AnalyzeEntityMissionIntel baseCustomBounty : entityMissionIntels) {
            if(bounty.equals(baseCustomBounty)){
                return true;
            }
        }
        return false;
    }
    private void handleAnalyzingMissions() {
        for (AnalyzeEntityMissionIntel bounty : getEntityMissions()) {
            if(bounty.getMissionState()!=null&& QoLMisc.isCommissionedBy(bounty.getFactionForUIColors().getId())){
                if(!isBaseMissionPresent(bounty)){
                    entityMissionIntels.add(bounty);
                    if(bounty.getMissionState().equals(BaseMissionIntel.MissionState.COMPLETED)){
                        int reward = (int) ReflectionUtilis.getPrivateVariable("reward",bounty);
                        int points  = (int) (reward /2000);
                        AoTDCommIntelPlugin.get().addFactor(new ExplorationCompletionFactor(points));
                    }

                }
            }
        }
        Iterator<AnalyzeEntityMissionIntel> iterator = entityMissionIntels.iterator();
        while (iterator.hasNext()) {
            AnalyzeEntityMissionIntel baseCustomBounty = iterator.next();
            if (baseCustomBounty.isEnded()) {
                iterator.remove(); // Removes the current item
            }
        }
    }
    public ArrayList<SurveyPlanetMissionIntel> getSurveyMissions(){
        ArrayList<SurveyPlanetMissionIntel> bounties = new ArrayList<>();
        for (IntelInfoPlugin intelInfoPlugin : Global.getSector().getIntelManager().getIntel(SurveyPlanetMissionIntel.class)) {
            if(intelInfoPlugin instanceof SurveyPlanetMissionIntel){
                bounties.add((SurveyPlanetMissionIntel) intelInfoPlugin);
            }
        }
        return bounties;
    }
    public boolean isSurveyMissionPresent(SurveyPlanetMissionIntel bounty){
        for (SurveyPlanetMissionIntel baseCustomBounty : surveyPlanetMissionIntels) {
            if(bounty.equals(baseCustomBounty)){
                return true;
            }
        }
        return false;
    }
    private void handleSurveyMissions() {
        for (SurveyPlanetMissionIntel bounty : getSurveyMissions()) {
            if(bounty.getMissionState()!=null&&bounty.getMissionState()!= BaseMissionIntel.MissionState.ACCEPTED &&QoLMisc.isCommissionedBy(bounty.getFactionForUIColors().getId())){
                if(!isSurveyMissionPresent(bounty)){
                    surveyPlanetMissionIntels.add(bounty);
                    if(bounty.getMissionState().equals(BaseMissionIntel.MissionState.COMPLETED)){
                        int reward = (int) ReflectionUtilis.getPrivateVariable("reward",bounty);
                        int points  = (int) (reward /2000);
                        AoTDCommIntelPlugin.get().addFactor(new ExplorationCompletionFactor(points));
                    }
//                    if(bounty.getMissionState().equals(BaseMissionIntel.MissionState.FAILED)){
//                        int reward = (int) ReflectionUtilis.getPrivateVariable("reward",bounty);
//                        int points  = (int) (reward /15000);
//                        AoTDCommIntelPlugin.get().addFactor(new ExplorationCompletionFactor(points));
//                    }

                }
            }
        }
        Iterator<SurveyPlanetMissionIntel> iterator = surveyPlanetMissionIntels.iterator();
        while (iterator.hasNext()) {
            SurveyPlanetMissionIntel baseCustomBounty = iterator.next();
            if (baseCustomBounty.isEnded()) {
                iterator.remove(); // Removes the current item
            }
        }
    }
}
