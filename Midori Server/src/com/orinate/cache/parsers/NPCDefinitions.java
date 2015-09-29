package com.orinate.cache.parsers;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.alex.io.InputStream;
import com.orinate.cache.Cache;

public final class NPCDefinitions {

	private static final ConcurrentHashMap<Integer, NPCDefinitions> npcDefinitions = new ConcurrentHashMap<Integer, NPCDefinitions>();

	private int id;
	public HashMap<Integer, Object> clientScriptData;
	public int anInt833;
	public int anInt836;
	public int anInt837;
	public byte respawnDirection;
	public int size = 1;
	public int[][] anIntArrayArray840;
	public boolean aBoolean841;
	public int anInt842;
	public int bConfig;
	public int[] transformTo;
	public int anInt846;
	public int renderEmote;
	public boolean aBoolean849 = false;
	public int anInt850;
	public byte aByte851;
	public boolean aBoolean852;
	public int anInt853;
	public byte aByte854;
	public boolean aBoolean856;
	public boolean aBoolean857;
	public short[] aShortArray859;
	public int combatLevel;
	public byte[] aByteArray861;
	public short aShort862;
	public boolean aBoolean863;
	public int anInt864;
	public String name;
	public short[] aShortArray866;
	public byte walkMask;
	public int[] modelIds;
	public int anInt869;
	public int anInt870;
	public int anInt871;
	public int anInt872;
	public int anInt874;
	public int anInt875;
	public int anInt876;
	public int headIcons;
	public int anInt879;
	public short[] aShortArray880;
	public int[][] anIntArrayArray882;
	public int anInt884;
	public int[] anIntArray885;
	public int config;
	public int anInt889;
	public boolean isVisibleOnMap;
	public int[] anIntArray892;
	public short aShort894;
	public String[] options;
	public short[] aShortArray896;
	public int anInt897;
	public int anInt899;
	public int npcId;
	public int anInt901;
	public boolean aBoolean3190;

	public short aShort5849;
	public static short[] aShortArray5851 = new short[256];
	public int anInt5853;
	public String aString5854 = "null";
	public int anInt5855 = 619962765;
	public int[] anIntArray5856;
	public int[] anIntArray5857;
	int[][] anIntArrayArray5858;
	public int anInt5859 = -2062510325;
	short[] aShortArray5860;
	byte[] aByteArray5862;
	short[] aShortArray5863;
	public short[] aShortArray5864;
	public byte[] aByteArray5865;
	public byte[] aByteArray5866;
	byte aByte5867;
	public int anInt5868;
	byte aByte5869;
	public byte aByte5870;
	public boolean aBool5871;
	int[] anIntArray5872;
	public int anInt5873;
	public boolean aBool5874;
	public int anInt5875;
	public int anInt5876;
	byte aByte5877;
	public boolean aBool5878;
	public boolean aBool5879;
	public boolean aBool5880;
	int anInt5881;
	public int anInt5882;
	public int[] anIntArray5883;
	public short[] aShortArray5884;
	public int anInt5885;
	public int anInt5886;
	public int[] anIntArray5887;
	public int anInt5888;
	int anInt5889;
	public boolean aBool5890;
	public int anInt5891;
	public boolean aBool5892;
	public short aShort5893;
	public int anInt5894;
	public byte aByte5895;
	public short[] aShortArray5896;
	public int anInt5897;
	public boolean aBool5898;
	public byte aByte5899;
	int anInt5900;
	byte aByte5901 = 0;
	int anInt5902;
	public short aShort5903;
	public int anInt5904;
	int anInt5905;
	public int anInt5907;
	public int anInt5908;
	public String[] aStringArray5910;
	public int[] anIntArray5913;
	public byte aByte5914;
	int anInt5915;
	public int anInt5917;
	public int anInt5918;
	public byte aByte5919;
	public int anInt5920;
	public int anInt5921;

	public static void main(String[] args) throws IOException {
		Cache.init();

		for (int i = 0; i < 20000; i++) {
			NPCDefinitions def = forId(i);
			if (def == null)
				continue;
			if (i != 50)
				continue;

			System.out.println(i + "=" + def.clientScriptData);
		}
	}

	public static final NPCDefinitions forId(int id) {
		NPCDefinitions def = npcDefinitions.get(id);
		if (def == null) {
			def = new NPCDefinitions(id);
			def.method694();
			byte[] data = Cache.STORE.getIndexes()[18].getFile(id >>> 134238215, id & 0x7f);
			if (data == null) {
				// System.out.println("Failed loading NPC " + id + ".");
			} else
				def.readValueLoop(new InputStream(data));
			npcDefinitions.put(id, def);
		}
		return def;
	}

