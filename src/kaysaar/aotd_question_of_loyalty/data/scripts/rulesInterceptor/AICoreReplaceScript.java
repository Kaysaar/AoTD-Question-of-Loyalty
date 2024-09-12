package kaysaar.aotd_question_of_loyalty.data.scripts.rulesInterceptor;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;

public class AICoreReplaceScript extends BaseReplaceScript {
    @Override
    public void advance(float amount) {
        if (Global.getSector().getCampaignUI().isShowingDialog()) {
                InteractionDialogAPI dialog = Global.getSector().getCampaignUI().getCurrentInteractionDialog();
                if (dialog != null) {
                    if (dialog.getOptionPanel().hasOption("aiCores_selectCores")&&!isInInteraction) {
                        isInInteraction = true;
                        dialog.getOptionPanel().clearOptions();
                        dialog.getOptionPanel().addOption("Select AI cores to turn in", "aotd_aiCores_selectCores");
                        dialog.getOptionPanel().addOption("Never mind, I don't actually have any.)", "aiCores_neverMind");
                    } else if (!dialog.getOptionPanel().hasOption("aiCores_selectCores")) {
                        isInInteraction = false;
                    }
                }


        } else {
            isInInteraction = false;
        }
    }
}

