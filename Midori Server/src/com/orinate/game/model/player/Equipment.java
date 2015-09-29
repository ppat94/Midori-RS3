package com.orinate.game.model.player;

import java.io.Serializable;

import com.orinate.cache.parsers.ItemDefinition;
import com.orinate.game.content.combat.CombatStyle;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.container.ItemContainer;

/**
 * @author Tom
 * 
 */
public class Equipment implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final byte SLOT_HAT = 0, SLOT_CAPE = 1, SLOT_AMULET = 2, SLOT_ONHAND = 3, SLOT_CHEST = 4, SLOT_OFFHAND = 5, SLOT_LEGS = 7, SLOT_HANDS = 9, SLOT_FEET = 10, SLOT_RING = 12, SLOT_ARROWS = 13, SLOT_AURA = 14, SLOT_POCKET = 15;
	private static final int EQUIP_SIZE = 16;

	private transient Player player;
	private ItemContainer<Item> items;

	public Equipment(Player player) {
		this.items = new ItemContainer<Item>(EQUIP_SIZE, false);
		this.player = player;
	}

	public int getItemRenderID(int id) {
		return ItemDefinition.forId(id).getRenderAnimId(player);
	}

	public int getItemEquipSlot(int id) {
		return ItemDefinition.forId(id).equipSlotId;
	}

	public void init() {
		player.getWriter().sendContainerUpdate(94, items);
		updateBonuses();
	}

	public void refresh(int... slots) {
		if (slots != null) {
			player.getWriter().sendUpdateItems(94, items, slots);
			updateBonuses(slots);
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Item get(int slot) {
		return items.get(slot);
	}

	public ItemContainer<Item> getItems() {
		return items;
	}

	public boolean is2h() {
		return isTwoHandedWeapon(get(3));
	}

	/**
	 * Gets the accuracy ratio.
	 * 
	 * @param style
	 *            The combat style used.
	 * @return The accuracy ratio.
	 */
	public double getAccuracyRatio(CombatStyle style) {
		double ratio = 1.0;
		for (Item item : items.toArray()) {
			if (item != null) {
				CombatStyle s = item.getDefinitions().getCombatStyle();
				if (s == null) {
					continue;
				}
				if (s != style) {
					ratio -= 0.05;
				}
			}
		}
		return ratio;
	}

	/**
	 * Gets the defence ratio.
	 * 
	 * @param style
	 *            The combat style to defend against.
	 * @return The defence ratio.
	 */
	public double getDefenceRatio(CombatStyle style) {
		double ratio = 1.0;
		for (Item item : items.toArray()) {
			if (item != null && !item.getDefinitions().isHybridType()) {
				CombatStyle s = item.getDefinitions().getCombatStyle();
				if (s == null) {
					continue;
				}
				if (s != style) {
					if ((s.ordinal() + 1) % 3 == style.ordinal()) {
						ratio -= 0.05;
					} else {
						ratio += 0.05;
					}
				}
			}
		}
		return ratio;
	}

	public static boolean isTwoHandedWeapon(Item item) {
		int itemId = item.getId();
		if (itemId == 4212)
			return true;
		else if (itemId == 4214)
			return true;
		else if (itemId == 20281)
			return true;
		String wepEquiped = item.getDefinitions().getName().toLowerCase();
		if (wepEquiped == null)
			return false;
		else if (wepEquiped.endsWith("anchor"))
			return true;
		else if (wepEquiped.contains("2h sword"))
			return true;
		else if (wepEquiped.contains("katana"))
			return true;
		else if (wepEquiped.equals("seercull"))
			return true;
		else if (wepEquiped.contains("shortbow"))
			return true;
		else if (wepEquiped.contains("longbow"))
			return true;
		else if (wepEquiped.contains("shortbow"))
			return true;
		else if (wepEquiped.contains("bow full"))
			return true;
		else if (wepEquiped.equals("zaryte bow"))
			return true;
		else if (wepEquiped.equals("dark bow"))
			return true;
		else if (wepEquiped.endsWith("halberd"))
			return true;
		else if (wepEquiped.contains("maul"))
			return true;
		else if (wepEquiped.equals("karil's crossbow"))
			return true;
		else if (wepEquiped.equals("torag's hammers"))
			return true;
		else if (wepEquiped.equals("verac's flail"))
			return true;
		else if (wepEquiped.contains("greataxe"))
			return true;
		else if (wepEquiped.contains("spear"))
			return true;
		else if (wepEquiped.equals("tzhaar-ket-om"))
			return true;
		else if (wepEquiped.contains("godsword"))
			return true;
		else if (wepEquiped.equals("saradomin sword"))
			return true;
		else if (wepEquiped.equals("hand cannon"))
			return true;
		return false;
	}

	/**
	 * Updates the bonuses.
	 * 
	 * @param slots
	 *            The slots refreshed.
	 */
	public void updateBonuses(int... slots) {
		Bonuses bonuses = player.getProperties().getBonuses().reset();
		for (int i = 0; i < items.getSize(); i++) {
			Item item = items.get(i);
			if (i == SLOT_ARROWS) {
				continue;
			}
			if (i == SLOT_ONHAND || i == SLOT_OFFHAND) {
				Bonuses b = null;
				if (item != null) {
					b = item.getDefinitions().getBonuses();
					bonuses.setLifepoints(bonuses.getLifepoints() + b.getLifepoints());
					bonuses.setArmour(bonuses.getArmour() + b.getArmour());
					bonuses.setPrayer(bonuses.getPrayer() + b.getPrayer());
				}
				player.getProperties().setWeaponBonus(i == SLOT_ONHAND ? 0 : 1, b);
			} else if (item != null) {
				bonuses.update(item.getDefinitions().getBonuses());
			}
		}
		if (slots.length < 1 || (slots[0] == SLOT_ONHAND || slots[0] == SLOT_OFFHAND)) {
			player.getCombatSchedule().setDefaultAttacks();
		}
	}

	public static int getItemSlot(int itemId) {
		return ItemDefinition.forId(itemId).equipSlotId;
	}

	public boolean hasShield() {
		return items.get(SLOT_OFFHAND) != null;
	}

	public static boolean hideArms(Item item) {
		String name = item.getDefinitions().getName().toLowerCase();
		if (name.contains("d'hide body") || name.contains("dragonhide body") || name.equals("stripy pirate shirt") || (name.contains("chainbody") && (name.contains("iron") || name.contains("bronze") || name.contains("steel") || name.contains("black") || name.contains("mithril") || name.contains("adamant") || name.contains("rune") || name.contains("white"))) || name.equals("leather body") || name.equals("hardleather body") || name.contains("studded body"))
			return false;
		return item.getDefinitions().equipId == 6;
	}

	public static boolean hideHair(Item item) {
		return item.getDefinitions().equipId == 8;
	}

	public static boolean showBeard(Item item) {
		String name = item.getDefinitions().getName().toLowerCase();
		return !hideHair(item) || name.contains("horns") || name.contains("hat") || name.contains("afro") || name.contains("cowl") || name.contains("tattoo") || name.contains("headdress") || name.contains("hood") || (name.contains("mask") && !name.contains("h'ween")) || (name.contains("helm") && !name.contains("full"));
	}

	public boolean isDual() {
		boolean hasOffhand = false;
		if (get(Equipment.SLOT_OFFHAND) != null) {
			Item item = player.getEquipment().get(Equipment.SLOT_OFFHAND);
			String name = item.getDefinitions().getName().toLowerCase();
			if (name.contains("off-hand") || name.contains("off hand") || name.contains("offhand") || name.contains("defender")) {
				hasOffhand = true;
			}
		}
		return get(SLOT_ONHAND) != null && hasOffhand;
	}

	public Item getItem(int slotId) {
		return items.get(slotId);
	}

	public int getWeaponId() {
		return getItem(SLOT_ONHAND) == null ? -1 : getItem(SLOT_ONHAND).getId();
	}

	public int getHeadId() {
		return getItem(SLOT_HAT) == null ? -1 : getItem(SLOT_HAT).getId();
	}

	public int getBodyId() {
		return getItem(SLOT_CHEST) == null ? -1 : getItem(SLOT_CHEST).getId();
	}

	public int getLegsId() {
		return getItem(SLOT_LEGS) == null ? -1 : getItem(SLOT_LEGS).getId();
	}

	public int getHandsId() {
		return getItem(SLOT_HANDS) == null ? -1 : getItem(SLOT_HANDS).getId();
	}

	public int getFeetId() {
		return getItem(SLOT_FEET) == null ? -1 : getItem(SLOT_FEET).getId();
	}

	public int getRingId() {
		return getItem(SLOT_RING) == null ? -1 : getItem(SLOT_RING).getId();
	}
	
	public int getCapeId() {
		return getItem(SLOT_CAPE) == null ? -1 : getItem(SLOT_CAPE).getId();
	}

	public void clear() {
		items.clear();
		init();
	}
}
