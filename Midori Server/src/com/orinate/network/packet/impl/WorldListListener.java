package com.orinate.network.packet.impl;

import com.orinate.game.model.player.Player;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;
import com.orinate.util.world.WorldList;

/**
 * @author Tyler
 * 
 */
public class WorldListListener implements PacketListener {

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		int updateType = buffer.getInt();
		boolean fullRefresh = updateType == 0;
		player.write(WorldList.request(fullRefresh));
	}

	@Override
	public boolean register() {
		return PacketRepository.register(34, this);
	}
}
