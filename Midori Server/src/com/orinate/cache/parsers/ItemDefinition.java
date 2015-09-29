package com.orinate.cache.parsers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import com.alex.io.InputStream;
import com.orinate.cache.Cache;
import com.orinate.game.content.combat.CombatStyle;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.player.Bonuses;
import com.orinate.game.model.player.Player;

/**
 * @author Tom
 * @author Tyler
 */
public class ItemDefinition {

	public static final int STAFF_WEAPON_TYPE = 7;
	public static final int BOW_WEAPON_TYPE = 8;
	public static final int CROSSBOW_WEAPON_TYPE = 9;
	public static final int THROWN_WEAPON_TYPE = 10;

	public HashMap<Integer, Object> params;
	public int anInt7867;
	public int[] anIntArray7869;
	public int anInt7871;
	public int anInt7874;
	public short[] originalModelColors;
	public int noteTemplateID;
	public String name;
	public String[] inventoryOptions;
	public short[] argbModelColors;
	public boolean aBool7880;
	public short[] textureColour1;
	public short[] textureColour2;
	public byte[] aByteArray7883;
	public byte[] aByteArray7884;
	public int equipId;
	public int modelRotation1;
	public int modelRotation2;
	public int anInt7888;
	public int modelOffset1;
	public int modelOffset2;
	public int anInt7891;
	public int value;
	public boolean membersOnly;
	public String[] groundOptions;
	public int interfaceModelId;
	public int[] anIntArray7897;
	public int anInt7898;
	public int equipSlotId;
	public int anInt7900;
	public int anInt7901;
	public int maleEquip1;
	public int maleEquip2;
	public int femaleEquip1;
	public int femaleEquip2;
	public int anInt7906;
	public int colourEquip2;
	public int anInt7908;
	public int[] stackIds;
	public int anInt7910;
	public int anInt7911;
	public int anInt7912;
	public int anInt7913;
	public String aString7914;
	public int anInt7915;
	public int anInt7916;
	public int anInt7917;
	public int anInt7918;
	public int[] stackAmounts;
	public int noteID;
	public int lendId;
	public int stackable;
	public int anInt7924;
	public int anInt7925;
	public int modelZoom;
	byte[] aByteArray7927;
	public int anInt7928;
	public int colourEquip1;
	public int anInt7930;
	public boolean aBool7931;
	public int teamId;
	public boolean unnoted;
	public int anInt7935;
	public int[] anIntArray7937;
	public int anInt7938;
	public int bindId;
	public int bindTemplateId;
	public int anInt7941;
	public int anInt7942;
	public int lendTemplateId;
	public int id;
	public boolean noted;
	public boolean lended;
	public boolean bound;
	private Bonuses bonuses;

	private static ItemDefinition[] itemDefinitions;
	private HashMap<Integer, Integer> itemRequiriments;

	public static void main(String[] args) throws IOException {
		Cache.init();
		BufferedWriter writer = new BufferedWriter(new FileWriter("protocol-information/Dumps/interfaces.txt"));
		for (int i = 0; i < IComponentDefinitions.getInterfaceDefinitionsSize(); i++) {
			IComponentDefinitions[] defs = IComponentDefinitions.getInterface(i);
			writer.write("INTERFACE: " + i + "");
			for (IComponentDefinitions def : defs) {
				if (def != null) {
					writer.write(def.aString542);
					writer.newLine();
					writer.write(def.aString560);
					writer.newLine();
					writer.write(def.aString561);
					writer.newLine();
					writer.write(def.aString632);
					writer.newLine();
					writer.write(def.aString649);
					writer.newLine();
					writer.write(def.aString658);
					writer.newLine();
				}
				writer.newLine();
			}
		}
		writer.close();
		// ItemDefinition def = ItemDefinition.forId(6739);
		// GeneralMapParser map = def.params.containsKey(686) ?
		// GeneralMapParser.getMap((int) def.params.get(686)) : null;
		// System.out.println(def.params);
	}

