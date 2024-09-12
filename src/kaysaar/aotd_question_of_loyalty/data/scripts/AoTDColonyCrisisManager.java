package kaysaar.aotd_question_of_loyalty.data.scripts;

import com.fs.starfarer.api.impl.campaign.intel.AoTDHostileActivity;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityManager;
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin;
import com.fs.starfarer.api.util.Misc;

public class AoTDColonyCrisisManager extends HostileActivityManager {
    @Override
    public void advance(float amount) {
        tracker.advance(amount);
        if (tracker.intervalElapsed()) {
            boolean playerHasColonies = !Misc.getPlayerMarkets(false).isEmpty();
            if (AoTDHostileActivity.get() == null && playerHasColonies) {
                new AoTDHostileActivity();
            } else if (AoTDHostileActivity.get() != null && !playerHasColonies) {
                AoTDHostileActivity.get().endImmediately();
            }
        }
    }

}