	public void method694() {
		if (modelIds == null)
			modelIds = new int[0];
	}

	private void readValueLoop(InputStream stream) {
		while (true) {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				break;
			readValues(stream, opcode);
		}
	}

	private void readValues(InputStream stream, int opcode) {
		if (1 == opcode) {
			int i_11_ = stream.readUnsignedByte();
			anIntArray5856 = new int[i_11_];
			for (int i_12_ = 0; i_12_ < i_11_; i_12_++)
				anIntArray5856[i_12_] = stream.readBigSmart();
		} else if (2 == opcode) {
			name = stream.readString();
		} else if (opcode == 12)
			size = stream.readUnsignedByte();
		else if (opcode >= 30 && opcode < 35)
			options[opcode - 30] = stream.readString();
		else if (opcode == 40) {
			int i_13_ = stream.readUnsignedByte();
			aShortArray5860 = new short[i_13_];
			aShortArray5896 = new short[i_13_];
			for (int i_14_ = 0; i_14_ < i_13_; i_14_++) {
				aShortArray5860[i_14_] = (short) stream.readUnsignedShort();
				aShortArray5896[i_14_] = (short) stream.readUnsignedShort();
			}
		} else if (41 == opcode) {
			int i_15_ = stream.readUnsignedByte();
			aShortArray5863 = new short[i_15_];
			aShortArray5864 = new short[i_15_];
			for (int i_16_ = 0; i_16_ < i_15_; i_16_++) {
				aShortArray5863[i_16_] = (short) stream.readUnsignedShort();
				aShortArray5864[i_16_] = (short) stream.readUnsignedShort();
			}
		} else if (opcode == 42) {
			int i_17_ = stream.readUnsignedByte();
			aByteArray5862 = new byte[i_17_];
			for (int i_18_ = 0; i_18_ < i_17_; i_18_++)
				aByteArray5862[i_18_] = (byte) stream.readByte();
		} else if (44 == opcode) {
			int i_19_ = stream.readUnsignedShort();
			int i_20_ = 0;
			for (int i_21_ = i_19_; i_21_ > 0; i_21_ >>= 1)
				i_20_++;
			aByteArray5865 = new byte[i_20_];
			byte i_22_ = 0;
			for (int i_23_ = 0; i_23_ < i_20_; i_23_++) {
				if ((i_19_ & 1 << i_23_) > 0) {
					aByteArray5865[i_23_] = i_22_;
					i_22_++;
				} else
					aByteArray5865[i_23_] = (byte) -1;
			}
		} else if (45 == opcode) {
			int i_24_ = stream.readUnsignedShort();
			int i_25_ = 0;
			for (int i_26_ = i_24_; i_26_ > 0; i_26_ >>= 1)
				i_25_++;
			aByteArray5866 = new byte[i_25_];
			byte i_27_ = 0;
			for (int i_28_ = 0; i_28_ < i_25_; i_28_++) {
				if ((i_24_ & 1 << i_28_) > 0) {
					aByteArray5866[i_28_] = i_27_;
					i_27_++;
				} else
					aByteArray5866[i_28_] = (byte) -1;
			}
		} else if (opcode == 60) {
			int i_29_ = stream.readUnsignedByte();
			anIntArray5857 = new int[i_29_];
			for (int i_30_ = 0; i_30_ < i_29_; i_30_++)
				anIntArray5857[i_30_] = stream.readBigSmart();
		} else if (opcode == 93)
			isVisibleOnMap = false;
		else if (opcode == 95)
			combatLevel = stream.readUnsignedShort();
		else if (97 == opcode)
			anInt5902 = stream.readUnsignedShort();
		else if (opcode == 98)
			anInt5900 = stream.readUnsignedShort();
		else if (99 == opcode)
			aBool5878 = true;
		else if (opcode == 100)
			anInt5881 = stream.readByte();
		else if (101 == opcode)
			anInt5915 = stream.readByte();
		else if (102 == opcode) {
			int i_31_ = stream.readUnsignedByte();
			int i_32_ = 0;
			for (int i_33_ = i_31_; i_33_ != 0; i_33_ >>= 1)
				i_32_++;
			anIntArray5883 = new int[i_32_];
			aShortArray5884 = new short[i_32_];
			for (int i_34_ = 0; i_34_ < i_32_; i_34_++) {
				if (0 == (i_31_ & 1 << i_34_)) {
					anIntArray5883[i_34_] = -1;
					aShortArray5884[i_34_] = (short) -1;
				} else {
					anIntArray5883[i_34_] = stream.readBigSmart();
					aShortArray5884[i_34_] = (short) stream.readUnsignedSmart();
				}
			}
		} else if (103 == opcode)
			headIcons = stream.readUnsignedShort();
		else if (106 == opcode || 118 == opcode) {
			bConfig = stream.readUnsignedShort();
			if (65535 == bConfig)
				bConfig = -527181913;
			config = stream.readUnsignedShort();
			if (65535 == config)
				config = 740536151;
			int i_35_ = -1;
			if (118 == opcode) {
				i_35_ = stream.readUnsignedShort();
				if (i_35_ == 65535)
					i_35_ = -1;
			}
			int i_36_ = stream.readUnsignedByte();
			transformTo = new int[2 + i_36_];
			for (int i_37_ = 0; i_37_ <= i_36_; i_37_++) {
				transformTo[i_37_] = stream.readUnsignedShort();
				if (65535 == transformTo[i_37_])
					transformTo[i_37_] = -1;
			}
			transformTo[1 + i_36_] = i_35_;
		} else if (opcode == 107)
			aBool5890 = false;
		else if (opcode == 109)
			aBool5871 = false;
		else if (opcode == 111)
			aBool5892 = false;
		else if (opcode == 113) {
			aShort5893 = (short) stream.readUnsignedShort();
			aShort5849 = (short) stream.readUnsignedShort();
		} else if (114 == opcode) {
			aByte5895 = (byte) stream.readByte();
			aByte5870 = (byte) stream.readByte();
		} else if (opcode == 119)
			walkMask = (byte) stream.readByte();
		else if (opcode == 121) {
			anIntArrayArray5858 = new int[anIntArray5856.length][];
			int i_38_ = stream.readUnsignedByte();
			for (int i_39_ = 0; i_39_ < i_38_; i_39_++) {
				int i_40_ = stream.readUnsignedByte();
				int[] is = (anIntArrayArray5858[i_40_] = new int[3]);
				is[0] = stream.readByte();
				is[1] = stream.readByte();
				is[2] = stream.readByte();
			}
		} else if (123 == opcode)
			anInt5882 = stream.readUnsignedShort();
		else if (125 == opcode)
			respawnDirection = (byte) stream.readByte();
		else if (opcode == 127)
			renderEmote = stream.readUnsignedShort();
		else if (128 == opcode)
			stream.readUnsignedByte();
		else if (134 == opcode) {
			anInt876 = stream.readUnsignedShort();
			if (anInt876 == 65535)
				anInt876 = -1;
			anInt842 = stream.readUnsignedShort();
			if (65535 == anInt842)
				anInt842 = -1;
			anInt884 = stream.readUnsignedShort();
			if (anInt884 == 65535)
				anInt884 = -1;
			anInt871 = stream.readUnsignedShort();
			if (65535 == anInt871)
				anInt871 = 1;
			anInt875 = stream.readUnsignedByte();
		} else if (135 == opcode || opcode == 136) {
			stream.readUnsignedByte();
			stream.readUnsignedShort();
		} else if (opcode == 137)
			anInt5868 = stream.readUnsignedShort();
		else if (138 == opcode)
			anInt5885 = stream.readBigSmart();
		else if (140 == opcode)
			anInt5908 = stream.readUnsignedByte();
		else if (opcode == 141)
			aBool5880 = true;
		else if (opcode == 142)
			anInt5888 = stream.readUnsignedShort();
		else if (opcode == 143)
			aBool5879 = true;
		else if (opcode >= 150 && opcode < 155) {
			options[opcode - 150] = stream.readString();
		} else if (opcode == 155) {
			aByte5867 = (byte) stream.readByte();
			aByte5877 = (byte) stream.readByte();
			aByte5869 = (byte) stream.readByte();
			aByte5901 = (byte) stream.readByte();
		} else if (158 == opcode)
			aByte5914 = (byte) 1;
		else if (opcode == 159)
			aByte5914 = (byte) 0;
		else if (opcode == 160) {
			int i_41_ = stream.readUnsignedByte();
			anIntArray5913 = new int[i_41_];
			for (int i_42_ = 0; i_42_ < i_41_; i_42_++)
				anIntArray5913[i_42_] = stream.readUnsignedShort();
		} else if (opcode != 162) {
			if (163 == opcode)
				anInt5897 = stream.readUnsignedByte();
			else if (164 == opcode) {
				anInt5917 = stream.readUnsignedShort();
				anInt5918 = stream.readUnsignedShort();
			} else if (165 == opcode)
				anInt5920 = stream.readUnsignedByte();
			else if (168 == opcode)
				anInt5907 = stream.readUnsignedByte();
			else if (opcode == 169)
				aBool5898 = false;
			else if (opcode >= 170 && opcode < 176) {
				if (anIntArray5872 == null) {
					anIntArray5872 = new int[6];
					Arrays.fill(anIntArray5872, -1);
				}
				int i_43_ = stream.readUnsignedShort();
				if (65535 == i_43_)
					i_43_ = -1;
				anIntArray5872[opcode - 170] = i_43_;
			} else if (178 != opcode) {
				if (179 == opcode) {
					readIdk(stream);
					readIdk(stream);
					readIdk(stream);
					readIdk(stream);
					readIdk(stream);
					readIdk(stream);
				} else if (opcode == 180)
					anInt5921 = (stream.readUnsignedByte() & 0xff);
				else if (opcode == 181) {
					aShort5903 = (short) stream.readUnsignedShort();
					aByte5919 = (byte) stream.readUnsignedByte();
				} else if (opcode == 249) {
					int i = stream.readUnsignedByte();
					if (clientScriptData == null) {
						clientScriptData = new HashMap<Integer, Object>(i);
					}
					for (int i_60_ = 0; i > i_60_; i_60_++) {
						boolean stringInstance = stream.readUnsignedByte() == 1;
						int key = stream.read24BitInt();
						Object value;
						if (stringInstance)
							value = stream.readString();
						else
							value = stream.readInt();
						clientScriptData.put(key, value);
					}
				}
			}
		} else {
			aBoolean3190 = true;
		}
	}

