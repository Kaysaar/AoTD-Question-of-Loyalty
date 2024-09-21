package kaysaar.aotd_question_of_loyalty.data.scripts.effectapplier;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;

public class AoTDIgnoreTurningOffTransponder implements EveryFrameScript {
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
        if (QoLMisc.isCommissioned()) {
            if (Global.getSector().getPlayerFleet().getStarSystem() != null) {
                for (CampaignFleetAPI fleet : Global.getSector().getPlayerFleet().getStarSystem().getFleets()) {
                    if (Misc.getCommissionFactionId().equals(fleet.getFaction().getId())) {
                        fleet.getMemory().set("$patrolAllowTOff", true, 100);
                        fleet.getMemory().set("$sawPlayerWithTOffCount", 0);
                        fleet.getMemory().unset("$sawPlayerWithTOffCount");
                        fleet.getMemory().unset("$cargoScanConv");
                        fleet.getMemory().unset("$smugglingScanComplete");


                    }
                }
            }

        }
    }
}
