package com.orinate.game.content.skills.impl.herblore;

import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.visual.Animation;

/**
 * Handles herb grinding and tar making.
 * 
 * @author _Jordan / Apollo <citellumrsps@gmail.com>
 * Feb 3, 2014, 2014, 4:33:34 PM
 * 
 */
public class HerbGrinding {

	/**
	 * Represents the pestle and mortar item.
	 */
	private final static int PESTLE_AND_MORTAR = 233;

	/**
	 * Represents the grinding animation.
	 */
	private final static int GRINDING_ANIMATION = 364;

	/**
	 * Grinds an ingredient item in the inventory.
	 * 
	 * @param player
	 *            the player.
	 * @param item
	 *            the item selected to grind.
	 * @param slotId
	 *            the slot of the inventory.
	 */
	public static void grindIngredient(Player player, Item item, int slotId) {
		final IngredientDefinitions definitions = getIngredient(item.getId());
		if (!player.getInventory().containsItem(PESTLE_AND_MORTAR, 1)) {
			player.getWriter().sendGameMessage("You need a pestle and mortar to grind this.");
			return;
		}
		player.lock(3);
		player.getAnimator().animate(Animation.create(GRINDING_ANIMATION));
		player.getInventory().deleteItem(slotId, item);
		player.getInventory().addItem(new Item(definitions.getProduct()));
		player.getSkills().addXp(Skills.HERBLORE, definitions.getExperience());
		player.getWriter().sendGameMessage("You grind the item using your pestle and mortar.");
	}

	/**
	 * Gets the ingredient to grind.
	 * 
	 * @param item
	 *            the item selected.
	 * @return the definitions.
	 */
	public static IngredientDefinitions getIngredient(int item) {
		for (final IngredientDefinitions definitions : IngredientDefinitions.values()) {
			if (definitions.getItem() == item)
				return definitions;
		}
		return null;
	}

}
