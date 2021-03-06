package net.splodgebox.elitechallenges.utils;

import net.splodgebox.elitechallenges.Challenge;
import net.splodgebox.elitechallenges.EliteChallenges;
import net.splodgebox.elitechallenges.gui.ChallengesGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
public class Util {
	
	public static String getSkullItem() {
		if (Bukkit.getVersion().contains("1.13")) {
			return "LEGACY_SKULL_ITEM";
		} else {
			return "SKULL_ITEM";
		}
	}

	public static LinkedHashMap<String, Integer> sortByValue(LinkedHashMap<String, Integer> map) {
		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		ArrayList<String> takenKeys = new ArrayList<String>();
		String greatestKey = "";
		int greatestNum = -1;
		for (int i = 0; i < map.size(); i++) {
			for (Entry<String, Integer> entry : map.entrySet()) {
				if (entry.getValue() > greatestNum && !takenKeys.contains(entry.getKey())) {
					greatestKey = entry.getKey();
					greatestNum = entry.getValue();
				}
			}
			sortedMap.put(greatestKey, greatestNum);
			takenKeys.add(greatestKey);
			greatestKey = "";
			greatestNum = -1;
		}
		return sortedMap;
	}

	public static String getDateMessage() {
		Calendar c = Calendar.getInstance();
		String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
		String date = String.valueOf(c.getTime().getDate());
		String year = c.getTime().toString().split(" ")[5];
		return month + " " + date + ", " + year;
	}

	public static String timeMessage(long seconds) {
		int day = (int) TimeUnit.SECONDS.toDays(seconds);
		long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
		long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
		long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
		return day + " Days " + hours + "h " + minute + "m " + second + "s";
	}

	public static String color(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	public static ItemStack createItemStack(Material type, int amt, String name, int data, List<String> lore) {
		ItemStack stack = null;
		if (data != -1) {
			stack = new ItemStack(type, amt, (short) data);
		} else {
			stack = new ItemStack(type, amt);
		}
		ItemMeta im = stack.getItemMeta();
		im.setDisplayName(Util.color(name));
		ArrayList<String> lorelist = new ArrayList<String>();
		for (String s : lore) {
			lorelist.add(Util.color(s));
		}
		im.setLore(lorelist);
		stack.setItemMeta(im);

		return stack;
	}

	public static ItemStack createItemStackSkull(String playerName, String skullName, List<String> lores) {
		ItemStack stack = new ItemStack(Material.valueOf(getSkullItem()), 1, (short) 3);
		SkullMeta im = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.valueOf(getSkullItem()));
		im.setOwner(playerName);
		im.setDisplayName(Util.color(skullName));
		ArrayList<String> lore = new ArrayList<String>();
		for (String str : lores) {
			lore.add(Util.color(str));
		}
		im.setLore(lore);
		stack.setItemMeta(im);
		return stack;
	}
	
	public static boolean isInEnabledWorld(Player player) {
		String world = player.getWorld().getName();
		if (EliteChallenges.getInstance().getConfig().getBoolean("per-world.is-enabled") == true) {
			for (String loopWorld : EliteChallenges.getInstance().getConfig().getStringList("per-world.worlds")) {
				if (world.equals(loopWorld)) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}
	
	public static void sortRanks() {
		for (Challenge challenge : ChallengesGUI.challengesInGUI) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!challenge.getCounters().containsKey(player.getName())) {
					challenge.getCounters().put(player.getName(), 0);
				}
			}
			LinkedHashMap<String, Integer> map = Util.sortByValue(challenge.getCounters());
			challenge.setCounters(map);
		}
	}

	public static boolean allowsPVP(Player player, Location loc) {
		return EliteChallenges.getInstance().worldGuardAPI.isPVPAllowed(player, loc);
	}

	public static boolean allowsBreaking(Player player, Block block) {
		return EliteChallenges.getInstance().worldGuardAPI.canBreak(player, block);
	}

	public static boolean allowsPlacing(Player player, Block block) {
		return EliteChallenges.getInstance().worldGuardAPI.hasBuildPermission(player, block);
	}
	
	public static boolean hasFactions() {
		return Bukkit.getPluginManager().getPlugin("Factions") != null;
	}
}
