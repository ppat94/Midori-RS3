package com.orinate.network.packet.impl;

import com.orinate.game.model.Location;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.route.FixedTileStrategy;
import com.orinate.game.model.route.RouteFinder;
import com.orinate.io.InBuffer;
import com.orinate.network.packet.PacketListener;
import com.orinate.network.packet.PacketRepository;

/**
 * @author Tom
 * 
 */
public class WalkPacketListener implements PacketListener {

	@Override
	public void handle(Player player, int opcode, InBuffer buffer) {
		int baseY = buffer.getLEShort();
		boolean forceRun = buffer.getByte() == 1;
		int baseX = buffer.getLEShortA();
		if (player.isLocked()) {
			player.getWriter().sendResetMinimapFlag();
			return;
		}
		int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getPlane(), player.getSize(), new FixedTileStrategy(baseX, baseY), true);
		int[] bufferX = RouteFinder.getLastPathBufferX();
		int[] bufferY = RouteFinder.getLastPathBufferY();

		if (forceRun) {
			player.setRunning(!player.isRunning());
		}

		player.resetAll();
		player.setFirstStep(true);

		int lastStepIdx = -1;
		for (int step = steps - 1; step >= 0; step--) {
			int x = bufferX[step];
			int y = bufferY[step];
			if (!player.addWalkSteps(x, y, 25, true)) {
				break;
			}
			lastStepIdx = step;
		}

		if (lastStepIdx == -1) {
			player.getWriter().sendResetMinimapFlag();
		} else {
			Location tile = new Location(bufferX[lastStepIdx], bufferY[lastStepIdx], player.getLocation().getPlane());
			player.getWriter().sendMinimapFlag(tile.getLocalX(player.getLastLoadedLocation()), tile.getLocalY(player.getLastLoadedLocation()));
		}
	}

	@Override
	public boolean register() {
		return PacketRepository.register(this, 95, 79);
	}
}
