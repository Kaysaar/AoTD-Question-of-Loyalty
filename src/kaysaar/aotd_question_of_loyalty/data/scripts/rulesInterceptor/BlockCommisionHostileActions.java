package kaysaar.aotd_question_of_loyalty.data.scripts.rulesInterceptor;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;

import java.awt.*;

public class BlockCommisionHostileActions implements EveryFrameScript {
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {
        if (Global.getSector().getCampaignUI().isShowingDialog()) {
            InteractionDialogAPI dialog  = Global.getSector().getCampaignUI().getCurrentInteractionDialog();
            if(dialog==null)return;
            if(dialog.getOptionPanel()==null)return;
            if(!dialog.getOptionPanel().hasOptions())return;
            if(dialog.getInteractionTarget()!=null&&dialog.getInteractionTarget().getFaction()!=null&& QoLMisc.isCommissionedBy(dialog.getInteractionTarget().getFaction().getId())){
                if(dialog.getOptionPanel().hasOption("marketConsiderHostile")){
                    dialog.getOptionPanel().setEnabled("marketConsiderHostile",false);
                    dialog.getOptionPanel().addOptionTooltipAppender("marketConsiderHostile", new OptionPanelAPI.OptionTooltipCreator() {
                        @Override
                        public void createTooltip(TooltipMakerAPI tooltip, boolean hadOtherText) {
                            tooltip.addPara("We can't attack market of faction, that commissions us!",Misc.getNegativeHighlightColor(),0f);
                        }
                    });

                }
                if(dialog.getOptionPanel().hasOption("COB_salvage")){
                    dialog.getOptionPanel().setEnabled("COB_salvage",false);
                    dialog.getOptionPanel().addOptionTooltipAppender("COB_salvage", new OptionPanelAPI.OptionTooltipCreator() {
                        @Override
                        public void createTooltip(TooltipMakerAPI tooltip, boolean hadOtherText) {
                            tooltip.addPara("We can't dismantle structure of faction, that commissions us!",Misc.getNegativeHighlightColor(),0f);
                        }
                    });

                }
                if(dialog.getOptionPanel().hasOption(FleetInteractionDialogPluginImpl.OptionId.ENGAGE)){
                    dialog.getOptionPanel().setEnabled(FleetInteractionDialogPluginImpl.OptionId.ENGAGE,false);
                    dialog.getOptionPanel().addOptionTooltipAppender(FleetInteractionDialogPluginImpl.OptionId.ENGAGE, new OptionPanelAPI.OptionTooltipCreator() {
                        @Override
                        public void createTooltip(TooltipMakerAPI tooltip, boolean hadOtherText) {
                            tooltip.addPara("We can't attack fleet tied to faction, that commissions us!", Misc.getNegativeHighlightColor(),10f);
                        }
                    });

                }
                if(dialog.getOptionPanel().hasOption("nex_buyColony")&& !AoTDCommIntelPlugin.get().canBuyMarket()){
                    dialog.getOptionPanel().setEnabled("nex_buyColony",false);
                    dialog.getOptionPanel().addOptionTooltipAppender("nex_buyColony", new OptionPanelAPI.OptionTooltipCreator() {
                        @Override
                        public void createTooltip(TooltipMakerAPI tooltip, boolean hadOtherText) {
                            tooltip.addPara("We can't buy additional markets due to not having permit that allows us to have additional colony under control", Misc.getNegativeHighlightColor(),10f);
                        }
                    });

                }
                if(dialog.getOptionPanel().hasOption("COB_takeControl")){
                    dialog.getOptionPanel().setEnabled("COB_takeControl",false);
                    dialog.getOptionPanel().addOptionTooltipAppender("COB_takeControl", new OptionPanelAPI.OptionTooltipCreator() {
                        @Override
                        public void createTooltip(TooltipMakerAPI tooltip, boolean hadOtherText) {
                            tooltip.addPara("We can't take structure of faction, that commissions us!",Misc.getNegativeHighlightColor(),0f);
                        }
                    });

                }
            }

        }
    }
}
