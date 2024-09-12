package kaysaar.aotd_question_of_loyalty.data.campaign.econ.conditions;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class BaseColonyContractCondiiton implements MarketConditionPlugin {
    @Override
    public void advance(float amount) {

    }

    @Override
    public void init(MarketAPI market, MarketConditionAPI condition) {

    }

    @Override
    public void apply(String id) {

    }

    @Override
    public void unapply(String id) {

    }

    @Override
    public List<String> getRelatedCommodities() {
        return null;
    }

    @Override
    public Map<String, String> getTokenReplacements() {
        return null;
    }

    @Override
    public String[] getHighlights() {
        return new String[0];
    }

    @Override
    public Color[] getHighlightColors() {
        return new Color[0];
    }

    @Override
    public void setParam(Object param) {

    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public boolean showIcon() {
        return false;
    }

    @Override
    public boolean isPlanetary() {
        return false;
    }

    @Override
    public boolean hasCustomTooltip() {
        return false;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {

    }

    @Override
    public boolean isTooltipExpandable() {
        return false;
    }

    @Override
    public float getTooltipWidth() {
        return 0;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getIconName() {
        return null;
    }
}
