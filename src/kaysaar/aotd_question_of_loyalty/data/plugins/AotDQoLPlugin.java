package kaysaar.aotd_question_of_loyalty.data.plugins;


import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityManager;
import kaysaar.aotd_question_of_loyalty.data.listeners.AoTDDestructionOfEnemiesTracker;
import kaysaar.aotd_question_of_loyalty.data.listeners.AoTDFreeStorageComm;
import kaysaar.aotd_question_of_loyalty.data.listeners.AoTDTransactionListener;
import kaysaar.aotd_question_of_loyalty.data.scripts.AoTDColonyCrisisManager;
import kaysaar.aotd_question_of_loyalty.data.scripts.AoTDDealWithNexerlinComm;
import kaysaar.aotd_question_of_loyalty.data.scripts.commision.AoTDCommissionDataManager;
import kaysaar.aotd_question_of_loyalty.data.scripts.effectapplier.AoTDSharedSensorLink;
import kaysaar.aotd_question_of_loyalty.data.scripts.rulesInterceptor.*;
import kaysaar.aotd_question_of_loyalty.data.scripts.trackers.BountyTracker;
import kaysaar.aotd_question_of_loyalty.data.scripts.trackers.DeliveryTracker;
import kaysaar.aotd_question_of_loyalty.data.scripts.trackers.ExplorationTracker;
import kaysaar.aotd_question_of_loyalty.data.scripts.trackers.MagicLibBountyTracker;

//import data.colonyevents.ui.ExampleIDP;


public class AotDQoLPlugin extends BaseModPlugin {

    @Override
    public void onApplicationLoad() throws Exception {
        super.onApplicationLoad();
        AoTDCommissionDataManager.getInstance();
    }

    @Override
    public void onNewGameAfterProcGen() {
        if (!Global.getSector().hasScript(AoTDColonyCrisisManager.class)) {
            Global.getSector().removeScriptsOfClass(HostileActivityManager.class);
            if (HostileActivityEventIntel.get() != null) {
                HostileActivityEventIntel.get().endImmediately();
            }
            Global.getSector().addScript(new AoTDColonyCrisisManager());
        }
    }

    @Override
    public void onGameLoad(boolean newGame) {
        super.onGameLoad(newGame);
        ListenerManagerAPI listener = Global.getSector().getListenerManager();
        listener.addListener(new AoTDFreeStorageComm(), true);
        listener.addListener(new AoTDTransactionListener(), true);
        Global.getSector().addTransientScript(new AICoreReplaceScript());
        Global.getSector().addTransientScript(new CommisionReplaceScript());
        Global.getSector().addTransientScript(new ResignCommsionReplaceScript());
        Global.getSector().addTransientScript(new TalkWayFromScanScript());
        Global.getSector().addTransientScript(new BountyTracker());
        Global.getSector().addTransientScript(new ExplorationTracker());
        Global.getSector().addTransientScript(new DeliveryTracker());
        Global.getSector().addTransientScript(new AoTDDealWithNexerlinComm());
        Global.getSector().addTransientScript(new AoTDSharedSensorLink());
        Global.getSector().addTransientScript(new BlockCommisionHostileActions());
        Global.getSector().addTransientListener(new AoTDDestructionOfEnemiesTracker());
        if (Global.getSettings().getModManager().isModEnabled("MagicLib")) {
            Global.getSector().addTransientScript(new MagicLibBountyTracker());
        }
        if (AoTDCommIntelPlugin.get() != null) {
            AoTDCommIntelPlugin.get().updateData();
        }
    }
}
