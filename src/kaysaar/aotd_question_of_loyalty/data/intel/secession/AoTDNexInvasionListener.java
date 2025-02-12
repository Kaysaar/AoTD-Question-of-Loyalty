package kaysaar.aotd_question_of_loyalty.data.intel.secession;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.Nex_MarketCMD;
import exerelin.campaign.InvasionRound;
import exerelin.utilities.InvasionListener;
import kaysaar.aotd_question_of_loyalty.data.intel.AoTDSecessionManager;
import kaysaar.aotd_question_of_loyalty.data.intel.secession.eventfactors.ClaimingPlanetEventFactor;

import java.util.List;

public class AoTDNexInvasionListener implements InvasionListener {
    @Override
    public void reportInvadeLoot(InteractionDialogAPI dialog, MarketAPI market, Nex_MarketCMD.TempDataInvasion actionData, CargoAPI cargo) {

    }

    @Override
    public void reportInvasionRound(InvasionRound.InvasionRoundResult result, CampaignFleetAPI fleet, MarketAPI defender, float atkStr, float defStr) {

    }

    @Override
    public void reportInvasionFinished(CampaignFleetAPI fleet, FactionAPI attackerFaction, MarketAPI market, float numRounds, boolean success) {

    }

    @Override
    public void reportMarketTransfered(MarketAPI market, FactionAPI newOwner, FactionAPI oldOwner, boolean playerInvolved, boolean isCapture, List<String> factionsToNotify, float repChangeStrength) {
        if(AoTDSecessionManager.get()!=null){
            FactionAPI rebelionAgaist = AoTDSecessionManager.get().rebellingAgainst;
            if(rebelionAgaist.getId().equals(oldOwner.getId())&&newOwner.isPlayerFaction()){
                AoTDSecessionManager.get().marketsCaptured.add(market);
                AoTDSecessionManager.get().addFactor(new ClaimingPlanetEventFactor(AoTDSecessionManager.get().getPointsToReachNearThreshold(),market));
            }
        }
    }
}
