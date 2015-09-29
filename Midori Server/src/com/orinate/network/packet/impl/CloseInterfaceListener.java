package com.orinate.network.packet.impl;

import com.orinate.game.model.player.Player;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

/**
 * @author Tom
 * 
 */
public class CloseInterfaceListener implements PacketListener {

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		player.resetAll();
		player.getWriter().sendCS2Script(9210);// Reset locked screen.
	}

	@Override
	public boolean register() {
		return PacketRepository.register(54, this);
	}
}
