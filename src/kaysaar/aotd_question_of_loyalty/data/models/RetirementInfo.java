package kaysaar.aotd_question_of_loyalty.data.models;

public class RetirementInfo {
    String prevRankId;
    float salary;
    String prevFaction;

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

}
