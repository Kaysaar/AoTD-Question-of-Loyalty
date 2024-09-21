package kaysaar.aotd_question_of_loyalty.data.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.scripts.commision.AoTDCommissionUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RankShowcaseUIDialog implements CustomDialogDelegate {
    public static final float WIDTH = 600f;
    public static final float HEIGHT = Global.getSettings().getScreenHeight() - 500f;
    public List<ButtonAPI> buttons = new ArrayList<>();
    String selected;
    InteractionDialogAPI dialog;
    Map<String, MemoryAPI> memoryMap;
    public RankShowcaseUIDialog(InteractionDialogAPI dialogAPI,Map<String,MemoryAPI>memoryMap){
        dialog = dialogAPI;
        this.memoryMap = memoryMap;
    }
    @Override
    public void createCustomDialog(CustomPanelAPI panel, CustomDialogCallback callback) {
        CustomPanelAPI panelS = panel.createCustomPanel(WIDTH,HEIGHT,new ButtonReportingCustomPanel(this));
        TooltipMakerAPI panelTooltip = panelS.createUIElement(WIDTH, HEIGHT, true);
        LabelAPI labelAPI = panelTooltip.addSectionHeading("Select a rank", Alignment.MID, 0f);
        for (String s : AoTDCommIntelPlugin.get().getRanksForPromotion()) {
           ButtonAPI button =  makeButton(panelTooltip,s,AoTDCommIntelPlugin.get().isStageActive(s));
           buttons.add(button);
            panelTooltip.setHeightSoFar(-button.getPosition().getY());
            panelTooltip.addSpacer(5f);

        }

        panelS.addUIElement(panelTooltip).inTL(0,0);
        panel.addComponent(panelS).inTL(0,0);
    }

    @NotNull
    private  ButtonAPI makeButton(TooltipMakerAPI panelTooltip, String role, boolean stageActive) {
        TooltipMakerAPI dummy = panelTooltip.beginSubTooltip(WIDTH-10f);
        Color base = Global.getSector().getPlayerFaction().getBaseUIColor();
        Color bg = Global.getSector().getPlayerFaction().getDarkUIColor();
        Color highlight = Global.getSector().getPlayerFaction().getBrightUIColor();
        AoTDCommIntelPlugin.get().getStageTooltipImpl(role).createTooltip(dummy,true,null);
        if(stageActive){
            dummy.addPara("You met requirement", Misc.getPositiveHighlightColor(),10f);
        }
        else{
            dummy.addPara("You have not met requirement", Misc.getNegativeHighlightColor(),10f);

        }
        ButtonAPI button = panelTooltip.addAreaCheckbox("",role,base,bg,highlight,WIDTH-10f,dummy.getHeightSoFar()+10,5f);

        button.setEnabled(stageActive);
        panelTooltip.endSubTooltip();
        panelTooltip.addCustom(dummy,0f).getPosition().inTL(button.getPosition().getX(),-button.getPosition().getY()-button.getPosition().getHeight()+5);
        return button;
    }

    @Override
    public boolean hasCancelButton() {
        return true;
    }

    @Override
    public String getConfirmText() {
        return null;
    }

    @Override
    public String getCancelText() {
        return null;
    }

    @Override
    public void customDialogConfirm() {
        Global.getSector().getMemory().set("$aotd_rank_temp",selected,0.1f);
        AoTDCommissionUtil.reportPlayerGotNewRank(AoTDCommIntelPlugin.get().getCurrentRankData());
        FireBest.fire(null, dialog, memoryMap, "AotdPromotion");
    }
    @Override
    public void customDialogCancel() {

    }

    @Override
    public CustomUIPanelPlugin getCustomPanelPlugin() {
        return null;
    }
    public void reportButtonPressed(Object id) {
        selected = (String) id;

        for (ButtonAPI button : buttons) {
            if (button.isChecked() && button.getCustomData() != id) button.setChecked(false);
        }
    }

    public static class ButtonReportingCustomPanel extends BaseCustomUIPanelPlugin {
        public RankShowcaseUIDialog delegate;

        public ButtonReportingCustomPanel(RankShowcaseUIDialog delegate) {
            this.delegate = delegate;
        }

        @Override
        public void buttonPressed(Object buttonId) {
            super.buttonPressed(buttonId);
            delegate.reportButtonPressed(buttonId);
        }
    }
}
