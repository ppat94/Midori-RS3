package com.orinate.network.packet.impl;

import com.orinate.game.model.player.Player;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

public class ClientFocusPacketListener implements PacketListener {

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		boolean isClientFocused = buffer.getUnsignedByte() == 1;
		player.setClientFocused(isClientFocused);
	}

	@Override
	public boolean register() {
		return PacketRepository.register(114, this);
	}

}
