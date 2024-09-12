package kaysaar.aotd_question_of_loyalty.data.scripts.rulesInterceptor;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;

import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;
import kaysaar.aotd_question_of_loyalty.data.tags.AoTDRankTags;

import java.util.List;

public class TalkWayFromScanScript extends BaseReplaceScript{
    public boolean shouldInsert(InteractionDialogAPI dialogAPI){
        boolean goodOpton = dialogAPI.getOptionPanel().hasOption("tOff_comply2")||dialogAPI.getOptionPanel().hasOption("tOff_comply")||dialogAPI.getOptionPanel().hasOption("cargoScan_comply");
//       return d
         return goodOpton&& QoLMisc.isCommissionedBy(dialogAPI.getInteractionTarget().getFaction().getId())&&AoTDCommIntelPlugin.get().getCurrentRankData().hasTag(AoTDRankTags.CAN_INTIMIDATE_CARGO_PATROL_FLEETS);

    }
    @Override
    public void advance(float amount) {
        if (Global.getSector().getCampaignUI().isShowingDialog()) {
            InteractionDialogAPI dialog = Global.getSector().getCampaignUI().getCurrentInteractionDialog();
            if (dialog != null) {
                if (shouldInsert(dialog)&&!isInInteraction) {
                    isInInteraction = true;
                    List list = dialog.getOptionPanel().getSavedOptionList();
                    dialog.getOptionPanel().clearOptions();
                    dialog.getOptionPanel().addOption("I would not recommend that if I were you","aotd_refuse_scan", AoTDCommIntelPlugin.optionColor,"We can intimidate this officer due to our rank within faction");
                    List list2 = dialog.getOptionPanel().getSavedOptionList();
                    Object obj = list2.get(0);
                    List list3 = dialog.getOptionPanel().getSavedOptionList();
                    list3.clear();

                    dialog.getOptionPanel().clearOptions();
                    int i = 0;
                    for (Object o : list) {
                        if(i==2){
                            list3.add(obj);
                        }
                        list3.add(o);
                        i++;
                    }
                    dialog.getOptionPanel().restoreSavedOptions(list3);


                } else if (!dialog.getOptionPanel().hasOption("aotd_refuse_scan")) {
                    isInInteraction = false;
                }
            }


        } else {
            isInInteraction = false;
        }
    }
}
