package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.util.Misc;
import kaysaar.aotd_question_of_loyalty.data.models.RankData;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class AoTDHandlePromotion extends BaseCommandPlugin {

    protected CampaignFleetAPI playerFleet;
    protected SectorEntityToken entity;
    protected FactionAPI playerFaction;
    protected FactionAPI entityFaction;
    protected TextPanelAPI text;
    protected OptionPanelAPI options;
    protected CargoAPI playerCargo;
    protected MemoryAPI memory;
    protected InteractionDialogAPI dialog;
    protected Map<String, MemoryAPI> memoryMap;
    protected PersonAPI person;
    protected FactionAPI faction;
    protected RankData daten;
    protected RankData currDaten;

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        this.dialog = dialog;
        this.memoryMap = memoryMap;
        memory = getEntityMemory(memoryMap);
        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;
        entity = dialog.getInteractionTarget();
        text = dialog.getTextPanel();
        options = dialog.getOptionPanel();

        playerFleet = Global.getSector().getPlayerFleet();
        playerCargo = playerFleet.getCargo();
        String id = Global.getSector().getMemory().getString("$aotd_rank_temp");
        String currid = AoTDCommIntelPlugin.get().getRank();
        daten = AoTDCommIntelPlugin.get().getRankForID(id);
        currDaten = AoTDCommIntelPlugin.get().getRankForID(currid);
        playerFaction = Global.getSector().getPlayerFaction();
        entityFaction = entity.getFaction();

        person = dialog.getInteractionTarget().getActivePerson();
        faction = person.getFaction();
        if (command.equals("initConversation")) {
            dialog.getOptionPanel().clearOptions();
            if (!daten.isLeavingAnOption() && currDaten.isLeavingAnOption()) {
                dialog.getTextPanel().addPara("Once you attain this rank, leaving our faction won't be an option. Should you betray us, we will treat you as threat of high priority.",Color.ORANGE);
                dialog.getOptionPanel().addOption("Yes, I am aware of this", "aotd_confirm_no_going_back");
                dialog.getOptionPanel().addOption("On second thought, I think i am not ready for this", "aotd_return_no_rank");
            } else {
                dialog.getTextPanel().addPara("You have proven yourself useful, and your efforts have greatly helped our faction");
                dialog.getTextPanel().addPara("Given your request and power bestowed by our leaders");
                dialog.getTextPanel().addPara("From now on your rank shall be %s", Color.ORANGE, daten.name);
                dialog.getOptionPanel().addOption("I shall serve.", "aotd_return");
            }
        }
        if (command.equals("overrideInitConversation")) {
            dialog.getOptionPanel().clearOptions();
            dialog.getTextPanel().addPara("You have proven yourself useful, and your efforts have greatly helped our faction");
            dialog.getTextPanel().addPara("Given your request and power bestowed by our leaders");
            dialog.getTextPanel().addPara("From now on your rank shall be %s", Color.ORANGE, daten.name);
            dialog.getOptionPanel().addOption("I shall serve.", "aotd_return");

        }
        if (command.equals("promote")) {
            AoTDCommIntelPlugin.get().setRank(id);
        }
        return true;
    }
}
