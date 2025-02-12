package kaysaar.aotd_question_of_loyalty.data.plugins;


import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.impl.campaign.econ.LuddicMajority;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityManager;
import exerelin.utilities.NexUtilsMarket;
import kaysaar.aotd_question_of_loyalty.data.intel.AoTDCommIntelPlugin;
import kaysaar.aotd_question_of_loyalty.data.intel.secession.AoTDNexInvasionListener;
import kaysaar.aotd_question_of_loyalty.data.intel.secession.EmergentAuthorityApplier;
import kaysaar.aotd_question_of_loyalty.data.listeners.AoTDColonyCrisisInserter;
import kaysaar.aotd_question_of_loyalty.data.listeners.AoTDDestructionOfEnemiesTracker;
import kaysaar.aotd_question_of_loyalty.data.listeners.AoTDFreeStorageComm;
import kaysaar.aotd_question_of_loyalty.data.listeners.AoTDTransactionListener;
import kaysaar.aotd_question_of_loyalty.data.scripts.AoTDDealWithNex;
import kaysaar.aotd_question_of_loyalty.data.scripts.commision.AoTDCommissionDataManager;
import kaysaar.aotd_question_of_loyalty.data.scripts.effectapplier.AoTDIgnoreTurningOffTransponder;
import kaysaar.aotd_question_of_loyalty.data.scripts.effectapplier.AoTDSharedSensorLink;
import kaysaar.aotd_question_of_loyalty.data.scripts.rulesInterceptor.*;
import kaysaar.aotd_question_of_loyalty.data.scripts.trackers.*;

import java.util.Iterator;


public class AotDQoLPlugin extends BaseModPlugin {

    @Override
    public void onApplicationLoad() throws Exception {
        super.onApplicationLoad();
        AoTDCommissionDataManager.getInstance();
    }



    @Override
    public void onGameLoad(boolean newGame) {
        super.onGameLoad(newGame);
        ListenerManagerAPI listener = Global.getSector().getListenerManager();
        listener.addListener(new AoTDFreeStorageComm(), true);
        listener.addListener(new AoTDTransactionListener(), true);
        listener.addListener(new AoTDColonyCrisisInserter(),true);
        listener.addListener(new EmergentAuthorityApplier(),true);
        Global.getSector().addTransientScript(new AICoreReplaceScript());
        Global.getSector().addTransientScript(new CommisionReplaceScript());
        Global.getSector().addTransientScript(new ResignCommsionReplaceScript());
        Global.getSector().addTransientScript(new TalkWayFromScanScript());
        Global.getSector().addTransientScript(new BountyTracker());
        Global.getSector().addTransientScript(new ExplorationTracker());
        Global.getSector().addTransientScript(new DeliveryTracker());
        Global.getSector().addTransientScript(new AoTDDealWithNex());
        Global.getSector().addTransientScript(new AoTDSharedSensorLink());
        Global.getSector().addTransientScript(new BlockCommisionHostileActions());
        Global.getSector().addTransientListener(new AoTDDestructionOfEnemiesTracker());
        Global.getSector().addTransientScript(new AoTDIgnoreTurningOffTransponder());
        Global.getSector().addTransientScript(new AoTDHubMissionTracker());
        Iterator < EveryFrameScript> iter = Global.getSector().getScripts().iterator();
        if(Global.getSettings().getModManager().isModEnabled("nexerlin")){
            Global.getSector().getListenerManager().addListener(new AoTDNexInvasionListener(),true);
        }
        while (iter.hasNext()) {
            EveryFrameScript next = iter.next();
            if(next instanceof AoTDCommIntelPlugin){
                if(AoTDCommIntelPlugin.get()!=null&&next.equals(AoTDCommIntelPlugin.get())){
                    continue;
                }
                iter.remove();
            }

        }

        if (Global.getSettings().getModManager().isModEnabled("MagicLib")) {
            Global.getSector().addTransientScript(new MagicLibBountyTracker());
        }
        Global.getSector().addTransientScript(new AoTDIncomeFixerTracker());
        if (AoTDCommIntelPlugin.get() != null) {
            AoTDCommIntelPlugin.get().updateData();
        }
    }
}
