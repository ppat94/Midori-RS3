package com.orinate.cache.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.alex.io.InputStream;
import com.orinate.cache.Cache;

/**
 * 
 * @author Tyler
 * 
 */
public class AnimationParser {

	public int[] anIntArray7184;
	public int anInt7186;
	public int[] anIntArray7187;
	public int[] anIntArray7189;
	public int anInt7190;
	public int[] anIntArray7191;
	public int anInt7192;
	public int anInt7193;
	public int anInt7194;
	public int anInt7195;
	public int itemInHand;
	public int anInt7197;
	public int[] anIntArray7198;
	public int anInt7199;
	public int anInt7200 = 0;
	public boolean aBool7201;
	public boolean aBool7202;
	public int[] anIntArray7203;
	public int[][] anIntArrayArray7204;
	public static boolean aBool7205 = false;
	public int anInt7207;
	public boolean[] aBoolArray7026;
	public HashMap<Integer, Object> params;
	private static final ConcurrentHashMap<Integer, AnimationParser> animDefs = new ConcurrentHashMap<Integer, AnimationParser>();

	public static void main(String[] args) {
		Cache.init();
		for (int i = 0; i < getSize(); i++) {
			AnimationParser parser = AnimationParser.forId(i);
			if (parser.params.isEmpty())
				continue;
			System.out.println(i + " " + parser.params);
		}
	}

