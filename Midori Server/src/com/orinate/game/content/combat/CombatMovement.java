package com.orinate.game.content.combat;

import java.util.ArrayList;
import java.util.List;

import com.orinate.game.World;
import com.orinate.game.model.Entity;
import com.orinate.game.model.Location;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.route.FixedTileStrategy;
import com.orinate.game.model.route.RouteFinder;

/**
 * Handles the combat movement.
 * 
 * @author Emperor
 * 
 */
public final class CombatMovement {

	/**
	 * The entity to move.
	 */
	private final Entity mover;

	/**
	 * The target location.
	 */
	private Location targetLocation;

	/**
	 * The interaction location.
	 */
	private Location interactionLocation;

	/**
	 * Constructs a new {@code CombatMovement} {@code Object}.
	 * 
	 * @param mover
	 *            The moving entity.
	 */
	public CombatMovement(Entity mover) {
		this.mover = mover;
	}

	/**
	 * Checks if the moving entity can interact with the target.
	 * 
	 * @param target
	 *            The target entity.
	 * @return {@code True} if the moving entity can interact.
	 */
	public boolean interact(Entity target, CombatStyle style) {
		if (!target.getLocation().equals(targetLocation)) {
			interactionLocation = walkPath(getClosest(mover, target));
			if (interactionLocation != null) {
				targetLocation = new Location(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getPlane());
			}
		}
		return style.canInteract(mover, target, interactionLocation);
	}

