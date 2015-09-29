package com.orinate.game.content.skills.impl.herblore;

/**
 * Definitions for {@link HerbGrinding}.
 * 
 * @author _Jordan / Apollo <citellumrsps@gmail.com>
 * Feb 3, 2014, 2014, 4:38:40 PM
 * 
 */
public enum IngredientDefinitions {
	
	UNICORN_HORN(237, 235, 1),
	KEBBIT_TEETH(10109, 10111, 1),
	GORAK_CLAWA(9016, 9018, 1),
	BIRD_NEST(5075, 6693, 1),
	DESERT_GOAT_HORN(9735, 9736, 1),
	BLUE_DRAGON_SCALES(243, 241, 1);

	/**
	 * Represents the item needed.
	 */
	private final int item;

	/**
	 * Represents the product of the ingredient.
	 */
	private final int product;

	/**
	 * Represents the experience gained.
	 */
	private final double experience;

	/**
	 * Constructs a new {@code IngredientDefinitions} {@code Object}.
	 * 
	 * @param item
	 *            the item id of the ingredient.
	 * @param product
	 *            the product item of the ingredient.
	 * @param experience
	 *            the experience gained.
	 */
	IngredientDefinitions(int item, int product, double experience) {
		this.item = item;
		this.product = product;
		this.experience = experience;
	}

	/**
	 * Gets the item.
	 * 
	 * @return the item requirement to make.
	 */
	public int getItem() {
		return item;
	}

	/**
	 * Gets the product of the ingredient.
	 * 
	 * @return the product of the ingredient.
	 */
	public int getProduct() {
		return product;
	}

	/**
	 * Gets the experience.
	 * 
	 * @return the experience gained.
	 */
	public double getExperience() {
		return experience;
	}
}
