package net.splodgebox.elitechallenges.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import net.splodgebox.elitechallenges.EliteChallenges;
import net.splodgebox.elitechallenges.gui.ChallengesGUI;
import net.splodgebox.elitechallenges.runnables.ChallengeTimeUpdater;
import net.splodgebox.elitechallenges.utils.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("challenges")
public class ChallengesCMD extends BaseCommand {

    @Default
    public void openChallengesInventory(CommandSender commandSender) {
        new ChallengesGUI((Player) commandSender);
    }

    @Subcommand("time")
    public void showTimer(CommandSender commandSender) {
        commandSender.sendMessage(Util.color(EliteChallenges.getInstance().getConfig().getString("messages.time-message").replace("%time%",
                Util.timeMessage(ChallengeTimeUpdater.counter))));
    }

    @Subcommand("reset")
    public void resetChallenges(CommandSender commandSender) {
        ChallengeTimeUpdater.counter = 86400;
        ChallengesGUI.resetChallengesInGUI();
        commandSender.sendMessage(Util.color(EliteChallenges.getInstance().getConfig().getString("messages.challenges-reset-message")));
    }
}
