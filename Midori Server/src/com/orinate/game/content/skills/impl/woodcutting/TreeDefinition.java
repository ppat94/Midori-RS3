package com.orinate.game.content.skills.impl.woodcutting;

/**
 * An enumeration representing tree information.
 * 
 * @author _Jordan / Apollo <citellumrsps@gmail.com>
 * @author Tom Feb 2, 2014, 2014, 7:22:42 PM
 * 
 */
public enum TreeDefinition {
	NORMAL_TREE(1, 25, 1511, 20, 4, 1341, 8, 0), EVERGREEN_TREE(1, 25, 1511, 20, 4, 57931, 8, 0), DEAD_TREE(1, 25, 1511, 20, 4, 12733, 8, 0), OAK_TREE(15, 37.5, 1521, 30, 4, 38741, 15, 15), WILLOW_TREE(30, 67.5, 1519, 60, 4, 38725, 51, 15),
	MAPLE_TREE(45, 100, 1517, 83, 16, 31057, 72, 10), YEW_TREE(60, 175, 1515, 120, 17, 78118, 94, 10), IVY_TREE(68, 332.5, -1, 120, 17, 46319, 58, 10), MAGIC_TREE(75, 250, 1513, 150, 21, 37824, 121, 10), CURSED_MAGIC_TREE(82, 250, 1513, 150, 21, 37822, 121, 10);

	/**
	 * Represents the required level in order to chop trees.
	 */
	private final int level;

	/**
	 * Represents the experience a player recieves per log.
	 */
	private final double experience;

	/**
	 * Represents the log chopped from tree.
	 */
	private final int logId;

	/**
	 * Represents the base time of the tree.
	 */
	private final int baseTime;

	/**
	 * Represents a random time until tree is chopped.
	 */
	private final int randomTime;

	/**
	 * Represents the stump for the tree.
	 */
	private final int stumpId;

	/**
	 * Represents the tree respawn delay time.
	 */
	private final int respawnDelay;

	/**
	 * Represents how long the life of the tree will last.
	 */
	private final int lifeProbability;

	/**
	 * Constructs a new {@code TreeDefinition} {@code Object}.
	 * 
	 * @param level
	 *            the level required.
	 * @param experience
	 *            the experience per log.
	 * @param logId
	 *            the log id.
	 * @param baseTime
	 *            the base time.
	 * @param randomTime
	 *            the random time probability.
	 * @param stumpId
	 *            the stump id.
	 * @param respawnDelay
	 *            the respawn delay.
	 * @param lifeProbability
	 *            the life probability.
	 */
	private TreeDefinition(int level, double experience, int logId, int baseTime, int randomTime, int stumpId, int respawnDelay, int lifeProbability) {
		this.level = level;
		this.experience = experience;
		this.logId = logId;
		this.baseTime = baseTime;
		this.randomTime = randomTime;
		this.stumpId = stumpId;
		this.respawnDelay = respawnDelay;
		this.lifeProbability = lifeProbability;
	}

	/**
	 * Gets the level of the tree.
	 * 
	 * @return the level.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Gets the experience gained per chop.
	 * 
	 * @return the experience.
	 */
	public double getExperience() {
		return experience;
	}

	/**
	 * Gets the log chopped from the tree.
	 * 
	 * @return the log id.
	 */
	public int getLogId() {
		return logId;
	}

	/**
	 * Gets the base time of the tree.
	 * 
	 * @return the base time.
	 */
	public int getBaseTime() {
		return baseTime;
	}

	/**
	 * Gets the random time of the tree.
	 * 
	 * @return the random time.
	 */
	public int getRandomTime() {
		return randomTime;
	}

	/**
	 * Gets the stump id of the tree once chopped.
	 * 
	 * @return the stump id.
	 */
	public int getStumpId() {
		return stumpId;
	}

	/**
	 * Gets the respawn delay of the tree.
	 * 
	 * @return the respawn delay.
	 */
	public int getRespawnDelay() {
		return respawnDelay;
	}

	/**
	 * Gets the life probability of the tree.
	 * 
	 * @return the probability.
	 */
	public int getLifeProbability() {
		return lifeProbability;
	}
}