package com.orinate.network.packet.impl;

import com.orinate.game.World;
import com.orinate.game.model.Location;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.region.GroundItem;
import com.orinate.game.model.region.Region;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

public class GroundItemListener implements PacketListener {

	@SuppressWarnings("unused")
	@Override
	public void handle(final Player player, int opcode, InBuffer buffer) {
		boolean forceRun = buffer.getByteC() == 1;
		int y = buffer.getLEShort();
		final int id = buffer.getLEShort();
		int x = buffer.getShort();
		int plane = player.getLocation().getPlane();
		final Location location = new Location(x, y, plane);
		Region region = World.getWorld().getRegion(location.getRegionId());
		if (region == null)
			return;
		GroundItem item = region.getGroundItem(id, location);
		if (item != null) {
			if (player.getInventory().addItem(item)) {
				World.getWorld().removeGroundItem(player, item);
				return;
			}
		}

	}

	@Override
	public boolean register() {
		return PacketRepository.register(70, this);
	}

}
