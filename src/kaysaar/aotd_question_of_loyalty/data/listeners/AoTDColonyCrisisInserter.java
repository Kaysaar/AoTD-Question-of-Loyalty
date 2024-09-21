package kaysaar.aotd_question_of_loyalty.data.listeners;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.PlayerColonizationListener;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.AoTDHostileActivity;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;
import kaysaar.aotd_question_of_loyalty.data.tags.AoTDCommisionTags;

public class AoTDColonyCrisisInserter implements PlayerColonizationListener {
    @Override
    public void reportPlayerColonizedPlanet(PlanetAPI planet) {
        if(QoLMisc.isCommissioned()){

                planet.setFaction(Misc.getCommissionFactionId());
                planet.getMarket().setFactionId(Misc.getCommissionFactionId());
                planet.getMarket().setPlayerOwned(true);


        }


    }

    @Override
    public void reportPlayerAbandonedColony(MarketAPI colony) {

    }
}
