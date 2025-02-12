package kaysaar.aotd_question_of_loyalty.data.console;

import kaysaar.aotd_question_of_loyalty.data.intel.AoTDCommIntelPlugin;
import org.jetbrains.annotations.NotNull;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

import static org.lazywizard.console.CommandUtils.format;
import static org.lazywizard.console.CommandUtils.isInteger;

public class IncreasePoints  implements BaseCommand {
    @Override
    public CommandResult runCommand(@NotNull String args, @NotNull CommandContext context) {
        if (context.isInCombat()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }
        if (!context.isInCampaign())
        {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        if (args.isEmpty())
        {
            return CommandResult.BAD_SYNTAX;
        }

        if (!isInteger(args))
        {
            Console.showMessage("Error: credit amount must be a whole number!");
            return CommandResult.BAD_SYNTAX;
        }
        final int amount = Integer.parseInt(args);
        if(AoTDCommIntelPlugin.get()==null) {
            Console.showMessage("You are not commissioned");
            return CommandResult.ERROR;

        }
        if (amount >= 0)
        {
            final  int am2 =Math.min(AoTDCommIntelPlugin.get().getProgress()+amount,AoTDCommIntelPlugin.get().getMaxProgress());
            AoTDCommIntelPlugin.get().setProgress(am2);
            Console.showMessage("Currently having  " + format(am2) + " points");
        }
        else
        {
            final int removed = Math.min(-amount, AoTDCommIntelPlugin.get().getProgress());
            AoTDCommIntelPlugin.get().setProgress(AoTDCommIntelPlugin.get().getProgress()-removed);

            Console.showMessage("Currently having " + format(removed) + " points");
        }
        return CommandResult.SUCCESS;

    }


}
