package kaysaar.aotd_question_of_loyalty.data.scripts.crisisalt;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.CampaignClock;
import kaysaar.aotd_question_of_loyalty.data.listeners.AoTDColonyCrisisInserter;
import kaysaar.aotd_question_of_loyalty.data.models.AoTDHandleBonusesFromBeingRetired;

public class ActivityCrisisRemover implements EveryFrameScript {
    public IntervalUtil util = new IntervalUtil(CampaignClock.SECONDS_PER_GAME_DAY/4,CampaignClock.SECONDS_PER_GAME_DAY/4);

    protected boolean didIt = false;
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
        util.advance(amount);
        if(util.intervalElapsed()&&!didIt){
            if(HostileActivityEventIntel.get()!=null){
                AoTDHandleBonusesFromBeingRetired.applyBonusesFromRetirement(null);
                didIt = true;
                Global.getSector().removeScriptsOfClass(this.getClass());

            }
        }
    }
}
