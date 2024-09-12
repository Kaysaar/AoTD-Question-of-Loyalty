package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.models.BaseContractInfo;

public class AotDMigrationContract extends BaseContractInfo {
    @Override
    public void createTooltip(TooltipMakerAPI tooltip) {
        tooltip.addSectionHeading("Accessibility contract", Alignment.MID,5f);
        tooltip.addPara("We will lower restrictions with your trade",5f);
        tooltip.addSectionHeading("Contract Benefits",Alignment.MID,10f);
        tooltip.addPara("All colonies get %s accessibility bonus",5f,Misc.getPositiveHighlightColor(),"30%");
        tooltip.addSectionHeading("Contract Obligations",Alignment.MID,10f);
        tooltip.addPara("After this contract is signed: ",5f);
        tooltip.addPara("We will loose %s of fleet size from all colonies as they will be dedicated towards protection of new trade fleets",5f,Misc.getNegativeHighlightColor(),"50%");
        tooltip.addPara("Should you terminate contract you will be obliged to pay fine of %s",10f,Misc.getNegativeHighlightColor(),Misc.getDGSCredits(2500000));
    }
}
