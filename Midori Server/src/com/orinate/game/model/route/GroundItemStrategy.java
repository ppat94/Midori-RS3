package com.orinate.game.model.route;

import com.orinate.game.model.region.GroundItem;

public class GroundItemStrategy extends RouteStrategy {

	/**
	 * Entity position x.
	 */
	private int x;
	/**
	 * Entity position y.
	 */
	private int y;

	public GroundItemStrategy(GroundItem entity) {
		this.x = entity.getLocation().getX();
		this.y = entity.getLocation().getY();
	}

	@Override
	public boolean canExit(int currentX, int currentY, int sizeXY, int[][] clip, int clipBaseX, int clipBaseY) {
		return RouteStrategy.checkFilledRectangularInteract(clip, currentX - clipBaseX, currentY - clipBaseY, sizeXY, sizeXY, x - clipBaseX, y - clipBaseY, 1, 1, 0);
	}

	@Override
	public int getApproxDestinationX() {
		return x;
	}

	@Override
	public int getApproxDestinationY() {
		return y;
	}

	@Override
	public int getApproxDestinationSizeX() {
		return 1;
	}

	@Override
	public int getApproxDestinationSizeY() {
		return 1;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof GroundItemStrategy))
			return false;
		GroundItemStrategy strategy = (GroundItemStrategy) other;
		return x == strategy.x && y == strategy.y;
	}

}
