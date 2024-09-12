package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.models.BaseContractInfo;

import java.awt.*;

public class AoTDMilitaryContractInfo extends BaseContractInfo {
    @Override
    public void createTooltip(TooltipMakerAPI tooltip) {
        tooltip.addSectionHeading("Military Protection Contract", Alignment.MID,5f);
        tooltip.addPara("We shall send to each of colonies under your supervision, a defence fleet, that will protect your holdings",5f);
        tooltip.addSectionHeading("Contract Benefits",Alignment.MID,10f);
        tooltip.addPara("Gain medium defence fleet for each planet", Misc.getPositiveHighlightColor(),5f);
        tooltip.addPara("Increase protection points in colony crisis from 40 to 50",Misc.getPositiveHighlightColor(),5f);
        tooltip.addSectionHeading("Contract Obligations",Alignment.MID,10f);
        tooltip.addPara("After this contract is signed: ",5f);
        tooltip.addPara("You have %s cycles own at least 4 heavy industry facilities, after that you need to designate at least 4 of them to contribute towards our faction.",3f, Color.ORANGE,"3","4");
        tooltip.addPara("Pay protection fee which is %s credits per colony",3f, Color.ORANGE,Misc.getDGSCredits(20000));
        tooltip.addPara("Should you fail to meet contract obligations or terminate contract you will be obliged to pay fine of %s",10f,Misc.getNegativeHighlightColor(),Misc.getDGSCredits(5000000));


    }
}
