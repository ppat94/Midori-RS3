package com.orinate.network.packet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Tom
 * 
 */
public class PacketRepository {

	private static final Logger logger = Logger.getLogger(PacketRepository.class.getName());
	private static final Map<Integer, PacketListener> PACKETS = new HashMap<>();

	public static boolean load() throws Exception {// ./src/com/orinate/network/packet/impl/
		for (File file : new File("./src/com/orinate/network/packet/impl/").listFiles()) {
			if (!((PacketListener) Class.forName("com.orinate.network.packet.impl." + file.getName().replace(".java", "")).newInstance()).register()) {
				logger.warning("Failed to bind a packet!");
				continue;
			}
		}
		logger.info("Successfully bound " + PACKETS.size() + " packet opcodes to their handlers!");
		return true;
	}

	public static boolean register(int opcode, PacketListener listener) {
		if (PACKETS.containsKey(opcode)) {
			throw new RuntimeException("Could not register packet [Already registered key: " + listener + ", name=" + listener.getClass().getSimpleName() + ", old=" + PACKETS.get(opcode).getClass().getSimpleName() + "].");
		}
		PACKETS.put(opcode, listener);
		return true;
	}

	public static boolean register(PacketListener listener, int... opcodes) {
		for (int opcode : opcodes) {
			if (PACKETS.containsKey(opcode)) {
				throw new RuntimeException("Could not register packet [Already registered key: " + listener + ", name=" + listener.getClass().getSimpleName() + ", old=" + PACKETS.get(opcode).getClass().getSimpleName() + "].");
			}
			PACKETS.put(opcode, listener);
		}
		return true;
	}

	public static PacketListener get(int opcode) {
		return PACKETS.get(opcode);
	}
}
