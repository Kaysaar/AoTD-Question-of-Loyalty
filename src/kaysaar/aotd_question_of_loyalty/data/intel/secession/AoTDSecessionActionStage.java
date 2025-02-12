package kaysaar.aotd_question_of_loyalty.data.intel.secession;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.ai.CampaignFleetAIAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.StatBonus;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.MilitaryResponseScript;
import com.fs.starfarer.api.impl.campaign.command.WarSimScript;
import com.fs.starfarer.api.impl.campaign.econ.impl.OrbitalStation;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.intel.raid.ActionStage;
import com.fs.starfarer.api.impl.campaign.intel.raid.RaidIntel;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseAssignmentAI;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.intel.AoTDSecessionManager;
import kaysaar.aotd_question_of_loyalty.data.plugins.AoTDNexMarketUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD.*;

public class AoTDSecessionActionStage extends ActionStage implements BaseAssignmentAI.FleetActionDelegate {

    protected MarketAPI target;
    protected boolean playerTargeted = false;
    protected List<MilitaryResponseScript> scripts = new ArrayList<MilitaryResponseScript>();
    protected boolean gaveOrders = true; // will be set to false in updateRoutes()
    protected float untilAutoresolve = 30f;
    public AoTDSecessionActionStage(AoTDSecessionFleetIntel raid, MarketAPI target) {
        super(raid);
        this.target = target;
        playerTargeted = target.isPlayerOwned();

        untilAutoresolve = 15f + 5f * (float) Math.random();
    }


    @Override
    public void advance(float amount) {
        super.advance(amount);

        float days = Misc.getDays(amount);
        untilAutoresolve -= days;
        if (DebugFlags.PUNITIVE_EXPEDITION_DEBUG || DebugFlags.FAST_RAIDS) {
            untilAutoresolve -= days * 100f;
        }

        if (!gaveOrders) {
            gaveOrders = true;

            removeMilScripts();

            // getMaxDays() is always 1 here
            // scripts get removed anyway so we don't care about when they expire naturally
            // just make sure they're around for long enough
            float duration = 100f;

            MilitaryResponseScript.MilitaryResponseParams params = new MilitaryResponseScript.MilitaryResponseParams(CampaignFleetAIAPI.ActionType.HOSTILE,
                    "PE_" + Misc.genUID() + target.getId(),
                    intel.getFaction(),
                    target.getPrimaryEntity(),
                    1f,
                    duration);
            MilitaryResponseScript script = new MilitaryResponseScript(params);
            target.getContainingLocation().addScript(script);
            scripts.add(script);

            MilitaryResponseScript.MilitaryResponseParams defParams = new MilitaryResponseScript.MilitaryResponseParams(CampaignFleetAIAPI.ActionType.HOSTILE,
                    "defPE_" + Misc.genUID() + target.getId(),
                    target.getFaction(),
                    target.getPrimaryEntity(),
                    1f,
                    duration);
            MilitaryResponseScript defScript = new MilitaryResponseScript(defParams);
            target.getContainingLocation().addScript(defScript);
            scripts.add(defScript);
        }
    }

    protected void removeMilScripts() {
        if (scripts != null) {
            for (MilitaryResponseScript s : scripts) {
                s.forceDone();
            }
        }
    }

    @Override
    protected void updateStatus() {
//		if (true) {
//			status = RaidStageStatus.SUCCESS;
//			return;
//		}

        abortIfNeededBasedOnFP(true);
        if (status != RaidIntel.RaidStageStatus.ONGOING) return;

        boolean inSpawnRange = RouteManager.isPlayerInSpawnRange(target.getPrimaryEntity());
        if (!inSpawnRange && untilAutoresolve <= 0) {
            autoresolve();
            return;
        }

        if (!target.isInEconomy() || !target.isPlayerOwned()) {
            status = RaidIntel.RaidStageStatus.FAILURE;
            removeMilScripts();
            giveReturnOrdersToStragglers(getRoutes());
            return;
        }

    }

    public String getRaidActionText(CampaignFleetAPI fleet, MarketAPI market) {
        AoTDSecessionFleetIntel intel = ((AoTDSecessionFleetIntel) this.intel);
        AoTDSecessionManager.AoTDSecessionGoal goal = intel.getGoal();
        if (goal ==  AoTDSecessionManager.AoTDSecessionGoal.BOMBARD) {
            return "bombarding " + market.getName();
        }
        return "invading " + market.getName();
    }

    public String getRaidApproachText(CampaignFleetAPI fleet, MarketAPI market) {
        AoTDSecessionFleetIntel intel = ((AoTDSecessionFleetIntel) this.intel);
        AoTDSecessionManager.AoTDSecessionGoal goal = intel.getGoal();
        if (goal == AoTDSecessionManager.AoTDSecessionGoal.BOMBARD) {
            return "moving in to bombard " + market.getName();
        }
        return "moving in for invasion " + market.getName();
    }

