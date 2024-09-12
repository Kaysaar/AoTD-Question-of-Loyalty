package kaysaar.aotd_question_of_loyalty.data.scripts.trackers;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.BaseMissionIntel;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.DeliveryMissionIntel;
import com.fs.starfarer.api.impl.campaign.intel.eventfactors.onetime.MakingDeliveryFactor;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;

import java.util.ArrayList;
import java.util.Iterator;

public class DeliveryTracker implements EveryFrameScript {
    protected ArrayList<DeliveryMissionIntel> bountiesStored = new ArrayList<>();
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

        for (DeliveryMissionIntel bounty : getDeliveries()) {
            if(QoLMisc.isCommissionedBy(bounty.getFactionForUIColors().getId())&&!isAlreadyPresent(bounty)){
                BaseMissionIntel.MissionState state = bounty.getMissionState();
                if(state.equals(BaseMissionIntel.MissionState.COMPLETED)){
                    bountiesStored.add(bounty);
                    int cash = bounty.getMissionResult().payment;
                    int factor = cash/5000;
                    AoTDCommIntelPlugin.addFactorCreateIfNecessary(new MakingDeliveryFactor(factor),null);
                }
                if(state.equals(BaseMissionIntel.MissionState.FAILED)||state.equals(BaseMissionIntel.MissionState.ABANDONED)||state.equals(BaseMissionIntel.MissionState.CANCELLED)){
                    bountiesStored.add(bounty);
                    int cash = bounty.getMissionResult().payment;
                    int factor = cash/30000;
                    AoTDCommIntelPlugin.addFactorCreateIfNecessary(new MakingDeliveryFactor(-factor),null);

                }
            }
        }
        Iterator<DeliveryMissionIntel> iterator = bountiesStored.iterator();
        while (iterator.hasNext()) {
            DeliveryMissionIntel baseCustomBounty = iterator.next();
            if (baseCustomBounty.isEnded()) {
                iterator.remove(); // Removes the current item
            }
        }
    }
    public ArrayList<DeliveryMissionIntel> getDeliveries(){
        ArrayList<DeliveryMissionIntel> bounties = new ArrayList<>();
        for (IntelInfoPlugin intelInfoPlugin : Global.getSector().getIntelManager().getIntel(DeliveryMissionIntel.class)) {
            if(intelInfoPlugin instanceof DeliveryMissionIntel){
                bounties.add((DeliveryMissionIntel) intelInfoPlugin);
            }
        }
        return bounties;
    }
    public boolean isAlreadyPresent(DeliveryMissionIntel bounty){
        for (DeliveryMissionIntel baseCustomBounty : bountiesStored) {
            if(bounty.getEvent().equals(baseCustomBounty.getEvent())){
                return true;
            }
        }
        return false;
    }
}