	public int readIdk(InputStream stream) {
		int i_24_ = stream.readUnsignedByte();
		if (i_24_ < 128)
			return stream.readUnsignedByte() - 64;
		return stream.readUnsignedShort() - 49152;
	}

	public static final void clearNPCDefinitions() {
		npcDefinitions.clear();
	}

	public NPCDefinitions(int id) {
		this.id = id;
		anInt842 = -1;
		bConfig = -1;
		anInt837 = -1;
		anInt846 = -1;
		anInt853 = 32;
		combatLevel = -1;
		anInt836 = -1;
		name = "null";
		anInt869 = 0;
		walkMask = (byte) 0;
		anInt850 = 255;
		anInt871 = -1;
		aBoolean852 = true;
		aShort862 = (short) 0;
		anInt876 = -1;
		aByte851 = (byte) -96;
		anInt875 = 0;
		anInt872 = -1;
		renderEmote = -1;
		respawnDirection = (byte) 7;
		aBoolean857 = true;
		anInt870 = -1;
		anInt874 = -1;
		anInt833 = -1;
		anInt864 = 128;
		headIcons = -1;
		aBoolean856 = false;
		config = -1;
		aByte854 = (byte) -16;
		aBoolean863 = false;
		isVisibleOnMap = true;
		anInt889 = -1;
		anInt884 = -1;
		aBoolean841 = true;
		anInt879 = -1;
		anInt899 = 128;
		aShort894 = (short) 0;
		options = new String[5];
		anInt897 = 0;
		anInt901 = -1;
	}

	public boolean hasMarkOption() {
		for (String option : options) {
			if (option != null && option.equalsIgnoreCase("mark"))
				return true;
		}
		return false;
	}

	public boolean hasOption(String op) {
		for (String option : options) {
			if (option != null && option.equalsIgnoreCase(op))
				return true;
		}
		return false;
	}

	public boolean hasAttackOption() {
		if (id == 14899)
			return true;
		for (String option : options) {
			if (option != null && option.equalsIgnoreCase("attack"))
				return true;
		}
		return false;
	}

	public static int getSize() {
		int lastArchiveId = Cache.getStore().getIndexes()[18].getLastArchiveId();
		return (lastArchiveId * 256 + Cache.getStore().getIndexes()[18].getValidFilesCount(lastArchiveId));
	}

	public String getName() {
		return name;
	}
}