    public void performRaid(CampaignFleetAPI fleet, MarketAPI market) {
        removeMilScripts();

        AoTDSecessionFleetIntel intel = ((AoTDSecessionFleetIntel) this.intel);
        AoTDSecessionManager.AoTDSecessionGoal goal = intel.getGoal();

        status = RaidIntel.RaidStageStatus.SUCCESS;

        if (goal ==  AoTDSecessionManager.AoTDSecessionGoal.BOMBARD) {
            float cost = MarketCMD.getBombardmentCost(market, fleet);
            //float maxCost = intel.getAssembleStage().getOrigSpawnFP() * Misc.FP_TO_BOMBARD_COST_APPROX_MULT;
            float maxCost = intel.getRaidFP() / intel.getNumFleets() * Misc.FP_TO_BOMBARD_COST_APPROX_MULT;
            if (fleet != null) {
                maxCost = fleet.getCargo().getMaxFuel() * 0.25f;
            }

            if (cost <= maxCost) {
                new MarketCMD(market.getPrimaryEntity()).doBombardment(intel.getFaction(), MarketCMD.BombardType.SATURATION);
                intel.setOutcome(AoTDSecessionFleetIntel.AoTDSecessionOutcome.BOMB_SUCCESS);
            } else {
                intel.setOutcome(AoTDSecessionFleetIntel.AoTDSecessionOutcome.BOMBARD_FAIL);
                status = RaidIntel.RaidStageStatus.FAILURE;

                Misc.setFlagWithReason(market.getMemoryWithoutUpdate(), MemFlags.RECENTLY_BOMBARDED,
                        intel.getFaction().getId(), true, 30f);
            }
        } else {
            //float str = intel.getAssembleStage().getOrigSpawnFP() * Misc.FP_TO_GROUND_RAID_STR_APPROX_MULT;
            float str = intel.getRaidFPAdjusted() / intel.getNumFleets() * Misc.FP_TO_GROUND_RAID_STR_APPROX_MULT;

            if (fleet != null) str = MarketCMD.getRaidStr(fleet);
            //float re = MarketCMD.getRaidEffectiveness(target, str);

            //str = 10f;

            float durMult = Global.getSettings().getFloat("punitiveExpeditionDisruptDurationMult");
            boolean raidSuccess = doInvasion(intel.getFaction(),str,market);

            if (raidSuccess) {
                intel.setOutcome(AoTDSecessionFleetIntel.AoTDSecessionOutcome.RAID_SUCCESS);
            } else {
                intel.setOutcome(AoTDSecessionFleetIntel.AoTDSecessionOutcome.RAID_FAIL);
                status = RaidIntel.RaidStageStatus.FAILURE;

                Misc.setFlagWithReason(market.getMemoryWithoutUpdate(), MemFlags.RECENTLY_RAIDED,
                        intel.getFaction().getId(), true, 30f);
                Misc.setRaidedTimestamp(market);
            }
        }

//		// so it doesn't keep trying to raid/bombard
//		if (fleet != null) {
//			fleet.getMemoryWithoutUpdate().unset(MemFlags.MEMORY_KEY_RAIDER);
//		}

        // when FAILURE, gets sent by RaidIntel
        if (intel.getOutcome() != null) {
            if (status == RaidIntel.RaidStageStatus.SUCCESS) {
                intel.sendOutcomeUpdate();
            } else {
                removeMilScripts();
                giveReturnOrdersToStragglers(getRoutes());
            }
        }
    }


    protected void autoresolve() {
        float str = WarSimScript.getFactionStrength(intel.getFaction(), target.getStarSystem());
        float enemyStr = WarSimScript.getFactionStrength(target.getFaction(), target.getStarSystem());

        float defensiveStr = enemyStr + WarSimScript.getStationStrength(target.getFaction(),
                target.getStarSystem(), target.getPrimaryEntity());
        if (defensiveStr >= str) {
            status = RaidIntel.RaidStageStatus.FAILURE;
            removeMilScripts();
            giveReturnOrdersToStragglers(getRoutes());

            // not strictly necessary, I think, but shouldn't hurt
            // otherwise would get set in PunitiveExpeditionIntel.notifyRaidEnded()
            AoTDSecessionFleetIntel intel = ((AoTDSecessionFleetIntel) this.intel);
            intel.setOutcome(AoTDSecessionFleetIntel.AoTDSecessionOutcome.TASK_FORCE_DEFEATED);
            return;
        }

        Industry station = Misc.getStationIndustry(target);
        if (station != null) {
            OrbitalStation.disrupt(station);
        }

        performRaid(null, target);
    }


    protected void updateRoutes() {
        resetRoutes();

        gaveOrders = false;

        ((AoTDSecessionFleetIntel) intel).sendEnteredSystemUpdate();

        List<RouteManager.RouteData> routes = RouteManager.getInstance().getRoutesForSource(intel.getRouteSourceId());
        for (RouteManager.RouteData route : routes) {
            if (target.getStarSystem() != null) { // so that fleet may spawn NOT at the target
                route.addSegment(new RouteManager.RouteSegment(Math.min(5f, untilAutoresolve), target.getStarSystem().getCenter()));
            }
            route.addSegment(new RouteManager.RouteSegment(1000f, target.getPrimaryEntity()));
        }
    }


