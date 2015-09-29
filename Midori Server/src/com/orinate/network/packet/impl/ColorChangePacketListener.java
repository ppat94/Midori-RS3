package com.orinate.network.packet.impl;

import com.orinate.game.model.player.Player;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

/**
 * 
 * @author Tyler
 * 
 */
public class ColorChangePacketListener implements PacketListener {

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		int colorId = buffer.getShort() & 0xFFFF;
		System.out.println(colorId);
	}

	@Override
	public boolean register() {
		return PacketRepository.register(57, this);
	}
}
