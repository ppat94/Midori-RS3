package com.orinate.util.world;

import java.util.LinkedList;
import java.util.List;

import com.orinate.game.World;
import com.orinate.io.OutBuffer;

/**
 * @author Tom
 * 
 */
public class WorldList {

	private static final List<WorldEntry> worlds = new LinkedList<>();

	static {
		worlds.add(new WorldEntry(1, 0, 1, "Atrium 1", "127.0.0.1", "USA", 0, true));
	}

	public static OutBuffer request(boolean fullUpdate) {
		OutBuffer buffer = new OutBuffer();
		buffer.putVarShort(153);
		buffer.putByte(1);
		buffer.putByte(2);
		buffer.putByte(fullUpdate ? 1 : 0);
		int size = worlds.size();
		if (fullUpdate) {
			buffer.putSmart(size);
			for (WorldEntry world : worlds) {
				buffer.putSmart(world.getCountry());
				buffer.putJagString(world.getActivity());
			}
			buffer.putSmart(0);
			buffer.putSmart(size + 1);
			buffer.putSmart(size);
			for (WorldEntry world : worlds) {
				buffer.putSmart(world.getWorldId());
				buffer.putByte(0);
				int flags = 0;
				if (world.isMembers())
					flags |= 0x1;
				flags |= 0x8;
				buffer.putInt(flags);
				buffer.putSmart(0);
				buffer.putJagString("");
				buffer.putJagString(world.getIp());
			}
			buffer.putInt(-1723296702);
		}
		for (WorldEntry world : worlds) {
			buffer.putSmart(world.getWorldId());
			buffer.putShort(World.getWorld().getPlayerCount());
		}
		buffer.finishVarShort();
		return buffer;
	}
}