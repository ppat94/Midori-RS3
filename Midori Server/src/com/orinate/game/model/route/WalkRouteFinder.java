package com.orinate.game.model.route;

import com.orinate.game.World;
import com.orinate.game.model.region.Region;
import com.orinate.game.model.region.RegionMap;

/**
 * @author Mangis
 * 
 */
public class WalkRouteFinder {

	private static final int GRAPH_SIZE = 128;
	private static final int QUEUE_SIZE = (GRAPH_SIZE * GRAPH_SIZE) / 4;
	private static final int ALTERNATIVE_ROUTE_MAX_DISTANCE = 100;
	private static final int ALTERNATIVE_ROUTE_RANGE = 10;

	private static final int DIR_NORTH = 0x1;
	private static final int DIR_EAST = 0x2;
	private static final int DIR_SOUTH = 0x4;
	private static final int DIR_WEST = 0x8;

	private static final int[][] directions = new int[GRAPH_SIZE][GRAPH_SIZE];
	private static final int[][] distances = new int[GRAPH_SIZE][GRAPH_SIZE];
	private static final int[][] clip = new int[GRAPH_SIZE][GRAPH_SIZE];
	private static final int[] bufferX = new int[QUEUE_SIZE];
	private static final int[] bufferY = new int[QUEUE_SIZE];
	private static int exitX = -1;
	private static int exitY = -1;
	private static boolean isAlternative;

	public static boolean debug = true;
	public static long debug_transmittime = 0;

	protected static int findRoute(int srcX, int srcY, int srcZ, int srcSizeXY, RouteStrategy strategy, boolean findAlternative) {
		isAlternative = false;
		for (int x = 0; x < GRAPH_SIZE; x++) {
			for (int y = 0; y < GRAPH_SIZE; y++) {
				directions[x][y] = 0;
				distances[x][y] = 99999999;
			}
		}

		if (debug) {
			long start = System.nanoTime();
			transmitClipData(srcX, srcY, srcZ);
			debug_transmittime = System.nanoTime() - start;
		} else {
			transmitClipData(srcX, srcY, srcZ);
		}

		boolean found = false;
		switch (srcSizeXY) {
		case 1:
			found = performCalculationS1(srcX, srcY, strategy);
			break;
		case 2:
			found = performCalculationS2(srcX, srcY, strategy);
			break;
		default:
			found = performCalculationSX(srcX, srcY, srcSizeXY, strategy);
			break;
		}

		if (!found && !findAlternative)
			return -1;

		int graphBaseX = srcX - (GRAPH_SIZE / 2);
		int graphBaseY = srcY - (GRAPH_SIZE / 2);
		int endX = exitX;
		int endY = exitY;

		if (!found && findAlternative) {
			isAlternative = true;
			int lowestCost = Integer.MAX_VALUE;
			int lowestDistance = Integer.MAX_VALUE;

			int approxDestX = strategy.getApproxDestinationX();
			int approxDestY = strategy.getApproxDestinationY();

			for (int checkX = (approxDestX - ALTERNATIVE_ROUTE_RANGE); checkX <= (approxDestX + ALTERNATIVE_ROUTE_RANGE); checkX++) {
				for (int checkY = (approxDestY - ALTERNATIVE_ROUTE_RANGE); checkY <= (approxDestY + ALTERNATIVE_ROUTE_RANGE); checkY++) {
					int graphX = checkX - graphBaseX;
					int graphY = checkY - graphBaseY;
					if (graphX < 0 || graphY < 0 || graphX >= GRAPH_SIZE || graphY >= GRAPH_SIZE || distances[graphX][graphY] >= ALTERNATIVE_ROUTE_MAX_DISTANCE)
						continue;
					int deltaX = 0;
					int deltaY = 0;
					if (approxDestX <= checkX) {
						deltaX = 1 - approxDestX - (strategy.getApproxDestinationSizeX() - checkX);
					} else
						deltaX = approxDestX - checkX;
					if (approxDestY <= checkY) {
						deltaY = 1 - approxDestY - (strategy.getApproxDestinationSizeY() - checkY);
					} else
						deltaY = approxDestY - checkY;

					int cost = (deltaX * deltaX) + (deltaY * deltaY);
					if (cost < lowestCost || (cost <= lowestCost && distances[graphX][graphY] < lowestDistance)) {
						lowestCost = cost;
						lowestDistance = distances[graphX][graphY];
						endX = checkX;
						endY = checkY;
					}
				}
			}

			if (lowestCost == Integer.MAX_VALUE || lowestDistance == Integer.MAX_VALUE)
				return -1;
		}

		if (endX == srcX && endY == srcY)
			return 0;

		int steps = 0;
		int traceX = endX;
		int traceY = endY;
		int direction = directions[traceX - graphBaseX][traceY - graphBaseY];
		int lastwritten = direction;
		bufferX[steps] = traceX;
		bufferY[steps++] = traceY;
		while (traceX != srcX || traceY != srcY) {
			if (lastwritten != direction) {
				bufferX[steps] = traceX;
				bufferY[steps++] = traceY;
				lastwritten = direction;
			}

			if ((direction & DIR_EAST) != 0)
				traceX++;
			else if ((direction & DIR_WEST) != 0)
				traceX--;

			if ((direction & DIR_NORTH) != 0)
				traceY++;
			else if ((direction & DIR_SOUTH) != 0)
				traceY--;

			direction = directions[traceX - graphBaseX][traceY - graphBaseY];
		}

		return steps;
	}

