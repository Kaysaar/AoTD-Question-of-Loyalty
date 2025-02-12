package kaysaar.aotd_question_of_loyalty.data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.impl.campaign.rulecmd.AoTDRetirementOption;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.models.BaseFactionCommisionData;
import kaysaar.aotd_question_of_loyalty.data.models.RetirementInfo;
import kaysaar.aotd_question_of_loyalty.data.scripts.commision.AoTDCommissionDataManager;

public class AoTDPensionFund implements EconomyTickListener {
    protected RetirementInfo info;

    public AoTDPensionFund(String factionId, float assignedPension,String prevRankId) {
        info = new RetirementInfo(prevRankId,assignedPension,factionId);
        Global.getSector().getMemory().set(AoTDRetirementOption.memKey,info);
        Global.getSector().getListenerManager().addListener(this);

    }
    @Override
    public void reportEconomyTick(int iterIndex) {
        if(!Misc.getPlayerMarkets(false).isEmpty()){
            Global.getSector().getListenerManager().removeListener(this);
            return;
        }
        float numIter = Global.getSettings().getFloat("economyIterPerMonth");
        float f = 1f / numIter;

        //CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        MonthlyReport report = SharedData.getData().getCurrentReport();

        MonthlyReport.FDNode fleetNode = report.getNode(MonthlyReport.FLEET);
        fleetNode.name = "Fleet";
        fleetNode.custom = MonthlyReport.FLEET;
        fleetNode.tooltipCreator = report.getMonthlyReportTooltip();
        final FactionAPI faction = Global.getSector().getFaction(info.getPrevFaction());
        float stipend = info.getSalary();

        MonthlyReport.FDNode stipendNode = report.getNode(fleetNode, "node_id_pension_" + info.getPrevFaction());
        stipendNode.income += stipend * f;

        if (stipendNode.name == null) {
            stipendNode.name = faction.getDisplayName() + " Retirement pension";
            stipendNode.icon = faction.getCrest();
            stipendNode.tooltipCreator = new TooltipMakerAPI.TooltipCreator() {
                public boolean isTooltipExpandable(Object tooltipParam) {
                    return false;
                }

                public float getTooltipWidth(Object tooltipParam) {
                    return 450;
                }

                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.addPara("Your monthly retirement pension, for serving  " + faction.getDisplayName() + " as "+ AoTDCommissionDataManager.getInstance().getRank(info.getPrevRankId()).getName(), 0f);
                }
            };
        }
    }

    @Override
    public void reportEconomyMonthEnd() {

    }
}
