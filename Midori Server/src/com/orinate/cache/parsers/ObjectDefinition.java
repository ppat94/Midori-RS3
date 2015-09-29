package com.orinate.cache.parsers;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.alex.io.InputStream;
import com.orinate.cache.Cache;

public class ObjectDefinition {

	private static ObjectDefinition[] objectDefinitions;

	public Map<Integer, Object> clientScripts = new HashMap<>();
	public int id;
	public boolean projectileClipped;
	public String name;
	public int sizeX;
	public int sizeY;
	public int clipType;
	public String[] actions;
	public int configFileId;
	public int configId;
	public int[] toObjectIds;
	public int walkToData;
	public int miniMapSpriteId;
	public boolean clippingFlag;

	public ObjectDefinition(int id) {
		this.id = id;
		this.configFileId = -1;
		this.actions = new String[5];
		this.sizeY = 1;
		this.sizeX = 1;
		this.clipType = 2;
		this.projectileClipped = true;
		this.clippingFlag = false;
		this.name = "null";
		this.configId = -1;
		this.configFileId = -1;
		this.walkToData = 0;
		this.miniMapSpriteId = -1;
		this.loadObjectDefinitions();
	}

	public static void main(String[] args) throws IOException {
		Cache.init();
		ObjectDefinition def = ObjectDefinition.forId(69827);
		System.out.println(def.configFileId + ", " + def.configId + ", " + Arrays.toString(def.toObjectIds));
	}

