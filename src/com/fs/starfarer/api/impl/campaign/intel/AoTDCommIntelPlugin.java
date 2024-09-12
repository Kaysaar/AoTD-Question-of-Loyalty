package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.ReputationActionResponsePlugin;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.campaign.listeners.ListenerUtil;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.eventfactors.monthly.MonthlyObligationFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.EventFactor;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.listeners.AoTDFreeStorageComm;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;
import kaysaar.aotd_question_of_loyalty.data.models.BaseFactionCommisionData;
import kaysaar.aotd_question_of_loyalty.data.models.RankData;
import kaysaar.aotd_question_of_loyalty.data.scripts.commision.AoTDCommissionDataManager;
import kaysaar.aotd_question_of_loyalty.data.tags.AoTDCommisionTags;
import kaysaar.aotd_question_of_loyalty.data.tags.AoTDRankTags;
import kaysaar.aotd_question_of_loyalty.data.ui.RankShowcaseUIDialog;

import java.awt.*;
import java.util.*;
import java.util.List;

public class AoTDCommIntelPlugin extends BaseEventIntel implements EconomyTickListener {
    BaseFactionCommisionData data;
    public static Color optionColor = new Color(239, 139, 24);
    public static Color BAR_COLOR = Global.getSettings().getColor("progressBarFleetPointsColor");
    public static String RESEARCH_KEY = "$aotd_commision"; //we assume you can only have one contract  at the time;
    public String currentRank;
    public static String RANK_KEY = "$aotd_rank"; //we assume you can only have one contract  at the time;

    public String getRank() {
        return data.getFaction().getMemoryWithoutUpdate().getString(RANK_KEY);
    }

    public void setRank(String id) {
        data.getFaction().getMemory().set(RANK_KEY, id);
    }
    protected LinkedHashMap<String, FactionCommissionIntel.RepChangeData> repChanges = new LinkedHashMap<String, FactionCommissionIntel.RepChangeData>();
    public void unset() {
        data.getFaction().getMemory().unset(RANK_KEY);
    }
    public void updateData(){
        String factionId = data.factionID;
        data = AoTDCommissionDataManager.getInstance().getCommisionData(factionId);
        boolean timeToBreak = false;
        for (EventStageData stage : stages) {
            if(timeToBreak)break;
            if(stage.id.equals(getRank()))timeToBreak = true;
            stage.wasEverReached = true;
        }
    }
    public static void addFactorCreateIfNecessary(EventFactor factor, InteractionDialogAPI dialog) {
        if (get() == null) {
            return;

        }
        if (get() != null) {
            get().addFactor(factor, dialog);
        }
    }

    public FactionAPI getCurrentlyCommisonedFaction() {
        return data.getFaction();
    }

