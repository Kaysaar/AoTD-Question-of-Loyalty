package kaysaar.aotd_question_of_loyalty.data.intel.dialog;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.intel.AoTDCommIntelPlugin;
import kaysaar.aotd_question_of_loyalty.data.intel.AoTDSecessionManager;
import kaysaar.aotd_question_of_loyalty.data.intel.secession.AoTDSecessionFleetIntel;
import kaysaar.aotd_question_of_loyalty.data.scripts.commision.FactionHatredManager;

import java.awt.*;

public class RebelionEventDialog extends BasePopUpDialog {
    transient FactionAPI factionToRebel;
    transient IntelUIAPI api;
    public RebelionEventDialog(String headerTitle, FactionAPI factionAPI, IntelUIAPI api) {
        super(headerTitle);
        this.api = api;
        this.factionToRebel = factionAPI;
    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        super.createContentForDialog(tooltip, width);
        tooltip.setParaInsigniaLarge();
        tooltip.addPara("Breaking free will allow you and your colonies to run independently and be under your control", 5f, Color.ORANGE, factionToRebel.getDisplayName());
        tooltip.addPara("None of blueprints, that has been learned due to being commissioned will be lost.", 5f);
        tooltip.addPara("However %s authorities will not take kindly to you breaking free from their control and will try to get rid of you and retake the colonies by force", 5f, Color.ORANGE, factionToRebel.getDisplayNameLongWithArticle());
        tooltip.addPara("Declaring outright rebellion is a dangerous prospect that cannot be undone. Be sure your colonies are well defended and your fleet well supplied", Misc.getNegativeHighlightColor(), 5f);
     ;

    }

    @Override
    public void applyConfirmScript() {

        //float fp = from.getSize() * 20 + threshold * 0.5f;
        float fp = 520f;
        //fp = 500;
//		if (from.getFaction().isHostileTo(target.getFaction())) {
//			fp *= 1.25f;
//		}

        float orgDur = 1f;

        AoTDCommIntelPlugin.get().endCommision(null,true);

        factionToRebel.getRelToPlayer().adjustRelationship(-2f, RepLevel.VENGEFUL);
        AoTDSecessionManager man = new AoTDSecessionManager(Misc.getPlayerMarkets(false),factionToRebel);
        FactionHatredManager.get().addFactionThatHatePlayer(factionToRebel.getId());
        FactionHatredManager.get().addRebellionCount(1);
        api.updateIntelList();
        api.recreateIntelUI();
        api.selectItem(man);
        api.recreateIntelUI();

    }
}
