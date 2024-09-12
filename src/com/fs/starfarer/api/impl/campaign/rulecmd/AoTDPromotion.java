package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.ui.RankShowcaseUIDialog;

import java.util.List;
import java.util.Map;

public class AoTDPromotion extends BaseCommandPlugin{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        dialog.showCustomDialog(RankShowcaseUIDialog.WIDTH,RankShowcaseUIDialog.HEIGHT,new RankShowcaseUIDialog(dialog,memoryMap));

        return true;
    }
}
