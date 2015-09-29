package com.orinate.network.packet.impl;

import com.orinate.game.model.player.Player;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

/**
 * @author Tom
 * 
 */
public class PingListener implements PacketListener {

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		player.getWriter().sendPing();
	}

	@Override
	public boolean register() {
		return PacketRepository.register(93, this);
	}
}