    @Override
    public void reportEconomyTick(int iterIndex) {
        super.reportEconomyTick(iterIndex);
        float numIter = Global.getSettings().getFloat("economyIterPerMonth");
        float f = 1f / numIter;

        //CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        MonthlyReport report = SharedData.getData().getCurrentReport();

        MonthlyReport.FDNode fleetNode = report.getNode(MonthlyReport.FLEET);
        fleetNode.name = "Fleet";
        fleetNode.custom = MonthlyReport.FLEET;
        fleetNode.tooltipCreator = report.getMonthlyReportTooltip();
        final FactionAPI faction = data.getFaction();
        float stipend = getCurrentRankData().salary;
        MonthlyReport.FDNode stipendNode = report.getNode(fleetNode, "node_id_stipend_" + data.getFaction().getId());
        stipendNode.income += stipend*f;

        if (stipendNode.name == null) {
            stipendNode.name = faction.getDisplayName() + " Commission";
            stipendNode.icon = faction.getCrest();
            stipendNode.tooltipCreator = new TooltipMakerAPI.TooltipCreator() {
                public boolean isTooltipExpandable(Object tooltipParam) {
                    return false;
                }

                public float getTooltipWidth(Object tooltipParam) {
                    return 450;
                }

                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.addPara("Your monthly stipend for holding a " + faction.getDisplayName() + " commission", 0f);
                }
            };
        }
    }

    @Override
    public void reportEconomyMonthEnd() {
        super.reportEconomyMonthEnd();
    }

    public static AoTDCommIntelPlugin get() {
        return (AoTDCommIntelPlugin) Global.getSector().getMemoryWithoutUpdate().get(RESEARCH_KEY);
    }

    public BaseFactionCommisionData getData() {
        return data;
    }
    public List<FactionAPI> getHostileFactions() {
        FactionAPI player = Global.getSector().getPlayerFaction();
        List<FactionAPI> hostile = new ArrayList<FactionAPI>();
        for (FactionAPI other : getRelevantFactions()) {
            if (this.data.getFaction().isHostileTo(other)) {
                hostile.add(other);
            }
        }
        return hostile;
    }
    public static Object getAsPureObj() {
        return Global.getSector().getMemoryWithoutUpdate().get(RESEARCH_KEY);
    }
    public void initializeFully(BaseFactionCommisionData contract,TextPanelAPI text, boolean withIntelNotification,String startingRankId,String firstOfficialRank){


        this.data = contract;
        this.progress = 10;
        boolean havePlanets  = !Misc.getPlayerMarkets(false).isEmpty();
        if (havePlanets&&firstOfficialRank!=null) {
            setRank(firstOfficialRank);
            this.progress = data.rankValue.get(firstOfficialRank) + 10;
        } else {
            setRank(startingRankId);

        }
        setup();
        Global.getSector().getMemoryWithoutUpdate().set(RESEARCH_KEY, this);
        setSortTier(IntelSortTier.TIER_2);
        Global.getSector().addScript(this);
        Global.getSector().getListenerManager().addListener(this);
        Global.getSector().getIntelManager().addIntel(this, !withIntelNotification, text);
    }

    @Override
    protected void notifyEnded() {
        super.notifyEnded();
        Global.getSector().getIntelManager().removeIntel(this);
        Global.getSector().getMemoryWithoutUpdate().unset(RESEARCH_KEY);
    }

    protected void setup() {
        factors.clear();
        stages.clear();
        setMaxProgress(data.maxProgress);
        for (RankData rank : data.ranks) {
            addStage(rank.id, data.rankValue.get(rank.id), StageIconSize.MEDIUM);
            getDataFor(rank.id).keepIconBrightWhenLaterStageReached = true;
        }


        stages.get(stages.size() - 1).isOneOffEvent = true;
        addFactor(new MonthlyObligationFactor());

    }
