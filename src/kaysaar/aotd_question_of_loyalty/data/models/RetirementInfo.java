package kaysaar.aotd_question_of_loyalty.data.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.rulecmd.AoTDRetirementOption;
import kaysaar.aotd_question_of_loyalty.data.scripts.commision.AoTDCommissionDataManager;

public class RetirementInfo {
    String prevRankId;
    float salary;
    String prevFaction;
    public static RetirementInfo getInstance(){
        return (RetirementInfo) Global.getSector().getMemory().get(AoTDRetirementOption.memKey);
    }
    public RetirementInfo(String prevRankId, float assignedPension, String factionId) {
        this.prevRankId = prevRankId;
        this.salary = salary;
        this.prevFaction = factionId;
    }

    public float getSalary() {
        return salary;
    }

    public String getPrevFaction() {
        return prevFaction;
    }

    public String getPrevRankId() {
        return prevRankId;
    }
    public RankData getRankData() {
        return AoTDCommissionDataManager.getInstance().getRank(prevRankId);
    }

}
