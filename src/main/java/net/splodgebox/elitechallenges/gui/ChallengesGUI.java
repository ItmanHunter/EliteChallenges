package net.splodgebox.elitechallenges.gui;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import net.splodgebox.elitechallenges.EliteChallenges;
import net.splodgebox.elitechallenges.utils.gui.Gui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

import net.splodgebox.elitechallenges.Challenge;

import net.splodgebox.elitechallenges.runnables.ChallengeTimeUpdater;
import net.splodgebox.elitechallenges.utils.Util;

public class ChallengesGUI {
	public static ArrayList<Challenge> challengesInGUI = new ArrayList<Challenge>();
	
	public ChallengesGUI(Player player) {
		openChallengesGUI(player);
	}
	
	@SuppressWarnings("deprecation")
	public void openChallengesGUI(Player player) {
		Util.sortRanks();
		FileConfiguration config = EliteChallenges.getInstance().getConfig();
		Gui challengesGUI = new Gui(Util.color(config.getString("gui.name")), (config.getInt("gui.size")/9));
		for (String key : config.getConfigurationSection("gui.icons").getKeys(false)) {
			String iconPath = "gui.icons." + key;
			ItemStack icon = createIcon(iconPath, config.getStringList(iconPath + ".lore"));
			challengesGUI.setItem(Integer.parseInt(key), icon, (clicker, event) -> {});
		}

		int slot = 11;
		for (Challenge challenge : challengesInGUI) {
			String iconPath = "challenges." + challenge.getChallengeName() + ".icon";
			List<String> newList = new ArrayList<String>();
			for (String string : config.getStringList(iconPath + ".lore")) {
				string = string.replace("%date%", Util.getDateMessage()).replace("%time%", Util.timeMessage(ChallengeTimeUpdater.counter)).replace("%player%", player.getName()).replace("%counter%", Integer.toString(challenge.getCounters().get(player.getName()))).replace("%rank%", Integer.toString(challenge.getRanking(player.getName())));
				int i = 1;
				for (String playerName : challenge.getCounters().keySet()) {
					string = string.replace("%player" + i + "%", playerName).replace("%counter" + i + "%", Integer.toString(challenge.getCounters().get(playerName)));
					if (Util.hasFactions()) {
						FPlayer fplayer = FPlayers.getInstance().getByOfflinePlayer(Bukkit.getOfflinePlayer(playerName));
						Faction faction = fplayer.getFaction();
						string = string.replace("%faction" + i + "%", faction.getTag());
					} else {
						string = string.replace("%faction" + i + "%", "");
					}
					i++;
				}
				
				if (i == 1) {
					string = string.replace("%player" + 1 + "%", "Empty").replace("%counter" + 1 + "%", "")
							.replace("%faction" + 1 + "%", "");
					string = string.replace("%player" + 2 + "%", "Empty").replace("%counter" + 2 + "%", "")
							.replace("%faction" + 2 + "%", "");
					string = string.replace("%player" + 3 + "%", "Empty").replace("%counter" + 3 + "%", "")
							.replace("%faction" + 3 + "%", "");
				} else if (i == 2) {
					string = string.replace("%player" + 2 + "%", "Empty").replace("%counter" + 2 + "%", "")
							.replace("%faction" + 2 + "%", "");
					string = string.replace("%player" + 3 + "%", "Empty").replace("%counter" + 3 + "%", "")
							.replace("%faction" + 3 + "%", "");
				} else if (i == 3) {
					string = string.replace("%player" + 3 + "%", "Empty").replace("%counter" + 3 + "%", "")
							.replace("%faction" + 3 + "%", "");
				}

				string = string.replace("null", "None");
				newList.add(string);
			}
			ItemStack icon = createIcon(iconPath, newList);
			challengesGUI.setItem(slot, icon, (clicker, event) -> {
				Gui challengeStatistics = new Gui(Util.color(config.getString("stats-gui-format.name")), config.getInt("stats-gui-format.size")/9);
				for (String playerName : challenge.getCounters().keySet()) {
					int rank = challenge.getRanking(playerName);
					if (rank > 50) {
						break;
					}
					List<String> list = new ArrayList<String>();
					for (String string : config.getStringList("stats-gui-format.skull-lore")) {
						list.add(string.replace("%player%", playerName).replace("%rank%", Integer.toString(rank)));
					}
					ItemStack skull = Util
							.createItemStackSkull(playerName,
									Util.color(config.getString("stats-gui-format.skull-name")
											.replace("%player%", playerName).replace("%rank%", Integer.toString(rank))),
									list);
					challengeStatistics.addItem(skull, (clicker1, event1) -> {});
				}
				challengeStatistics.open(player);
			});
			slot++;
		}
		challengesGUI.open(player);
	}
	
	public static void resetChallengesInGUI() {
		challengesInGUI.clear();
		LinkedHashSet<Challenge> hashSet = new LinkedHashSet<>(Challenge.getRandomChallenges(3));
		challengesInGUI.addAll(hashSet);
	}
	
	public ItemStack createIcon(String iconPath, List<String> list) {
		FileConfiguration config = EliteChallenges.getInstance().getConfig();
		ItemStack icon = Util.createItemStack(Material.valueOf(config.getString(iconPath + ".material")), config.getInt(iconPath + ".amount"), config.getString(iconPath + ".name"), config.getInt(iconPath + ".data"), list);
		return icon;
	}
}
