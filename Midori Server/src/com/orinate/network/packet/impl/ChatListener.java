package com.orinate.network.packet.impl;

import com.orinate.game.content.CommandsHandler;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.player.PublicChatMessage;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;
import com.orinate.util.text.Huffman;

public class ChatListener implements PacketListener {

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		final int colorEffect = buffer.getUnsignedByte();
		final int moveEffect = buffer.getUnsignedByte();

		final int effectHash = (colorEffect << 8) | (moveEffect & 0xff);
		final String message = Huffman.readEncryptedMessage(200, buffer);

		/* Handle commands with colon or semi-colon. */
		if (message.startsWith("::") || message.startsWith(";;") || message.startsWith(";")) {
			String command = message.replace("::", "").replace(";;", "").replace(";", "");
			CommandsHandler.handleCommand(player, command);
			return;
		}

		player.sendPublicChat(new PublicChatMessage(message, effectHash));
	}

	@Override
	public boolean register() {
		return PacketRepository.register(107, this);
	}

}