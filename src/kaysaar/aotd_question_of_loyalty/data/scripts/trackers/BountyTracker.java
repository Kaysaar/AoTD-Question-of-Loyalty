package kaysaar.aotd_question_of_loyalty.data.scripts.trackers;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.PersonBountyIntel;
import com.fs.starfarer.api.impl.campaign.intel.eventfactors.onetime.BountyCompletionFactor;
import com.fs.starfarer.api.impl.campaign.missions.cb.BaseCustomBounty;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;

import java.util.ArrayList;
import java.util.Iterator;

public class BountyTracker implements EveryFrameScript {

    protected ArrayList<BaseCustomBounty> baseCustomBounties = new ArrayList<>();
    protected ArrayList<PersonBountyIntel> basePersonBounties = new ArrayList<>();
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
        handlePersonalBounties();
    }

    private void handleBarBounties() {
        for (BaseCustomBounty bounty : getBaseBounties()) {
            if(bounty.getResult()!=null&& QoLMisc.isCommissionedBy(bounty.getFactionForUIColors().getId())){
                if(!isBaseBountyPresent(bounty)){
                    baseCustomBounties.add(bounty);
                    int val = 0;
                    if(!bounty.getResult().success){
                        val=-20;
                    }

                    else{
                        val = 50;
                    }
                    AoTDCommIntelPlugin.get().addFactor(new BountyCompletionFactor(val));
                }
            }
        }
        Iterator<BaseCustomBounty> iterator = baseCustomBounties.iterator();
        while (iterator.hasNext()) {
            BaseCustomBounty baseCustomBounty = iterator.next();
            if (baseCustomBounty.isEnded()) {
                iterator.remove(); // Removes the current item
            }
        }
    }
    public ArrayList<BaseCustomBounty> getBaseBounties(){
        ArrayList<BaseCustomBounty> bounties = new ArrayList<>();
        for (IntelInfoPlugin intelInfoPlugin : Global.getSector().getIntelManager().getIntel(BaseCustomBounty.class)) {
            if(intelInfoPlugin instanceof BaseCustomBounty){
                bounties.add((BaseCustomBounty) intelInfoPlugin);
            }
        }
        return bounties;
    }
    public boolean isBaseBountyPresent(BaseCustomBounty bounty){
        for (BaseCustomBounty baseCustomBounty : baseCustomBounties) {
            if(bounty.getMissionId().equals(baseCustomBounty.getMissionId())){
                return true;
            }
        }
        return false;
    }
    private void handlePersonalBounties() {
        for (PersonBountyIntel bounty : getPersonBounties()) {
            if(bounty.getResult()!=null&& QoLMisc.isCommissionedBy(bounty.getFactionForUIColors().getId())){
                if(!isPersonalBountyPresent(bounty)){
                    basePersonBounties.add(bounty);
                    if(bounty.getResult().type.equals(PersonBountyIntel.BountyResultType.END_PLAYER_BOUNTY)){
                       int points  = (int) (bounty.getBountyCredits()/10000);
                        AoTDCommIntelPlugin.get().addFactor(new BountyCompletionFactor(points));
                    }

                }
            }
        }
        Iterator<BaseCustomBounty> iterator = baseCustomBounties.iterator();
        while (iterator.hasNext()) {
            BaseCustomBounty baseCustomBounty = iterator.next();
            if (baseCustomBounty.isEnded()) {
                iterator.remove(); // Removes the current item
            }
        }
    }

    public ArrayList<PersonBountyIntel> getPersonBounties(){
        ArrayList<PersonBountyIntel> bounties = new ArrayList<>();
        for (IntelInfoPlugin intelInfoPlugin : Global.getSector().getIntelManager().getIntel(PersonBountyIntel.class)) {
            if(intelInfoPlugin instanceof PersonBountyIntel){
                bounties.add((PersonBountyIntel) intelInfoPlugin);
            }
        }
        return bounties;
    }

    public boolean isPersonalBountyPresent(PersonBountyIntel bounty){
        for (PersonBountyIntel baseCustomBounty : basePersonBounties) {
            if(bounty.equals(baseCustomBounty)){
                return true;
            }
        }
        return false;
    }
}
