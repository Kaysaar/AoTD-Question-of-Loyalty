package kaysaar.aotd_question_of_loyalty.data.intel.secession;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.fleets.RouteLocationCalculator;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.punitive.*;
import com.fs.starfarer.api.impl.campaign.intel.raid.RaidAssignmentAI;
import com.fs.starfarer.api.impl.campaign.intel.raid.RaidIntel;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RouteFleetAssignmentAI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.intel.AoTDSecessionManager;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.Random;
import java.util.Set;

public class AoTDSecessionFleetIntel extends RaidIntel implements RaidIntel.RaidDelegate {
    public static enum AoTDSecessionOutcome {
        TASK_FORCE_DEFEATED,
        COLONY_HAS_BEEN_TAKEN,
        COLONY_DOES_NOT_EXISTS,
        BOMB_SUCCESS,
        BOMBARD_FAIL,
        RAID_FAIL,
        RAID_SUCCESS,
        AVERTED
    }
    public static final Object ENTERED_SYSTEM_UPDATE = new Object();
    public static final Object OUTCOME_UPDATE = new Object();

    protected AoTDSecessionActionStage action;
    protected AoTDSecessionManager.AoTDSecessionGoal goal;
    protected MarketAPI target;
    protected MarketAPI from;
    protected AoTDSecessionOutcome outcome;
    protected FactionAPI factionWantsToTake;
    protected Random random = new Random();
    protected AoTDSecessionManager.AoTDSecessionReason reason;
    protected FactionAPI targetFaction;

    @Override
    public void advance(float amount) {
        super.advance(amount);

    }

    public AoTDSecessionFleetIntel(FactionAPI faction, MarketAPI from, MarketAPI target,
                                   float expeditionFP, float organizeDuration,
                                   AoTDSecessionManager.AoTDSecessionGoal goal, AoTDSecessionManager.AoTDSecessionReason bestReason) {
        super(target.getStarSystem(), faction, null);
        this.goal = goal;
        this.reason = bestReason;
        this.delegate = this;
        this.from = from;
        this.target = target;
        targetFaction = target.getFaction();

        SectorEntityToken gather = from.getPrimaryEntity();
        SectorEntityToken raidJump = RouteLocationCalculator.findJumpPointToUse(getFactionForUIColors(), target.getPrimaryEntity());

        if (gather == null || raidJump == null) {
            endImmediately();
            return;
        }


        float orgDur = organizeDuration;
        if (DebugFlags.PUNITIVE_EXPEDITION_DEBUG || DebugFlags.FAST_RAIDS) orgDur = 0.5f;

        addStage(new PEOrganizeStage(this, from, orgDur));

        float successMult = 0.5f;
        PEAssembleStage assemble = new PEAssembleStage(this, gather);
        assemble.addSource(from);
        assemble.setSpawnFP(expeditionFP);
        assemble.setAbortFP(expeditionFP * successMult);
        addStage(assemble);


        PETravelStage travel = new PETravelStage(this, gather, raidJump, false);
        travel.setAbortFP(expeditionFP * successMult);
        addStage(travel);

        action = new AoTDSecessionActionStage(this, target);
        action.setAbortFP(expeditionFP * successMult);
        addStage(action);

        addStage(new PEReturnStage(this));

        setImportant(true);

        //applyRepPenalty();
        Global.getSector().getIntelManager().addIntel(this);

        //repResult = null;
    }


    public Random getRandom() {
        return random;
    }

    public MarketAPI getTarget() {
        return target;
    }

    public FactionAPI getTargetFaction() {
        return targetFaction;
    }

