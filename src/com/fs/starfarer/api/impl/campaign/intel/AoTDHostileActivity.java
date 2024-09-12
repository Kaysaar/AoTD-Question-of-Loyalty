package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.eventcause.AoTDHegemonyAICoresActivityCause;
import com.fs.starfarer.api.impl.campaign.intel.eventfactors.monthly.*;
import com.fs.starfarer.api.impl.campaign.intel.events.*;

public class AoTDHostileActivity extends HostileActivityEventIntel {
    public AoTDHostileActivity(){
        super();
    }

    @Override
    protected void setup() {

        boolean minorCompleted = false;
        EventStageData minor = getDataFor(Stage.MINOR_EVENT);
        if (minor != null) minorCompleted = minor.wasEverReached;

        factors.clear();
        stages.clear();

        setMaxProgress(MAX_PROGRESS);

        addStage(Stage.START, 0);

        addStage(Stage.MINOR_EVENT, 300, StageIconSize.MEDIUM);
        addStage(Stage.HA_EVENT, 600, true, StageIconSize.LARGE);

        setRandomized(Stage.MINOR_EVENT, RandomizedStageType.BAD, 200, 250, false, false);
        setRandomized(Stage.HA_EVENT, RandomizedStageType.BAD, 425, 500, false);

        minor = getDataFor(Stage.MINOR_EVENT);
        if (minor != null) {
            minor.wasEverReached = minorCompleted;
        }

        Global.getSector().getListenerManager().removeListenerOfClass(PirateHostileActivityFactor.class);
        Global.getSector().getListenerManager().removeListenerOfClass(LuddicPathHostileActivityFactor.class);
        Global.getSector().getListenerManager().removeListenerOfClass(PerseanLeagueHostileActivityFactor.class);
        Global.getSector().getListenerManager().removeListenerOfClass(TriTachyonHostileActivityFactor.class);
        Global.getSector().getListenerManager().removeListenerOfClass(LuddicChurchHostileActivityFactor.class);
        Global.getSector().getListenerManager().removeListenerOfClass(SindrianDiktatHostileActivityFactor.class);
        Global.getSector().getListenerManager().removeListenerOfClass(HegemonyHostileActivityFactor.class);
        Global.getSector().getListenerManager().removeListenerOfClass(RemnantHostileActivityFactor.class);


        addFactor(new HAColonyDefensesFactor());
        addFactor(new HAShipsDestroyedFactorHint());
        addFactor(new CommisionProtectionFactor());
        addFactor(new HABlowbackFactor());

        AoTDPirateHostileActivityFactor pirate = new AoTDPirateHostileActivityFactor(this);
        addActivity(pirate, new KantasProtectionPirateActivityCause2(this));
        addActivity(pirate, new StandardPirateActivityCause2(this));
        addActivity(pirate, new PirateBasePirateActivityCause2(this));
        addActivity(pirate, new KantasWrathPirateActivityCause2(this));

        LuddicPathHostileActivityFactor path = new AoTDLuddicPathHostileActivityFactor(this);
        addActivity(path, new LuddicPathAgreementHostileActivityCause2(this));
        addActivity(path, new StandardLuddicPathActivityCause2(this));
        AoTDHegemonyHostileActivityFactor factor = new AoTDHegemonyHostileActivityFactor(this);
        addActivity(factor, new AoTDHegemonyAICoresActivityCause(this));


        addActivity(new AoTDPerseanLeagueHostileActivityFactor(this), new StandardPerseanLeagueActivityCause(this));
        addActivity(new AoTDTriTachyonHostileActivityFactor(this), new TriTachyonStandardActivityCause(this));
        addActivity(new AoTDLuddicChurchHostileActivityFactor(this), new LuddicChurchStandardActivityCause(this));
        addActivity(new AoTDSindrianDiktatHostileActivityFactor(this), new SindrianDiktatStandardActivityCause(this));

        addActivity(new RemnantHostileActivityFactor(this), new RemnantNexusActivityCause(this));

    }
}