	private void loadObjectDefinitions() {
		try {
			byte[] data = Cache.getStore().getIndexes()[16].getFile(getArchiveId(), getFileId());
			if (data == null) {
				return;
			}
			InputStream inStream = new InputStream(data);
			parseOpcode(inStream);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void parseOpcode(InputStream buffer) {
		for (;;) {
			int opcode = buffer.readUnsignedByte();
			if (opcode == 0) {
				break;
			}
			decode(buffer, opcode);
		}
	}

	public void decode(InputStream buffer, int opcode) {
		if (opcode == 1) {
			int size = buffer.readUnsignedByte();
			for (int index = 0; index < size; index++) {
				buffer.readUnsignedByte();
				int idxs = buffer.readUnsignedByte();
				for (int idx = 0; idx < idxs; idx++) {
					buffer.readBigSmart();
				}
			}
		} else if (opcode == 2) {
			name = buffer.readString();
		} else if (opcode == 14) {
			sizeX = buffer.readUnsignedByte();
		} else if (opcode == 15) {
			sizeY = buffer.readUnsignedByte();
		} else if (17 == opcode) {
			clipType = 0;
			projectileClipped = false;
		} else if (18 == opcode) {
			projectileClipped = false;
		} else if (opcode == 19) {
			buffer.readUnsignedByte();
		} else if (opcode == 24) {
			buffer.readBigSmart();
		} else if (opcode == 27) {
			clipType = 1;
		} else if (28 == opcode) {
			buffer.readUnsignedByte();
		} else if (29 == opcode) {
			buffer.readUnsignedByte();
		} else if (opcode == 39) {
			buffer.readUnsignedByte();
		} else if (opcode >= 30 && opcode < 35) {
			actions[opcode - 30] = buffer.readString();
		} else if (40 == opcode) {
			int i_202_ = buffer.readUnsignedByte();
			for (int i_203_ = 0; i_203_ < i_202_; i_203_++) {
				buffer.readUnsignedShort();
				buffer.readUnsignedShort();
			}
		} else if (41 == opcode) {
			int idxs = buffer.readUnsignedByte();
			for (int idx = 0; idx < idxs; idx++) {
				buffer.readUnsignedShort();
				buffer.readUnsignedShort();
			}
		} else if (42 == opcode) {
			int idxs = buffer.readUnsignedByte();
			for (int idx = 0; idx < idxs; idx++) {
				buffer.readUnsignedByte();
			}
		} else if (44 == opcode) {
			buffer.readUnsignedShort();
		} else if (opcode == 45) {
			buffer.readUnsignedShort();
		} else if (65 == opcode) {
			buffer.readUnsignedShort();
		} else if (66 == opcode) {
			buffer.readUnsignedShort();
		} else if (opcode == 67) {
			buffer.readUnsignedShort();
		} else if (69 == opcode) {
			walkToData = buffer.readUnsignedByte();
		} else if (70 == opcode) {
			buffer.readUnsignedShort();
		} else if (71 == opcode) {
			buffer.readUnsignedShort();
		} else if (opcode == 72) {
			buffer.readUnsignedShort();
		} else if (74 == opcode) {
			clippingFlag = true;
		} else if (opcode == 75) {
			buffer.readUnsignedByte();
		} else if (opcode == 77 || opcode == 92) {
			configFileId = buffer.readUnsignedShort();
			if (configFileId == 65535) {
				configFileId = -1;
			}
			configId = buffer.readUnsignedShort();
			int obj = -1;
			if (opcode == 92) {
				obj = buffer.readBigSmart();
			}
			int size = buffer.readUnsignedByte();
			toObjectIds = new int[size - -2];
			for (int idx = 0; idx <= size; idx++) {
				toObjectIds[idx] = buffer.readBigSmart();
			}
			toObjectIds[size + 1] = obj;
		} else if (opcode == 78) {
			buffer.readUnsignedShort();
			buffer.readUnsignedByte();
		} else if (opcode == 79) {
			buffer.readUnsignedShort();
			buffer.readUnsignedShort();
			buffer.readUnsignedByte();
			int idxs = buffer.readUnsignedByte();
			for (int idx = 0; idx < idxs; idx++) {
				buffer.readUnsignedShort();
			}
		} else if (81 == opcode) {
			buffer.readUnsignedByte();
		} else if (93 == opcode) {
			buffer.readUnsignedShort();
		} else if (95 == opcode) {
			buffer.readUnsignedShort();
		} else if (opcode == 99 || 100 == opcode) {
			buffer.readUnsignedByte();
			buffer.readUnsignedShort();
		} else if (101 == opcode) {
			buffer.readUnsignedByte();
		} else if (102 == opcode) {
			buffer.readUnsignedShort();
		} else if (104 == opcode) {
			buffer.readUnsignedByte();
		} else if (opcode == 106) {
			int idxs = buffer.readUnsignedByte();
			for (int idx = 0; idx < idxs; idx++) {
				buffer.readBigSmart();
				buffer.readUnsignedByte();
			}
		} else if (107 == opcode) {
			miniMapSpriteId = buffer.readUnsignedShort();
		} else if (opcode >= 150 && opcode < 155) {
			actions[opcode - 150] = buffer.readString();
		} else if (160 == opcode) {
			int i_227_ = buffer.readUnsignedByte();
			for (int i_228_ = 0; i_228_ < i_227_; i_228_++) {
				buffer.readUnsignedShort();
			}
		} else if (162 == opcode) {
			buffer.readInt();
		} else if (opcode == 163) {
			buffer.readUnsignedByte();
			buffer.readUnsignedByte();
			buffer.readUnsignedByte();
			buffer.readUnsignedByte();
		} else if (164 == opcode) {
			buffer.readUnsignedShort();
		} else if (opcode == 165) {
			buffer.readUnsignedShort();
		} else if (opcode == 166) {
			buffer.readUnsignedShort();
		} else if (167 == opcode) {
			buffer.readUnsignedShort();
		} else if (170 == opcode) {
			buffer.readUnsignedSmart();
		} else if (171 == opcode) {
			buffer.readUnsignedSmart();
		} else if (opcode == 173) {
			buffer.readUnsignedShort();
			buffer.readUnsignedShort();
		} else if (178 == opcode) {
			buffer.readUnsignedByte();
		} else if (opcode == 186) {
			buffer.readUnsignedByte();
		} else if (opcode >= 190 && opcode < 196) {
			buffer.readUnsignedShort();
		} else if (opcode == 196) {
			buffer.readUnsignedByte();
		} else if (197 == opcode) {
			buffer.readUnsignedByte();
		} else if (201 == opcode) {
			buffer.readUnsignedSmart();
			buffer.readUnsignedSmart();
			buffer.readUnsignedSmart();
			buffer.readUnsignedSmart();
			buffer.readUnsignedSmart();
			buffer.readUnsignedSmart();
		} else if (opcode == 249) {
			int size = buffer.readUnsignedByte();
			for (int index = 0; index < size; index++) {
				boolean stringVal = buffer.readUnsignedByte() == 1;
				int key = buffer.read24BitInt();
				Object val;
				if (stringVal) {
					val = buffer.readString();
				} else {
					val = buffer.readInt();
				}
				clientScripts.put(key, val);
			}
		}
	}

	public static ObjectDefinition forId(int id) {
		if (objectDefinitions == null) {
			objectDefinitions = new ObjectDefinition[getSize()];
		}
		if (id < 0 || id > objectDefinitions.length) {
			id = 0;
		}
		ObjectDefinition objectDef = objectDefinitions[id];
		if (objectDef == null) {
			objectDefinitions[id] = objectDef = new ObjectDefinition(id);
		}
		return objectDef;
	}

	public static int getSize() {
		int lastArchiveId = Cache.getStore().getIndexes()[16].getLastArchiveId();
		return (lastArchiveId * 256 + Cache.getStore().getIndexes()[16].getValidFilesCount(lastArchiveId));
	}

	private int getFileId() {
		return id & 0xFF;
	}

	private int getArchiveId() {
		return id >>> 8;
	}

	public boolean containsAction(String action) {
		for (String act : actions) {
			if (act == null)
				continue;
			if (act.equalsIgnoreCase(action)) {
				return true;
			}
		}
		return false;
	}
}
