package kaysaar.aotd_question_of_loyalty.data.scripts.rulesInterceptor;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;

public class BaseReplaceScript implements EveryFrameScript {
    public boolean isInInteraction = false;
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {
        if(AoTDCommIntelPlugin.get()==null)return;
    }
}
