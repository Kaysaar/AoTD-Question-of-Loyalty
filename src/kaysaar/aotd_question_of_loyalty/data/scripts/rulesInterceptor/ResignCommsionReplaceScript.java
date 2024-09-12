package kaysaar.aotd_question_of_loyalty.data.scripts.rulesInterceptor;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import kaysaar.aotd_question_of_loyalty.data.tags.AoTDCommisionTags;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class ResignCommsionReplaceScript extends BaseReplaceScript {

    @Override
    public void advance(float amount) {
     ;
        if (Global.getSector().getCampaignUI().isShowingDialog()) {
            InteractionDialogAPI dialog = Global.getSector().getCampaignUI().getCurrentInteractionDialog();
            if (dialog != null&&AoTDCommIntelPlugin.get()!=null) {
                if (dialog.getOptionPanel().hasOption("cmsn_resignCommission") && !isInInteraction) {
                    isInInteraction = true;
                    dialog.getOptionPanel().removeOption("cmsn_resignCommission");
                    List list = dialog.getOptionPanel().getSavedOptionList();
                    dialog.getOptionPanel().clearOptions();
                    String tooltip = null;
                    if(!AoTDCommIntelPlugin.get().canResign()){
                        tooltip = "We can't resign our commission, due to our high ranking position.";
                    }
                    dialog.getOptionPanel().addOption("I would like to resign my commission", "aotd_cmsn_resignCommission", AoTDCommIntelPlugin.optionColor,tooltip);
                    if(!AoTDCommIntelPlugin.get().getData().hasTag(AoTDCommisionTags.UP_RANK_AUTOMATICALLY)){

                    }
                    dialog.getOptionPanel().addOption("I would want to ask for promotion", "aotd_ask_promotion", AoTDCommIntelPlugin.optionColor,null);

                    List list2 = dialog.getOptionPanel().getSavedOptionList();
                    Object obj = list2.get(0);
                    Object obj2 = list2.get(1);
                    List list3 = dialog.getOptionPanel().getSavedOptionList();
                    list3.clear();

                    dialog.getOptionPanel().clearOptions();
                    int i = 0;
                    for (Object o : list) {
                        if (i == 1) {
                            list3.add(obj);
                            list3.add(obj2);
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