	// {2822=1, 686=14939, 2820=74185, 4=1486, 750=70, 749=4, 644=2588, 2195=30,
	// 2826=1, 14=12, 643=8925, 2868=40, 2870=2972, 21=62, 1326=1050, 2867=55,
	// 23=70, 2832=1, 2866=75}, {2853=8, 2914=18237, 2916=18348, 2955=2691,
	// 2917=18295, 2954=2698, 2918=18032}
	// {687=1, 686=14921, 3=1, 4=0, 750=60, 5=0, 749=0, 6=0, 7=0, 8=1, 644=2584,
	// 9=0, 2195=23, 11=0, 743=68, 641=400, 14=4, 3267=-4}, {2853=5, 2914=18225,
	// 2916=18347, 2955=2687, 2917=18292, 2954=2698, 2918=18025, 2831=18235}

	// {686=14940, 2820=74196, 4=1924, 750=80, 749=4, 644=2588, 2195=30, 2826=1,
	// 2946=2, 643=14700, 14=5, 528=Check-charges, 2281=14641, 23=80,
	// 2940=1066}, {2853=8, 2914=18237, 2916=18348, 2955=2691, 2917=18295,
	// 2954=2698, 2918=18032}
	// {686=14940, 2820=74203, 751=16, 4=1486, 750=70, 749=4, 644=2588, 2826=1,
	// 743=90, 14=5, 643=12863, 2281=14639, 23=70, 752=50, 2940=249}, {2853=8,
	// 2914=18237, 2916=18348, 2955=2691, 2917=18295, 2954=2698, 2918=18032}

	// {2640=19, 686=14939, 2820=90602, 2645=90, 4=1132, 750=60, 749=4,
	// 644=2588, 2195=30, 2826=1, 2655=29736, 14=5, 643=5265, 2656=1777,
	// 2697=1012, 21=62, 2696=19, 23=60, 2666=1, 2665=1, 2639=1}, {2853=8,
	// 2914=18237, 2916=18348, 2955=2691, 2917=18295, 2954=2698, 2918=18032}
	// {2640=19, 686=14939, 2820=74204, 2645=80, 4=850, 750=50, 749=4, 644=2588,
	// 2195=30, 2826=1, 2655=72, 14=5, 643=4388, 2656=1777, 2697=830, 21=62,
	// 2696=19, 23=50, 2666=1, 2665=1, 2639=1}, {2853=8, 2914=18237, 2916=18348,
	// 2955=2691, 2917=18295, 2954=2698, 2918=18032}

