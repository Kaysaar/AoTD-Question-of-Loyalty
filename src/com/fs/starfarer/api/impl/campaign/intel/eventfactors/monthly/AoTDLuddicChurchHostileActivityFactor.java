package com.fs.starfarer.api.impl.campaign.intel.eventfactors.monthly;

import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.LuddicChurchHostileActivityFactor;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;

public class AoTDLuddicChurchHostileActivityFactor extends LuddicChurchHostileActivityFactor {
    public AoTDLuddicChurchHostileActivityFactor(HostileActivityEventIntel intel) {
        super(intel);
    }

    @Override
    public boolean shouldShow(BaseEventIntel intel) {
        return super.shouldShow(intel)&&!QoLMisc.isCommissionedBy(Factions.LUDDIC_CHURCH);
    }

    @Override
    public void rollEvent(HostileActivityEventIntel intel, BaseEventIntel.EventStageData stage) {
        if(QoLMisc.isCommissionedBy(Factions.LUDDIC_CHURCH))return;
        super.rollEvent(intel, stage);
    }

    @Override
    public boolean fireEvent(HostileActivityEventIntel intel, BaseEventIntel.EventStageData stage) {
        if(QoLMisc.isCommissionedBy(Factions.LUDDIC_CHURCH))return false;
        return super.fireEvent(intel, stage);
    }
}
