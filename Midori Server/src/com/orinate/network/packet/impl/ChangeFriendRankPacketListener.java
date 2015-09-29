package com.orinate.network.packet.impl;

import com.orinate.game.model.player.Player;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

public class ChangeFriendRankPacketListener implements PacketListener {

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		String name = buffer.getString();
		player.getFriendsChatManager().getRanks().put(name, getRank(buffer.getUnsignedByte128()));
		player.getFriendsListManager().sendFriends();
	}
	
	public int getRank(int rank) {
		return 256 - rank;
	}

	@Override
	public boolean register() {
		return true;
	}

}
