package com.orinate.game.content.skills.impl.prayer;

/**
 * Represents an enumeration of prayer ashes types.
 * 
 * @author _Jordan / Apollo <citellumrsps@gmail.com>
 * Feb 6, 2014, 2014, 10:05:37 PM
 * 
 */
public enum AshDefinitions {

	IMPIOUS(20264, 56, 4), ACCURSED(20266, 47, 12.5), INFERNAL(20268, 40, 62.5);

	/**
	 * Represents the ashes item id.
	 */
	private final int item;

	/**
	 * Represents the graphic id of the scattering.
	 */
	private final int graphic;

	/**
	 * Represents the ashes experience when scattered.
	 */
	private final double experience;

	/**
	 * Constructs a new {@code AshDefinitions} {@code Object}.
	 * 
	 * @param item
	 *            the ashes.
	 * @param graphic
	 *            the graphic id of scattering ashes.
	 * @param experience
	 *            the ashes experience when scattered.
	 */
	AshDefinitions(int item, int graphic, double experience) {
		this.item = item;
		this.graphic = graphic;
		this.experience = experience;
	}

	/**
	 * Gets the item of the ashes.
	 * 
	 * @return the item id of the ashes.
	 */
	public int getItem() {
		return item;
	}

	/**
	 * Gets the graphic id of scattering.
	 * 
	 * @return the graphic id of the ashes being scattered.
	 */
	public int getGraphic() {
		return graphic;
	}

	/**
	 * Gets the experience of the ashes.
	 * 
	 * @return the experience gained when scattered.
	 */
	public double getExperience() {
		return experience;
	}

}