	/**
	 * Adds a path to the entity's walking queue.
	 * 
	 * @param d
	 *            The destination of the path.
	 * @return The interaction location.
	 */
	public Location walkPath(Location d) {
		int size = mover.getSize();
		Location l = mover.getLocation();
		if (mover.getLocation().equals(d)) {
			return d;
		}
		int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, l.getX(), l.getY(), l.getPlane(), size, new FixedTileStrategy(d.getX(), d.getY()), mover instanceof Player);
		int[] bufferX = RouteFinder.getLastPathBufferX();
		int[] bufferY = RouteFinder.getLastPathBufferY();
		if (steps <= 0) {
			return null;
		}
		mover.setFirstStep(true);
		mover.resetWalkSteps();
		for (int i = steps - 1; i >= 0; i--) {
			if (!mover.addWalkSteps(bufferX[i], bufferY[i], 25, true)) {
				break;
			}
		}
		return new Location(bufferX[0], bufferY[0], 0);
	}

	/**
	 * Resets the combat movement.
	 */
	public void reset() {
		targetLocation = null;
	}

	/**
	 * Gets the closest surrounding location.
	 * 
	 * @param e
	 *            The entity.
	 * @param node
	 *            The node.
	 * @return The location closest to the node.
	 */
	public static Location getClosest(Entity e, Entity n) {
		int distance = 9999;
		int current;
		Location l = n.getLocation();
		int size = n.getSize();
		List<Point> poss = new ArrayList<Point>();
		for (int i = 0; i < size; i++) {
			if ((current = getDistance(e, n, i, -1)) < distance) {
				distance = current;
				poss.clear();
				poss.add(new Point(i, -1));
			} else if (current == distance) {
				poss.add(new Point(i, -1));
			}
			if ((current = getDistance(e, n, i, size)) < distance) {
				distance = current;
				poss.clear();
				poss.add(new Point(i, size));
			} else if (current == distance) {
				poss.add(new Point(i, size));
			}
		}
		for (int i = 0; i < size; i++) {
			if ((current = getDistance(e, n, -1, i)) < distance) {
				distance = current;
				poss.clear();
				poss.add(new Point(-1, i));
			} else if (current == distance) {
				poss.add(new Point(-1, i));
			}
			if ((current = getDistance(e, n, size, i)) < distance) {
				distance = current;
				poss.clear();
				poss.add(new Point(size, i));
			} else if (current == distance) {
				poss.add(new Point(size, i));
			}
		}
		Point p = poss.isEmpty() ? new Point(0, 0) : getLogicalDestination(e, n, poss);
		return l.transform(p.getX(), p.getY(), 0);
	}

	/**
	 * Gets the logical destination from the points array.
	 * 
	 * @param poss
	 *            The possible points.
	 * @return The point used to calculate the destination.
	 */
	private static Point getLogicalDestination(Entity e, Entity n, List<Point> poss) {
		int diffX = Integer.MAX_VALUE;
		int diffY = Integer.MAX_VALUE;
		Point destination = poss.get(0);
		Location el = e.getLocation();
		Location nl = n.getLocation();
		for (Point p : poss) {
			int curX = el.getX() - (nl.getX() + p.getX());
			int curY = el.getY() - (nl.getY() + p.getY());
			if (curX < 0) {
				curX = -curX;
			}
			if (curY < 0) {
				curY = -curY;
			}
			if (curX < diffX) {
				if ((diffX - curX) > (curY - diffY)) {
					destination = p;
					diffX = curX;
					diffY = curY;
					continue;
				}
			}
			if (curY < diffY) {
				if ((diffY - curY) > (curX - diffX)) {
					destination = p;
					diffX = curX;
					diffY = curY;
				}
			}
		}
		return destination;
	}

	/**
	 * Checks if the mover can interact from a given point.
	 * 
	 * @param p
	 *            The point where the mover will stand.
	 * @param n
	 *            The node.
	 * @return {@code True} if the mover can interact from the point to the
	 *         node.
	 */
	public static boolean canInteract(Point p, Entity n) {
		Location l = n.getLocation();
		if (p == null || l == null) {
			return true;
		}
		if (p.getX() == l.getX() && p.getY() == l.getY()) {
			return !(n instanceof Entity);
		}
		for (int x = l.getX(); x < l.getX() + n.getSize(); x++) {
			if (p.getX() == x) {
				if (p.getY() == l.getY() - 1 || p.getY() == l.getY() + n.getSize()) {
					return true;
				}
			}
		}
		for (int y = l.getY(); y < l.getY() + n.getSize(); y++) {
			if (p.getY() == y) {
				if (p.getX() == l.getX() - 1 || p.getX() == l.getX() + n.getSize()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks the distance between the entity and the destination location.
	 * 
	 * @param e
	 *            The entity.
	 * @param n
	 *            The node.
	 * @param diffX
	 *            The x difference between the destination location & the node
	 *            location.
	 * @param diffY
	 *            The y difference between the destination location & the node
	 *            location.
	 * @return The distance.
	 */
	private static int getDistance(Entity e, Entity n, int diffX, int diffY) {
		Location l = n.getLocation().transform(diffX, diffY, 0);
		if (World.getWorld().getMask(l.getPlane(), l.getX(), l.getY()) > 0) {
			return Integer.MAX_VALUE;
		}
		return e.getLocation().distance(l);
	}

	/**
	 * Represents a point.
	 * 
	 * @author Emperor
	 * 
	 */
	static final class Point {

		/**
		 * The x-coordinate.
		 */
		private final int x;

		/**
		 * The y-coordinate.
		 */
		private final int y;

		/**
		 * The difference x between previous and current point.
		 */
		private final int diffX;

		/**
		 * The difference y between previous and current point.
		 */
		private final int diffY;

		/**
		 * Constructs a new {@code Point} {@code Object}.
		 * 
		 * @param x
		 *            The x-coordinate.
		 * @param y
		 *            The y-coordinate.
		 */
		public Point(int x, int y) {
			this(x, y, 0, 0);
		}

		/**
		 * Constructs a new {@code Point} {@code Object}.
		 * 
		 * @param x
		 *            The x-coordinate.
		 * @param y
		 *            The y-coordinate.
		 * @param diffX
		 *            The difference x between previous and current point.
		 * @param diffY
		 *            The difference y between previous and current point.
		 */
		public Point(int x, int y, int diffX, int diffY) {
			this.x = x;
			this.y = y;
			this.diffX = diffX;
			this.diffY = diffY;
		}

		/**
		 * Gets the x.
		 * 
		 * @return The x.
		 */
		public int getX() {
			return x;
		}

		/**
		 * Gets the y.
		 * 
		 * @return The y.
		 */
		public int getY() {
			return y;
		}

		/**
		 * Gets the diffX.
		 * 
		 * @return The diffX.
		 */
		public int getDiffX() {
			return diffX;
		}

		/**
		 * Gets the diffY.
		 * 
		 * @return The diffY.
		 */
		public int getDiffY() {
			return diffY;
		}
	}

	/**
	 * @return the mover
	 */
	public Entity getMover() {
		return mover;
	}

}