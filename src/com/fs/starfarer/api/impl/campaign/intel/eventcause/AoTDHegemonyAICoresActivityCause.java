package com.fs.starfarer.api.impl.campaign.intel.eventcause;

import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.events.HegemonyAICoresActivityCause;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;

public class AoTDHegemonyAICoresActivityCause extends HegemonyAICoresActivityCause {
    public AoTDHegemonyAICoresActivityCause(HostileActivityEventIntel intel) {
        super(intel);
    }

    @Override
    public boolean shouldShow() {
        return super.shouldShow()&& !QoLMisc.isCommissionedBy(Factions.HEGEMONY);
    }

    @Override
    public int getProgress(boolean checkNegated) {
        if(QoLMisc.isCommissionedBy(Factions.HEGEMONY))return 0;
        return super.getProgress(checkNegated);
    }

    @Override
    public float getMagnitudeContribution(StarSystemAPI system) {
        if(QoLMisc.isCommissionedBy(Factions.HEGEMONY))return 0f;
        return super.getMagnitudeContribution(system);
    }
}
