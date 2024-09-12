package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class AoTDInsertRefuseOption extends BaseCommandPlugin{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        List list = dialog.getOptionPanel().getSavedOptionList();
        dialog.getOptionPanel().clearOptions();
        Object obj ;
        dialog.getOptionPanel().addOption("Who are you to scan me?","aotd_refuse_scan",new Color(239, 139, 24),"We can intimidate this officer due to our rank within faction");
        obj = dialog.getOptionPanel().getSavedOptionList().get(0);
        List newList = dialog.getOptionPanel().getSavedOptionList();
        newList.clear();
        int i =0;

        for (Object o : list) {
            if(i==1){
                newList.add(obj);
            }
            newList.add(o);
            i++;
        }
        dialog.getOptionPanel().clearOptions();
        dialog.getOptionPanel().restoreSavedOptions(newList);
        return true;
    }
}
