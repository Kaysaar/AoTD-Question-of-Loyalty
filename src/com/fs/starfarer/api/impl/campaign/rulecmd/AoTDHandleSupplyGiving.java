package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class AoTDHandleSupplyGiving extends BaseCommandPlugin{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;
        CargoAPI deliverCargo = null;
        if (command.equals("toShip")) {
        deliverCargo = Global.getSector().getPlayerFleet().getCargo();

        }
        if (command.equals("toStorage")) {
        deliverCargo = dialog.getInteractionTarget().getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo();

        }
        AoTDCommIntelPlugin.get().deliverCargo(dialog,deliverCargo);

        return true;

    }
}
