package com.orinate.game.content.skills.impl.herblore;

import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.player.Player;

/**
 * Handles cleaning herbs.
 * 
 * @author _Jordan / Apollo <citellumrsps@gmail.com>
 * Feb 2, 2014, 2014, 10:27:12 PM
 * 
 */
public class HerbCleaning {

	/**
	 * Represents a method to clean herbs in the player's inventory.
	 * 
	 * @param player
	 *            the player.
	 * @param item
	 *            the item selected to clean.
	 * @param slotId
	 *            the slot id of the inventory.
	 */
	public static void cleanHerb(Player player, Item item, int slotId) {
		final HerbDefinitions definitions = getHerb(item.getId());
		if (player.getSkills().getLevel(Skills.HERBLORE) < definitions.getLevel()) {
			player.getWriter().sendGameMessage("You need a Herblore level of " + definitions.getLevel() + " to clean this herb.");
			return;
		}
		player.getInventory().deleteItem(slotId, item);
		player.getInventory().addItem(new Item(definitions.getClean()));
		player.getSkills().addXp(Skills.HERBLORE, definitions.getExperience());
		player.getWriter().sendGameMessage("You clean the herb.");
	}

	/**
	 * Gets the herb based on item click.
	 * 
	 * @param item
	 *            the herb.
	 * @return the definitions of the herb.
	 */
	public static HerbDefinitions getHerb(int item) {
		for (final HerbDefinitions definitions : HerbDefinitions.values()) {
			if (definitions.getGrimy() == item)
				return definitions;
		}
		return null;
	}

}
