package kaysaar.aotd_question_of_loyalty.data.scripts.rulesInterceptor;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;

import kaysaar.aotd_question_of_loyalty.data.intel.AoTDCommIntelPlugin;
import kaysaar.aotd_question_of_loyalty.data.tags.AoTDCommisionTags;
import kaysaar.aotd_question_of_loyalty.data.tags.AoTDRankTags;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class ResignCommsionReplaceScript extends BaseReplaceScript {

    @Override
    public void advance(float amount) {
     ;
        if (Global.getSector().getCampaignUI().isShowingDialog()) {
            InteractionDialogAPI dialog = Global.getSector().getCampaignUI().getCurrentInteractionDialog();
            if (dialog != null&& AoTDCommIntelPlugin.get()!=null) {
                if (dialog.getOptionPanel().hasOption("cmsn_resignCommission") && !isInInteraction) {
                    isInInteraction = true;
                    dialog.getOptionPanel().removeOption("cmsn_resignCommission");
                    List list = dialog.getOptionPanel().getSavedOptionList();
                    dialog.getOptionPanel().clearOptions();
                    String tooltip = null;
                    if(!AoTDCommIntelPlugin.get().canResign()){
                        tooltip = "We can't resign our commission, due to our high ranking position.";
                    }
                    if(!AoTDCommIntelPlugin.get().getCurrentRankData().hasTag(AoTDRankTags.CAN_RETIRE)){
                        dialog.getOptionPanel().addOption("I would like to resign my commission", "aotd_cmsn_resignCommission", AoTDCommIntelPlugin.optionColor,tooltip);

                    }
                    else{
                        dialog.getOptionPanel().addOption("I would like to retire from commission", "aotd_cmsn_retire", AoTDCommIntelPlugin.optionColor,tooltip);

                    }
                    if(!AoTDCommIntelPlugin.get().getData().hasTag(AoTDCommisionTags.UP_RANK_AUTOMATICALLY)){
                        dialog.getOptionPanel().addOption("I would want to ask for promotion", "aotd_ask_promotion", AoTDCommIntelPlugin.optionColor,null);

                    }
                    List list2 = dialog.getOptionPanel().getSavedOptionList();
                    ArrayList<Object> objects = new ArrayList<>(list2);
                    List list3 = dialog.getOptionPanel().getSavedOptionList();
                    list3.clear();

                    dialog.getOptionPanel().clearOptions();
                    int i = 0;
                    for (Object o : list) {
                        if (i == 1) {
                           list3.addAll(objects);
                        }
                        list3.add(o);
                        i++;
                    }
                    dialog.getOptionPanel().restoreSavedOptions(list3);
                    dialog.getOptionPanel().setShortcut(list3.get(list3.size() - 1), Keyboard.KEY_ESCAPE, false, false, false, true);
                    if(AoTDCommIntelPlugin.get().getData().hasTag(AoTDCommisionTags.UP_RANK_AUTOMATICALLY)){
                        dialog.getOptionPanel().removeOption("aotd_ask_promotion");
                    }
                    dialog.getOptionPanel().setEnabled("aotd_cmsn_resignCommission", AoTDCommIntelPlugin.get().canResign());

                } else if (!dialog.getOptionPanel().hasOption("aotd_cmsn_resignCommission")) {
                    isInInteraction = false;
                }
            }


        } else {
            isInInteraction = false;
        }
    }
}