    public MarketAPI getFrom() {
        return from;
    }
    public RouteFleetAssignmentAI createAssignmentAI(CampaignFleetAPI fleet, RouteManager.RouteData route) {
        RaidAssignmentAI raidAI = new RaidAssignmentAI(fleet, route, action);
        //raidAI.setDelegate(action);
        return raidAI;
    }
    protected transient String targetOwner = null;
    @Override
    protected void advanceImpl(float amount) {
        super.advanceImpl(amount);
        if (target != null && targetOwner == null) targetOwner = target.getFactionId();
        if (failStage < 0 && targetOwner != null && target != null && !targetOwner.equals(target.getFactionId())) {
            forceFail(false);
            setOutcome(AoTDSecessionOutcome.AVERTED);
        }
    }
    public void sendOutcomeUpdate() {
        sendUpdateIfPlayerHasIntel(OUTCOME_UPDATE, false);
    }
    public void sendEnteredSystemUpdate() {
        //applyRepPenalty();
        sendUpdateIfPlayerHasIntel(ENTERED_SYSTEM_UPDATE, false);
        //repResult = null;
    }
    @Override
    public String getName() {
        String base = Misc.ucFirst(faction.getPersonNamePrefix()) + " Expedition";
        if (isEnding()) {
            if (isSendingUpdate() && isFailed()) {
                return base + " - Has been defeated!";
            }
            if (isSucceeded() || outcome == AoTDSecessionOutcome.BOMB_SUCCESS) {
                return base + " - Successful";
            }
            if (outcome == AoTDSecessionOutcome.RAID_FAIL ||
                    outcome ==AoTDSecessionOutcome.BOMBARD_FAIL ||
                    outcome == AoTDSecessionOutcome.COLONY_DOES_NOT_EXISTS ||
                    outcome == AoTDSecessionOutcome.TASK_FORCE_DEFEATED) {
                return base + " - Failed";
            }
        }
        return base;
    }
    @Override
    protected void addBulletPoints(TooltipMakerAPI info, ListInfoMode mode) {
        //super.addBulletPoints(info, mode);

        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        float pad = 3f;
        float opad = 10f;

        float initPad = pad;
        if (mode == ListInfoMode.IN_DESC) initPad = opad;

        Color tc = getBulletColorForMode(mode);

        bullet(info);

        FactionAPI other = targetFaction;
        if (outcome != null) {
            generateInfoFromOutcome(outcome,info, tc, initPad, other);

            return;
        }

        info.addPara("Target: %s", initPad, tc,
                other.getBaseUIColor(), target.getName());
        initPad = 0f;

        if (goal == AoTDSecessionManager.AoTDSecessionGoal.BOMBARD) {
            String goalStr = "saturation bombardment";
            info.addPara("Goal: %s", initPad, tc, Misc.getNegativeHighlightColor(), goalStr);
        }
        if (goal == AoTDSecessionManager.AoTDSecessionGoal.RETAKE) {
            String goalStr = "retaking market";
            info.addPara("Goal: %s", initPad, tc, Misc.getNegativeHighlightColor(), goalStr);
        }
        float eta = getETA();
        if (eta > 1 && !isEnding()) {
            String days = getDaysString(eta);
            info.addPara("Estimated %s " + days + " until arrival",
                    initPad, tc, h, "" + (int)Math.round(eta));
            initPad = 0f;
        } else if (!isEnding() && action.getElapsed() > 0) {
            info.addPara("Currently in-system", tc, initPad);
            initPad = 0f;
        }


        unindent(info);
    }

