package kaysaar.aotd_question_of_loyalty.data.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.characters.AbilityPlugin;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.FactionCommissionIntel;
import com.fs.starfarer.api.impl.campaign.intel.eventfactors.onetime.DestroyingEnemyFactor;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;

public class AoTDDestructionOfEnemiesTracker implements CampaignEventListener {
    @Override
    public void reportPlayerOpenedMarket(MarketAPI market) {

    }

    @Override
    public void reportPlayerClosedMarket(MarketAPI market) {

    }

    @Override
    public void reportPlayerOpenedMarketAndCargoUpdated(MarketAPI market) {

    }

    @Override
    public void reportEncounterLootGenerated(FleetEncounterContextPlugin plugin, CargoAPI loot) {

    }

    @Override
    public void reportPlayerMarketTransaction(PlayerMarketTransaction transaction) {

    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI primaryWinner, BattleAPI battle) {

    }

    @Override
    public void reportBattleFinished(CampaignFleetAPI primaryWinner, BattleAPI battle) {

    }

    @Override
    public void reportPlayerEngagement(EngagementResultAPI result) {
        if (QoLMisc.isCommissioned()) {

            CampaignFleetAPI fleet = QoLMisc.getNonPlayerFleet(result).getFleet();
            for (FactionAPI hostileFaction : AoTDCommIntelPlugin.get().getHostileFactions()) {
                if (fleet.getFaction().getId().equals(hostileFaction.getId())) {
                    float factor = 2;
                    float score = 0;
                    for (FleetMemberAPI o : QoLMisc.getNonPlayerFleet(result).getDestroyed()) {
                        score += o.getVariant().getHullSize().ordinal() * factor;
                    }
                    for (FleetMemberAPI o : QoLMisc.getNonPlayerFleet(result).getDisabled()) {
                        score += (o.getVariant().getHullSize().ordinal() * factor);
                    }
                    AoTDCommIntelPlugin.addFactorCreateIfNecessary(new DestroyingEnemyFactor((int) score), Global.getSector().getCampaignUI().getCurrentInteractionDialog());
                }
            }
        }


    }

    @Override
    public void reportFleetDespawned(CampaignFleetAPI fleet, FleetDespawnReason reason, Object param) {

    }

    @Override
    public void reportFleetSpawned(CampaignFleetAPI fleet) {

    }

    @Override
    public void reportFleetReachedEntity(CampaignFleetAPI fleet, SectorEntityToken entity) {

    }

    @Override
    public void reportFleetJumped(CampaignFleetAPI fleet, SectorEntityToken from, JumpPointAPI.JumpDestination to) {

    }

    @Override
    public void reportShownInteractionDialog(InteractionDialogAPI dialog) {

    }

    @Override
    public void reportPlayerReputationChange(String faction, float delta) {

    }

    @Override
    public void reportPlayerReputationChange(PersonAPI person, float delta) {

    }

    @Override
    public void reportPlayerActivatedAbility(AbilityPlugin ability, Object param) {

    }

    @Override
    public void reportPlayerDeactivatedAbility(AbilityPlugin ability, Object param) {

    }

    @Override
    public void reportPlayerDumpedCargo(CargoAPI cargo) {

    }

    @Override
    public void reportPlayerDidNotTakeCargo(CargoAPI cargo) {

    }

    @Override
    public void reportEconomyTick(int iterIndex) {

    }

    @Override
    public void reportEconomyMonthEnd() {

    }
}
