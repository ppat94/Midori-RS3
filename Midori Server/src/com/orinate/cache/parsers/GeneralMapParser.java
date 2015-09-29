package com.orinate.cache.parsers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.alex.io.InputStream;
import com.orinate.cache.Cache;

/**
 * 
 * @author Tyler Telis
 * 
 */
public class GeneralMapParser {

	public HashMap<Long, Object> values = new HashMap<>();
	private static final ConcurrentHashMap<Integer, GeneralMapParser> generalRequirementMap = new ConcurrentHashMap<>();

	private static long[] mapKeys = new long[] { 2795, 2794, 2793, 2915, 2809, 2799, 2796, 2802, 2800, 2806, 2807 };

	public static void main(String[] args) throws IOException {
		Cache.init();
		for (int curScript = 0; curScript < getSize(); curScript++) {
			GeneralMapParser map = getMap(curScript);
			if (map.values.isEmpty()) {
				continue;
			}

			Set<Long> mapKeySet = map.values.keySet();

			int containsCount = 0;
			for (int index = 0; index < mapKeys.length; index++) {
				if (mapKeySet.contains(mapKeys[index])) {
					containsCount += 1;
				}
			}

			if (containsCount >= 10) {
				String abilityDefinition = (String) map.values.get(mapKeys[0]);
				if (abilityDefinition == null) {
					return;
				}
				if (abilityDefinition.contains("<col=")) {
					System.out.println(curScript + " - " + map.values.get(mapKeys[1]) + " - " + map.values);
				}
			}
		}
	}

	public static final GeneralMapParser getMap(int scriptId) {
		GeneralMapParser script = generalRequirementMap.get(scriptId);
		if (script != null)
			return script;
		byte[] data = Cache.STORE.getIndexes()[22].getFile(getArchiveId(scriptId), getFileId(scriptId));
		script = new GeneralMapParser();
		if (data != null) {
			script.decode(new InputStream(data));
		}
		generalRequirementMap.put(scriptId, script);
		return script;
	}

	public static int getFileId(int i) {
		return i & (1 << 5) - 1;
	}

	public static int getArchiveId(int i) {
		return i >>> 5;
	}

	private void decode(InputStream stream) {
		while (true) {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				break;
			decode(opcode, stream);
		}
	}

	private void decode(int opcode, InputStream buffer) {
		if (opcode == 249) {
			int length = buffer.readUnsignedByte();
			for (int index = 0; index < length; index++) {
				boolean isString = buffer.readUnsignedByte() == 1;
				int key = buffer.read24BitInt();
				Object value;
				if (isString)
					value = buffer.readString();
				else
					value = buffer.readInt();
				values.put((long) key, value);
			}
		}
	}
	
	public Map<Long, Object> getValues() {
		return values;
	}

	public static int getSize() {
		int lastArchiveId = Cache.getStore().getIndexes()[22].getLastArchiveId();
		return lastArchiveId * 256 + Cache.getStore().getIndexes()[22].getValidFilesCount(lastArchiveId);
	}
}
