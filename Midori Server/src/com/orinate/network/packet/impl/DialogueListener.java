package com.orinate.network.packet.impl;

import com.orinate.game.model.player.Player;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

/**
 * @author Tom
 * 
 */
public class DialogueListener implements PacketListener {

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		buffer.getLEShortA();
		int interfaceData = buffer.getIntB();
		int interfaceId = interfaceData >> 16;
		if (!player.getInterfaceManager().containsInterface(interfaceId) || player.isLocked()) {
			return;
		}
		int componentId = interfaceData - (interfaceId << 16);
		player.getDialogueManager().continueDialogue(componentId);
	}

	@Override
	public boolean register() {
		return PacketRepository.register(105, this);
	}
}