    private void generateInfoFromOutcome(AoTDSecessionOutcome outcome,TooltipMakerAPI info, Color tc, float initPad, FactionAPI other) {
        if(outcome == null){
            info.addPara("%s in danger from %s", initPad, Color.ORANGE,target.getName(),factionWantsToTake.getDisplayName());
        }
        else{
            if (outcome == AoTDSecessionOutcome.TASK_FORCE_DEFEATED) {
                info.addPara("Retaliatory strike force defeated", tc, initPad);
            } else if (outcome == AoTDSecessionOutcome.COLONY_DOES_NOT_EXISTS) {
                info.addPara("Retaliatory strike aborted", tc, initPad);
            } else if (outcome == AoTDSecessionOutcome.BOMBARD_FAIL) {
                info.addPara("Bombardment of %s failed", initPad, tc, other.getBaseUIColor(), target.getName());
            } else if (outcome == AoTDSecessionOutcome.RAID_FAIL) {
                info.addPara("Retaking efforts of %s failed", initPad, tc, other.getBaseUIColor(), target.getName());
            } else if (outcome == AoTDSecessionOutcome.BOMB_SUCCESS) {
//                    info.addPara(targetIndustry.getCurrentName() + " disrupted for %s days",
//                            initPad, tc, h, "" + (int)Math.round(targetIndustry.getDisruptedDays()));

            } else if (outcome == AoTDSecessionOutcome.RAID_SUCCESS) {
                info.addPara("Retaking efforts of %s have succeeded.", initPad, tc, other.getBaseUIColor(), target.getName());

            }
        }

    }

