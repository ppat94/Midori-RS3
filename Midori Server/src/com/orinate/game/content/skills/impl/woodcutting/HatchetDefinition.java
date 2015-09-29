package com.orinate.game.content.skills.impl.woodcutting;

/**
 * Represents an enumeration of woodcutting hatchet types.
 * 
 * @author _Jordan / Apollo <citellumrsps@gmail.com>
 * @author Tom
 * Feb 2, 2014, 2014, 7:29:42  PM
 * 
 */
public enum HatchetDefinition {

	BRONZE(1351, 1, 1, 879), IRON(1349, 1, 2, 877), STEEL(1353, 6, 3, 875), BLACK(1361, 11, 4, 873), MITHRIL(1355, 21, 5, 871), ADAMANT(1357, 31, 7, 869), RUNE(1359, 41, 10, 867), DRAGON(6739, 61, 13, 21669);

	/**
	 * Represents the item id of the hatchet.
	 */
	private final int item;

	/**
	 * Represents the level at which to use the hatchet.
	 */
	private final int level;

	/**
	 * Represents the base time of the hatchet.
	 */
	private final int baseTime;

	/**
	 * Represents the animation of the hatchet.
	 */
	private final int animation;

	/**
	 * Cnnstructs a new {@code HatchetDefinition} {@code Object}.
	 * 
	 * @param item
	 *            the item id.
	 * @param level
	 *            the level required.
	 * @param baseTime
	 *            the base time.
	 * @param animation
	 *            the animation of the hatchet.
	 */
	private HatchetDefinition(int item, int level, int baseTime, int animation) {
		this.item = item;
		this.level = level;
		this.baseTime = baseTime;
		this.animation = animation;
	}

	/**
	 * Gets the item id of the hatchet.
	 * 
	 * @return the item id.
	 */
	public int getItemId() {
		return item;
	}

	/**
	 * Gets the required level of the hatchet to use.
	 * 
	 * @return the level.
	 */
	public int getRequiredLevel() {
		return level;
	}

	/**
	 * Gets the base time of the hatchet.
	 * 
	 * @return the base time.
	 */
	public int getBaseTime() {
		return baseTime;
	}

	/**
	 * Gets the hatchet animation.
	 * 
	 * @return the animation.
	 */
	public int getAnimation() {
		return animation;
	}
}