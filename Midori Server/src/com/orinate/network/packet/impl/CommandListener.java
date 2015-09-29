package com.orinate.network.packet.impl;

import com.orinate.game.content.CommandsHandler;
import com.orinate.game.model.player.Player;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

/**
 * @author Tom
 * 
 */
public class CommandListener implements PacketListener {

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		buffer.getUnsignedByte();
		buffer.getUnsignedByte();

		String command = buffer.getString();
		CommandsHandler.handleCommand(player, command);
	}

	@Override
	public boolean register() {
		return PacketRepository.register(87, this);
	}
}
