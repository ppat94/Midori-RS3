package com.orinate.game.content.skills.impl.prayer;

/**
 * Represents an enumeration of prayer bone types.
 * 
 * @author _Jordan / Apollo <citellumrsps@gmail.com>
 * Feb 5, 2014, 2014, 10:29:21 PM
 * 
 */
public enum BoneDefinitions {

	BONE(526, 4.5), BURNT_BONE(528, 4.5), WOLF_BONE(2859, 4.5), BAT_BONE(530, 5.3), BIG_BONE(532, 15), DAGANNOTH_BONE(6729, 125), AIRUT_BONE(30209, 132.5), BABYDRAGON_BONE(534, 30), DRAGON_BONE(536, 72), WYVERN_BONE(6812, 50), FROST_DRAGON_BONE(18830, 180), MONKEY_BONE(3179, 5), JOGRE_BONE(3125, 15), BURNT_JOGRE_BONE(3127, 16), ZOGRE_BONE(4812, 22.5), FAYRG_BONE(4830, 84), RAURG_BONE(4832, 96), OURG_BONE(4834, 140), CURVED_BONE(10977, 30), LONG_BONE(10976, 15);

	/**
	 * Represents the bone item id.
	 */
	private final int item;

	/**
	 * Represents the experience gained.
	 */
	private final double experience;

	/**
	 * Constructs a new {@code BoneDefinitions} {@code Object}.
	 * 
	 * @param item
	 *            the bone.
	 * @param experience
	 *            the experience gained.
	 */
	BoneDefinitions(int item, double experience) {
		this.item = item;
		this.experience = experience;
	}

	/**
	 * Gets the item.
	 * 
	 * @return the item id of the bone.
	 */
	public int getItem() {
		return item;
	}

	/**
	 * Gets the experience.
	 * 
	 * @return the experience for bury.
	 */
	public double getExperience() {
		return experience;
	}

}
