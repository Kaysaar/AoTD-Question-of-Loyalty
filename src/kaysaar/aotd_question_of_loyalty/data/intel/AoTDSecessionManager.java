package kaysaar.aotd_question_of_loyalty.data.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.FactionCommissionIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.EventFactor;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import exerelin.campaign.DiplomacyManager;
import exerelin.campaign.diplomacy.DiplomacyBrain;
import kaysaar.aotd_question_of_loyalty.data.intel.secession.AoTDSecessionFleetIntel;
import kaysaar.aotd_question_of_loyalty.data.intel.secession.EmergentAuthorityApplier;
import kaysaar.aotd_question_of_loyalty.data.intel.secession.eventfactors.HoldoutEventFactor;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class AoTDSecessionManager extends BaseEventIntel implements FleetEventListener {
    public static String memFlag = "$aotd_secession";
    public FactionAPI rebellingAgainst;
    public ArrayList<MarketAPI> originalMarkets = new ArrayList<>();
    public ArrayList<MarketAPI>marketsCaptured = new ArrayList<>();
    public float fleetsDefeatedSince;

    public static AoTDSecessionManager get() {
        return (AoTDSecessionManager) Global.getSector().getMemory().get(memFlag);
    }

    public class AoTDExpeditionData {
        public MarketAPI target;
        public AoTDSecessionFleetIntel intel;
        public AoTDExpeditionData(MarketAPI market, AoTDSecessionFleetIntel intel) {
            this.target = market;
            this.intel = intel;
        }
        public void generateTooltipForStage(TooltipMakerAPI tooltip, FactionAPI faction) {
            generateInfoFromOutcome(intel, tooltip, Misc.getTooltipTitleAndLightHighlightColor(), 3f, faction);
        }

        private void generateInfoFromOutcome(AoTDSecessionFleetIntel intel, TooltipMakerAPI info, Color tc, float initPad, FactionAPI other) {
               if(intel.getOutcome()==null){
                   if(intel.getGoal()==AoTDSecessionGoal.BOMBARD){
                       info.addPara(INDENT+INDENT+"Under threat of saturation bombardment!",Misc.getNegativeHighlightColor() ,3f);

                   }
                   else{
                       info.addPara(INDENT+INDENT+"Under threat of invasion!",Misc.getNegativeHighlightColor() ,3f);

                   }
               }
               else{
                   info.addPara(INDENT+INDENT+"Defended !",Misc.getPositiveHighlightColor() ,3f);
               }
        }
    }

    public LinkedHashMap<Stage, ArrayList<AoTDExpeditionData>> dataOfExpeditions = new LinkedHashMap<>();

    public int getMarketsStillOwned() {
        int ow = 0;
        for (MarketAPI originalMarket : originalMarkets) {
            if (originalMarket.getFaction().isPlayerFaction()) {
                ow++;
            }
        }
        return ow;
    }

    public ArrayList<MarketAPI> getOriginalMarkets() {
        return originalMarkets;
    }

    public static enum AoTDSecessionGoal {
        BOMBARD,
        RETAKE,
    }


    public static enum Stage {
        START,
        THRESHOLD_1,
        THRESHOLD_2,
        THRESHOLD_3,
    }

    @Override
    protected void notifyStageReached(EventStageData stage) {
        Stage stg = getStage(stage.id);
        if (stg == Stage.THRESHOLD_1) {
            if(dataOfExpeditions == null)dataOfExpeditions = new LinkedHashMap<>();
            ArrayList<AoTDExpeditionData>data = new ArrayList<>();

            for (MarketAPI playerMarket : Misc.getPlayerMarkets(false)) {
                float fp = MathUtils.getRandomNumberInRange(90, 120);
                AoTDSecessionFleetIntel intel = new AoTDSecessionFleetIntel(rebellingAgainst, Misc.getFactionMarkets(rebellingAgainst).get(0), playerMarket, fp, MathUtils.getRandomNumberInRange(10, 15),
                        AoTDSecessionManager.AoTDSecessionGoal.RETAKE, new AoTDSecessionManager.AoTDSecessionReason(AoTDSecessionManager.AoTDSecessionGoal.RETAKE));
                data.add(new AoTDExpeditionData(playerMarket, intel));


            }
            for (MarketAPI playerMarket : marketsCaptured) {
                if(!playerMarket.isPlayerOwned())continue;
                float fp = MathUtils.getRandomNumberInRange(120, 150);
                AoTDSecessionFleetIntel intel = new AoTDSecessionFleetIntel(rebellingAgainst, Misc.getFactionMarkets(rebellingAgainst).get(0), playerMarket, fp, MathUtils.getRandomNumberInRange(10, 15),
                        AoTDSecessionManager.AoTDSecessionGoal.RETAKE, new AoTDSecessionManager.AoTDSecessionReason(AoTDSecessionManager.AoTDSecessionGoal.RETAKE));
                data.add(new AoTDExpeditionData(playerMarket, intel));
            }
            dataOfExpeditions.put(stg,data);
        }
        if (stg == Stage.THRESHOLD_2) {
            if(dataOfExpeditions == null)dataOfExpeditions = new LinkedHashMap<>();
            ArrayList<AoTDExpeditionData>data = new ArrayList<>();

            for (MarketAPI playerMarket : Misc.getPlayerMarkets(false)) {
                float fp = MathUtils.getRandomNumberInRange(140, 180);
                AoTDSecessionFleetIntel intel = new AoTDSecessionFleetIntel(rebellingAgainst, Misc.getFactionMarkets(rebellingAgainst).get(0), playerMarket, fp, MathUtils.getRandomNumberInRange(10, 15),
                        AoTDSecessionManager.AoTDSecessionGoal.RETAKE, new AoTDSecessionManager.AoTDSecessionReason(AoTDSecessionManager.AoTDSecessionGoal.RETAKE));
                data.add(new AoTDExpeditionData(playerMarket, intel));


            }
            for (MarketAPI playerMarket : marketsCaptured) {
                if(!playerMarket.isPlayerOwned())continue;
                float fp = MathUtils.getRandomNumberInRange(200, 250);
                AoTDSecessionFleetIntel intel = new AoTDSecessionFleetIntel(rebellingAgainst, Misc.getFactionMarkets(rebellingAgainst).get(0), playerMarket, fp, MathUtils.getRandomNumberInRange(10, 15),
                        AoTDSecessionManager.AoTDSecessionGoal.RETAKE, new AoTDSecessionManager.AoTDSecessionReason(AoTDSecessionManager.AoTDSecessionGoal.RETAKE));
                data.add(new AoTDExpeditionData(playerMarket, intel));
            }
            dataOfExpeditions.put(stg,data);
        }
        if (stg == Stage.THRESHOLD_3) {
            if(dataOfExpeditions == null)dataOfExpeditions = new LinkedHashMap<>();
            ArrayList<AoTDExpeditionData>data = new ArrayList<>();

            for (MarketAPI playerMarket : Misc.getPlayerMarkets(false)) {
                float fp = MathUtils.getRandomNumberInRange(250, 270);
                AoTDSecessionFleetIntel intel = new AoTDSecessionFleetIntel(rebellingAgainst, Misc.getFactionMarkets(rebellingAgainst).get(0), playerMarket, fp, MathUtils.getRandomNumberInRange(10, 15),
                        AoTDSecessionGoal.BOMBARD, new AoTDSecessionManager.AoTDSecessionReason(AoTDSecessionManager.AoTDSecessionGoal.RETAKE));
                data.add(new AoTDExpeditionData(playerMarket, intel));
            }
            for (MarketAPI playerMarket : marketsCaptured) {
                if(!playerMarket.isPlayerOwned())continue;
                float fp = MathUtils.getRandomNumberInRange(250, 280);
                AoTDSecessionFleetIntel intel = new AoTDSecessionFleetIntel(rebellingAgainst, Misc.getFactionMarkets(rebellingAgainst).get(0), playerMarket, fp, MathUtils.getRandomNumberInRange(10, 15),
                        AoTDSecessionManager.AoTDSecessionGoal.RETAKE, new AoTDSecessionManager.AoTDSecessionReason(AoTDSecessionManager.AoTDSecessionGoal.RETAKE));
                data.add(new AoTDExpeditionData(playerMarket, intel));
            }
            dataOfExpeditions.put(stg,data);
        }
    }

    public static class AoTDSecessionReason {
        public AoTDSecessionGoal type;
        public String marketId;
        public float weight;

        public AoTDSecessionReason(AoTDSecessionGoal type) {
            this.type = type;
        }
    }

    public AoTDSecessionManager(List<MarketAPI> startingMarkets, FactionAPI rebellingAgainst) {
        super();
        this.originalMarkets = new ArrayList<>(startingMarkets);
        this.rebellingAgainst = rebellingAgainst;
        setUp();
        Global.getSector().getMemory().set(memFlag, this);
        Global.getSector().getIntelManager().addIntel(this);
    }

    @Override
    public void addStageDescriptionWithImage(TooltipMakerAPI main, Object stageId) {
        super.addStageDescriptionWithImage(main, stageId);
    }

    @Override
    public void addStageDescriptionText(TooltipMakerAPI info, float width, Object stageId) {
        float opad = 10f;
        float small = 0f;
        Color h = Misc.getHighlightColor();

        EventStageData stage = getDataFor(stageId);
        if (stage == null) return;

//		if (isStageActiveAndLast(stageId) &&  stageId == Stage.START) {
//			addStageDesc(info, stageId, small, false);
//		} else if (isStageActive(stageId) && stageId != Stage.START) {
//			addStageDesc(info, stageId, small, false);
//		}

        if (isStageActive(stageId)) {
            addStageDesc(info, getStage(stageId), small, false);
        }
    }

    public void addStageDesc(TooltipMakerAPI info, Stage stageId, float initPad, boolean forTooltip) {
        if (stageId == Stage.START) {
            info.addPara("We have started our fight against our masters. At this stage they are in shock  of our treason, but this will not last. We must move quickly", 5f);
        }
        if (stageId == Stage.THRESHOLD_1) {
            info.addPara("Internal security and local defensive forces are scrambling to deal with our worlds. They are fully equipped and have standing orders to take our planets. Protect our people.", 5f);
            info.addSectionHeading("Current markets", Alignment.MID, 5f);
            generateTooltipForMarkets(info, stageId);
        }
        if (stageId == Stage.THRESHOLD_2) {
            info.addPara("A massive armada has been assembled to conquer our worlds. We must bolster our defences!", 5f);
            info.addSectionHeading("Current markets", Alignment.MID, 5f);
            generateTooltipForMarkets(info, stageId);
        }
        if (stageId == Stage.THRESHOLD_3) {
            info.addPara("The military itself has mobilized to a total war standing against us. They no longer consider us as insignificant rebellion, they have been fully authorized to raze our worlds!", 5f);
            info.addSectionHeading("Current markets", Alignment.MID, 5f);
            generateTooltipForMarkets(info, stageId);
            info.addPara("This is their final attempt to put rebellion down. If we manage to defeat all fleets , we will gain independence!", 5f);

        }


    }

    private void generateTooltipForMarkets(TooltipMakerAPI info, Stage stageId) {
        boolean generated = false;
        for (AoTDExpeditionData aoTDExpeditionData : dataOfExpeditions.get(stageId)) {
            if(aoTDExpeditionData.target.isPlayerOwned()){
                info.addPara(BULLET+aoTDExpeditionData.target.getName(),Misc.getTooltipTitleAndLightHighlightColor(),5f);
                aoTDExpeditionData.generateTooltipForStage(info,rebellingAgainst);
                generated = true;
            }

        }
        if(!generated){
            info.addPara("All markets are currently safe!",Misc.getPositiveHighlightColor(),5f);
        }
    }

    public void increaseAmountOfDefeatedFleets(int amount) {
        fleetsDefeatedSince += amount;
    }

    public Stage getStage(Object stage) {
        return (Stage) stage;
    }


    public void setUp() {
        this.maxProgress = 250;
        addStage(Stage.START, 0);
        addStage(Stage.THRESHOLD_1, 10);
        addStage(Stage.THRESHOLD_2, 90);
        addStage(Stage.THRESHOLD_3, 170);
        addFactor(new HoldoutEventFactor());

    }
    public int getPointsToReachNearThreshold(){
        int curr = this.progress;
        if(curr>=getDataFor(Stage.THRESHOLD_1).progress && curr<getDataFor(Stage.THRESHOLD_2).progress ){
            return  getDataFor(Stage.THRESHOLD_2).progress;
        }
        if(curr>=getDataFor(Stage.THRESHOLD_2).progress && curr<getDataFor(Stage.THRESHOLD_3).progress ){
            return  getDataFor(Stage.THRESHOLD_3).progress;
        }
        return 0;

    }
    @Override
    protected void notifyEnding() {
        super.notifyEnding();
        Global.getSector().getListenerManager().removeListener(this);
    }

    @Override
    protected void notifyEnded() {
        super.notifyEnded();
        Global.getSector().getMemoryWithoutUpdate().unset(memFlag);
    }

    @Override
    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {

    }

    @Override
    protected void advanceImpl(float amount) {
        super.advanceImpl(amount);
        if(Global.getSettings().getModManager().isModEnabled("nexerlin")){
            DiplomacyBrain brain = DiplomacyManager.getManager().getDiplomacyBrain(rebellingAgainst.getId());
            brain.getRecentWars().put(Factions.PLAYER,120f);
        }
        rebellingAgainst.getRelToPlayer().setLevel(RepLevel.VENGEFUL);
        if (Misc.getFactionMarkets(rebellingAgainst).isEmpty()) {
            endRebellion(true);
            return;
        }
       if(Misc.getPlayerMarkets(false).isEmpty()){
           endRebellion(false);
           return;
       }
       if(getLastActiveStage(true).id == Stage.THRESHOLD_3){
           boolean defeatedAll = true;
           for (AoTDExpeditionData aoTDExpeditionData : dataOfExpeditions.get(Stage.THRESHOLD_3)) {
               if(aoTDExpeditionData.intel.getOutcome() ==null){
                   setProgress(195);
                   defeatedAll = false;
                   break;
               }
           }
           if(defeatedAll){
               endRebellion(true);

           }

       }

    }
    public void makeSuspicius(FactionAPI other, InteractionDialogAPI dialog) {
        ReputationActionResponsePlugin.ReputationAdjustmentResult rep = Global.getSector().adjustPlayerReputation(
                new CoreReputationPlugin.RepActionEnvelope(CoreReputationPlugin.RepActions.MAKE_SUSPICOUS_AT_WORST,
                        null, null, dialog != null ? dialog.getTextPanel() : null, false, true),
                other.getId());

        FactionCommissionIntel.RepChangeData data = new FactionCommissionIntel.RepChangeData();
        data.faction = other;
        data.delta = rep.delta;
    }
    @Override
    public int getMaxMonthlyProgress() {
        if(getLastActiveStage(true).id == Stage.THRESHOLD_3){
            for (AoTDExpeditionData aoTDExpeditionData : dataOfExpeditions.get(Stage.THRESHOLD_3)) {
                if(aoTDExpeditionData.intel.getOutcome() ==null){
                    setProgress(195);
                    return  0;
                }
            }
        }
        return super.getMaxMonthlyProgress();
    }

    public void endRebellion(boolean success) {
        for (ArrayList<AoTDExpeditionData> value : dataOfExpeditions.values()) {
            for (AoTDExpeditionData aoTDExpeditionData : value) {
                aoTDExpeditionData.intel = null;
            }
            value.clear();
        }
        dataOfExpeditions.clear();

        if(success){
            makeSuspicius(rebellingAgainst,null);
            Global.getSector().getMemory().set(EmergentAuthorityApplier.memKey,true);
        }
        marketsCaptured.clear();
        originalMarkets.clear();

        endImmediately();
    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {

    }

    protected String getStageIconImpl(Object stageId) {
        EventStageData esd = getDataFor(stageId);
        if (esd == null) return null;

//		if (esd.id == Stage.MINOR_EVENT) {
//			System.out.println("ewfwfew");
//		}

        //setProgress(48);
        //setProgress(74);
//		if (esd.id == Stage.HA_EVENT) {
//			System.out.println("wefwefwe");
//		}
        //if (stageId == Stage.START) return null;

        if (esd.id != Stage.START) {
            return Global.getSettings().getSpriteName("events", "hostile_activity_HA_" + (((Stage) esd.id).ordinal() + 1));
        }
        // should not happen - the above cases should handle all possibilities - but just in case
        return Global.getSettings().getSpriteName("events", "hostile_activity_HA_1");
    }


    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("events", "hostile_activity_HA_5");
    }

    @Override
    protected String getName() {
        return "Independence war";
    }

    @Override
    public void createLargeDescription(CustomPanelAPI panel, float width, float height) {

        float opad = 10f;
        uiWidth = width;

        // TODO DEBUG
        //setProgress(900);
        //setProgress((int) (0 + (float) Math.random() * 400));
        //setProgress(900);
        //setProgress(499);
        //setProgress(200);
        //setProgress(0);

        TooltipMakerAPI main = panel.createUIElement(width, height, true);

        main.setTitleOrbitronVeryLarge();
        main.addTitle(getName(), Misc.getBasePlayerColor());

        EventProgressBarAPI bar = main.addEventProgressBar(this, 100f);
        TooltipMakerAPI.TooltipCreator barTC = getBarTooltip();
        if (barTC != null) {
            main.addTooltipToPrevious(barTC, TooltipMakerAPI.TooltipLocation.BELOW, false);
        }

        for (EventStageData curr : stages) {
            if (curr.progress <= 0) continue; // no icon for "starting" stage
            //if (curr.rollData == null || curr.rollData.equals(RANDOM_EVENT_NONE)) continue;
            if (RANDOM_EVENT_NONE.equals(curr.rollData)) continue;
            if (curr.wasEverReached && curr.isOneOffEvent && !curr.isRepeatable) continue;

            if (curr.hideIconWhenPastStageUnlessLastActive &&
                    curr.progress <= progress &&
                    getLastActiveStage(true) != curr) {
                continue;
            }

            EventStageDisplayData data = createDisplayData(curr.id);
            UIComponentAPI marker = main.addEventStageMarker(data);
            float xOff = bar.getXCoordinateForProgress(curr.progress) - bar.getPosition().getX();
            marker.getPosition().aboveLeft(bar, data.downLineLength).setXAlignOffset(xOff - data.size / 2f - 1);

            TooltipMakerAPI.TooltipCreator tc = getStageTooltip(curr.id);
            if (tc != null) {
                main.addTooltipTo(tc, marker, TooltipMakerAPI.TooltipLocation.LEFT, false);
            }
        }

        // progress indicator
        {
            UIComponentAPI marker = main.addEventProgressMarker(this);
            float xOff = bar.getXCoordinateForProgress(progress) - bar.getPosition().getX();
            marker.getPosition().belowLeft(bar, -getBarProgressIndicatorHeight() * 0.5f - 2)
                    .setXAlignOffset(xOff - getBarProgressIndicatorWidth() / 2 - 1);
        }
        float barW = getBarWidth();
        main.addSpacer(opad);
        main.addSpacer(opad);
        TooltipMakerAPI tp = main.beginSubTooltip(barW);
        tp.addSectionHeading("Rebellion", Alignment.MID, 0f);
        tp.addPara("We have started a righteous rebellion to break free from the shackles %s imposed on us!", 5f, Color.ORANGE, rebellingAgainst.getDisplayNameLongWithArticle());
        tp.addPara("To succeed, we must tire them out and repel their final assault. ", 5f, Color.ORANGE, "" + getMaxProgress());
        tp.addSectionHeading("Earning points", Alignment.MID, 5f);
        tp.addPara(BaseIntelPlugin.BULLET + "Hold our ground", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
        tp.addPara(BaseIntelPlugin.INDENT + "Time is on our side. The longer we hold as many world as possible, the quicker %s 's loyalists dissolve, the more legitimate our rebellion becomes.", 3f,Color.ORANGE,rebellingAgainst.getDisplayNameLong());

        if(Global.getSettings().getModManager().isModEnabled("nexerelin")){
           tp.addPara(BaseIntelPlugin.BULLET + "Conquer their worlds", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
            tp.addPara(BaseIntelPlugin.INDENT + "Managing to take even one of loyalists world will significantly increase our effort of gaining independence.", 3f);
        }
        main.endSubTooltip();
        main.addCustom(tp, 5f);
        main.addSpacer(opad);
        ;
        if (getLastActiveStage(true) != null) {
            addStageDescriptionWithImage(main, getLastActiveStage(true).id);

        }


        float factorWidth = (barW - opad) / 2f;

        if (withMonthlyFactors() != withOneTimeFactors()) {
            //factorWidth = barW;
            factorWidth = (int) (barW * 0.6f);
        }

        TooltipMakerAPI mFac = main.beginSubTooltip(factorWidth);

        Color c = getFactionForUIColors().getBaseUIColor();
        Color bg = getFactionForUIColors().getDarkUIColor();
        mFac.addSectionHeading("Monthly factors", c, bg, Alignment.MID, opad).getPosition().setXAlignOffset(0);

        float strW = 40f;
        float rh = 20f;
        //rh = 15f;
        mFac.beginTable2(getFactionForUIColors(), rh, false, false,
                "Monthly factors", factorWidth - strW - 3,
                "Progress", strW
        );

        for (EventFactor factor : factors) {
            if (factor.isOneTime()) continue;
            if (!factor.shouldShow(this)) continue;

            String desc = factor.getDesc(this);
            if (desc != null) {
                mFac.addRowWithGlow(Alignment.LMID, factor.getDescColor(this), desc,
                        Alignment.RMID, factor.getProgressColor(this), factor.getProgressStr(this));
                TooltipMakerAPI.TooltipCreator t = factor.getMainRowTooltip(this);
                if (t != null) {
                    mFac.addTooltipToAddedRow(t, TooltipMakerAPI.TooltipLocation.RIGHT, false);
                }
            }
            factor.addExtraRows(mFac, this);
        }

        //mFac.addButton("TEST", new String(), factorWidth, 20f, opad);
        mFac.addTable("None", -1, opad);
        mFac.getPrev().getPosition().setXAlignOffset(-5);

        main.endSubTooltip();

        TooltipMakerAPI oFac = main.beginSubTooltip(factorWidth);

        oFac.addSectionHeading("Recent one-time factors", c, bg, Alignment.MID, opad).getPosition().setXAlignOffset(0);

        oFac.beginTable2(getFactionForUIColors(), 20f, false, false,
                "One-time factors", factorWidth - strW - 3,
                "Progress", strW
        );

        List<EventFactor> reversed = new ArrayList<EventFactor>(factors);
        Collections.reverse(reversed);
        for (EventFactor factor : reversed) {
            if (!factor.isOneTime()) continue;
            if (!factor.shouldShow(this)) continue;

            String desc = factor.getDesc(this);
            if (desc != null) {
                oFac.addRowWithGlow(Alignment.LMID, factor.getDescColor(this), desc,
                        Alignment.RMID, factor.getProgressColor(this), factor.getProgressStr(this));
                TooltipMakerAPI.TooltipCreator t = factor.getMainRowTooltip(this);
                if (t != null) {
                    oFac.addTooltipToAddedRow(t, TooltipMakerAPI.TooltipLocation.LEFT);
                }
            }
            factor.addExtraRows(oFac, this);
        }

        oFac.addTable("None", -1, opad);
        oFac.getPrev().getPosition().setXAlignOffset(-5);
        main.endSubTooltip();


        float factorHeight = Math.max(mFac.getHeightSoFar(), oFac.getHeightSoFar());
        mFac.setHeightSoFar(factorHeight);
        oFac.setHeightSoFar(factorHeight);


        if (withMonthlyFactors() && withOneTimeFactors()) {
            main.addCustom(mFac, opad * 2f);
            main.addCustomDoNotSetPosition(oFac).getPosition().rightOfTop(mFac, opad);
        } else if (withMonthlyFactors()) {
            main.addCustom(mFac, opad * 2f);
        } else if (withOneTimeFactors()) {
            main.addCustom(oFac, opad * 2f);
        }

        //main.addButton("TEST", new String(), factorWidth, 20f, opad);

        panel.addUIElement(main).inTL(0, 0);
    }

}
