package kaysaar.aotd_question_of_loyalty.data.scripts.fleets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;

import com.fs.starfarer.api.impl.campaign.missions.DelayedFleetEncounter;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithTriggers;
import com.fs.starfarer.api.util.Misc;

import java.util.Random;

public class TraitorBountyFleetApplier {
    public static void triggerTreasonFleets(String factionId) {
        if (Misc.getFactionMarkets(factionId).isEmpty()) {
            return;
        }
        Random r = Misc.getRandom(Misc.random.nextLong(), 11);
        DelayedFleetEncounter e = new DelayedFleetEncounter(r, "traitor_hunter_" + factionId);
        e.setTypes(DelayedFleetEncounter.EncounterType.OUTSIDE_SYSTEM,
                DelayedFleetEncounter.EncounterType.IN_HYPER_EN_ROUTE, DelayedFleetEncounter.EncounterType.JUMP_IN_NEAR_PLAYER);
        e.setDelay(15, 20);
        e.setLocationInnerSector(true, factionId);

        e.beginCreate();
        HubMissionWithTriggers.FleetQuality quality1 = HubMissionWithTriggers.FleetQuality.DEFAULT;
        e.triggerCreateFleet(HubMissionWithTriggers.FleetSize.MAXIMUM, quality1, factionId, "aotd_traitor_hunters", Global.getSector().getPlayerFleet().getLocationInHyperspace());
        e.triggerSetFleetDoctrineOther(5, 5);
        e.triggerSetFleetOfficers(HubMissionWithTriggers.OfficerNum.MORE, HubMissionWithTriggers.OfficerQuality.UNUSUALLY_HIGH);
        e.triggerFleetMakeFaster(true, 0, true);
        e.triggerSetFleetFaction(factionId);
        e.triggerFleetSetName("HVT Hunter");
        // Need to write in rules.csv generailHailPermament
        e.triggerSetFleetGenericHailPermanent("AoTDQolTraitorHunterFleet");
        e.setDoNotAbortWhenPlayerFleetTooStrong();
        e.setRepPersonChangesVeryHigh();
        e.triggerSetFleetMemoryValue(MemFlags.MEMORY_KEY_MAKE_ALWAYS_PURSUE, true);
        e.triggerSetFleetMemoryValue(MemFlags.MEMORY_KEY_SAW_PLAYER_WITH_TRANSPONDER_ON, true);
        e.triggerSetFleetMemoryValue(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
        e.triggerMakeFleetIgnoreOtherFleetsExceptPlayer();
        e.triggerSetFleetFlagPermanent("$aotd_traitor_hunter");
        e.endCreate();


    }
}
