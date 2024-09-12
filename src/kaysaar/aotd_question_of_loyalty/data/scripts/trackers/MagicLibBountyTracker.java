package kaysaar.aotd_question_of_loyalty.data.scripts.trackers;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.eventfactors.onetime.BountyCompletionFactor;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;
import org.magiclib.bounty.MagicBountyCoordinator;
import org.magiclib.bounty.MagicBountyLoader;

import java.util.ArrayList;
import java.util.List;

public class MagicLibBountyTracker implements EveryFrameScript {
    public static String memKeyForAlreadyChecked = "$aotd_magic_already_checked";
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
        if(!QoLMisc.isCommissioned())return;
        List<String> alreadyDoneQuests = getAlreadyDoneFromMemory();
        for (String completedBounty : MagicBountyCoordinator.getInstance().getCompletedBounties()) {
            if(alreadyDoneQuests.contains(completedBounty))continue;
            if(haveSucceededBounty(MagicBountyLoader.BOUNTIES.get(completedBounty).job_memKey)){
                if(QoLMisc.isCommissionedBy(MagicBountyLoader.BOUNTIES.get(completedBounty).job_forFaction)){
                    int factor = 10000;
                     int value =   MagicBountyLoader.BOUNTIES.get(completedBounty).job_credit_reward/factor;
                     AoTDCommIntelPlugin.get().addFactor(new BountyCompletionFactor(value),null);
                    alreadyDoneQuests.add(completedBounty);
                }
            }
            if(haveFailedBounty(MagicBountyLoader.BOUNTIES.get(completedBounty).job_memKey)){
                if(QoLMisc.isCommissionedBy(MagicBountyLoader.BOUNTIES.get(completedBounty).job_forFaction)){
                    int factor = 20000;
                    int value =   MagicBountyLoader.BOUNTIES.get(completedBounty).job_credit_reward/factor;
                    AoTDCommIntelPlugin.get().addFactor(new BountyCompletionFactor(-value),null);
                    alreadyDoneQuests.add(completedBounty);
                }

            }
        }

        updateMemory(alreadyDoneQuests);
    }

    public List<String>getAlreadyDoneFromMemory(){
        if(Global.getSector().getMemoryWithoutUpdate().contains(memKeyForAlreadyChecked)){
            return (List<String>) Global.getSector().getMemory().get(memKeyForAlreadyChecked);
        }
        return new ArrayList<>();
    }
    public void updateMemory(List<String> toUpdate){
        Global.getSector().getMemory().set(memKeyForAlreadyChecked,toUpdate);
    }
    public boolean haveSucceededBounty(String memKeyForAlreadyChecked){

        return Global.getSector().getMemoryWithoutUpdate().contains( memKeyForAlreadyChecked + "_succeeded");
    }
    public boolean haveFailedBounty(String memKeyForAlreadyChecked){
        return Global.getSector().getMemoryWithoutUpdate().contains( memKeyForAlreadyChecked + "_failed");
    }
    //We have here seperate trakcer due to being from other mod

}
