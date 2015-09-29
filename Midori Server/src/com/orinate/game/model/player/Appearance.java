package com.orinate.game.model.player;

import java.io.Serializable;

import com.orinate.cache.parsers.ItemDefinition;
import com.orinate.game.model.container.Item;
import com.orinate.io.OutBuffer;
import com.orinate.util.Utilities;

/**
 * @author Dragonkk
 */
public class Appearance implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final int[] DISABLED_SLOTS = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1 };

	public enum Gender {
		MALE, FEMALE
	}

	private transient Player player;
	private transient byte[] appearanceHash;
	private transient byte[] appearanceData;
	private int customRender = -1;

	private int[] look;
	private int[] colour;
	private Gender gender;
	private boolean showSkill;

	public Appearance(Player player) {
		this.player = player;
		this.look = new int[7];
		this.colour = new int[10];
		this.gender = Gender.MALE;
		this.set();
	}

	private void set() {
		look[0] = 268;
		look[1] = 14;
		look[2] = 459;
		look[3] = 26;
		look[4] = 34;
		look[5] = 646;
		look[6] = 440;
		colour[0] = 15;
		colour[1] = 71;
		colour[2] = -38;
		colour[3] = 4;
		colour[4] = 1;
		colour[5] = 0;
		colour[6] = 0;
		colour[7] = 0;
		colour[8] = 0;
		colour[9] = 0;
	}

	public void refresh() {
		OutBuffer update = new OutBuffer();
		int flagData = 0x0;

		if (gender.equals(Gender.FEMALE)) {
			flagData |= 0x1;
		}

		if (showSkill) {
			flagData |= 0x4;
		}

		/* Append flag data. */
		update.putByte((byte) flagData);

		/* hide player. */
		update.putByte((byte) 0);

		/* Handles first four equipment slots. */
		for (int index = 0; index < 4; index++) {
			Item item = player.getEquipment().get(index);
			if (item == null) {
				update.putByte(0);
			} else {
				update.putShort(0x4000 + item.getId());
			}
		}

		/* Append chest. */
		Item item = player.getEquipment().get(Equipment.SLOT_CHEST);
		if (item == null) {
			update.putShort(0x100 + look[2]);
		} else {
			update.putShort(0x4000 + item.getId());
		}

		/* Append Shield. */
		item = player.getEquipment().get(Equipment.SLOT_OFFHAND);
		if (item == null) {
			update.putByte(0);
		} else {
			update.putShort(0x4000 + item.getId());
		}

		/* Append Chest (Arm portion). */
		item = player.getEquipment().get(Equipment.SLOT_CHEST);
		if (item == null) {
			update.putShort(0x100 + look[3]);
		} else {
			update.putByte(0);
		}

		/* Append legs. */
		item = player.getEquipment().get(Equipment.SLOT_LEGS);
		if (item == null) {
			update.putShort(0x100 + look[5]);
		} else {
			update.putShort(0x4000 + item.getId());
		}

		/* Append hat. */
		item = player.getEquipment().get(Equipment.SLOT_HAT);
		if (item == null) {
			update.putShort(0x100 + look[0]);
		} else {
			update.putByte(0);
		}

		/* Append Hands. */
		item = player.getEquipment().get(Equipment.SLOT_HANDS);
		if (item == null) {
			update.putShort(0x100 + look[4]);
		} else {
			update.putShort(0x4000 + item.getId());
		}

		/* Append Feet. */
		item = player.getEquipment().get(Equipment.SLOT_FEET);
		if (item == null) {
			update.putShort(0x100 + look[6]);
		} else {
			update.putShort(0x4000 + item.getId());
		}

		/* Append Beard/Boobs. */
		item = player.getEquipment().get(gender.equals(Gender.MALE) ? Equipment.SLOT_HAT : Equipment.SLOT_CHEST);
		if (item == null || (gender.equals(Gender.MALE) && Equipment.showBeard(item))) {
			update.putShort(0x100 + look[1]);
		} else {
			update.putByte(0);
		}

		/* Append Aura. */
		item = player.getEquipment().get(Equipment.SLOT_AURA);
		if (item == null) {
			update.putByte(0);
		} else {
			update.putShort(0x4000 + item.getId());
		}

		/* Append Pocket. */
		item = player.getEquipment().get(Equipment.SLOT_POCKET);
		if (item == null) {
			update.putByte(0);
		} else {
			update.putShort(0x4000 + item.getId());
		}

		/* Append special appearance. */
		update.putByte((byte) 0);

		int initialPosition = update.offset();
		update.putShort(0);

		int hashData = 0, slotFlag = -1;
		for (int slotId = 0; slotId < 15; slotId++) {
			if (DISABLED_SLOTS[slotId] != 0) {
				continue;
			}
			slotFlag++;
			if ((slotId == Equipment.SLOT_ONHAND || slotId == Equipment.SLOT_OFFHAND) && player.getActionBar().isSheathing() && !player.getCombatSchedule().isCombatStance()) {
				item = player.getEquipment().get(slotId);
				if (item == null) {
					continue;
				}
				ItemDefinition def = item.getDefinitions();
				if (def == null) {
					continue;
				}
				int modelId = def.getModelOnBackId();
				hashData |= 1 << slotFlag;
				update.putByte(0x1);
				update.putBigSmart(modelId);
				update.putBigSmart(modelId);
				if (def.maleEquip1 == -1 || def.femaleEquip2 == -1) {
					continue;
				}
				update.putBigSmart(-1);
				update.putBigSmart(-1);
			} else if (slotId == Equipment.SLOT_CAPE) {
				Item cape = player.getEquipment().get(Equipment.SLOT_CAPE);
				if (cape == null) {
					continue;
				}

				if (cape.getId() != 20769 && cape.getId() != 20771 && cape.getId() != 20767) {
					continue;
				}

				hashData |= 1 << slotFlag;
				update.putByte(0x4);

				short[] capeColours = player.getCapeColours()[cape.getId() == 20767 ? 0 : 1];
				int slots = 0 | 1 << 4 | 2 << 8 | 3 << 12;

				update.putShort(slots);
				for (int curSlot = 0; curSlot < 4; curSlot++) {
					update.putShort(capeColours[curSlot]);
				}
			}
		}

		int currentPosition = update.offset();
		update.offset(initialPosition);

		update.putShort(hashData);
		update.offset(currentPosition);

		/* Write miscellaneous data. */
		for (int index = 0; index < colour.length; index++) {
			update.putByte((byte) colour[index]);
		}

		update.putShort(player.getRenderAnim());
		update.putString(player.getDefinition().getDisplayName());
		update.putByte((byte) player.getSkills().getCombatLevel());

		if (!showSkill) {
			update.putByte((byte) 0);
			update.putByte((byte) -1);
		} else {
			update.putShort(player.getSkills().getTotalLevel());
		}
		update.putByte((byte) 0);

		byte[] appeareanceData = new byte[update.offset()];
		System.arraycopy(update.buffer(), 0, appeareanceData, 0, appeareanceData.length);

		byte[] md5Hash = Utilities.encrypt(appeareanceData);
		this.appearanceData = appeareanceData;
		this.appearanceHash = md5Hash;
	}

	public Player getPlayer() {
		return player;
	}

	public byte[] getHash() {
		return appearanceHash;
	}

	public byte[] getData() {
		return appearanceData;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setShowSkillLevel(boolean showSkill) {
		this.showSkill = showSkill;
	}

	public boolean isMale() {
		return gender.equals(Gender.MALE);
	}

	/**
	 * @return the customRender
	 */
	public int getCustomRender() {
		return customRender;
	}

	/**
	 * @param customRender
	 *            the customRender to set
	 */
	public void setCustomRender(int customRender) {
		this.customRender = customRender;
	}
}
