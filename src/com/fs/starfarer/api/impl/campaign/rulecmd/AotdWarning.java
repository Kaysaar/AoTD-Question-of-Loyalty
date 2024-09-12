package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class AotdWarning extends BaseCommandPlugin{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        dialog.getTextPanel().addPara("Due to your colonial holdings we are going to propose you one of governor titles, instead of mercenary titles.", Color.ORANGE);

        dialog.getTextPanel().addPara("Once you attain this rank, leaving our faction won't be an option. Should you betray us, we will treat you as threat of high priority.", Color.ORANGE);
return true;
    }
}
