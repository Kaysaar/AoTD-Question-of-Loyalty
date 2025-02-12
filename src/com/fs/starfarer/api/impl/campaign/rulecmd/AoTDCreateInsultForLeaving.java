package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class AoTDCreateInsultForLeaving extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        dialog.getTextPanel().addPara("Your life is nothing!",Misc.getNegativeHighlightColor());
        dialog.getTextPanel().addPara("You serve zero purpose!",Misc.getNegativeHighlightColor());
        dialog.getTextPanel().addPara("You should kill yourself... now!",Misc.getNegativeHighlightColor());
        dialog.getTextPanel().addImage("misc","kys");
        return false;
    }
}
