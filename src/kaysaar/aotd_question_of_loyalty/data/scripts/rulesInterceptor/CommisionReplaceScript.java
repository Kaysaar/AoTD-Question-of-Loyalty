package kaysaar.aotd_question_of_loyalty.data.scripts.rulesInterceptor;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.AoTDHostileActivity;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityManager;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class CommisionReplaceScript extends BaseReplaceScript {

    @Override
    public void advance(float amount) {
        if (Global.getSector().getCampaignUI().isShowingDialog()) {
            InteractionDialogAPI dialog = Global.getSector().getCampaignUI().getCurrentInteractionDialog();
            if (dialog != null) {
                if (dialog.getOptionPanel().hasOption("cmsn_askCommission")&&!isInInteraction) {
                    isInInteraction = true;
                    dialog.getOptionPanel().removeOption("cmsn_askCommission");
                    List list = dialog.getOptionPanel().getSavedOptionList();
                    dialog.getOptionPanel().clearOptions();
                    dialog.getOptionPanel().addOption("I would like to be commissioned by "+dialog.getInteractionTarget().getFaction().getDisplayName(),"aotd_cmsn_askCommission", AoTDCommIntelPlugin.optionColor,null);
                    List list2 = dialog.getOptionPanel().getSavedOptionList();
                    Object obj = list2.get(0);
                    List list3 = dialog.getOptionPanel().getSavedOptionList();
                    list3.clear();

                    dialog.getOptionPanel().clearOptions();
                    int i = 0;
                    for (Object o : list) {
                        if(i==1){
                            list3.add(obj);
                        }
                        list3.add(o);
                        i++;
                    }
                    dialog.getOptionPanel().restoreSavedOptions(list3);
                    dialog.getOptionPanel().setEnabled("aotd_cmsn_askCommission", !QoLMisc.isCommissioned());
                    dialog.getOptionPanel().setShortcut(list3.get(list3.size()-1), Keyboard.KEY_ESCAPE,false,false,false,true);


                } else if (!dialog.getOptionPanel().hasOption("aotd_cmsn_askCommission")) {
                    isInInteraction = false;
                }
            }


        } else {
            isInInteraction = false;
        }
    }
}
