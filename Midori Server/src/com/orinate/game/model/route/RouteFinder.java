package com.orinate.game.model.route;

import com.orinate.game.model.player.Player;

/**
 * @author Mangis
 * 
 */
public class RouteFinder {

	public static final int WALK_ROUTEFINDER = 0;
	private static int lastUsed;

	public static int findRoute(int type, int srcX, int srcY, int srcZ, int srcSizeXY, RouteStrategy strategy, boolean findAlternative) {
		switch (lastUsed = type) {
		case WALK_ROUTEFINDER:
			return WalkRouteFinder.findRoute(srcX, srcY, srcZ, srcSizeXY, strategy, findAlternative);
		default:
			throw new RuntimeException("Unknown routefinder type.");
		}
	}

	public static int[] getLastPathBufferX() {
		switch (lastUsed) {
		case WALK_ROUTEFINDER:
			return WalkRouteFinder.getLastPathBufferX();
		default:
			throw new RuntimeException("Unknown routefinder type.");
		}
	}

	public static int[] getLastPathBufferY() {
		switch (lastUsed) {
		case WALK_ROUTEFINDER:
			return WalkRouteFinder.getLastPathBufferY();
		default:
			throw new RuntimeException("Unknown routefinder type.");
		}
	}

	public static boolean lastIsAlternative() {
		switch (lastUsed) {
		case WALK_ROUTEFINDER:
			return WalkRouteFinder.lastIsAlternative();
		default:
			throw new RuntimeException("Unknown routefinder type.");
		}
	}

	public static boolean walkTo(Player player, int x, int y, boolean forceRun) {
		int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getPlane(), player.getSize() /* Size */, new FixedTileStrategy(x, y), true);
		int[] bufferX = RouteFinder.getLastPathBufferX();
		int[] bufferY = RouteFinder.getLastPathBufferY();
		if (player.getLocation().getX() == x && player.getLocation().getY() == y) {
			return false;
		}
		if (steps <= 0) {
			return false;
		}
		if (forceRun) {
			player.setRunning(!player.isRunning());
		}
		player.setFirstStep(true);
		player.resetWalkSteps();
		for (int i = steps - 1; i >= 0; i--) {
			if (!player.addWalkSteps(bufferX[i], bufferY[i], 25, true)) {
				break;
			}
		}
		return true;
	}
}
