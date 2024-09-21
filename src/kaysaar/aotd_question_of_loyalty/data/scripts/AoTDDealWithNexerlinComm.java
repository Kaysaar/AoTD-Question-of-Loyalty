package kaysaar.aotd_question_of_loyalty.data.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.models.BaseFactionCommisionData;
import kaysaar.aotd_question_of_loyalty.data.scripts.commision.AoTDCommissionDataManager;

public class AoTDDealWithNexerlinComm implements EveryFrameScript {
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        if(Misc.getCommissionIntel()==null){
            Global.getSector().removeTransientScriptsOfClass(this.getClass());
        }

        else{

            FactionAPI factionAPI = Misc.getCommissionFaction();
            Misc.getCommissionIntel().endImmediately();
            if(AoTDCommIntelPlugin.get()==null&&factionAPI!=null){
                try {
                    BaseFactionCommisionData data = AoTDCommissionDataManager.getInstance().getCommisionData(factionAPI.getId());
                    if(data==null){
                        data = BaseFactionCommisionData.getDefaultCommisionData(factionAPI.getId());
                    }
                    AoTDCommIntelPlugin intel  = data.getPlugin();
                    intel.initializeFully(data,null,false,data.getFirstDefRank(),data.getFirstOfficialRank());
                }
                catch (Exception e){
                    throw new RuntimeException(e);
                }


            }
            Global.getSector().removeTransientScriptsOfClass(this.getClass());
        }
    }
}
