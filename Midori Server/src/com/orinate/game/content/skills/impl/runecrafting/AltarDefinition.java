package com.orinate.game.content.skills.impl.runecrafting;

/**
 * Represents an enumeration of runecrafting altar types.
 * 
 * @author _Jordan / Apollo <citellumrsps@gmail.com>
 * Feb 1, 2014, 2014, 1:33:17  PM
 */
public enum AltarDefinition {
	AIR_ALTAR(1, 5, 556, 2478), MIND_ALTAR(2, 5.5, 558, 2479), WATER_ALTAR(5, 6, 555, 2480), EARTH_ALTAR(9, 6.5, 557, 2481), FIRE_ALTAR(14, 7, 554, 2482), BODY_ALTAR(20, 7.5, 559, 2483), COSMIC_ALTAR(27, 8, 564, 2484), CHAOS_ALTAR(35, 8.5, 562, 2487), ASTRAL_ALTAR(40, 8.7, 9075, 17010), NATURE_ALTAR(44, 9, 561, 2486), LAW_ALTAR(54, 9.5, 563, 2485), DEATH_ALTAR(65, 10, 560, 2488), BLOOD_ALTAR(77, 10.5, 565, 30624), OURANIA_ALTAR(1, 1, 1, 1); // TODO

	/**
	 * Represents the required level in order to craft runes.
	 */
	private final int level;

	/**
	 * Represents the experience gained from this altar.
	 */
	private final double experience;

	/**
	 * Represents the reward id of the product.
	 */
	private final int reward;
	
	/**
	 * Represents the object id of the altar.
	 */
	private final int object;

	/**
	 * Constructs a new {@code AltarDefinition} {@code Object}.
	 * 
	 * @param level
	 *            the level required.
	 * @param experience
	 *            the experience.
	 * @param reward
	 *            the reward id.
	 * @param object
	 * 			  the object id.
	 */
	AltarDefinition(int level, double experience, int reward, int object) {
		this.level = level;
		this.experience = experience;
		this.reward = reward;
		this.object = object;
	}

	/**
	 * Gets the level required.
	 * 
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Gets the experience gained.
	 * 
	 * @return the experience
	 */
	public double getExperience() {
		return experience;
	}

	/**
	 * Gets the reward id.
	 * 
	 * @return the reward
	 */
	public int getRewardId() {
		return reward;
	}

	/**
	 * Gets the object id.
	 * @return the object of the altar.
	 */
	public int getObject() {
		return object;
	}

}