    public AoTDSecessionActionStage getActionStage() {
        for (RaidStage stage : stages) {
            if (stage instanceof PEActionStage) {
                return (AoTDSecessionActionStage) stage;
            }
        }
        return null;
        //return (PEActionStage) stages.get(2);
    }
    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        super.createIntelInfo(info, mode);
    }

    public void addInitialDescSection(TooltipMakerAPI info, float initPad) {
        Color h = Misc.getHighlightColor();
        float opad = 10f;

        FactionAPI faction = getFaction();
        String is = faction.getDisplayNameIsOrAre();

        String goalDesc = "";
        String goalHL = "";
        Color goalColor = Misc.getTextColor();
        switch (goal) {
            case RETAKE:
                goalDesc = "an invasion of the colony";
                goalHL = "invasion of the colony";
                goalColor = Misc.getNegativeHighlightColor();
                break;
            case BOMBARD:
                goalDesc = "a saturation bombardment of the colony";
                goalHL = "saturation bombardment of the colony";
                goalColor = Misc.getNegativeHighlightColor();
                break;
        }

        String strDesc = getRaidStrDesc();
        int numFleets = (int) getOrigNumFleets();
        String fleets = "fleets";
        if (numFleets == 1) fleets = "fleet";

        if (outcome == null) {
            LabelAPI label = info.addPara(Misc.ucFirst(faction.getDisplayNameWithArticle()) + " " + is +
                            " targeting %s with a " + strDesc + " expeditionary force, projected to be comprised of " +
                            numFleets + " " + fleets + ". " +
                            "Its likely goal is " + goalDesc + ".",
                    initPad, faction.getBaseUIColor(), target.getName());
            label.setHighlight(faction.getDisplayNameWithArticleWithoutArticle(), target.getName(), strDesc, "" + numFleets, goalHL);
            label.setHighlightColors(faction.getBaseUIColor(), targetFaction.getBaseUIColor(), h, h, goalColor);
        } else {
            LabelAPI label = info.addPara(Misc.ucFirst(faction.getDisplayNameWithArticle()) + " " + is +
                            " targeting %s with an expeditionary force. " +
                            "Its likely goal is " + goalDesc + ".",
                    initPad, faction.getBaseUIColor(), target.getName());
            label.setHighlight(faction.getDisplayNameWithArticleWithoutArticle(), target.getName(), goalHL);
            label.setHighlightColors(faction.getBaseUIColor(), targetFaction.getBaseUIColor(), goalColor);
        }
    }
    @Override
    public void sendUpdateIfPlayerHasIntel(Object listInfoParam, boolean onlyIfImportant, boolean sendIfHidden) {

        if (listInfoParam == UPDATE_RETURNING) {
            // we're using sendOutcomeUpdate() to send an end-of-event update instead
            return;
        }


        super.sendUpdateIfPlayerHasIntel(listInfoParam, onlyIfImportant, sendIfHidden);
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        //return super.getIntelTags(map);

        Set<String> tags = super.getIntelTags(map);
        tags.add(Tags.INTEL_MAJOR_EVENT);
        tags.add(Tags.INTEL_COLONIES);
        tags.add(getFaction().getId());
        return tags;
    }
    @Override
    public void notifyRaidEnded(RaidIntel raid, RaidStageStatus status) {
        if (outcome == null && failStage >= 0) {
            if (!target.isInEconomy() || !target.isPlayerOwned()) {
                outcome = AoTDSecessionOutcome.COLONY_DOES_NOT_EXISTS;
            } else {
                outcome = AoTDSecessionOutcome.TASK_FORCE_DEFEATED;
            }
        }

    }
    @Override
    public String getIcon() {
        return faction.getCrest();
    }

    public AoTDSecessionManager.AoTDSecessionGoal getGoal() {
        return goal;
    }


    public AoTDSecessionOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(AoTDSecessionOutcome outcome) {
        this.outcome = outcome;
    }

    public CampaignFleetAPI spawnFleet(RouteManager.RouteData route) {
        Random random = route.getRandom();

        MarketAPI market = route.getMarket();
        CampaignFleetAPI fleet = createFleet(market.getFactionId(), route, market, null, random);

        if (fleet == null || fleet.isEmpty()) return null;

        //fleet.addEventListener(this);

        market.getContainingLocation().addEntity(fleet);
        fleet.setFacing((float) Math.random() * 360f);
        // this will get overridden by the patrol assignment AI, depending on route-time elapsed etc
        fleet.setLocation(market.getPrimaryEntity().getLocation().x, market.getPrimaryEntity().getLocation().x);

        fleet.addScript(createAssignmentAI(fleet, route));

        return fleet;
    }
    public CampaignFleetAPI createFleet(String factionId, RouteManager.RouteData route, MarketAPI market, Vector2f locInHyper, Random random) {
        if (random == null) random = new Random();

        RouteManager.OptionalFleetData extra = route.getExtra();

        float combat = extra.fp;
        float tanker = extra.fp * (0.1f + random.nextFloat() * 0.05f);
        float transport = extra.fp * (0.1f + random.nextFloat() * 0.05f);
        float freighter = 0f;

        if (goal == AoTDSecessionManager.AoTDSecessionGoal.BOMBARD) {
            tanker += transport;
        }
        else {
            transport += tanker / 2f;
            tanker *= 0.5f;
        }

        combat -= tanker;
        combat -= transport;


        FleetParamsV3 params = new FleetParamsV3(
                market,
                locInHyper,
                factionId,
                route == null ? null : route.getQualityOverride(),
                extra.fleetType,
                combat, // combatPts
                freighter, // freighterPts
                tanker, // tankerPts
                transport, // transportPts
                0f, // linerPts
                0f, // utilityPts
                0f // qualityMod, won't get used since routes mostly have quality override set
        );
        //params.ignoreMarketFleetSizeMult = true; // already accounted for in extra.fp

        if (route != null) {
            params.timestamp = route.getTimestamp();
        }
        params.random = random;
        CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);

        if (fleet == null || fleet.isEmpty()) return null;

        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_WAR_FLEET, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_RAIDER, true);

        if (fleet.getFaction().getCustomBoolean(Factions.CUSTOM_PIRATE_BEHAVIOR)) {
            fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PIRATE, true);
        }

        String postId = Ranks.POST_PATROL_COMMANDER;
        String rankId = Ranks.SPACE_COMMANDER;

        fleet.getCommander().setPostId(postId);
        fleet.getCommander().setRankId(rankId);

        Misc.makeNoRepImpact(fleet, "punex");
        Misc.makeHostile(fleet);

        return fleet;
    }
    public AoTDSecessionManager.AoTDSecessionReason getBestReason() {
        return reason;
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        if (target != null && target.isInEconomy() && target.getPrimaryEntity() != null) {
            return target.getPrimaryEntity();
        }
        return super.getMapLocation(map);
    }
}
