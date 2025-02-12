package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.intel.AoTDCommIntelPlugin;
import kaysaar.aotd_question_of_loyalty.data.models.AoTDHandleBonusesFromBeingRetired;
import kaysaar.aotd_question_of_loyalty.data.scripts.AoTDPensionFund;
import kaysaar.aotd_question_of_loyalty.data.scripts.crisisalt.ActivityCrisisRemover;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class AoTDRetirementOption extends BaseCommandPlugin{
    public static String memKey = "$aotd_retired_from";
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;

        if(command.equals("init")){
            dialog.getOptionPanel().clearOptions();
            dialog.getTextPanel().addPara("\"Wishing to explore new horizons, huh ?\"");
            dialog.getTextPanel().addPara("The officer pauses.");
            dialog.getTextPanel().addPara("\"I think we can make that work.\"");
            dialog.getTextPanel().addPara("The officer shows you a retirement plan on his tri-pad.");
            dialog.getTextPanel().addPara("\"First, I would see myself thanking you for your work as %s, and offering you a generous retirement pension of %s.\"",Color.ORANGE,AoTDCommIntelPlugin.get().getCurrentRankData().name,Misc.getDGSCredits(AoTDCommIntelPlugin.get().getCreditsForRetirement()));
            dialog.getTextPanel().addPara("\"Obviously, you remain in our good grace as a fervent helper to our cause. And should you rise to higher spheres, we would respect your new autonomy and stop the pension.\"");
            dialog.getTextPanel().addPara("\"But should those new horizons include some... Unfortunate associations, we would see ourselves needing to send a stern warning.\"");
            dialog.getTextPanel().addPara("\"Now, do we have a deal ?\"");
            dialog.getOptionPanel().addOption("Confirm","aotd_confirm_retirement");
            dialog.getOptionPanel().addOption("On second thought...","aotd_cancel_retirement");

            // Do dialog explaining you will lose colonies but you will receive one time payment for retiring. If you join any other faction relations will drop to -100 , as it will be seemed as betrayal.
        }
        if(command.equals("proceed")){

            new AoTDPensionFund(AoTDCommIntelPlugin.get().getCurrentlyCommisonedFaction().getId(),AoTDCommIntelPlugin.get().getCreditsForRetirement(),AoTDCommIntelPlugin.get().getCurrentRankData().getId());
            Global.getSector().addScript(new ActivityCrisisRemover());
            AoTDHandleBonusesFromBeingRetired.applyTTDeal(dialog);
            AoTDCommIntelPlugin.get().endCommision(dialog,false);
        }
        return true;
    }

}
