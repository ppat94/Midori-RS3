package com.orinate.game.content.skills.impl.runecrafting;

import com.orinate.cache.parsers.ItemDefinition;
import com.orinate.game.content.skills.Skill;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.content.skills.impl.runecrafting.ResourceItems.TalismanDefinitions;
import com.orinate.game.content.skills.impl.runecrafting.ResourceItems.TalismanStaffDefinitions;
import com.orinate.game.content.skills.impl.runecrafting.ResourceItems.TiaraDefinitions;
import com.orinate.game.model.Location;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.region.WorldObject;
import com.orinate.game.model.update.masks.Graphics;
import com.orinate.game.model.visual.Animation;

/**
 * Handles the runecrafting skill.
 * 
 * @author _Jordan / Apollo <citellumrsps@gmail.com>
 * Jan 31, 2014, 2014, 5:42:33 PM
 * 
 */
public class Runecrafting extends Skill {

	/**
	 * The {@code WorldObject} instance.
	 */
	private final WorldObject altarObject;

	/**
	 * The {@code AltarDefinition} instance.
	 */
	private final AltarDefinition altar;

	/**
	 * Represents the rune essence item id.
	 */
	private final int RUNE_ESSENCE = 1436;

	/**
	 * Represents the pure essense item id.
	 */
	private final int PURE_ESSENCE = 7936;

	/**
	 * Constructs a new {@code Runecrafting} {@code Object}.
	 * 
	 * @param player
	 *            the player.
	 * @param altarObject
	 *            the altar clicked.
	 * @param altar
	 *            the altar.
	 */
	public Runecrafting(Player player, WorldObject altarObject, AltarDefinition altar) {
		super(player);
		this.altarObject = altarObject;
		this.altar = altar;
	}

	@Override
	public boolean onSkillStart() {
		if (!player.getInventory().containsItem(RUNE_ESSENCE, 1) && !player.getInventory().containsItem(PURE_ESSENCE, 1)) {
			player.getWriter().sendGameMessage("You do not have any essence to craft these runes.");
			return false;
		}
		if (player.getSkills().getLevel(Skills.RUNECRAFTING) < altar.getLevel()) {
			player.getWriter().sendGameMessage("You do not have the required level to craft these runes.");
			return false;
		}
		return true;
	}

	@Override
	public boolean canProcess() {
		if (!player.getInventory().containsItem(RUNE_ESSENCE, 1) && !player.getInventory().containsItem(PURE_ESSENCE, 1))
			return false;
		return true;
	}

	@Override
	public boolean onProcess() {
		final int runeEss = player.getInventory().getNumberOf(RUNE_ESSENCE);
		final int pureEss = player.getInventory().getNumberOf(PURE_ESSENCE);
		final int totalAmount = player.getInventory().getNumberOf(RUNE_ESSENCE) + player.getInventory().getNumberOf(PURE_ESSENCE);
		double experience = totalAmount * altar.getExperience();
		if (wearingRunecraftingSuit())
			experience = experience * 1.25;
		player.lock(3);
		player.getInventory().deleteItem(RUNE_ESSENCE, runeEss);
		player.getInventory().deleteItem(PURE_ESSENCE, pureEss);
		player.getInventory().addItem(altar.getRewardId(), totalAmount * 5);
		player.getSkills().addXp(Skills.RUNECRAFTING, experience);
		player.getAnimator().animate(Animation.create(791));
		player.setGraphics(new Graphics(186));
		player.getWriter().sendGameMessage("You bind the temple's power into " + ItemDefinition.forId(altar.getRewardId()).getName().toLowerCase() + "s.");
		return true;
	}

	/**
	 * If the player is wearing the runecrafting suit.
	 * 
	 * @return if player is wearing the runecrafting suit.
	 */
	private boolean wearingRunecraftingSuit() {
		if (player.getEquipment().getHeadId() == 21485 && player.getEquipment().getBodyId() == 21484 && player.getEquipment().getLegsId() == 21486 && player.getEquipment().getFeetId() == 21487)
			return true;
		return false;
	}

