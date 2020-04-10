package net.splodgebox.elitechallenges.runnables;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

import net.splodgebox.elitechallenges.Challenge;
import net.splodgebox.elitechallenges.ChallengeType;
import net.splodgebox.elitechallenges.gui.ChallengesGUI;
import net.splodgebox.elitechallenges.utils.Util;

public class TimeChallengesListener extends BukkitRunnable {

	public void run() {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			Location loc = player.getLocation();
			if (Util.isInEnabledWorld(player)) {
				for (Challenge challenge : ChallengesGUI.challengesInGUI) {
					if (challenge.getChallengeType().equals(ChallengeType.WARZONE_TIME)) {
						if (Util.allowsPVP(player, loc)) {
							if (Util.hasFactions()) {
								FLocation fLoc = new FLocation(loc);
								Faction fac = Board.getInstance().getFactionAt(fLoc);
								if (fac.isWarZone()) {
									challenge.updateCounter(player.getName(), 1);
								}
							}
						}
					} else if (challenge.getChallengeType().equals(ChallengeType.PTIME)) {
						challenge.updateCounter(player.getName(), 1);
					}
				}
			}
		}
	}
}
