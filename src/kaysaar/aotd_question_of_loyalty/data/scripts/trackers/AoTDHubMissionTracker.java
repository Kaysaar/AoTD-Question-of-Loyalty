package kaysaar.aotd_question_of_loyalty.data.scripts.trackers;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.impl.campaign.intel.PersonBountyIntel;
import com.fs.starfarer.api.impl.campaign.missions.cb.BaseCustomBounty;
import com.fs.starfarer.api.impl.campaign.missions.hub.BaseHubMission;
import kaysaar.aotd_question_of_loyalty.data.intel.AoTDCommIntelPlugin;
import kaysaar.aotd_question_of_loyalty.data.intel.eventfactors.onetime.BountyCompletionFactor;
import kaysaar.aotd_question_of_loyalty.data.intel.eventfactors.onetime.HubMissionFactor;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;
import org.lazywizard.lazylib.MathUtils;

import java.util.ArrayList;
import java.util.Iterator;

public class AoTDHubMissionTracker implements EveryFrameScript {
    protected ArrayList<BaseHubMission> baseCustomBounties = new ArrayList<>();
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
        handleBarBounties();
    }

    private void handleBarBounties() {
        for (BaseHubMission bounty : getBaseBounties()) {
            if(bounty.getResult()!=null&& QoLMisc.isCommissionedBy(bounty.getFactionForUIColors().getId())){
                if(!isBaseBountyPresent(bounty)){
                    baseCustomBounties.add(bounty);
                    int val =  (bounty.getCreditsReward()/2000);
                    if(!bounty.getResult().success){
                        val*=-1;
                    }
                    bounty.getCreditsReward();

                    AoTDCommIntelPlugin.get().addFactor(new HubMissionFactor(val,bounty.getBaseName()));
                }
            }
        }
        Iterator<BaseHubMission> iterator = baseCustomBounties.iterator();
        while (iterator.hasNext()) {
            BaseHubMission baseCustomBounty = iterator.next();
            if (baseCustomBounty.isEnded()) {
                iterator.remove(); // Removes the current item
            }
        }
    }
    public ArrayList<BaseHubMission> getBaseBounties(){
        ArrayList<BaseHubMission> bounties = new ArrayList<>();
        for (IntelInfoPlugin intelInfoPlugin : Global.getSector().getIntelManager().getIntel(BaseHubMission.class)) {
            if(intelInfoPlugin instanceof BaseHubMission){
                bounties.add((BaseHubMission) intelInfoPlugin);
            }
        }
        return bounties;
    }
    public boolean isBaseBountyPresent(BaseHubMission bounty){
        for (BaseHubMission baseCustomBounty : baseCustomBounties) {
            if(bounty.getMissionId().equals(baseCustomBounty.getMissionId())){
                return true;
            }
        }
        return false;
    }

}
