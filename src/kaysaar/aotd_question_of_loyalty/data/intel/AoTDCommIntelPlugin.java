package kaysaar.aotd_question_of_loyalty.data.intel;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.OrbitalStation;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.FactionCommissionIntel;

import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.EventFactor;
import com.fs.starfarer.api.impl.campaign.rulecmd.AoTDRetirementOption;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import kaysaar.aotd_question_of_loyalty.data.intel.dialog.RebelionEventDialog;
import kaysaar.aotd_question_of_loyalty.data.intel.eventfactors.monthly.MonthlyObligationFactor;
import kaysaar.aotd_question_of_loyalty.data.listeners.AoTDFreeStorageComm;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;
import kaysaar.aotd_question_of_loyalty.data.models.BaseFactionCommisionData;
import kaysaar.aotd_question_of_loyalty.data.models.RankData;
import kaysaar.aotd_question_of_loyalty.data.models.RetirementInfo;
import kaysaar.aotd_question_of_loyalty.data.plugins.AoTDNexMarketUtil;
import kaysaar.aotd_question_of_loyalty.data.plugins.ProductionUtil;
import kaysaar.aotd_question_of_loyalty.data.plugins.ReflectionUtilis;
import kaysaar.aotd_question_of_loyalty.data.scripts.commision.AoTDCommissionUtil;
import kaysaar.aotd_question_of_loyalty.data.scripts.commision.AoTDCommissionDataManager;
import kaysaar.aotd_question_of_loyalty.data.tags.AoTDCommisionTags;
import kaysaar.aotd_question_of_loyalty.data.tags.AoTDRankTags;
import kaysaar.aotd_question_of_loyalty.data.ui.RankShowcaseUIDialog;

import java.awt.*;
import java.util.*;
import java.util.List;

