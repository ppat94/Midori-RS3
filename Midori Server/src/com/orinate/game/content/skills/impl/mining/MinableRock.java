package com.orinate.game.content.skills.impl.mining;

/**
 * 
 * @author Trenton
 * 
 */
public enum MinableRock {

	CLAY(1, 5, 434, 10, 1, 11552, 5), COPPER(1, 17.5, 436, 10, 1, 11552, 5), TIN(1, 17.5, 438, 15, 1, 11552, 5), IRON(15, 35, 440, 15, 1, 11552, 10), SANDSTONE(35, 30, 6971, 30, 1, 11552, 10), SILVER(20, 40, 442, 25, 1, 11552, 20), COAL(30, 50, 453, 50, 10, 11552, 30), GRANITE(45, 50, 6979, 50, 10, 11552, 20), GOLD(40, 60, 444, 80, 20, 11554, 40), MITHRIL(55, 80, 447, 100, 20, 11552, 60), ADAMANT(70, 95, 449, 130, 25, 11552, 180), RUNITE(85, 125, 451, 150, 30, 11552, 360), BANE(77, 90, 21778, 130, 20, 11552, 50);

	private int level;
	private double xp;
	private int oreId;
	private int oreBaseTime;
	private int oreRandomTime;
	private int minedOutObject;
	private int respawnDelay;

	private MinableRock(int level, double xp, int oreId, int oreBaseTime, int oreRandomTime, int minedOutObject, int respawnDelay) {
		this.level = level;
		this.xp = xp;
		this.oreId = oreId;
		this.oreBaseTime = oreBaseTime;
		this.oreRandomTime = oreRandomTime;
		this.minedOutObject = minedOutObject;
		this.respawnDelay = respawnDelay;
	}

	public int getLevel() {
		return level;
	}

	public double getXp() {
		return xp;
	}

	public int getOreId() {
		return oreId;
	}

	public int getOreBaseTime() {
		return oreBaseTime;
	}

	public int getOreRandomTime() {
		return oreRandomTime;
	}

	public int getMinedOutObjectId() {
		return minedOutObject;
	}

	public int getRespawnDelay() {
		return respawnDelay;
	}
}
