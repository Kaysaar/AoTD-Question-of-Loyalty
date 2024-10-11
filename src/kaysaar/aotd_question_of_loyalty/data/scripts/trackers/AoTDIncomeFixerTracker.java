package kaysaar.aotd_question_of_loyalty.data.scripts.trackers;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.impl.campaign.intel.FactionCommissionIntel;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;

import java.util.Iterator;
import java.util.Map;

public class AoTDIncomeFixerTracker implements EveryFrameScript {
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
        Global.getSector().removeScriptsOfClass(FactionCommissionIntel.class);
        Global.getSector().getListenerManager().removeListenerOfClass(FactionCommissionIntel.class);
        Global.getSector().removeScript(this);


    }
}