	@Override
	public void onSkillEnd() {
	}

	@Override
	public int getSkillDelay() {
		return 0;
	}

	/**
	 * Gets the altar.
	 * 
	 * @return the altarObject
	 */
	public WorldObject getAltar() {
		return altarObject;
	}

	/**
	 * Gets the altar object id.
	 * 
	 * @param object
	 *            the altar object.
	 * @return the definitions of the altar.
	 */
	public static AltarDefinition getAltar(int object) {
		for (final AltarDefinition definitions : AltarDefinition.values()) {
			if (definitions.getObject() == object)
				return definitions;
		}
		return null;
	}

	/**
	 * Gets the tiara the player must have based on the temple clicked.
	 * 
	 * @param object
	 *            the temple.
	 * @return the definitions of the tiara.
	 */
	public static TiaraDefinitions getTiara(int object) {
		for (final TiaraDefinitions definitions : TiaraDefinitions.values()) {
			if (definitions.getObject() == object)
				return definitions;
		}
		return null;
	}

	/**
	 * Gets the talisman the player must have based on the temple clicked.
	 * 
	 * @param object
	 *            the temple.
	 * @return the definitions of the talisman.
	 */
	public static TalismanDefinitions getTalisman(int object) {
		for (final TalismanDefinitions definitions : TalismanDefinitions.values()) {
			if (definitions.getObject() == object)
				return definitions;
		}
		return null;
	}

	/**
	 * Gets the talisman staff the player must have based on the temple clicked.
	 * 
	 * @param object
	 *            the temple.
	 * @return the definitions of the talisman staff.
	 */
	public static TalismanStaffDefinitions getStaff(int object) {
		for (final TalismanStaffDefinitions definitions : TalismanStaffDefinitions.values()) {
			if (definitions.getObject() == object)
				return definitions;
		}
		return null;
	}

	/**
	 * Sends the player to enter a temple.
	 * 
	 * @param player
	 *            the player.
	 * @param object
	 *            the temple.
	 * @return if nothing added was went.
	 */
	public static boolean enterTemple(Player player, int object) {
		TiaraDefinitions tiara = getTiara(object);
		TalismanDefinitions talisman = getTalisman(object);
		TalismanStaffDefinitions staff = getStaff(object);
		if (checkPlayer(player, tiara, talisman, staff)) {
			switch (object) {
			case 2452:
				player.setNextLocation(new Location(2841, 4829, 0));
				break;
			case 2453:
				player.setNextLocation(new Location(2792, 4827, 0));
				break;
			case 2454:
				player.setNextLocation(new Location(3482, 4838, 0));
				break;
			case 2455:
				player.setNextLocation(new Location(2655, 4830, 0));
				break;
			case 2456:
				player.setNextLocation(new Location(2574, 4848, 0));
				break;
			case 2457:
				player.setNextLocation(new Location(2523, 4833, 0));
				break;
			case 2458:
				player.setNextLocation(new Location(2162, 4833, 0));
				break;
			case 2459:
				player.setNextLocation(new Location(2464, 4834, 0));
				break;
			case 2460:
				player.setNextLocation(new Location(2398, 4841, 0));
				break;
			case 2461:
				player.setNextLocation(new Location(2269, 4843, 0));
				break;
			}
		}
		return true;
	}

	/**
	 * Checks if the player is able to enter the temple.
	 * 
	 * @param player
	 *            the player.
	 * @param tiara
	 *            the tiara representing the temple.
	 * @return if able to enter.
	 */
	private static boolean checkPlayer(Player player, TiaraDefinitions tiara, TalismanDefinitions talisman, TalismanStaffDefinitions staff) {
		if (!(player.getEquipment().getHeadId() == tiara.getItem()) && !player.getInventory().containsItem(talisman.getItem(), 1) && player.getEquipment().getWeaponId() != staff.getItem()) {
			player.getWriter().sendGameMessage("You feel an oninous power emitting from the mysterious ruins.");
			return false;
		}
		return true;
	}

}
