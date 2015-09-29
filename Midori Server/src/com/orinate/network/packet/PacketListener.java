package com.orinate.network.packet;

import com.orinate.game.model.player.Player;
import com.orinate.io.InBuffer;

/**
 * @author Tom
 * 
 */
public interface PacketListener {

	void handle(Player player, int opcode, InBuffer buffer);

	boolean register();

}
