package com.orinate.network.packet.impl;

import com.orinate.game.model.player.Player;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;

/**
 * @author Tom
 * 
 */
public class DefaultPacketListener implements PacketListener {

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		System.out.println("Unhandled Packet with opcode "+opcode);
	}

	@Override
	public boolean register() {
		return true;
	}
}