    public void showStageInfo(TooltipMakerAPI info) {
        int curr = intel.getCurrentStage();
        int index = intel.getStageIndex(this);

        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        Color tc = Misc.getTextColor();
        float pad = 3f;
        float opad = 10f;

        if (curr < index) return;

        if (status == RaidIntel.RaidStageStatus.ONGOING && curr == index) {
            info.addPara("The retaliatory strike forces are currently in-system.", opad);
            return;
        }

        AoTDSecessionFleetIntel intel = ((AoTDSecessionFleetIntel) this.intel);
        if (intel.getOutcome() != null) {
            switch (intel.getOutcome()) {
                case BOMBARD_FAIL:
                    info.addPara("The ground defenses of " + target.getName() + " were sufficient to prevent bombardment.", opad);
                    break;
                case RAID_FAIL:
                    info.addPara("The invasion forces have been repelled by the ground defenses of " + target.getName() + ".", opad);
                    break;
                case BOMB_SUCCESS:
                    if (!target.isInEconomy()) {
                        info.addPara("The retaliatory strike force has successfully bombarded " + target.getName() + ", destroying the colony outright.", opad);
                    }
                    break;
                case RAID_SUCCESS:
                    info.addPara("The invasion forces successfully retaken " + target.getName() + ",thwarting rebellion plans. ", opad);

                    break;
                case TASK_FORCE_DEFEATED:
                    info.addPara("The retaliatory strike force  has been defeated by the defenders of " +
                            target.getName() + ".", opad);
                    break;
                case COLONY_DOES_NOT_EXISTS:
                    info.addPara("The retaliatory strike force has been aborted.", opad);
                    break;

            }
        } else if (status == RaidIntel.RaidStageStatus.SUCCESS) {
            info.addPara("The expeditionary force has succeeded.", opad); // shouldn't happen?
        } else {
            info.addPara("The expeditionary force has failed.", opad); // shouldn't happen?
        }
    }

    public boolean canRaid(CampaignFleetAPI fleet, MarketAPI market) {
        AoTDSecessionFleetIntel intel = ((AoTDSecessionFleetIntel) this.intel);
        if (intel.getOutcome() != null) return false;
        return market == target;
    }

    public String getRaidPrepText(CampaignFleetAPI fleet, SectorEntityToken from) {
        return "orbiting " + from.getName();
    }

    public String getRaidInSystemText(CampaignFleetAPI fleet) {
        return "traveling";
    }

    public String getRaidDefaultText(CampaignFleetAPI fleet) {
        return "traveling";
    }

    @Override
    public boolean isPlayerTargeted() {
        return playerTargeted;
    }

    public boolean doInvasion(FactionAPI faction, float attackerStr, MarketAPI market) {

        StatBonus defenderBase = new StatBonus();

        StatBonus defender = market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD);
        String increasedDefensesKey = "core_addedDefStr";
        float added = getDefenderIncreaseValue(market);
        if (added > 0) {
            defender.modifyFlat(increasedDefensesKey, added, "Increased defender preparedness");
        }
        float defenderStr = (int) Math.round(defender.computeEffective(defenderBase.computeEffective(0f)));
        defender.unmodifyFlat(increasedDefensesKey);
        MarketCMD.TempData temp = new MarketCMD.TempData();

        temp.attackerStr = attackerStr;
        temp.defenderStr = defenderStr;

        boolean hasForces = true;
        boolean canDisrupt = true;
        temp.raidMult = attackerStr / Math.max(1f, (attackerStr + defenderStr));
        temp.raidMult = Math.round(temp.raidMult * 100f) / 100f;


        Random random = Misc.random;

        applyDefenderIncreaseFromRaid(market);

        String reason = faction.getDisplayName() + " raid";
        if (faction.getPersonNamePrefix() != null) {
            reason = Misc.ucFirst(faction.getPersonNamePrefix()) + " raid";
        }

        if(attackerStr>defenderStr){
            applyRaidStabiltyPenalty(market, reason, 1f);
            Misc.setFlagWithReason(market.getMemoryWithoutUpdate(), MemFlags.RECENTLY_RAIDED,
                    faction.getId(), true, 30f);
            Misc.setRaidedTimestamp(market);
            market.setPlayerOwned( false);
            market.setFactionId(faction.getId());
            market.setAdmin(faction.createRandomPerson());
            for (SectorEntityToken connectedEntity : market.getConnectedEntities()) {
                connectedEntity.setFaction(faction.getId());
            }
            AoTDNexMarketUtil.addOrUpdateOfficials(market);
            return true;
        }



        return false;
    }

}
