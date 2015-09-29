package com.orinate.network.packet.impl;

import com.orinate.game.model.player.Player;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

public class LobbyLinkPacketListener implements PacketListener {

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		buffer.getShort();
		buffer.getString();
		String lobbyLink = buffer.getString();
		buffer.getString();
		buffer.getByte();
		switch (lobbyLink.toString()) {
		case "account_settings.ws?mod=email":
			player.getWriter().sendURL("http://google.com");
			break;
		case "account_settings.ws?mod=recoveries":
			player.getWriter().sendURL("http://google.com");
			break;
		case "account_settings.ws?mod=messages":
			player.getWriter().sendURL("http://google.com");
			break;
		case "purchasepopup.ws?externalName=rs":
			player.getWriter().sendURL("http://google.com");
			break;
		case "account_settings.ws?mod=uidPassport":
			player.getWriter().sendURL("http://google.com");
			break;
		}
	}

	@Override
	public boolean register() {
		return PacketRepository.register(32, this);
	}

}