	public boolean hasOption(String option) {
		if (inventoryOptions == null)
			return false;

		for (String opt : inventoryOptions) {
			if (opt == null || opt.equalsIgnoreCase("null"))
				continue;

			if (opt.toLowerCase().equalsIgnoreCase(option.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public ItemDefinition(int id) {
		this.id = id;
		equipSlotId = -1;
		name = "null";
		maleEquip1 = -1;
		femaleEquip1 = -1;
		maleEquip2 = -1;
		femaleEquip2 = -1;
		modelZoom = 2000;
		lendId = -1;
		lendTemplateId = -1;
		noteID = -1;
		noteTemplateID = -1;
		bindId = -1;
		bindTemplateId = -1;
		anInt7930 = 128;
		value = 1;
		colourEquip1 = -1;
		colourEquip2 = -1;
		unnoted = false;
		aBool7880 = false;
		aBool7931 = false;
		params = new HashMap<Integer, Object>();
		groundOptions = new String[] { null, null, "take", null, null };
		inventoryOptions = new String[] { null, null, null, null, "drop" };
		loadItemDefinitions();
		if (noteTemplateID != -1) {
			toNote();
		}
		if (lendTemplateId != -1) {
			toLend();
		}
		if (bindTemplateId != -1) {
			toBind();
		}
	}

	public void loadItemDefinitions() {
		try {
			byte[] data = Cache.getStore().getIndexes()[19].getFile(getArchiveId(), getFileId());
			if (data == null) {
				return;
			}
			read(new InputStream(data));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void read(InputStream buffer) {
		for (;;) {
			int opcode = buffer.readUnsignedByte();
			if (opcode == 0) {
				break;
			}
			decode(buffer, opcode);
		}
	}

	private void decode(InputStream buffer, int opcode) {
		if (1 == opcode)
			interfaceModelId = buffer.readBigSmart();
		else if (2 == opcode)
			name = buffer.readString();
		else if (4 == opcode)
			modelZoom = buffer.readUnsignedShort();
		else if (5 == opcode)
			modelRotation1 = buffer.readUnsignedShort();
		else if (6 == opcode)
			modelRotation2 = buffer.readUnsignedShort();
		else if (opcode == 7) {
			modelOffset1 = buffer.readUnsignedShort();
			if (modelOffset1 > 32767)
				modelOffset1 -= 65536;
		} else if (8 == opcode) {
			modelOffset2 = buffer.readUnsignedShort();
			if (modelOffset2 > 32767)
				modelOffset2 -= 65536;
		} else if (opcode == 11)
			stackable = 1;
		else if (opcode == 12)
			value = buffer.readInt();
		else if (13 == opcode)
			equipSlotId = buffer.readUnsignedByte();
		else if (opcode == 14)
			equipId = buffer.readUnsignedByte();
		else if (16 == opcode)
			membersOnly = true;
		else if (opcode == 18)
			anInt7888 = buffer.readUnsignedShort();
		else if (opcode == 23)
			maleEquip1 = buffer.readBigSmart();
		else if (opcode == 24)
			maleEquip2 = buffer.readBigSmart();
		else if (25 == opcode)
			femaleEquip1 = buffer.readBigSmart();
		else if (opcode == 26)
			femaleEquip2 = buffer.readBigSmart();
		else if (opcode == 27)
			anInt7901 = buffer.readUnsignedByte();
		else if (opcode >= 30 && opcode < 35)
			groundOptions[opcode - 30] = buffer.readString();
		else if (opcode >= 35 && opcode < 40)
			inventoryOptions[opcode - 35] = buffer.readString();
		else if (40 == opcode) {
			int i_8_ = buffer.readUnsignedByte();
			originalModelColors = new short[i_8_];
			argbModelColors = new short[i_8_];
			for (int i_9_ = 0; i_9_ < i_8_; i_9_++) {
				originalModelColors[i_9_] = (short) buffer.readUnsignedShort();
				argbModelColors[i_9_] = (short) buffer.readUnsignedShort();
			}
		} else if (41 == opcode) {
			int i_10_ = buffer.readUnsignedByte();
			textureColour1 = new short[i_10_];
			textureColour2 = new short[i_10_];
			for (int i_11_ = 0; i_11_ < i_10_; i_11_++) {
				textureColour1[i_11_] = (short) buffer.readUnsignedShort();
				textureColour2[i_11_] = (short) buffer.readUnsignedShort();
			}
		} else if (opcode == 42) {
			int i_12_ = buffer.readUnsignedByte();
			aByteArray7927 = new byte[i_12_];
			for (int i_13_ = 0; i_13_ < i_12_; i_13_++)
				aByteArray7927[i_13_] = (byte) buffer.readByte();
		} else if (opcode == 43) {
			anInt7942 = buffer.readInt() * 1478264181;
			aBool7880 = true;
		} else if (44 == opcode) {
			int i_14_ = buffer.readUnsignedShort();
			int i_15_ = 0;
			for (int i_16_ = i_14_; i_16_ > 0; i_16_ >>= 1)
				i_15_++;
			aByteArray7883 = new byte[i_15_];
			byte i_17_ = 0;
			for (int i_18_ = 0; i_18_ < i_15_; i_18_++) {
				if ((i_14_ & 1 << i_18_) > 0) {
					aByteArray7883[i_18_] = i_17_;
					i_17_++;
				} else
					aByteArray7883[i_18_] = (byte) -1;
			}
		} else if (45 == opcode) {
			int i_19_ = buffer.readUnsignedShort();
			int i_20_ = 0;
			for (int i_21_ = i_19_; i_21_ > 0; i_21_ >>= 1)
				i_20_++;
			aByteArray7884 = new byte[i_20_];
			byte i_22_ = 0;
			for (int i_23_ = 0; i_23_ < i_20_; i_23_++) {
				if ((i_19_ & 1 << i_23_) > 0) {
					aByteArray7884[i_23_] = i_22_;
					i_22_++;
				} else
					aByteArray7884[i_23_] = (byte) -1;
			}
		} else if (65 == opcode)
			unnoted = true;
		else if (78 == opcode)
			colourEquip1 = buffer.readBigSmart();
		else if (79 == opcode)
			colourEquip2 = buffer.readBigSmart();
		else if (90 == opcode)
			anInt7891 = buffer.readBigSmart();
		else if (91 == opcode)
			anInt7916 = buffer.readBigSmart();
		else if (92 == opcode)
			anInt7915 = buffer.readBigSmart();
		else if (93 == opcode)
			anInt7917 = buffer.readBigSmart();
		else if (opcode == 94)
			anInt7900 = buffer.readUnsignedShort();
		else if (opcode == 95)
			anInt7898 = buffer.readUnsignedShort();
		else if (96 == opcode)
			anInt7935 = buffer.readUnsignedByte();
		else if (97 == opcode)
			noteID = buffer.readUnsignedShort();
		else if (opcode == 98)
			noteTemplateID = buffer.readUnsignedShort();
		else if (opcode >= 100 && opcode < 110) {
			if (stackIds == null) {
				stackIds = new int[10];
				stackAmounts = new int[10];
			}
			stackIds[opcode - 100] = buffer.readUnsignedShort();
			stackAmounts[opcode - 100] = buffer.readUnsignedShort();
		} else if (110 == opcode)
			anInt7928 = buffer.readUnsignedShort();
		else if (opcode == 111)
			anInt7867 = buffer.readUnsignedShort();
		else if (112 == opcode)
			anInt7930 = buffer.readUnsignedShort();
		else if (opcode == 113)
			anInt7941 = buffer.readByte();
		else if (opcode == 114)
			anInt7871 = buffer.readByte();
		else if (opcode == 115)
			teamId = buffer.readUnsignedByte();
		else if (121 == opcode)
			lendId = buffer.readUnsignedShort();
		else if (opcode == 122)
			lendTemplateId = buffer.readUnsignedShort();
		else if (opcode == 125) {
			anInt7908 = (buffer.readByte() << 2);
			anInt7910 = (buffer.readByte() << 2);
			anInt7912 = (buffer.readByte() << 2);
		} else if (opcode == 126) {
			anInt7924 = (buffer.readByte() << 2);
			anInt7911 = (buffer.readByte() << 2);
			anInt7913 = (buffer.readByte() << 2);
		} else if (opcode == 127 || 128 == opcode || 129 == opcode || opcode == 130) {
			buffer.readUnsignedByte();
			buffer.readUnsignedShort();
		} else if (132 == opcode) {
			int i_24_ = buffer.readUnsignedByte();
			anIntArray7937 = new int[i_24_];
			for (int i_25_ = 0; i_25_ < i_24_; i_25_++)
				anIntArray7937[i_25_] = buffer.readUnsignedShort();
		} else if (134 == opcode)
			anInt7938 = buffer.readUnsignedByte();
		else if (opcode == 139)
			bindId = buffer.readUnsignedShort();
		else if (opcode == 140)
			bindTemplateId = buffer.readUnsignedShort();
		else if (opcode >= 142 && opcode < 147) {
			if (anIntArray7897 == null) {
				anIntArray7897 = new int[6];
				Arrays.fill(anIntArray7897, -1);
			}
			anIntArray7897[opcode - 142] = buffer.readUnsignedShort();
		} else if (opcode >= 150 && opcode < 155) {
			if (null == anIntArray7869) {
				anIntArray7869 = new int[5];
				Arrays.fill(anIntArray7869, -1);
			}
			anIntArray7869[opcode - 150] = buffer.readUnsignedShort();
		} else if (156 != opcode) {
			if (opcode == 157)
				aBool7931 = true;
			else if (opcode == 161)
				anInt7918 = buffer.readUnsignedShort();
			else if (162 == opcode)
				anInt7925 = buffer.readUnsignedShort();
			else if (163 == opcode)
				anInt7906 = buffer.readUnsignedShort();
			else if (164 == opcode)
				aString7914 = buffer.readString();
			else if (opcode == 249) {
				int length = buffer.readUnsignedByte();
				for (int index = 0; index < length; index++) {
					boolean stringInstance = buffer.readUnsignedByte() == 1;
					int key = buffer.read24BitInt();
					Object val;
					if (stringInstance) {
						val = buffer.readString();
					} else {
						val = buffer.readInt();
					}
					params.put(key, val);
				}
			}
		}
	}

	public static int getSize() {
		int lastArchiveId = Cache.getStore().getIndexes()[19].getLastArchiveId();
		return (lastArchiveId * 256 + Cache.getStore().getIndexes()[19].getValidFilesCount(lastArchiveId));
	}

	public int getArchiveId() {
		return id >>> 8;
	}

	public int getFileId() {
		return 0xFF & id;
	}

	public static ItemDefinition forId(int id) {
		if (itemDefinitions == null)
			itemDefinitions = new ItemDefinition[getSize()];
		if (id < 0 || id > itemDefinitions.length)
			id = 0;
		ItemDefinition itemDef = itemDefinitions[id];
		if (itemDef == null)
			itemDefinitions[id] = itemDef = new ItemDefinition(id);
		return itemDef;
	}

	public static ItemDefinition forName(String name) {
		if (itemDefinitions == null) {
			itemDefinitions = new ItemDefinition[getSize()];
		}
		for (int id = 0; id < itemDefinitions.length; id++) {
			ItemDefinition itemDef = forId(id);
			if (itemDef == null) {
				continue;
			}
			String itemName = itemDef.getName().toLowerCase();
			if (name.equalsIgnoreCase(itemName)) {
				return itemDef;
			}
		}
		return null;
	}

	public static ItemDefinition forName(String name, boolean wear) {
		if (wear) {
			for (int i = 0; i < getSize(); i++) {
				ItemDefinition definition = ItemDefinition.forId(i);
				if (definition == null || definition.getName() == null) {
					continue;
				}
				if (definition.getName().equalsIgnoreCase(name) && (definition.isWearItem() && hasOption(definition, "Wear"))) {
					return definition;
				}
			}
		}
		return forName(name);
	}

	public static boolean hasOption(ItemDefinition definition, String string) {
		for (String opt : definition.inventoryOptions) {
			if (opt == null)
				continue;
			if (opt.toLowerCase().equalsIgnoreCase(string.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public boolean isWearItem() {
		return equipSlotId != -1;
	}

	public int getDefenceEmote() {
		try {
			if (params == null) {
				return 18292;
			}
			GeneralMapParser map = GeneralMapParser.getMap((int) params.get(686));
			long opcode = 2917;
			if (map.values.get(opcode) != null) {
				return (int) map.values.get(opcode);
			}
			return 18292;
		} catch (NullPointerException e) {
			return 18292;
		}
	}

	/**
	 * Checks if the bow uses charges rather than ammunition.
	 * 
	 * @return {@code True} if so.
	 */
	public boolean isChargeBow() {
		// TODO: Karil's crossbow.
		return getWeaponType() == BOW_WEAPON_TYPE && getProjectileId() > 0;
	}

	public int getWeaponType() {
		if (params == null || !params.containsKey(686)) {
			return -1;
		}
		GeneralMapParser map = GeneralMapParser.getMap((int) params.get(686));
		long opcode = 2853;
		if (map.values.get(opcode) != null) {
			return (int) map.values.get(opcode);
		}
		return -1;
	}

	public int getMainhandEmote() {
		if (params == null) {
			return 18226;
		}
		GeneralMapParser map = GeneralMapParser.getMap((int) params.get(686));
		if (map.values.get(2914L) != null) {
			return (int) map.values.get(2914L);
		}
		return 18224;
	}

	/**
	 * Gets the combat style.
	 * 
	 * @return The style.
	 */
	public CombatStyle getCombatStyle() {
		if (params == null) {
			return null;
		}
		if (params.containsKey(2821) || params.containsKey(2825)) {
			return CombatStyle.MELEE;
		} else if (params.containsKey(2822) || params.containsKey(2826)) {
			return CombatStyle.RANGE;
		} else if (params.containsKey(2823) || params.containsKey(2827)) {
			return CombatStyle.MAGIC;
		}
		System.out.println(params);
		return null;
	}

	/**
	 * Checks if the item is a hybrid of combat types.
	 * 
	 * @return {@code True} if so.
	 */
	public boolean isHybridType() {
		return params != null && (params.containsKey(2824) || params.containsKey(2828));
	}

	public int getOffhandEmote() {
		if (params == null || !params.containsKey(686)) {
			return -1;
		}
		GeneralMapParser map = GeneralMapParser.getMap((int) params.get(686));
		long opcode = 2831;
		if (map.values.get(opcode) != null) {
			return (int) map.values.get(opcode);
		}
		return -1; // 18239
	}

	public int getEndingEmote() {
		if (params == null) {
			return 18025;
		}
		GeneralMapParser map = GeneralMapParser.getMap((int) params.get(686));
		long opcode = 2918;
		if (map.values.get(opcode) != null) {
			return (int) map.values.get(opcode);
		}
		return 18025;
	}

	public int getProjectileId() {
		int opcode = 2940;
		if (params == null || !params.containsKey(opcode)) {
			return -1;
		}
		return (int) params.get(opcode);
	}

	public int getQuestId() {
		if (params == null) {
			return -1;
		}
		Object questId = params.get(861);
		if (questId != null && questId instanceof Integer) {
			return (Integer) questId;
		}
		return -1;
	}

	public HashMap<Integer, Object> getClientScriptData() {
		return (HashMap<Integer, Object>) params;
	}

	public int getHealAmount(int hp) {
		if (params.get(2645) == null) {
			return -1;
		}
		if (params.get(2951) == null) {
			return -1;
		}
		int cookingRequirement = (int) params.get(2951);
		return hp <= 12 || cookingRequirement == 1 ? 200 : hp >= cookingRequirement ? cookingRequirement * 20 : 16 * hp;
	}

	public int getSpeed() {
		if (params == null) {
			return 6;
		}
		return (int) params.get(14);
	}

	/**
	 * Checks if the item is a dark bow.
	 * 
	 * @return {@code True} if so.
	 */
	public boolean isDarkBow() {
		return getWeaponType() == BOW_WEAPON_TYPE && getSpeed() == 12;
	}

	public int getModelOnBackId() {
		if (params == null) {
			return -1;
		}
		Object modelId = params.get(2820);
		if (modelId != null && modelId instanceof Integer) {
			return (Integer) modelId;
		}
		return -1;
	}

	public boolean isNoted() {
		return noted;
	}

	public boolean isLent() {
		return lended;
	}

	public boolean isBind() {
		return bound;
	}

	public boolean isStackable() {
		if (isNoted()) {
			return true;
		}
		if (stackable == 1) {
			return true;
		}
		if (id == 9075 || id == 15243 || id >= 554 && id <= 566 || id >= 863 && id <= 869) {
			return true;
		}
		return false;
	}

	public double getArmourBonus() {
		int opcode = 2870;
		if (params == null || !params.containsKey(opcode)) {
			return 0;
		}
		int bonus = (int) params.get(opcode);
		if (bonus != 0) {
			return bonus * .1;
		}
		return 0.0;
	}

	public double getDamageBonus(CombatStyle style) {
		int opcode = 641 + style.ordinal();
		if (params == null || !params.containsKey(opcode)) {
			return 0;
		}
		int bonus = (int) params.get(opcode);
		if (bonus != 0) {
			return bonus * .1;
		}
		return 0.0;
	}

	public double getAccuracyBonus(CombatStyle s) {
		int opcode = 3267 + s.ordinal();
		if (params == null || !params.containsKey(opcode)) {
			return 0;
		}
		int bonus = (int) params.get(opcode);
		if (bonus != 0) {
			return bonus * .1;
		}
		return 0.0;
	}

	public double getCriticalBonus(CombatStyle style) {
		int opcode = 2833 + style.ordinal();
		if (params == null || !params.containsKey(opcode)) {
			return 0;
		}
		int bonus = (int) params.get(opcode);
		if (bonus != 0) {
			return bonus * .1;
		}
		return 0.0;
	}

	public int getLifeBonus() {
		int opcode = 1326;
		if (params == null || !params.containsKey(opcode)) {
			return 0;
		}
		return (int) params.get(opcode);
	}

	public int getPrayerBonus() {
		int opcode = 2946;
		if (params == null || !params.containsKey(opcode)) {
			return 0;
		}
		return (int) params.get(opcode);
	}

	public int getPassiveAnimation() {
		if (params == null) {
			return 2699;
		}
		GeneralMapParser map = GeneralMapParser.getMap((int) params.get(686));
		long opcode = 2954;
		if (map.values.get(opcode) != null) {
			return (int) map.values.get(opcode);
		}
		return 2699;
	}

	public int getAggressiveAnimation() {
		if (params == null) {
			return 2699;
		}
		GeneralMapParser map = GeneralMapParser.getMap((int) params.get(686));
		long opcode = 2955;
		if (map.values.get(opcode) != null) {
			return (int) map.values.get(opcode);
		}
		return 2699;
	}

	public int getRenderAnimId(Player player) {
		return getPassiveAnimation();
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	private void toBind() {
		ItemDefinition realItem = forId(bindId);
		originalModelColors = realItem.originalModelColors;
		colourEquip1 = realItem.colourEquip1;
		colourEquip2 = realItem.colourEquip2;
		teamId = realItem.teamId;
		value = 0;
		membersOnly = realItem.membersOnly;
		name = realItem.name;
		inventoryOptions = new String[5];
		groundOptions = realItem.groundOptions;
		if (realItem.inventoryOptions != null) {
			for (int optionIndex = 0; optionIndex < 4; optionIndex++) {
				inventoryOptions[optionIndex] = realItem.inventoryOptions[optionIndex];
			}
		}
		inventoryOptions[4] = "Discard";
		maleEquip1 = realItem.maleEquip1;
		maleEquip2 = realItem.maleEquip2;
		femaleEquip1 = realItem.femaleEquip1;
		femaleEquip2 = realItem.femaleEquip2;
		params = realItem.params;
		equipId = realItem.equipId;
		equipSlotId = realItem.equipSlotId;
		bound = true;
	}

	private void toLend() {
		ItemDefinition realItem = forId(lendId);
		originalModelColors = realItem.originalModelColors;
		colourEquip1 = realItem.colourEquip1;
		colourEquip2 = realItem.colourEquip2;
		teamId = realItem.teamId;
		value = 0;
		membersOnly = realItem.membersOnly;
		name = realItem.name;
		inventoryOptions = new String[5];
		groundOptions = realItem.groundOptions;
		if (realItem.inventoryOptions != null) {
			for (int optionIndex = 0; optionIndex < 4; optionIndex++) {
				inventoryOptions[optionIndex] = realItem.inventoryOptions[optionIndex];
			}
		}
		inventoryOptions[4] = "Discard";
		maleEquip1 = realItem.maleEquip1;
		maleEquip2 = realItem.maleEquip2;
		femaleEquip1 = realItem.femaleEquip1;
		femaleEquip2 = realItem.femaleEquip2;
		params = realItem.params;
		equipId = realItem.equipId;
		equipSlotId = realItem.equipSlotId;
		lended = true;
	}

	public boolean isLended() {
		return lended;
	}

	public boolean isDestroyItem() {
		if (inventoryOptions == null)
			return false;
		for (String option : inventoryOptions) {
			if (option == null)
				continue;
			if (option.equalsIgnoreCase("destroy"))
				return true;
		}
		return false;
	}

	public int getNotedId() {
		return noteID;
	}

	private void toNote() {
		ItemDefinition realItem = forId(noteID);
		membersOnly = realItem.membersOnly;
		value = realItem.value;
		name = realItem.name;
		stackable = 1;
		noted = true;
		params = realItem.params;
	}

	public HashMap<Integer, Integer> getWearingSkillRequiriments() {
		if (params == null) {
			return null;
		}
		if (itemRequiriments == null) {
			HashMap<Integer, Integer> skills = new HashMap<Integer, Integer>();
			for (int i = 0; i < 10; i++) {
				Integer skill = (Integer) params.get(749 + (i * 2));
				if (skill != null) {
					Integer level = (Integer) params.get(750 + (i * 2));
					if (level != null) {
						skills.put(skill, level);
					}
				}
			}
			Integer maxedSkill = (Integer) params.get(277);
			if (maxedSkill != null) {
				skills.put(maxedSkill, getId() == 19709 ? 120 : 99);
			}
			itemRequiriments = skills;
			if (getId() == 7462) {
				itemRequiriments.put(Skills.DEFENCE, 40);
			} else if (name.equals("Dragon defender")) {
				itemRequiriments.put(Skills.ATTACK, 60);
				itemRequiriments.put(Skills.DEFENCE, 60);
			}
		}

		return itemRequiriments;
	}

	/**
	 * @return the bonuses
	 */
	public Bonuses getBonuses() {
		if (bonuses == null) {
			bonuses = new Bonuses();
			bonuses.setArmour((int) getArmourBonus());
			bonuses.setLifepoints(getLifeBonus());
			bonuses.setPrayer(getPrayerBonus());
			int[][] data = new int[2][3];
			double[] critical = new double[3];
			for (CombatStyle s : CombatStyle.values()) {
				data[0][s.ordinal()] = (int) this.getDamageBonus(s);
				data[1][s.ordinal()] = (int) this.getAccuracyBonus(s);
				critical[s.ordinal()] = (int) this.getCriticalBonus(s);
			}
			bonuses.setDamage(data[0]);
			bonuses.setAccuracy(data[1]);
			bonuses.setCritical(critical);
		}
		return bonuses;
	}

	public int getValue() {
		return value;
	}
}
