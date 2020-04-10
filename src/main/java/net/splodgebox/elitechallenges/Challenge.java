package net.splodgebox.elitechallenges;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.splodgebox.elitechallenges.utils.Util;

public class Challenge {

	@Getter @Setter private String challengeName;
	@Getter @Setter private ChallengeType challengeType;
	@Getter @Setter private List<String> objectiveObjectTypes;
	@Getter @Setter private LinkedHashMap<String, Integer> counters;

	public static ArrayList<Challenge> challenges = new ArrayList<Challenge>();

	public Challenge(String challengeName, ChallengeType challengeType, List<String> objectiveObjectTypes, LinkedHashMap<String, Integer> counters) {
		setChallengeName(challengeName);
		setChallengeType(challengeType);
		setObjectiveObjectTypes(objectiveObjectTypes);
		setCounters(counters);
	}

	public Integer getRanking(String playerName) {
		int ranking = 1;
		for (String key : this.counters.keySet()) {
			if (key.equals(playerName)) {
				return ranking;
			}
			ranking++;
		}
		return -1;
	}

	public void updateCounter(String key, int amount) {
		if (this.counters.containsKey(key)) {
			this.counters.put(key, this.counters.get(key) + amount);
		} else {
			this.counters.put(key, 0);
		}
	}

	public static Challenge getChallengeByName(String name) {
		for (Challenge challenge : Challenge.challenges) {
			if (challenge.getChallengeName().equals(name)) {
				return challenge;
			}
		}
		return null;
	}

	public static ArrayList<Challenge> getRandomChallenges(int amountOfRandomChallenges) {
		ArrayList<Challenge> challenges = new ArrayList<Challenge>();
		if (Challenge.challenges.size() < 5) {
			amountOfRandomChallenges = Challenge.challenges.size();
		}
		ArrayList<Challenge> challengeArrayList = Challenge.challenges;
		for (int i = 0; i < amountOfRandomChallenges; i++) {
			int randInt = Util.randInt(0, challengeArrayList.size() - 1);
			Challenge challenge = challengeArrayList.get(randInt);
			challengeArrayList.remove(randInt);
			challenges.add(new Challenge(challenge.challengeName, challenge.challengeType, challenge.objectiveObjectTypes, challenge.counters));
		}
		return challenges;
	}
}
