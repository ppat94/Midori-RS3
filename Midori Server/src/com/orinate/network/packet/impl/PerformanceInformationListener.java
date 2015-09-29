package com.orinate.network.packet.impl;

import com.orinate.game.model.player.Player;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

public class PerformanceInformationListener implements PacketListener {

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		player.setFps(buffer.getByte());
		player.setOffheap(buffer.getByteS());
		player.setGameServerPing(buffer.getShortA());
	}

	@Override
	public boolean register() {
		return PacketRepository.register(this, 59);
	}

}