//Touchofvanilla code begins here


    //These add individual points on the progress bar
    protected void addBulletPoints(TooltipMakerAPI info, IntelInfoPlugin.ListInfoMode mode, boolean isUpdate, Color tc, float initPad) {
        if (!this.addEventFactorBulletPoints(info, mode, isUpdate, tc, initPad)) {
            Color h = Misc.getHighlightColor();
            if (isUpdate && this.getListInfoParam() instanceof BaseEventIntel.EventStageData) {
                BaseEventIntel.EventStageData esd = (BaseEventIntel.EventStageData) this.getListInfoParam();
                if(!esd.wasEverReached){
                    if(!data.hasTag(AoTDCommisionTags.UP_RANK_AUTOMATICALLY)){
                        info.addPara("You have earned right to become %s",0f,Color.ORANGE,getRankForID(getStageId(esd.id)).name);

                    }
                    else{
                        info.addPara("You are now %s",0f,Color.ORANGE,getRankForID(getStageId(esd.id)).name);
                        setRank(getStageId(esd.id));

                    }
                }

            }
        }
    }
    public List<FactionAPI> getRelevantFactions() {
        Set<FactionAPI> factions = new LinkedHashSet<FactionAPI>();
        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (market.isHidden()) continue;
            FactionAPI curr = market.getFaction();
            if (factions.contains(curr)) continue;

            if (curr.isShowInIntelTab()) {
                factions.add(curr);
            }
        }

        return new ArrayList<FactionAPI>(factions);
    }
    public void makeRepChanges(InteractionDialogAPI dialog) {
        FactionAPI player = Global.getSector().getPlayerFaction();
        for (FactionAPI other : getRelevantFactions()) {
            FactionCommissionIntel.RepChangeData change = repChanges.get(other.getId());

            boolean madeHostile = change != null;
            boolean factionHostile = data.getFaction().isHostileTo(other);
            boolean playerHostile = player.isHostileTo(other);

            if (factionHostile && !playerHostile && !madeHostile) {
                makeHostile(other, dialog);
            }

            if (!factionHostile && madeHostile) {
                undoRepChange(other, dialog);
            }
        }
    }

    public void makeHostile(FactionAPI other, InteractionDialogAPI dialog) {
        ReputationActionResponsePlugin.ReputationAdjustmentResult rep = Global.getSector().adjustPlayerReputation(
                new CoreReputationPlugin.RepActionEnvelope(CoreReputationPlugin.RepActions.MAKE_HOSTILE_AT_BEST,
                        null, null, dialog != null ? dialog.getTextPanel() : null, false, true),
                other.getId());

        FactionCommissionIntel.RepChangeData data = new FactionCommissionIntel.RepChangeData();
        data.faction = other;
        data.delta = rep.delta;
        repChanges.put(other.getId(), data);
    }

    public void undoRepChange(FactionAPI other, InteractionDialogAPI dialog) {
        String id = other.getId();
        FactionCommissionIntel.RepChangeData change = repChanges.get(id);

        if (change == null) return;

        CoreReputationPlugin.CustomRepImpact impact = new CoreReputationPlugin.CustomRepImpact();
        impact.delta = -change.delta;
        impact.delta = Math.max(0f, impact.delta - Global.getSettings().getFloat("factionCommissionRestoredRelationshipPenalty"));
        if (impact.delta > 0) {
            Global.getSector().adjustPlayerReputation(
                    new CoreReputationPlugin.RepActionEnvelope(CoreReputationPlugin.RepActions.CUSTOM,
                            impact, null, dialog != null ? dialog.getTextPanel() : null, false, true),
                    id);
        }
        repChanges.remove(id);
    }

    public void undoAllRepChanges(InteractionDialogAPI dialog) {
        for (FactionCommissionIntel.RepChangeData data : new ArrayList<FactionCommissionIntel.RepChangeData>(repChanges.values())) {
            undoRepChange(data.faction, dialog);
        }
    }
    protected boolean addEventFactorBulletPoints(TooltipMakerAPI info, ListInfoMode mode, boolean isUpdate,
                                                 Color tc, float initPad) {
        if (isUpdate && getListInfoParam() instanceof EventFactor) {
            EventFactor factor = (EventFactor) getListInfoParam();
            if (factor.isOneTime()) {
                factor.addBulletPointForOneTimeFactor(this, info, tc, initPad);
            }
            return true;
        }
        return false;
    }
    public float getImageSizeForStageDesc(Object stageId) {
        return 48f;
    }

    public float getImageIndentForStageDesc(Object stageId) {

        return 16f;
    }


    @Override
    public void addStageDescriptionText(TooltipMakerAPI info, float width, Object stageId) {
        float opad = 10.0F;
        float small = 0.0F;
        Color h = Misc.getHighlightColor();
        BaseEventIntel.EventStageData stage = this.getDataFor(stageId);
        if (stage != null && stage.id.equals(getRank())) {
            this.addStageDesc(info, stageId, small, false);


        }
    }

    public void addStageDesc(TooltipMakerAPI info, Object stageId, float initPad, boolean forTooltip) {
        float opad = 10.0F;
        Color h = Misc.getHighlightColor();
        if (!forTooltip) {
            info.addPara("Current Rank : %s", initPad, Color.ORANGE, getRankForID((String) stageId).name);
            getRankInfoForTooltip(info, stageId, initPad);
            if(data.hasTag(AoTDCommisionTags.UP_RANK_AUTOMATICALLY)){
                info.addPara("Once you gather enough points for higher rank you automatically attain new rank!", 5f);

            }
            else{
                info.addPara("Once you gather enough points for higher rank, you can go to one of Administrators from " + data.getFaction().getDisplayName() + " to ask for promotion", 5f);

            }
        } else {
            getRankInfoForTooltip(info, stageId, initPad);

        }

    }

    public RankData getRankForID(String id) {
        for (RankData rank : data.ranks) {
            if (rank.id.equals(id)) {
                return rank;
            }
        }
        return null;
    }

    public RankData getCurrentRankData() {
        return getRankForID(getRank());
    }

    public void getRankInfoForTooltip(TooltipMakerAPI info, Object stageId, float initPad) {
        RankData rank = getRankForID(getStageId(stageId));;
        info.addPara("Effects:", 3f);
        info.addPara("Salary : %s", initPad, Color.ORANGE, Misc.getDGSCredits(rank.salary));
        if(rank.hasTag(AoTDRankTags.NONRESTRICTIVE_COLONIZATION)){
            info.addPara("Receives permit for unrestricted colonization", Misc.getPositiveHighlightColor(), initPad);
        }
        else{
            if(rank.getAmountOfColoniesAbleToColonize()==0){
                info.addPara("Can't colonize any planets", Misc.getNegativeHighlightColor(), initPad);
            }
            else{
                info.addPara("Receives permit for having %s colonies either owned by you , or being under your direct command", initPad,Misc.getTooltipTitleAndLightHighlightColor(),Color.ORANGE,""+rank.getAmountOfColoniesAbleToColonize() );

            }
        }
        if(rank.getTarrifReduciton()>0){
            info.addPara("Reduced tariff for goods by %s", initPad,Misc.getTooltipTitleAndLightHighlightColor(),Color.ORANGE,""+(int)rank.getTarrifReduciton()+"%");
        }
        if(rank.hasTag(AoTDRankTags.CAN_SCAN_CARGO)){
            info.addPara("Ability to scan cargo in systems, where %s have any holdings!", initPad,Misc.getTooltipTitleAndLightHighlightColor(),Color.ORANGE,""+data.getFaction().getDisplayName());
        }
        if(rank.hasTag(AoTDRankTags.FREE_STORAGE)){
            info.addPara("Free storage on all planets belonging to %s", initPad,Misc.getTooltipTitleAndLightHighlightColor(),Color.ORANGE,""+data.getFaction().getDisplayName());
        }
        if(rank.hasTag(AoTDRankTags.HAVE_RESEARCH_PERMIT)&&Global.getSettings().getModManager().isModEnabled("aotd_vok")){
            info.addPara("Receives permit for establishing independent research operations", Misc.getPositiveHighlightColor(), initPad);
        }

        if(rank.hasTag(AoTDRankTags.CAN_INTIMIDATE_CARGO_PATROL_FLEETS)){
            info.addPara("Ability to deny scan of your cargo by patrol fleets of faction", Misc.getPositiveHighlightColor(), initPad);
        }
        if(rank.hasTag(AoTDRankTags.GETS_COLONY_PROTECTION)){
            info.addPara(data.getFaction().getDisplayName() + " will protect your colonies from other factions", Misc.getPositiveHighlightColor(), initPad);
        }
        if(rank.hasTag(AoTDRankTags.COLONY_CONTRACTS)){
            info.addPara("Ability to sign permanent colony deals with faction (WIP)", Misc.getPositiveHighlightColor(), initPad);
        }
        if(!rank.isLeavingAnOption()){
            info.addPara("You are now a permanent member of %s. Any act against %s interest or outright hostile attitude will lead to execution!", initPad,Misc.getTooltipTitleAndLightHighlightColor(),Color.ORANGE,data.getFaction().getDisplayNameWithArticle(),data.getFaction().getDisplayNameWithArticle());

        }
    }

    @Override
    public TooltipMakerAPI.TooltipCreator getStageTooltipImpl(Object stageId) {
        final BaseEventIntel.EventStageData esd = this.getDataFor(stageId);
        return esd != null && stages.contains(esd) ? new TooltipMakerAPI.TooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return true;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return RankShowcaseUIDialog.WIDTH;
            }

            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                float opad = 5.0F;
                tooltip.addTitle("Rank : " + getRankForID((String) esd.id).name);
                tooltip.addPara(getRankForID((String) esd.id).getDescription(), 3f);
                addStageDesc(tooltip, esd.id, opad, true);
                esd.addProgressReq(tooltip, opad);
            }
        } : null;
    }

    @Override
    public TooltipMakerAPI.TooltipCreator getStageTooltip(Object stageId) {
        return super.getStageTooltip(stageId);
    }

    public String getStageId(Object id) {
        return (String) id;
    }

    @Override
    protected String getName() {
        return "Commission " + data.getFaction().getDisplayName();
    }

    @Override
    public String getIcon() {
        return data.getFaction().getCrest();
    }

    protected String getStageIconImpl(Object stageId) {
        String stage = (String) stageId;
            return data.getFaction().getCrest();

        // should not happen - the above cases should handle all possibilities - but just in case
    }

    public void setProgress(int progress) {
        if (this.progress == progress) return;

        if (progress < 0) progress = 0;
        if (progress > maxProgress) progress = maxProgress;

        EventStageData prev = getLastActiveStage(true);
        prevProgressDeltaWasPositive = this.progress < progress;

        //progress += 30;
        //progress = 40;
        //progress = 40;
        //progress = 499;

        this.progress = progress;


        if (progress < 0) {
            progress = 0;
        }
        if (progress > getMaxProgress()) {
            progress = getMaxProgress();
        }

        // Check to see if randomized events need to be rolled/reset
        for (EventStageData esd : getStages()) {
            if (esd.wasEverReached && esd.isOneOffEvent && !esd.isRepeatable) continue;

            if (esd.randomized) {
                if ((esd.rollData != null && !esd.rollData.equals(RANDOM_EVENT_NONE)) && progress <= esd.progressToResetAt) {
                    resetRandomizedStage(esd);
                }
                if ((esd.rollData == null || esd.rollData.equals(RANDOM_EVENT_NONE)) && progress >= esd.progressToRollAt) {
                    rollRandomizedStage(esd);
                    if (esd.rollData == null) {
                        esd.rollData = RANDOM_EVENT_NONE;
                    }
                }
            }
        }

        // go through all of the stages made active by the new progress value
        // generally this'd just be one stage, but possible to have multiple for a large
        // progress increase
        for (EventStageData curr : getStages()) {
            if (curr.progress <= prev.progress && !prev.wasEverReached &&
                    (prev.rollData == null || prev.rollData.equals(RANDOM_EVENT_NONE))) continue;
            //if (curr.progress > progress) continue;

            // reached
            if (curr.progress <= progress) {

                if (curr != null && (!prev.wasEverReached)) {
                    if (curr.sendIntelUpdateOnReaching && curr.progress > 0 && (prev == null || prev.progress < curr.progress)) {
                        sendUpdateIfPlayerHasIntel(curr, getTextPanelForStageChange());
                    }
                    notifyStageReached(curr);
                    curr.rollData = null;
                    curr.wasEverReached = true;

                    progress = getProgress(); // in case it was changed by notifyStageReached()
                }
            }
        }
    }

    public void endCommision(InteractionDialogAPI dialog){
        undoAllRepChanges(dialog);
        endImmediately();
    }
    @Override
    public void endImmediately() {
        for (MarketAPI marketAPI : Global.getSector().getEconomy().getMarketsCopy()) {
            if (marketAPI.getFaction().getId().equals(data.factionID)) {
                marketAPI.getTariff().unmodifyFlat("aotd_commision");
            }
        }
        AoTDFreeStorageComm.runCleanUpScript(data.getFaction());
        //endAfterDelay();
        super.endImmediately();


    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
    }

    @Override
    protected void advanceImpl(float amount) {
        super.advanceImpl(amount);
        if(data!=null){
            if(!isDone()){
                for (MarketAPI marketAPI : Global.getSector().getEconomy().getMarketsCopy()) {
                    if (marketAPI.getFaction().getId().equals(data.factionID)) {
                        marketAPI.getTariff().modifyFlat("aotd_commision", -(getCurrentRankData().getTarrifReduciton()/100f), "Commission");
                    }
                }
                makeRepChanges(null);
            }
        }


    }

    @Override
    public int getProgress() {
        return super.getProgress();
    }

    public ArrayList<String> getRanksForPromotion() {
        ArrayList<String> ranks = new ArrayList<>();
        boolean aboveRank = false;
        for (RankData rank : data.ranks) {
            if (getRank().equals(rank.id)) {
                aboveRank = true;
                continue;
            }
            if (aboveRank) {
                ranks.add(rank.id);
            }
        }
        return ranks;
    }

    @Override
    public Color getBarColor() {
        Color color = BAR_COLOR;
        //color = Misc.getBasePlayerColor();
        color = Misc.interpolateColor(color, Color.black, 0.25f);
        return color;
    }

    public boolean canColonize() {

        RankData data = getRankForID(getRank());
        if(QoLMisc.isInSpaceofCommisionedFaction())return false;
        if(data.hasTag(AoTDRankTags.NONRESTRICTIVE_COLONIZATION))return true;
        return getAmountOfCurrentColonies() < data.getAmountOfColoniesAbleToColonize();

    }

    public boolean canResign() {
        return getCurrentRankData().isLeavingAnOption();
    }

    public int getAmountOfCurrentColonies() {
        return Misc.getPlayerMarkets(true).size();
    }

    public boolean haveMeetCriteriaForHandler(){
        return true;
    }
    public void createReqTooltip(TextPanelAPI panel){

    }
    @Override
    public TooltipMakerAPI.TooltipCreator getBarTooltip() {
        return super.getBarTooltip();
    }

    @Override
    public boolean withMonthlyFactors() {
        return true;
    }
}
