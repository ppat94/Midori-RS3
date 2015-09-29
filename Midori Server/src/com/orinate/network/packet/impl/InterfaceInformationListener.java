package com.orinate.network.packet.impl;

import com.orinate.game.model.player.Player;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

/**
 * @author Trent
 */
public class InterfaceInformationListener implements PacketListener {

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		@SuppressWarnings("unused")
		int length = buffer.getByte();
		while (buffer.hasAvailable()) {
			int key = buffer.getShort();
			int settings = buffer.getInt();
			if (key != 0 && settings != 0) {
				player.saveInterfaceSettingsKey(key, settings);
			}
		}
		player.getWriter().writeInterfaceInfoReset();
	}

	@Override
	public boolean register() {
		return PacketRepository.register(37, this);
	}

}
