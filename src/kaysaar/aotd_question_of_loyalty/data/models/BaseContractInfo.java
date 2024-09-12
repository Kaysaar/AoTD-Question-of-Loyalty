package kaysaar.aotd_question_of_loyalty.data.models;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class BaseContractInfo extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        TooltipMakerAPI tooltip = dialog.getTextPanel().beginTooltip();
        createTooltip(tooltip);
        dialog.getTextPanel().addTooltip();
        return true;
    }
    public void  createTooltip(TooltipMakerAPI tooltip){

    }

}