import static com.fs.starfarer.api.impl.campaign.rulecmd.missions.Commission.COMMISSION_REQ;

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

    public void updateData() {
        String factionId = data.factionID;
        data = AoTDCommissionDataManager.getInstance().getCommisionData(factionId);
        boolean timeToBreak = false;
        for (EventStageData stage : stages) {
            if (timeToBreak) break;
            if (stage.id.equals(getRank())) timeToBreak = true;
            stage.wasEverReached = true;
        }
    }

    public void setData(BaseFactionCommisionData data) {
        this.data = data;
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

    public void printInfo(InteractionDialogAPI dialogAPI,TooltipMakerAPI info) {
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        float pad = 3f;
        float opad = 10f;
        FactionAPI faction = data.getFaction();
        FactionCommissionIntel temp = new FactionCommissionIntel(faction);

        info.setParaSmallInsignia();
        BaseFactionCommisionData daten = AoTDCommissionDataManager.getInstance().getCommisionData(faction.getId());
        if (Misc.getPlayerMarkets(true).isEmpty() || data.hasTag(AoTDCommisionTags.DOES_NOT_CARE_ABOUT_PLAYER_COLONIES)) {

            info.addPara("Given current circumstances you will start with rank of %s and monthly salary of %s", 5f, Color.ORANGE, daten.getRankFromString(daten.getFirstDefRank()).name, Misc.getDGSCredits(daten.getRankFromString(daten.getFirstDefRank()).salary));

        } else {
            info.addPara("Given current circumstances you will start with rank of %s and monthly salary of %s", 5f, Color.ORANGE, daten.getAdequateRankForColonies(Misc.getPlayerMarkets(true).size()).name, Misc.getDGSCredits(daten.getAdequateRankForColonies(Misc.getPlayerMarkets(true).size()).salary));
            info.addPara("We propose such rank due to your colonial holdings", Color.ORANGE, 5f);
        }
        info.addSectionHeading("Commission Progression", Alignment.MID, 10f);
        info.setParaFontDefault();
        info.addPara("To progress through our ranks you need to prove a worthy asset to our faction.You can do by:", 10f);
        info.addPara("Selling AI Cores to our faction", 3f);
        if (Global.getSettings().getModManager().isModEnabled("aotd_vok")) {
            info.addPara("Selling Research databanks to our faction", 3f);

        }
        info.addPara("Completing survey missions and analyze missions", 3f);
//        info.addPara("Selling colony items to our faction",3f);
        info.addPara("Completing faction bounties", 3f);
        info.addPara("Catching smugglers within faction's star systems", 3f);
        info.addPara("Making profitable trades with our faction", 3f);

        info.setParaSmallInsignia();
        List<FactionAPI> hostile = temp.getHostileFactions();
        if (hostile.isEmpty()) {
            info.addPara(Misc.ucFirst(faction.getDisplayNameWithArticle()) + " is not currently hostile to any major factions.", 0f);
        } else {
            info.addPara(Misc.ucFirst(faction.getDisplayNameWithArticle()) + " is currently hostile to:", opad);

            info.setParaFontDefault();

            info.setBulletedListMode(BaseIntelPlugin.INDENT);
            float initPad = opad;
            for (FactionAPI other : hostile) {
                info.addPara(Misc.ucFirst(other.getDisplayName()), other.getBaseUIColor(), initPad);
                initPad = 3f;
            }
            info.setBulletedListMode(null);
        }
        if(hasRetiredFromFaction()){
            info.addPara("We do have records of your previous service within our ranks. We will look forward towards fruitful cooperation.", Misc.getPositiveHighlightColor(), 10f);

        }
        else{
            info.addPara("Should you prove to be burden to our faction, we will terminate this contract", Misc.getNegativeHighlightColor(), 10f);

        }
        dialogAPI.getTextPanel().addTooltip();
        if(this.hasRetiredFromAnyFaction()&&!this.hasRetiredFromFaction()){
            RetirementInfo retirementInfo = (RetirementInfo) Global.getSector().getMemory().get(AoTDRetirementOption.memKey);
            FactionAPI factionAPI = Global.getSector().getFaction(retirementInfo.getPrevFaction());
            dialogAPI.getTextPanel().addPara(factionAPI.getDisplayName()+" will treat our action as treason!",Misc.getNegativeHighlightColor());
        }
    }
    public boolean hasRetiredFromAnyFaction(){
        return Global.getSector().getMemory().contains(AoTDRetirementOption.memKey);
    }
    public boolean hasRetiredFromFaction(){
        if(hasRetiredFromAnyFaction()){
            RetirementInfo info = (RetirementInfo) Global.getSector().getMemory().get(AoTDRetirementOption.memKey);
            return info.getPrevFaction().equals(data.getFaction().getId());

        }
      return false;
    }
    public boolean isSteppingIntoNoReturn() {
        return !Misc.getPlayerMarkets(true).isEmpty() && !data.hasTag(AoTDCommisionTags.DOES_NOT_CARE_ABOUT_PLAYER_COLONIES);
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
        stipendNode.income += stipend * f;

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

    public void initializeFully(BaseFactionCommisionData contract, TextPanelAPI text, boolean withIntelNotification, String startingRankId, String firstOfficialRank) {


        this.data = contract;
        this.progress = 10;
        boolean havePlanets = !Misc.getPlayerMarkets(false).isEmpty();
        if (havePlanets && !data.hasTag(AoTDCommisionTags.DOES_NOT_CARE_ABOUT_PLAYER_COLONIES)) {
            setRank(data.getAdequateRankForColonies(Misc.getPlayerMarkets(true).size()).id);
            this.progress = data.rankValue.get(data.getAdequateRankForColonies(Misc.getPlayerMarkets(true).size()).id) + 10;
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
            addStage(rank.id, data.rankValue.get(rank.id), StageIconSize.SMALL);
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
                if (!esd.wasEverReached) {

                    if (!data.hasTag(AoTDCommisionTags.UP_RANK_AUTOMATICALLY)) {
                        info.addPara("You have earned right to become %s", 0f, Color.ORANGE, getRankForID(getStageId(esd.id)).name);

                    } else {
                        info.addPara("You are now %s", 0f, Color.ORANGE, getRankForID(getStageId(esd.id)).name);
                        setRank(getStageId(esd.id));
                        AoTDCommissionUtil.reportPlayerGotNewRank(getCurrentRankData());
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

    public void makeVengeful(FactionAPI factionAPI, InteractionDialogAPI dialog){
        CoreReputationPlugin.CustomRepImpact impact = new CoreReputationPlugin.CustomRepImpact();
        impact.delta = -10f;
            Global.getSector().adjustPlayerReputation(
                    new CoreReputationPlugin.RepActionEnvelope(CoreReputationPlugin.RepActions.CUSTOM,
                            impact, null, dialog != null ? dialog.getTextPanel() : null, false, true),
                    factionAPI.getId());

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
            info.addPara("Current Rank : %s", initPad, Color.ORANGE, getRankForID((String) stageId).name.trim());
            getRankInfoForTooltip(info, stageId, initPad);
            if (data.hasTag(AoTDCommisionTags.UP_RANK_AUTOMATICALLY)) {
                info.addPara("Once you gather enough points for higher rank you automatically attain new rank!", 5f);

            } else {
                info.addPara("Once you gather enough points for higher rank, you can go to one of Administrators from " + data.getFaction().getDisplayName() + " to ask for promotion", 5f);

            }
            if(!Misc.getPlayerMarkets(true).isEmpty() &&!getData().hasTag(AoTDCommisionTags.DOES_NOT_CARE_ABOUT_PLAYER_COLONIES)){
                ButtonAPI button = info.addButton("Declare Independence","secede",Misc.getNegativeHighlightColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.ALL,170,20,0f);
                button.getPosition().setXAlignOffset(info.getWidthSoFar()-button.getPosition().getWidth());
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

    public void printSuppliesForTaking(InteractionDialogAPI dialog) {
        String[] res = new String[2];
        res[0] = Commodities.SUPPLIES;
        res[1] = Commodities.FUEL;
        int[] qty = new int[2];
        qty[0] = (int) Math.max(Global.getSector().getPlayerFleet().getCargo().getMaxCapacity(),1000);
        qty[1] = (int) Global.getSector().getPlayerFleet().getCargo().getMaxFuel();
        dialog.getTextPanel().addPara("Given your current rank which is %s you will be given %s supplies and %s fuel", Color.ORANGE, getCurrentRankData().name, "" + qty[0], "" + qty[1]);
        dialog.getTextPanel().addPara("You will be able to ask for your supply package after 720 days", Misc.getTooltipTitleAndLightHighlightColor());
        Misc.showCost(dialog.getTextPanel(), "Supplies available", false, Color.ORANGE, Misc.getGrayColor(), res, qty);
        dialog.getTextPanel().addPara("You have available %s cargo space for supplies and %s for fuel", Color.ORANGE, "" + QoLMisc.getFreeSpaceAvailable(Global.getSector().getPlayerFleet().getCargo()), "" + Global.getSector().getPlayerFleet().getCargo().getFreeFuelSpace());
    }

    public boolean doesPlayerMeetCriteriaForCommision() {
        return data.getFaction().getRelToPlayer().isAtWorst(COMMISSION_REQ);
    }

    public void deliverCargo(InteractionDialogAPI dialogAPI, CargoAPI deliverCargo) {
        int[] qty = new int[2];
        qty[0] = (int) Math.max(Global.getSector().getPlayerFleet().getCargo().getMaxCapacity(),1000);
        qty[1] = (int) Global.getSector().getPlayerFleet().getCargo().getMaxFuel();
        deliverCargo.addCommodity(Commodities.SUPPLIES,qty[0]);
        deliverCargo.addCommodity(Commodities.FUEL,qty[1]);
        Global.getSector().getPlayerFleet().getMemory().set("$aotd_wait_for_resupply", true, 720);
        dialogAPI.getTextPanel().addPara("Supply package has been received!", Color.ORANGE);
    }

    public void printRequirements(InteractionDialogAPI dialog) {
        CoreReputationPlugin.addRequiredStanding(data.getFaction(), COMMISSION_REQ, null, dialog.getTextPanel(), null, null, 0f, true);
        CoreReputationPlugin.addCurrentStanding(data.getFaction(), null, dialog.getTextPanel(), null, null, 0f);
    }

    public RankData getCurrentRankData() {
        return getRankForID(getRank());
    }

    public void getRankInfoForTooltip(TooltipMakerAPI info, Object stageId, float initPad) {
        RankData rank = getRankForID(getStageId(stageId));
        ;
        info.addPara("Effects:", 3f);
        info.addPara("Salary : %s", initPad, Color.ORANGE, Misc.getDGSCredits(rank.salary));
        if (!data.hasTag(AoTDCommisionTags.DOES_NOT_CARE_ABOUT_PLAYER_COLONIES)) {
                if (rank.getAmountOfColoniesAbleToColonize() == 0) {
                    info.addPara("Can't colonize any planets", Misc.getNegativeHighlightColor(), initPad);
                } else {
                    info.addPara("Receives permit for having %s colonies either owned by you , or being under your direct command", initPad, Misc.getTooltipTitleAndLightHighlightColor(), Color.ORANGE, "" + rank.getAmountOfColoniesAbleToColonize());

                }
            info.addPara("Note: Colonies will be under supervision of %s, once we leave faction, all colonies will stay with %s", initPad, Color.ORANGE, data.getFaction().getDisplayNameWithArticle(), data.getFaction().getDisplayNameWithArticle());
        }


        if (rank.getTarrifReduciton() > 0) {
            info.addPara("Reduced tariff for goods by %s", initPad, Misc.getTooltipTitleAndLightHighlightColor(), Color.ORANGE, "" + (int) rank.getTarrifReduciton() + "%");
        }

        if (rank.hasTag(AoTDRankTags.ACCESS_TO_FACTION_BLUEPRINTS) && Global.getSettings().getModManager().isModEnabled("aotd_vok")) {
            if (GPManager.isEnabled) {
                info.addPara("Access to blueprints of weapons, fighters and ships that are under possession of %s", initPad, Misc.getTooltipTitleAndLightHighlightColor(), Color.ORANGE, "" + data.getFaction().getDisplayName());
            }
        }
        if (rank.hasTag(AoTDRankTags.FREE_STORAGE)) {
            info.addPara("Free storage on all planets belonging to %s", initPad, Misc.getTooltipTitleAndLightHighlightColor(), Color.ORANGE, "" + data.getFaction().getDisplayName());
        }
        if (rank.hasTag(AoTDRankTags.HAVE_RESEARCH_PERMIT) && Global.getSettings().getModManager().isModEnabled("aotd_vok")) {
            info.addPara("Receives permit for establishing independent research operations", Misc.getPositiveHighlightColor(), initPad);
        }

        if (rank.hasTag(AoTDRankTags.CAN_INTIMIDATE_CARGO_PATROL_FLEETS)) {
            info.addPara("Ability to deny scan of your cargo by patrol fleets of faction", Misc.getPositiveHighlightColor(), initPad);
        }

        if (rank.hasTag(AoTDRankTags.COLONY_CONTRACTS)) {
            info.addPara("Ability to sign permanent colony deals with faction (WIP)", Misc.getPositiveHighlightColor(), initPad);
        }

        if (!rank.isLeavingAnOption()) {
            info.addPara("You are now a permanent member of %s. Any act against %s interest or outright hostile attitude will lead to execution!", initPad, Misc.getTooltipTitleAndLightHighlightColor(), Color.ORANGE, data.getFaction().getDisplayNameWithArticle(), data.getFaction().getDisplayNameWithArticle());

        }
        info.addPara("Warning : If your relations with %s will drop to %s, due to hostile actions,it will result with immediate termination of contract and putting bounty on our fleet!",5f,Misc.getNegativeHighlightColor(),Misc.getTooltipTitleAndLightHighlightColor(),getCurrentlyCommisonedFaction().getDisplayName(),"-50");

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

    @Override
    protected void notifyStageReached(EventStageData stage) {
        super.notifyStageReached(stage);
        if(getData().hasTag(AoTDCommisionTags.UP_RANK_AUTOMATICALLY)){
            setRank(getStageId(stage.id));
        }

    }

    boolean commisionValid = true;

    public void generateNewAdministrators(){

    }
    public void endCommision(InteractionDialogAPI dialog,boolean isRebelling) {
        commisionValid = false;
        for (MarketAPI marketAPI : Global.getSector().getEconomy().getMarketsCopy()) {
            if (marketAPI.getFaction().getId().equals(data.factionID)) {
                marketAPI.getTariff().unmodifyFlat("aotd_commision");
            }
        }

        AoTDFreeStorageComm.runCleanUpScript(data.getFaction());
        Global.getSector().getListenerManager().removeListener(this);
        Global.getSector().removeScript(this);

        if (!data.hasTag(AoTDCommisionTags.DOES_NOT_CARE_ABOUT_PLAYER_COLONIES)&&!isRebelling) {
            for (MarketAPI factionMarket : Misc.getFactionMarkets(data.getFaction())) {
                if (factionMarket.isPlayerOwned()) {
                    PersonAPI person = factionMarket.getFaction().createRandomPerson();
                    factionMarket.setAdmin(person);
                    factionMarket.setPlayerOwned(false);
                    AoTDNexMarketUtil.addOrUpdateOfficials(factionMarket);
                }
            }
        } else {
            for (MarketAPI factionMarket : Misc.getFactionMarkets(data.getFaction())) {
                if (factionMarket.isPlayerOwned()) {
                    setMarketFaction(factionMarket,Global.getSector().getPlayerFaction().getId(),false);

                }
            }

        }
        MonthlyReport report = SharedData.getData().getCurrentReport();

        MonthlyReport.FDNode fleetNode = report.getNode(MonthlyReport.FLEET);
        fleetNode.name = "Fleet";
        fleetNode.custom = MonthlyReport.FLEET;
        fleetNode.tooltipCreator = report.getMonthlyReportTooltip();
        final FactionAPI faction = data.getFaction();
        float stipend = getCurrentRankData().salary;
        MonthlyReport.FDNode stipendNode = report.getNode(fleetNode, "node_id_stipend_" + data.getFaction().getId());
        stipendNode.income=0f;
        fleetNode.getChildren().remove(stipendNode);

        undoAllRepChanges(dialog);
        if(isRebelling){
            makeHostile(getCurrentlyCommisonedFaction(),dialog);

        }
        endImmediately();
        Global.getSector().getCharacterData().getMemory().unset(MemFlags.FCM_FACTION);
    }

    @Override
    public void endImmediately() {
        //endAfterDelay();

        super.endImmediately();



    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
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

        main.addSpacer(opad);
        main.addSpacer(opad);
        for (EventStageData curr : stages) {
            if (curr.wasEverReached && curr.isOneOffEvent && !curr.isRepeatable) continue;
            addStageDescriptionWithImage(main, curr.id);
        }



        float barW = getBarWidth();
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

    @Override
    protected void advanceImpl(float amount) {
        if(Misc.getFactionMarkets(getCurrentlyCommisonedFaction().getId()).isEmpty()){
            endCommision(null,false);
            return;
        }
        if(getCurrentlyCommisonedFaction().getRelToPlayer().isHostile()){
            String[]strs  = new String[1];
            strs[0] = getCurrentlyCommisonedFaction().getDisplayName();
            MessageIntel intel = new MessageIntel("Commission contract with %s : terminated!", Misc.getNegativeHighlightColor(),strs,Color.ORANGE);
            intel.addLine("Due to hostile actions taken against "+strs[0]+" they have terminated contract and put bounty on our head!");
            intel.setIcon(getCurrentlyCommisonedFaction().getCrest());
            intel.setSound(BaseIntelPlugin.getSoundColonyThreat());
            Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.INTEL_TAB, intel);


            endCommision(null,false);
            return;
        }
        if (!commisionValid) return;
        super.advanceImpl(amount);
        Iterator<AoTDCommIntelPlugin>listeners = Global.getSector().getListenerManager().getListeners(AoTDCommIntelPlugin.class).iterator();
        boolean foundAlreadyOne=false;
        while (listeners.hasNext()) {
            AoTDCommIntelPlugin plugin = listeners.next();
            if(plugin==this&&!foundAlreadyOne){
                foundAlreadyOne = true;
                continue;
            }
            listeners.remove();


        }
        if (QoLMisc.isCommissioned()) {
            if (data != null && data.ranks != null && getCurrentRankData() != null) {
                if (!isDone()) {
                    for (MarketAPI marketAPI : Global.getSector().getEconomy().getMarketsCopy()) {
                        if (marketAPI.getFaction().isPlayerFaction()) {
                            setMarketFaction(marketAPI,data.factionID,false);
                        }
                        if (marketAPI.getFaction().getId().equals(data.factionID)) {
                            marketAPI.getTariff().modifyFlat("aotd_commision", -(getCurrentRankData().getTarrifReduciton() / 100f), "Commission");
                        }
                    }
                    makeRepChanges(null);
                }
            }
        }


    }

    public static void setMarketFaction(MarketAPI marketAPI,String factionID,boolean joiningComm) {
        if(marketAPI.hasTag("nex_playerOutpost"))return;
        if(marketAPI.getCommDirectory().getEntriesCopy().isEmpty()){
            AoTDNexMarketUtil.addOrUpdateOfficials(marketAPI);
        }
        marketAPI.setFactionId(factionID);
        marketAPI.setPlayerOwned(true);
        marketAPI.getPrimaryEntity().setFaction(factionID);

        for (SectorEntityToken connectedEntity : marketAPI.getConnectedEntities()) {
            connectedEntity.setFaction(factionID);

        }
        for (Industry industry : marketAPI.getIndustries()) {
            if(industry instanceof OrbitalStation){
                CampaignFleetAPI fleet = (CampaignFleetAPI) ReflectionUtilis.getPrivateVariable("stationFleet",industry);
                if(fleet!=null){
                    fleet.setFaction(factionID);
                }

            }
        }
        if(joiningComm){
            for (CampaignFleetAPI fleet : marketAPI.getStarSystem().getFleets()) {
                if(fleet.getFaction().isPlayerFaction()&&!fleet.isPlayerFleet()){
                    fleet.setFaction(factionID,true);
                }
            }
        }
        for (CommDirectoryEntryAPI commDirectoryEntryAPI : marketAPI.getCommDirectory().getEntriesCopy()) {
            if(commDirectoryEntryAPI.getEntryData() instanceof PersonAPI){
                ((PersonAPI) commDirectoryEntryAPI.getEntryData()).setFaction(factionID);
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
        return canBuyMarket(getRank());

    }

    public void printWarning(InteractionDialogAPI dialog) {

        dialog.getTextPanel().addPara("Due to your colonial holdings we are going to propose you one of governor titles, instead of mercenary titles.", Color.ORANGE);

        dialog.getTextPanel().addPara("Not only that, but we require all of your colonies to serve under our banner, we will of course let you keep control over them, but they will belong to " + data.getFaction().getDisplayNameWithArticle(), Color.ORANGE);
    }

    //Function made to handle buying markets in Nexerelin
    public boolean canBuyMarket(String currentRank) {

        RankData data = getRankForID(currentRank);
        if (data.hasTag(AoTDRankTags.NONRESTRICTIVE_COLONIZATION)) return true;
        return getAmountOfCurrentColonies() < data.getAmountOfColoniesAbleToColonize();

    }

    public boolean hasAttainedThisRankBefore(String rankId) {
        for (RankData rank : data.ranks) {
            if (rank.getId().equals(rankId)) return true;
            if (rank.getId().equals(getCurrentRankData().getId())) break;
        }
        return false;
    }

    public boolean canResign() {
        return getCurrentRankData().isLeavingAnOption();
    }

    public int getAmountOfCurrentColonies() {
        int amount = 0;
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
            if(playerMarket.hasTag("nex_playerOutpost"))continue;
            amount++;
        }
        return amount;

    }

    @Override
    public TooltipMakerAPI.TooltipCreator getBarTooltip() {
        return super.getBarTooltip();
    }

    @Override
    public boolean withMonthlyFactors() {
        return true;
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        if (buttonId == BUTTON_DELETE) {
            endImmediately();
            ui.recreateIntelUI();


            return;
        }
        BasePopUpDialog dialog = new RebelionEventDialog("Start Rebellion",getCurrentlyCommisonedFaction(),ui);
        CustomPanelAPI panelAPI = Global.getSettings().createCustom(800, 300, dialog);
        UIPanelAPI panelAPI1 = ProductionUtil.getCoreUI();
        dialog.init(panelAPI, panelAPI1.getPosition().getCenterX() - (panelAPI.getPosition().getWidth() / 2), panelAPI1.getPosition().getCenterY() + (panelAPI.getPosition().getHeight() / 2), true);
        ui.updateUIForItem(this);
    }
    public float getCreditsForRetirement(){
        float currMoney = 0f;
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
            currMoney += playerMarket.getNetIncome()/2;
        }
        currMoney +=getCurrentRankData().salary/2;
        return currMoney;
    }
}
