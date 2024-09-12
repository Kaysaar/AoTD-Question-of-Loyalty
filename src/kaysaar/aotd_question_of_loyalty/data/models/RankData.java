package kaysaar.aotd_question_of_loyalty.data.models;

import kaysaar.aotd_question_of_loyalty.data.tags.AoTDRankTags;

import java.util.Set;

public class RankData {
    public String id;
    public String name;
    boolean isLeavingAnOption;
    float penaltyForLeaving;
    String description;
    int pointsOfObligation;
    public int points;
    public int salary;
    int amountOfColoniesAbleToColonize;
    boolean unRestrictedColonization;
    float tarrifReduciton;
    public String getId() {
        return id;
    }
    public Set<String> tags;
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLeavingAnOption() {
        return isLeavingAnOption;
    }

    public void setLeavingAnOption(boolean leavingAnOption) {
        isLeavingAnOption = leavingAnOption;
    }

    public float getPenaltyForLeaving() {
        return penaltyForLeaving;
    }

    public void setPenaltyForLeaving(float penaltyForLeaving) {
        this.penaltyForLeaving = penaltyForLeaving;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPointsOfObligation() {
        return pointsOfObligation;
    }

    public void setPointsOfObligation(int pointsOfObligation) {
        this.pointsOfObligation = pointsOfObligation;
    }


    public RankData(String id,String name,boolean canLeaveWhenAttainedThisRank,float penaltyforLeaving,String description,int pointsOfObligation,int salary,float tarrifReduciton,Set<String>set,int amountOfColoniesAbleToColonize){
        this.id = id;
        this.name = name;
        this.isLeavingAnOption = canLeaveWhenAttainedThisRank;
        this.penaltyForLeaving = penaltyforLeaving;
        this.description = description;
        this.pointsOfObligation = pointsOfObligation;
        this.salary = salary;
        this.tags = set;
        this.unRestrictedColonization = false;
        this.amountOfColoniesAbleToColonize = amountOfColoniesAbleToColonize;
        this.tarrifReduciton = tarrifReduciton;

    }
    public boolean hasTag(String tag){
        for (String s : tags) {
            if(s.equals(tag)){
                return true;
            }
        }
        return false;
    }

    public float getTarrifReduciton() {
        return tarrifReduciton;
    }

    public int getAmountOfColoniesAbleToColonize() {
        return amountOfColoniesAbleToColonize;
    }

    public boolean isUnRestrictedColonization() {
        return tags.contains(AoTDRankTags.NONRESTRICTIVE_COLONIZATION);
    }
}
