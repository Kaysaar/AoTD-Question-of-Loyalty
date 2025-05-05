package kaysaar.aotd_question_of_loyalty.data.campaign.econ.conditions;

import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;

public class AoTDEmergentAutonomy extends BaseMarketConditionPlugin {
    @Override
    public void apply(String id) {
        super.apply(id);
        if(market.getFaction()!=null&&market.getFaction().isPlayerFaction()){
            market.getStability().modifyFlat("aotd_emergent",2,"Emergent Autonomy");
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult("aotd_emergent",2f,"Emergent Autonomy");
        }
    }

    @Override
    public void unapply(String id) {
        super.unapply(id);
        market.getStability().unmodifyFlat("aotd_emergent");
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult("aotd_emergent");
    }

    @Override
    public String getIconName() {
        return market.getFaction().getCrest();
    }

    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        tooltip.addPara("Increase stability by %s",5f, Color.ORANGE,"2");
        tooltip.addPara("Increase ground defence multiplier by %s",5f,Color.ORANGE,"2");
    }
}
