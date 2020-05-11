package net.splodgebox.elitechallenges;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import net.splodgebox.elitechallenges.commands.ChallengesCMD;
import net.splodgebox.elitechallenges.events.ChallengeListener;
import net.splodgebox.elitechallenges.gui.ChallengesGUI;
import net.splodgebox.elitechallenges.runnables.ChallengeTimeUpdater;
import net.splodgebox.elitechallenges.runnables.TimeChallengesListener;
import net.splodgebox.elitechallenges.utils.Chat;
import net.splodgebox.elitechallenges.utils.FileManager;
import net.splodgebox.elitechallenges.utils.WorldGuardAPI;
import net.splodgebox.elitechallenges.utils.gui.GuiListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class EliteChallenges extends JavaPlugin {

	ChallengeTimeUpdater challengeRunner;
	TimeChallengesListener timeChallengesListener;

	@Getter
	private static EliteChallenges instance;
	
	public FileManager dataconfig;
	public WorldGuardAPI worldGuardAPI;

	public void reload() {
		deleteData();
		reloadConfig();
		Challenge.challenges.clear();
		for (String key : getConfig().getConfigurationSection("challenges").getKeys(false)) {
			String path = "challenges." + key;
			Challenge challenge = new Challenge(key, ChallengeType.valueOf(getConfig().getString(path + ".type")),
					getConfig().getStringList(path + ".object-types"), new LinkedHashMap<String, Integer>());
			Challenge.challenges.add(challenge);
		}
		registerDataFile();
		if (ChallengesGUI.challengesInGUI.size() == 0) {
			ChallengesGUI.resetChallengesInGUI();
		}
		challengeRunner.cancel();
		timeChallengesListener.cancel();
		challengeRunner = new ChallengeTimeUpdater();
		challengeRunner.runTaskTimer(this, 20, 20);
		timeChallengesListener = new TimeChallengesListener();
		timeChallengesListener.runTaskTimer(this, 20, 20);
	}

	private void deleteData() {
		File dataFile = new File(getDataFolder().getAbsolutePath(),"data.yml");
		dataFile.delete();
	}

	public void onEnable() {
		instance = this;

		try {
			worldGuardAPI = new WorldGuardAPI(getServer().getPluginManager().getPlugin("WorldGuard"), this);
		} catch (NullPointerException | NoClassDefFoundError exception) {
			Chat.log("&cWorldGuard Support Failed");
			getServer().getPluginManager().disablePlugin(this);
		}

		saveDefaultConfig();

		PaperCommandManager paperCommandManager = new PaperCommandManager(this);
		paperCommandManager.registerCommand(new ChallengesCMD());

		getServer().getPluginManager().registerEvents(new ChallengeListener(), this);
		getServer().getPluginManager().registerEvents(new GuiListener(), this);

		for (String key : getConfig().getConfigurationSection("challenges").getKeys(false)) {
			String path = "challenges." + key;
			Challenge challenge = new Challenge(key, ChallengeType.valueOf(getConfig().getString(path + ".type")),
					getConfig().getStringList(path + ".object-types"), new LinkedHashMap<String, Integer>());
			Challenge.challenges.add(challenge);
		}

		registerDataFile();

		if (ChallengesGUI.challengesInGUI.size() == 0) {
			ChallengesGUI.resetChallengesInGUI();
		}

		Metrics metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SimplePie("used_language", new Callable<String>() {
			@Override
			public String call() {
				return getConfig().getString("language", "en");
			}
		}));

		challengeRunner = new ChallengeTimeUpdater();
		challengeRunner.runTaskTimer(this, 20, 20);
		timeChallengesListener = new TimeChallengesListener();
		timeChallengesListener.runTaskTimer(this, 20, 20);
	}

	public void onDisable() {
		dataconfig.getConfig().set("timer.time", Long.toString(ChallengeTimeUpdater.counter));
		dataconfig.getConfig().createSection("challengesActive");
		dataconfig.getConfig().createSection("playerData");
		ChallengesGUI.challengesInGUI.forEach(challenge -> {
			dataconfig.getConfig().set("challengesActive." + challenge.getChallengeName(), challenge.getChallengeName());
			challenge.getCounters().keySet().forEach(playerName -> dataconfig.getConfig().set("playerData." + challenge.getChallengeName() + "." + playerName, challenge.getCounters().get(playerName)));
		});

		dataconfig.save();
	}

	public void registerDataFile() {
		dataconfig = new FileManager(this, "data", getDataFolder().getAbsolutePath());
		if (!dataconfig.getConfig().contains("challengesActive")) {
			dataconfig.getConfig().createSection("challengesActive");
			dataconfig.getConfig().createSection("playerData");
			dataconfig.getConfig().createSection("timer");
		} else {
			if (getTimerTime() != -1) {
				ChallengeTimeUpdater.setCounter(Integer.parseInt(dataconfig.getConfig().getString("timer.time")));
				dataconfig.getConfig().getConfigurationSection("challengesActive").getKeys(false).forEach(string -> {
					Challenge challenge = Challenge.getChallengeByName(string);
					LinkedHashMap<String, Integer> counters = dataconfig.getConfig().getConfigurationSection("playerData." + string).getKeys(false).stream().collect(Collectors.toMap(playerName -> playerName, playerName -> dataconfig.getConfig().getInt("playerData." + string + "." + playerName), (a, b) -> b, LinkedHashMap::new));
					challenge.setCounters(counters);
					ChallengesGUI.challengesInGUI.add(challenge);
				});
			}
		}
		dataconfig.save();
	}

	public int getTimerTime() {
		try {
			return Integer.parseInt(dataconfig.getConfig().getString("timer.time"));
		} catch (Exception e) {
			return -1;
		}
	}

}
