package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.models.RetirementInfo;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class AoTDCreateInsultForLeaving extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        dialog.getTextPanel().addPara("\"Consequences, %s, Consequences\"",Misc.getNegativeHighlightColor(), Color.ORANGE, RetirementInfo.getInstance().getRankData().name);
        dialog.getTextPanel().addPara("The officer looks up directly into the camera.");
        dialog.getTextPanel().addPara("\"You knew this time would come, didn't you ? At least, now you can take accountability, Traitor.\"",Misc.getNegativeHighlightColor());
        dialog.getTextPanel().addPara("Some very powerful people look forward to your disappearance, one of them being myself. As they say, if you want something done right, do it yourself. Some of our administrators could learn a thing or two.");
        dialog.getTextPanel().addPara("\"Nothing personal,%s, or maybe a bit.\"",Color.ORANGE, RetirementInfo.getInstance().getRankData().name);
        dialog.getTextPanel().addPara("The officer laughs.");
        dialog.getTextPanel().addPara("\"Or maybe a bit more ! Now squirm in anxiousness witnessing your fleet dismantled before your very eyes ! I shall feast my eyes upon your disfigured corpse, if I cannot put a bullet through that wretched head myself ! Good luck, %s .\"",Color.ORANGE, RetirementInfo.getInstance().getRankData().name);
        dialog.getTextPanel().addPara("The feed goes back to static as the officer's raucous laughter echoes in your head.");

        return false;
    }
}
