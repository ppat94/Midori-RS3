package com.orinate.game.content.skills.impl.fletching;

import com.orinate.cache.parsers.ItemDefinition;
import com.orinate.game.content.skills.Skill;
import com.orinate.game.model.player.Player;

/**
 * @author Tom
 * 
 */
public class Fletching extends Skill {

	@SuppressWarnings("unused")
	private FletchType type;

	public Fletching(Player player, FletchType type) {
		super(player);
		this.type = type;
	}

	public static Fletching getFletching(Player player, int primary, int secondary) {
		FletchType type = null;
		if ((primary == 946 || secondary == 946) && (isLog(primary)) || isLog(secondary)) {
			type = FletchType.CUT;
		} else if ((isUncutBow(primary) || isUncutBow(secondary)) || (primary == 1777 || secondary == 1777)) {
			type = FletchType.STRING;
		}

		if (type != null) {
			return new Fletching(player, type);
		}
		return null;
	}

	private static boolean isUncutBow(int itemId) {
		ItemDefinition itemDef = ItemDefinition.forId(itemId);
		if (itemDef == null) {
			return false;
		}

		String name = itemDef.name;
		if (name == null || name.equalsIgnoreCase("null")) {
			return false;
		}

		return name.contains("longbow (u)") || name.contains("shortbow (u)");
	}

	private static boolean isLog(int itemId) {
		ItemDefinition itemDef = ItemDefinition.forId(itemId);
		if (itemDef == null) {
			return false;
		}

		String name = itemDef.name;
		if (name == null || name.equalsIgnoreCase("null")) {
			return false;
		}

		return name.contains("logs");
	}

	@Override
	public boolean onSkillStart() {

		return false;
	}

	@Override
	public boolean canProcess() {
		if (!player.getInventory().hasFreeSlots()) {
			player.getWriter().sendGameMessage("Not enough free space in your inventory.");
			return false;
		}
		return true;
	}

	@Override
	public boolean onProcess() {
		return false;
	}

	@Override
	public void onSkillEnd() {
	}

	@Override
	public int getSkillDelay() {
		return 0;
	}
}