	public static List<Integer> getAnimatonsForId(int item) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int id = 0; id < getSize(); id++) {
			AnimationParser parser = AnimationParser.forId(id);
			if (parser == null)
				continue;
			if (item == parser.itemInHand)
				list.add(id);
		}
		return list;
	}

	public int getGFXId() {
		if (params.get(2920) == null)
			return -1;
		return (int) params.get(2920);
	}

	public static final AnimationParser forId(int emoteId) {
		try {
			AnimationParser defs = animDefs.get(emoteId);
			if (defs != null) {
				return defs;
			}
			byte[] data = Cache.STORE.getIndexes()[20].getFile(emoteId >>> 7, emoteId & 0x7f);
			defs = new AnimationParser();
			if (data != null) {
				defs.decode(new InputStream(data));
			}
			defs.set();
			animDefs.put(emoteId, defs);
			return defs;
		} catch (Throwable t) {
			return null;
		}
	}

	void set() {
		if (anInt7190 * -1641276135 == -1) {
			if (aBoolArray7026 != null)
				anInt7190 = 2065423954;
			else
				anInt7190 = 0;
		}
		if (anInt7199 * 1647971661 == -1) {
			if (aBoolArray7026 != null)
				anInt7199 = 1160242954;
			else
				anInt7199 = 0;
		}
		if (null != anIntArray7189) {
			anInt7200 = 0;
			for (int i = 0; i < anIntArray7189.length; i++)
				anInt7200 += -1824143135 * anIntArray7189[i];
		}
	}

	public void decode(InputStream inStream) {
		while (true) {
			int opcode = inStream.readUnsignedByte();
			if (0 == opcode) {
				return;
			}
			this.decode(inStream, opcode);
		}
	}

	/**
	 * Gets the duration of this animation in milliseconds.
	 * 
	 * @return The duration.
	 */
	public int getDuration() {
		if (anIntArray7189 == null) {
			return 0;
		}
		int time = 0;
		for (int i : anIntArray7189) {
			time += i;
		}
		return (int) ((time * 30d) * 0.6);
	}

	/**
	 * Gets the duration of this animation in (600ms) ticks.
	 * 
	 * @return The duration in ticks.
	 */
	public int getDurationTicks() {
		return getDuration() / 600;
	}

	public void decode(InputStream buffer, int opcode) {
		if (1 == opcode) {
			int i_7_ = buffer.readUnsignedShort();
			anIntArray7189 = new int[i_7_];
			for (int i_8_ = 0; i_8_ < i_7_; i_8_++)
				anIntArray7189[i_8_] = buffer.readUnsignedShort();
			anIntArray7187 = new int[i_7_];
			for (int i_9_ = 0; i_9_ < i_7_; i_9_++)
				anIntArray7187[i_9_] = buffer.readUnsignedShort();
			for (int i_10_ = 0; i_10_ < i_7_; i_10_++)
				anIntArray7187[i_10_] = (buffer.readUnsignedShort() << 16) + anIntArray7187[i_10_];
		} else if (2 == opcode) {
			anInt7193 = buffer.readUnsignedShort();
		} else if (5 == opcode) {
			anInt7194 = buffer.readUnsignedByte();
		} else if (6 == opcode) {
			anInt7195 = buffer.readUnsignedShort();
		} else if (opcode == 7) {
			itemInHand = buffer.readUnsignedShort();
		} else if (opcode == 8) {
			anInt7197 = buffer.readUnsignedByte();
		} else if (9 == opcode) {
			anInt7190 = buffer.readUnsignedByte();
		} else if (opcode == 10) {
			anInt7199 = buffer.readUnsignedByte();
		} else if (11 == opcode) {
			anInt7186 = buffer.readUnsignedByte();
		} else if (12 == opcode) {
			int i_11_ = buffer.readUnsignedByte();
			anIntArray7198 = new int[i_11_];
			for (int i_12_ = 0; i_12_ < i_11_; i_12_++)
				anIntArray7198[i_12_] = buffer.readUnsignedShort();
			for (int i_13_ = 0; i_13_ < i_11_; i_13_++)
				anIntArray7198[i_13_] = (buffer.readUnsignedShort() << 16) + anIntArray7198[i_13_];
		} else if (13 == opcode) {
			int i_14_ = buffer.readUnsignedShort();
			anIntArrayArray7204 = new int[i_14_][];
			for (int i_15_ = 0; i_15_ < i_14_; i_15_++) {
				int i_16_ = buffer.readUnsignedByte();
				if (i_16_ > 0) {
					anIntArrayArray7204[i_15_] = new int[i_16_];
					anIntArrayArray7204[i_15_][0] = buffer.read24BitInt();
					for (int i_17_ = 1; i_17_ < i_16_; i_17_++)
						anIntArrayArray7204[i_15_][i_17_] = buffer.readUnsignedShort();
				}
			}
		} else if (opcode == 14)
			aBool7201 = true;
		else if (opcode == 15)
			aBool7202 = true;
		else if (opcode != 16 && opcode != 18) {
			if (opcode == 19) {
				if (anIntArray7203 == null) {
					anIntArray7203 = new int[anIntArrayArray7204.length];
					for (int i_18_ = 0; i_18_ < anIntArrayArray7204.length; i_18_++)
						anIntArray7203[i_18_] = 255;
				}
				anIntArray7203[buffer.readUnsignedByte()] = buffer.readUnsignedByte();
			} else if (20 == opcode) {
				if (null == anIntArray7184 || anIntArray7191 == null) {
					anIntArray7184 = new int[anIntArrayArray7204.length];
					anIntArray7191 = new int[anIntArrayArray7204.length];
					for (int i_19_ = 0; i_19_ < anIntArrayArray7204.length; i_19_++) {
						anIntArray7184[i_19_] = 256;
						anIntArray7191[i_19_] = 256;
					}
				}
				int i_20_ = buffer.readUnsignedByte();
				anIntArray7184[i_20_] = buffer.readUnsignedShort();
				anIntArray7191[i_20_] = buffer.readUnsignedShort();
			} else if (22 == opcode)
				anInt7207 = buffer.readUnsignedByte();
			else if (23 == opcode)
				buffer.readUnsignedShort();
			else if (24 == opcode) {
				// TODO figure out why it's linking to another parser.
				buffer.readUnsignedShort();
				// aClass541_7188 = ((Class545) ((Class558)
				// this).aClass545_7185).aClass550_7039.method12143(i_21_,
				// 1299922425);
			} else if (opcode == 249) {
				int length = buffer.readUnsignedByte();
				if (params == null)
					params = new HashMap<Integer, Object>(length);
				for (int index = 0; index < length; index++) {
					boolean stringInstance = buffer.readUnsignedByte() == 1;
					int key = buffer.read24BitInt();
					Object value = stringInstance ? buffer.readString() : buffer.readInt();
					params.put(key, value);
				}
			}
		}
	}

	public AnimationParser() {
		params = new HashMap<Integer, Object>();
	}

	public static int getSize() {
		int lastArchiveId = Cache.getStore().getIndexes()[20].getLastArchiveId();
		return lastArchiveId * 128 + Cache.getStore().getIndexes()[20].getValidFilesCount(lastArchiveId);
	}

}