	private static boolean performCalculationS1(int srcX, int srcY, RouteStrategy strategy) {
		int[][] _directions = directions;
		int[][] _distances = distances;
		int[][] _clip = clip;
		int[] _bufferX = bufferX;
		int[] _bufferY = bufferY;

		int graphBaseX = srcX - (GRAPH_SIZE / 2);
		int graphBaseY = srcY - (GRAPH_SIZE / 2);
		int currentX = srcX;
		int currentY = srcY;
		int currentGraphX = srcX - graphBaseX;
		int currentGraphY = srcY - graphBaseY;

		_distances[currentGraphX][currentGraphY] = 0;
		_directions[currentGraphX][currentGraphY] = 99;

		int read = 0, write = 0;
		_bufferX[write] = currentX;
		_bufferY[write++] = currentY;

		while (read != write) {
			currentX = _bufferX[read];
			currentY = _bufferY[read];
			read = (read + 1) & (QUEUE_SIZE - 1);

			currentGraphX = currentX - graphBaseX;
			currentGraphY = currentY - graphBaseY;

			if (strategy.canExit(currentX, currentY, 1, _clip, graphBaseX, graphBaseY)) {
				exitX = currentX;
				exitY = currentY;
				return true;
			}

			int nextDistance = _distances[currentGraphX][currentGraphY] + 1;
			if (currentGraphX > 0 && _directions[currentGraphX - 1][currentGraphY] == 0 && (_clip[currentGraphX - 1][currentGraphY] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE)) == 0) {
				_bufferX[write] = currentX - 1;
				_bufferY[write] = currentY;
				write = (write + 1) & (QUEUE_SIZE - 1);

				_directions[currentGraphX - 1][currentGraphY] = DIR_EAST;
				_distances[currentGraphX - 1][currentGraphY] = nextDistance;
			}
			if (currentGraphX < (GRAPH_SIZE - 1) && _directions[currentGraphX + 1][currentGraphY] == 0 && (_clip[currentGraphX + 1][currentGraphY] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE)) == 0) {
				_bufferX[write] = currentX + 1;
				_bufferY[write] = currentY;
				write = (write + 1) & (QUEUE_SIZE - 1);

				_directions[currentGraphX + 1][currentGraphY] = DIR_WEST;
				_distances[currentGraphX + 1][currentGraphY] = nextDistance;
			}
			if (currentGraphY > 0 && _directions[currentGraphX][currentGraphY - 1] == 0 && (_clip[currentGraphX][currentGraphY - 1] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE)) == 0) {
				_bufferX[write] = currentX;
				_bufferY[write] = currentY - 1;
				write = (write + 1) & (QUEUE_SIZE - 1);

				_directions[currentGraphX][currentGraphY - 1] = DIR_NORTH;
				_distances[currentGraphX][currentGraphY - 1] = nextDistance;
			}
			if (currentGraphY < (GRAPH_SIZE - 1) && _directions[currentGraphX][currentGraphY + 1] == 0 && (_clip[currentGraphX][currentGraphY + 1] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE)) == 0) {
				_bufferX[write] = currentX;
				_bufferY[write] = currentY + 1;
				write = (write + 1) & (QUEUE_SIZE - 1);

				_directions[currentGraphX][currentGraphY + 1] = DIR_SOUTH;
				_distances[currentGraphX][currentGraphY + 1] = nextDistance;
			}
			if (currentGraphX > 0 && currentGraphY > 0 && _directions[currentGraphX - 1][currentGraphY - 1] == 0 && (_clip[currentGraphX - 1][currentGraphY - 1] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHEAST_BLOCKSWALK_ALTERNATIVE)) == 0 && (_clip[currentGraphX - 1][currentGraphY] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE)) == 0 && (clip[currentGraphX][currentGraphY - 1] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE)) == 0) {
				_bufferX[write] = currentX - 1;
				_bufferY[write] = currentY - 1;
				write = (write + 1) & (QUEUE_SIZE - 1);

				_directions[currentGraphX - 1][currentGraphY - 1] = DIR_NORTH | DIR_EAST;
				_distances[currentGraphX - 1][currentGraphY - 1] = nextDistance;
			}
			if (currentGraphX < (GRAPH_SIZE - 1) && currentGraphY > 0 && _directions[currentGraphX + 1][currentGraphY - 1] == 0 && (_clip[currentGraphX + 1][currentGraphY - 1] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHWEST_BLOCKSWALK_ALTERNATIVE)) == 0 && (_clip[currentGraphX + 1][currentGraphY] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE)) == 0 && (_clip[currentGraphX][currentGraphY - 1] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE)) == 0) {
				_bufferX[write] = currentX + 1;
				_bufferY[write] = currentY - 1;
				write = (write + 1) & (QUEUE_SIZE - 1);

				_directions[currentGraphX + 1][currentGraphY - 1] = DIR_NORTH | DIR_WEST;
				_distances[currentGraphX + 1][currentGraphY - 1] = nextDistance;
			}
			if (currentGraphX > 0 && currentGraphY < (GRAPH_SIZE - 1) && _directions[currentGraphX - 1][currentGraphY + 1] == 0 && (_clip[currentGraphX - 1][currentGraphY + 1] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHEAST_BLOCKSWALK_ALTERNATIVE)) == 0 && (_clip[currentGraphX - 1][currentGraphY] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE)) == 0 && (_clip[currentGraphX][currentGraphY + 1] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE)) == 0) {
				_bufferX[write] = currentX - 1;
				_bufferY[write] = currentY + 1;
				write = (write + 1) & (QUEUE_SIZE - 1);

				_directions[currentGraphX - 1][currentGraphY + 1] = DIR_SOUTH | DIR_EAST;
				_distances[currentGraphX - 1][currentGraphY + 1] = nextDistance;
			}
			if (currentGraphX < (GRAPH_SIZE - 1) && currentGraphY < (GRAPH_SIZE - 1) && _directions[currentGraphX + 1][currentGraphY + 1] == 0 && (_clip[currentGraphX + 1][currentGraphY + 1] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHWEST_BLOCKSWALK_ALTERNATIVE)) == 0 && (_clip[currentGraphX + 1][currentGraphY] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE)) == 0 && (_clip[currentGraphX][currentGraphY + 1] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE)) == 0) {
				_bufferX[write] = currentX + 1;
				_bufferY[write] = currentY + 1;
				write = (write + 1) & (QUEUE_SIZE - 1);

				_directions[currentGraphX + 1][currentGraphY + 1] = DIR_SOUTH | DIR_WEST;
				_distances[currentGraphX + 1][currentGraphY + 1] = nextDistance;
			}

		}

		exitX = currentX;
		exitY = currentY;
		return false;
	}

	private static boolean performCalculationS2(int srcX, int srcY, RouteStrategy strategy) {
		return performCalculationSX(srcX, srcY, 2, strategy);
	}

	private static boolean performCalculationSX(int srcX, int srcY, int size, RouteStrategy strategy) {
		int[][] _directions = directions;
		int[][] _distances = distances;
		int[][] _clip = clip;
		int[] _bufferX = bufferX;
		int[] _bufferY = bufferY;

		int graphBaseX = srcX - (GRAPH_SIZE / 2);
		int graphBaseY = srcY - (GRAPH_SIZE / 2);
		int currentX = srcX;
		int currentY = srcY;
		int currentGraphX = srcX - graphBaseX;
		int currentGraphY = srcY - graphBaseY;

		_distances[currentGraphX][currentGraphY] = 0;
		_directions[currentGraphX][currentGraphY] = 99;

		int read = 0, write = 0;
		_bufferX[write] = currentX;
		_bufferY[write++] = currentY;

		while (read != write) {
			currentX = _bufferX[read];
			currentY = _bufferY[read];
			read = (read + 1) & (QUEUE_SIZE - 1);

			currentGraphX = currentX - graphBaseX;
			currentGraphY = currentY - graphBaseY;

			if (strategy.canExit(currentX, currentY, size, _clip, graphBaseX, graphBaseY)) {
				exitX = currentX;
				exitY = currentY;
				return true;
			}

			int nextDistance = _distances[currentGraphX][currentGraphY] + 1;
			if (currentGraphX > 0 && _directions[currentGraphX - 1][currentGraphY] == 0 && (_clip[currentGraphX - 1][currentGraphY] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHEAST_BLOCKSWALK_ALTERNATIVE)) == 0 && (_clip[currentGraphX - 1][currentGraphY + (size - 1)] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHEAST_BLOCKSWALK_ALTERNATIVE)) == 0) {
				exit: do {
					for (int y = 1; y < (size - 1); y++) {
						if ((_clip[currentGraphX - 1][currentGraphY + y] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHEAST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHEAST_BLOCKSWALK_ALTERNATIVE)) != 0)
							break exit;
					}
					_bufferX[write] = currentX - 1;
					_bufferY[write] = currentY;
					write = (write + 1) & (QUEUE_SIZE - 1);

					_directions[currentGraphX - 1][currentGraphY] = DIR_EAST;
					_distances[currentGraphX - 1][currentGraphY] = nextDistance;
				} while (false);
			}
			if (currentGraphX < (GRAPH_SIZE - size) && _directions[currentGraphX + 1][currentGraphY] == 0 && (_clip[currentGraphX + size][currentGraphY] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHWEST_BLOCKSWALK_ALTERNATIVE)) == 0 && (_clip[currentGraphX + size][currentGraphY + (size - 1)] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHWEST_BLOCKSWALK_ALTERNATIVE)) == 0) {
				exit: do {
					for (int y = 1; y < (size - 1); y++) {
						if ((_clip[currentGraphX + size][currentGraphY + y] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHWEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHWEST_BLOCKSWALK_ALTERNATIVE)) != 0)
							break exit;
					}
					_bufferX[write] = currentX + 1;
					_bufferY[write] = currentY;
					write = (write + 1) & (QUEUE_SIZE - 1);

					_directions[currentGraphX + 1][currentGraphY] = DIR_WEST;
					_distances[currentGraphX + 1][currentGraphY] = nextDistance;
				} while (false);
			}
			if (currentGraphY > 0 && _directions[currentGraphX][currentGraphY - 1] == 0 && (_clip[currentGraphX][currentGraphY - 1] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHEAST_BLOCKSWALK_ALTERNATIVE)) == 0 && (_clip[currentGraphX + (size - 1)][currentGraphY - 1] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHWEST_BLOCKSWALK_ALTERNATIVE)) == 0) {
				exit: do {
					for (int y = 1; y < (size - 1); y++) {
						if ((_clip[currentGraphX + y][currentGraphY - 1] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHWEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHEAST_BLOCKSWALK_ALTERNATIVE)) != 0)
							break exit;
					}
					_bufferX[write] = currentX;
					_bufferY[write] = currentY - 1;
					write = (write + 1) & (QUEUE_SIZE - 1);

					_directions[currentGraphX][currentGraphY - 1] = DIR_NORTH;
					_distances[currentGraphX][currentGraphY - 1] = nextDistance;
				} while (false);
			}
			if (currentGraphY < (GRAPH_SIZE - size) && _directions[currentGraphX][currentGraphY + 1] == 0 && (_clip[currentGraphX][currentGraphY + size] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHEAST_BLOCKSWALK_ALTERNATIVE)) == 0 && (_clip[currentGraphX + (size - 1)][currentGraphY + size] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHWEST_BLOCKSWALK_ALTERNATIVE)) == 0) {
				exit: do {
					for (int y = 1; y < (size - 1); y++) {
						if ((_clip[currentGraphX + y][currentGraphY + size] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHEAST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHWEST_BLOCKSWALK_ALTERNATIVE)) != 0)
							break exit;
					}
					_bufferX[write] = currentX;
					_bufferY[write] = currentY + 1;
					write = (write + 1) & (QUEUE_SIZE - 1);

					_directions[currentGraphX][currentGraphY + 1] = DIR_SOUTH;
					_distances[currentGraphX][currentGraphY + 1] = nextDistance;
				} while (false);
			}
			if (currentGraphX > 0 && currentGraphY > 0 && _directions[currentGraphX - 1][currentGraphY - 1] == 0 && (_clip[currentGraphX - 1][currentGraphY - 1] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHEAST_BLOCKSWALK_ALTERNATIVE)) == 0) {
				exit: do {
					for (int y = 1; y < size; y++) {
						if ((_clip[currentGraphX - 1][currentGraphY + (y - 1)] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHEAST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHEAST_BLOCKSWALK_ALTERNATIVE)) != 0 || (_clip[currentGraphX + (y - 1)][currentGraphY - 1] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHWEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHEAST_BLOCKSWALK_ALTERNATIVE)) != 0)
							break exit;
					}
					_bufferX[write] = currentX - 1;
					_bufferY[write] = currentY - 1;
					write = (write + 1) & (QUEUE_SIZE - 1);

					_directions[currentGraphX - 1][currentGraphY - 1] = DIR_NORTH | DIR_EAST;
					_distances[currentGraphX - 1][currentGraphY - 1] = nextDistance;
				} while (false);
			}
			if (currentGraphX < (GRAPH_SIZE - size) && currentGraphY > 0 && _directions[currentGraphX + 1][currentGraphY - 1] == 0 && (_clip[currentGraphX + size][currentGraphY - 1] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHWEST_BLOCKSWALK_ALTERNATIVE)) == 0) {
				exit: do {
					for (int y = 1; y < size; y++) {
						if ((_clip[currentGraphX + size][currentGraphY + (y - 1)] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHWEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHWEST_BLOCKSWALK_ALTERNATIVE)) != 0 || (_clip[currentGraphX + y][currentGraphY - 1] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHWEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHEAST_BLOCKSWALK_ALTERNATIVE)) != 0)
							break exit;
					}
					_bufferX[write] = currentX + 1;
					_bufferY[write] = currentY - 1;
					write = (write + 1) & (QUEUE_SIZE - 1);

					_directions[currentGraphX + 1][currentGraphY - 1] = DIR_NORTH | DIR_WEST;
					_distances[currentGraphX + 1][currentGraphY - 1] = nextDistance;
				} while (false);
			}
			if (currentGraphX > 0 && currentGraphY < (GRAPH_SIZE - size) && _directions[currentGraphX - 1][currentGraphY + 1] == 0 && (_clip[currentGraphX - 1][currentGraphY + size] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHEAST_BLOCKSWALK_ALTERNATIVE)) == 0) {
				exit: do {
					for (int y = 1; y < size; y++) {
						if ((_clip[currentGraphX - 1][currentGraphY + y] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHEAST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHEAST_BLOCKSWALK_ALTERNATIVE)) != 0 || (_clip[currentGraphX + (y - 1)][currentGraphY + size] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHEAST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHWEST_BLOCKSWALK_ALTERNATIVE)) != 0)
							break exit;
					}
					_bufferX[write] = currentX - 1;
					_bufferY[write] = currentY + 1;
					write = (write + 1) & (QUEUE_SIZE - 1);

					_directions[currentGraphX - 1][currentGraphY + 1] = DIR_SOUTH | DIR_EAST;
					_distances[currentGraphX - 1][currentGraphY + 1] = nextDistance;
				} while (false);
			}
			if (currentGraphX < (GRAPH_SIZE - size) && currentGraphY < (GRAPH_SIZE - size) && _directions[currentGraphX + 1][currentGraphY + 1] == 0 && (_clip[currentGraphX + size][currentGraphY + size] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHWEST_BLOCKSWALK_ALTERNATIVE)) == 0) {
				exit: do {
					for (int y = 1; y < size; y++) {
						if ((_clip[currentGraphX + y][currentGraphY + size] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_EAST_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHEAST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHWEST_BLOCKSWALK_ALTERNATIVE)) != 0 || (_clip[currentGraphX + size][currentGraphY + y] & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_NORTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_SOUTH_BLOCKSWALK_ALTERNATIVE | Flags.WALLOBJ_WEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_NORTHWEST_BLOCKSWALK_ALTERNATIVE | Flags.CORNEROBJ_SOUTHWEST_BLOCKSWALK_ALTERNATIVE)) != 0)
							break exit;
					}
					_bufferX[write] = currentX + 1;
					_bufferY[write] = currentY + 1;
					write = (write + 1) & (QUEUE_SIZE - 1);

					_directions[currentGraphX + 1][currentGraphY + 1] = DIR_SOUTH | DIR_WEST;
					_distances[currentGraphX + 1][currentGraphY + 1] = nextDistance;
				} while (false);
			}

		}

		exitX = currentX;
		exitY = currentY;
		return false;
	}

	private static void transmitClipData(int x, int y, int z) {
		int graphBaseX = x - (GRAPH_SIZE / 2);
		int graphBaseY = y - (GRAPH_SIZE / 2);

		for (int transmitRegionX = graphBaseX >> 6; transmitRegionX <= (graphBaseX + (GRAPH_SIZE - 1)) >> 6; transmitRegionX++) {
			for (int transmitRegionY = graphBaseY >> 6; transmitRegionY <= (graphBaseY + (GRAPH_SIZE - 1)) >> 6; transmitRegionY++) {
				int startX = Math.max(graphBaseX, transmitRegionX << 6), startY = Math.max(graphBaseY, transmitRegionY << 6);
				int endX = Math.min(graphBaseX + GRAPH_SIZE, (transmitRegionX << 6) + 64), endY = Math.min(graphBaseY + GRAPH_SIZE, (transmitRegionY << 6) + 64);
				Region region = World.getWorld().getRegion(transmitRegionX << 8 | transmitRegionY, true);
				RegionMap map = region.getRegionMap();
				if (map == null || region.getLoadMapStage() != 2 || !region.isObjectsLoaded()) {
					for (int fillX = startX; fillX < endX; fillX++)
						for (int fillY = startY; fillY < endY; fillY++)
							clip[fillX - graphBaseX][fillY - graphBaseY] = -1;
				} else {
					int[][] masks = map.getMasks()[z];
					for (int fillX = startX; fillX < endX; fillX++) {
						for (int fillY = startY; fillY < endY; fillY++) {
							clip[fillX - graphBaseX][fillY - graphBaseY] = masks[fillX & 0x3F][fillY & 0x3F];
						}
					}
				}
			}
		}
	}

	protected static int[] getLastPathBufferX() {
		return bufferX;
	}

	protected static int[] getLastPathBufferY() {
		return bufferY;
	}

	protected static boolean lastIsAlternative() {
		return isAlternative;
	}
}
