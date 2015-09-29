package com.orinate.game.content.skills.impl.mining;

/**
 * 
 * @author Trenton
 * 
 */
public enum ProlongedMiningLocation {

	RUNE_ESSENCE(1, 5, 1436, 5, 0), PURE_ESSENCE(30, 5, 7936, 5, 0), LAVA_FLOW(68, 85, -1, 30, 20), CONCENTRATED_COAL(77, 50, 453, 20, 15), CONCENTRATED_GOLD(80, 65, 444, 30, 10), RED_SANDSTONE(81, 70, 23194, 30, 20);

	private int level;
	private double xp;
	private int oreId;
	private int oreBaseTime;
	private int oreRandomTime;

	private ProlongedMiningLocation(int level, double xp, int oreId, int oreBaseTime, int oreRandomTime) {
		this.level = level;
		this.xp = xp;
		this.oreId = oreId;
		this.oreBaseTime = oreBaseTime;
		this.oreRandomTime = oreRandomTime;
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
}
