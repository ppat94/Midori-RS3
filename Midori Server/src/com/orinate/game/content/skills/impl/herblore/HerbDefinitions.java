package com.orinate.game.content.skills.impl.herblore;

/**
 * The definitions for {@link HerbCleaning}.
 * 
 * @author _Jordan / Apollo <citellumrsps@gmail.com>
 * Feb 2, 2014, 2014, 10:04:55  PM
 * 
 */
public enum HerbDefinitions {

	GUAM(1, 199, 249, 2.5), TARROMIN(5, 203, 253, 3.8), MARRENTILL(9, 201, 251, 5), HARRALANDER(20, 205, 255, 6.3), RANARR(25, 207, 257, 7.5), TOADFLAX(30, 3049, 2998, 8), SPIRIT_WEED(35, 12174, 12172, 7.8), IRIT(40, 209, 259, 8.8), WERGALI(41, 14836, 14854, 9.5), AVANTOE(48, 211, 261, 10), KWUARM(54, 213, 263, 11.3), SNAPDRAGON(59, 3051, 3000, 11.8), CADANTINE(65, 215, 265, 12.5), LANTADYME(67, 2485, 2481, 13.1), DWARF_WEED(70, 217, 267, 13.8), TORSTOL(75, 219, 269, 15), FELLSTALK(91, 21626, 21624, 16.8);

	/**
	 * Represents the level at which to clean herbs.
	 */
	private final int level;
	
	/**
	 * Represents the grimy herb id.
	 */
	private final int grimy;

	/**
	 * Represents the clean herb id.
	 */
	private final int clean;

	/**
	 * Represents the experience gained per clean.
	 */
	private final double experience;

	/**
	 * Constructs a new {@code HerbDefinitions} {@code Object}.
	 * 
	 * @param level
	 *            the level of the herb.
	 * @param grimy
	 *            the grimy id of the herb.
	 * @param clean
	 *            the clean id of the herb.
	 */
	HerbDefinitions(int level, int grimy, int clean, double experience) {
		this.level = level;
		this.grimy = grimy;
		this.clean = clean;
		this.experience = experience;
	}

	/**
	 * Gets the level of the herb.
	 * 
	 * @return the level of the herb.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return the grimy
	 */
	public int getGrimy() {
		return grimy;
	}

	/**
	 * Gets the clean id of the herb.
	 * 
	 * @return the clean herb.
	 */
	public int getClean() {
		return clean;
	}

	/**
	 * Gets the experience.
	 * 
	 * @return the experience
	 */
	public double getExperience() {
		return experience;
	}
}
