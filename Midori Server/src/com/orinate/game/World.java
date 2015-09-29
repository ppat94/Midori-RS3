package com.orinate.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.orinate.Constants;
import com.orinate.game.content.skills.impl.divination.Wisp;
import com.orinate.game.content.skills.impl.divination.WispInfo;
import com.orinate.game.core.GameCore;
import com.orinate.game.model.Entity;
import com.orinate.game.model.Location;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.npc.NPC;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.region.GroundItem;
import com.orinate.game.model.region.Region;
import com.orinate.game.model.region.WorldObject;
import com.orinate.game.model.route.Flags;
import com.orinate.io.OutBuffer;
import com.orinate.network.codec.login.LoginDecoder.LoginType;
import com.orinate.util.Utilities;
import com.orinate.util.entity.EntityList;

/**
 * @author Tom
 * 
 */
public class World {

	private static final World world = new World();

	private static EntityList<Player> lobbyPlayers = new EntityList<>(2500);
	private static EntityList<Player> worldPlayers = new EntityList<>(2500);

	private static EntityList<NPC> npcs = new EntityList<>(65000);

	private final Map<Integer, Region> regions = Collections.synchronizedMap(new HashMap<Integer, Region>());

	private World() {
	}

	public void register(Player player, int returnCode, LoginType loginType) {
		if (returnCode == 2) {
			if (loginType.equals(LoginType.LOBBY)) {
				if (inLobby(player)) {
					returnCode = 5;
				} else if (inWorld(player)) {
					returnCode = 5;
				}
			} else {
				if (inWorld(player)) {
					returnCode = 5;
				}
			}
		}
		if (returnCode == 2) {
			switch (loginType) {
			case GAME:
				worldPlayers.add(player);
				break;
			case LOBBY:
				lobbyPlayers.add(player);
				break;
			}

			if (loginType.equals(LoginType.GAME)) {
				if (!inLobby(player)) {
					if (returnCode == 2) {
						returnCode = 13;
					}
				}
			}
		}

		writeReturnCode(player, returnCode, loginType);
		if (returnCode == 2) {
			player.sendLoginData(loginType);
		}
	}

	public static Player getPlayer(String username) {
		for (Player player : worldPlayers) {
			if (player == null)
				continue;
			if (player.getDefinition().getUsername().equalsIgnoreCase(username))
				return player;
		}
		return null;
	}

	private void writeReturnCode(Player player, int returnCode, LoginType loginType) {
		if (returnCode != 2) {
			OutBuffer response = new OutBuffer();
			response.putByte(returnCode);
			player.write(response);
			return;
		}

		if (loginType.equals(LoginType.LOBBY)) {
			OutBuffer buffer = new OutBuffer();
			buffer.putVarByte(2);

			buffer.putByte(2);
			buffer.putByte(1);
			buffer.putByte(1);

			buffer.putTri(343932928);
			buffer.putByte(0);
			buffer.putByte(1);
			buffer.putByte(1);

			buffer.putLong(1386299915556L);
			buffer.put5ByteInteger(372495705);
			buffer.putByte(0x1);

			buffer.putInt(0);
			buffer.putInt(0);
			buffer.putShort(3843);
			buffer.putShort(0);
			buffer.putShort(32512);

			buffer.putInt(0);
			buffer.putByte(3);
			buffer.putShort(53791);
			buffer.putShort(53791);

			buffer.putByte(0);
			buffer.putJagString(player.getDefinition().getDisplayName());
			buffer.putByte(10);

			buffer.putInt(27900335);
			buffer.putShort(1);
			buffer.putJagString(Constants.HOST);
			buffer.finishVarByte();
			player.write(buffer);
		} else {
			OutBuffer response = new OutBuffer();
			response.putVarShort(2);
			response.putByte(1);
			for (Entry<Integer, Object> entry : player.getInterfaceSettings().entrySet()) {
				if (entry == null)
					continue;

				int key = entry.getKey();
				int setting = (Integer) entry.getValue();

				response.putShort(key);
				response.putInt(setting);
			}
			response.finishVarShort();
			player.write(response);

			/* World response. */
			OutBuffer buffer = new OutBuffer();
			buffer.putVarByte(2);

			buffer.putByte(player.getRights());
			buffer.putByte(0);
			buffer.putByte(1);
			buffer.putByte(0);
			buffer.putByte(0);
			buffer.putByte(0);

			buffer.putShort(player.getIndex());
			buffer.putByte(1);
			buffer.putTri(0);

			buffer.putByte(1);// members.
			buffer.putString(player.getDefinition().getUsername());

			buffer.finishVarByte();
			player.write(buffer);
		}
	}

	private boolean inWorld(Player player) {
		for (Player p : worldPlayers) {
			if (p == null) {
				continue;
			}
			if (p.getDefinition().getUsername().equalsIgnoreCase(player.getDefinition().getUsername())) {
				return true;
			}
		}
		return false;
	}

	public NPC spawnNpc(int id, Location location) {
		NPC npc = null;
		if (WispInfo.forNpcId(id) != null) {
			npc = new Wisp(id, location);
		} else {
			npc = new NPC(id, location);
		}
		return npc;
	}

	public boolean inLobby(Player player) {
		for (Player p : lobbyPlayers) {
			if (p == null) {
				continue;
			}
			if (p.getDefinition().getUsername().equalsIgnoreCase(player.getDefinition().getUsername())) {
				return true;
			}
		}
		return false;
	}

	public Region getRegion(int regionId) {
		return getRegion(regionId, false);
	}

	public Region getRegion(int regionId, boolean load) {
		Region region = regions.get(regionId);
		if (region == null) {
			region = new Region(regionId);
			regions.put(regionId, region);
		}
		if (load) {
			region.checkLoadMap();
		}
		return region;
	}

	public final boolean checkWalkStep(int plane, int x, int y, int dir, int size) {
		return checkWalkStep(plane, x, y, Utilities.DIRECTION_DELTA_X[dir], Utilities.DIRECTION_DELTA_Y[dir], size);
	}

	public final boolean checkWalkStep(int plane, int x, int y, int xOffset, int yOffset, int size) {
		if (size == 1) {
			int mask = getMask(plane, x + xOffset, y + yOffset);
			if (xOffset == -1 && yOffset == 0)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST)) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_WEST)) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH)) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH)) == 0;
			if (xOffset == -1 && yOffset == -1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0 && (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST)) == 0 && (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH)) == 0;
			if (xOffset == 1 && yOffset == -1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0 && (getMask(plane, x + 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_WEST)) == 0 && (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH)) == 0;
			if (xOffset == -1 && yOffset == 1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0 && (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST)) == 0 && (getMask(plane, x, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH)) == 0;
			if (xOffset == 1 && yOffset == 1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0 && (getMask(plane, x + 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_WEST)) == 0 && (getMask(plane, x, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH)) == 0;
		} else if (size == 2) {
			if (xOffset == -1 && yOffset == 0)
				return (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0 && (getMask(plane, x - 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (getMask(plane, x + 2, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0 && (getMask(plane, x + 2, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0 && (getMask(plane, x + 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (getMask(plane, x, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0 && (getMask(plane, x + 1, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == -1 && yOffset == -1)
				return (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) == 0 && (getMask(plane, x - 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0 && (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) == 0;
			if (xOffset == 1 && yOffset == -1)
				return (getMask(plane, x + 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) == 0 && (getMask(plane, x + 2, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0 && (getMask(plane, x + 2, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == -1 && yOffset == 1)
				return (getMask(plane, x - 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) == 0 && (getMask(plane, x - 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0 && (getMask(plane, x, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == 1 && yOffset == 1)
				return (getMask(plane, x + 1, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) == 0 && (getMask(plane, x + 2, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0 && (getMask(plane, x + 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
		} else {
			if (xOffset == -1 && yOffset == 0) {
				if ((getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0 || (getMask(plane, x - 1, -1 + (y + size)) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x - 1, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 0) {
				if ((getMask(plane, x + size, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0 || (getMask(plane, x + size, y - (-size + 1)) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + size, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == -1) {
				if ((getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0 || (getMask(plane, x + size - 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == 1) {
				if ((getMask(plane, x, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0 || (getMask(plane, x + (size - 1), y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == -1) {
				if ((getMask(plane, x - 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x - 1, y + (-1 + sizeOffset)) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0 || (getMask(plane, sizeOffset - 1 + x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == -1) {
				if ((getMask(plane, x + size, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x + size, sizeOffset + (-1 + y)) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0 || (getMask(plane, x + sizeOffset, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == 1) {
				if ((getMask(plane, x - 1, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x - 1, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0 || (getMask(plane, -1 + (x + sizeOffset), y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 1) {
				if ((getMask(plane, x + size, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0 || (getMask(plane, x + size, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the location is permitted for an entity.
	 * 
	 * @param z
	 *            The plane.
	 * @param x
	 *            The absolute x-coordinate.
	 * @param y
	 *            The absolute y-coordinate.
	 * @return The clipping flags.
	 */
	public static boolean isPermitted(int z, int x, int y) {
		int flag = getWorld().getMask(z, x, y);
		return flag == 0 || ((flag & Flags.WALLOBJ_NORTH) == 0 || (flag & Flags.WALLOBJ_EAST) == 0 || (flag & Flags.WALLOBJ_SOUTH) == 0 || (flag & Flags.WALLOBJ_WEST) == 0);
	}

	public static final void updateEntityRegion(Entity entity) {
		if (entity.isFinished()) {
			if (entity instanceof Player) {
				getWorld().getRegion(entity.getLastRegionId()).removePlayerIndex(entity.getIndex());
			} else {
				getWorld().getRegion(entity.getLastRegionId()).removeNPCIndex(entity.getIndex());
			}
			return;
		}
		int regionId = entity.getLocation().getRegionId();
		if (entity.getLastRegionId() != regionId) {
			if (entity instanceof Player) {
				if (entity.getLastRegionId() > 0) {
					getWorld().getRegion(entity.getLastRegionId()).removePlayerIndex(entity.getIndex());
				}
				Region region = getWorld().getRegion(regionId);
				region.addPlayerIndex(entity.getIndex());
			} else {
				if (entity.getLastRegionId() > 0) {
					getWorld().getRegion(entity.getLastRegionId()).removeNPCIndex(entity.getIndex());
				}
				getWorld().getRegion(regionId).addNPCIndex(entity.getIndex());
			}
			entity.setLastRegionId(regionId);
		}
	}

	public int getMask(int plane, int x, int y) {
		Location tile = new Location(x, y, plane);
		Region region = getRegion(tile.getRegionId());
		if (region == null) {
			return -1;
		}
		return region.getMask(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion());
	}

	public int getRotation(int plane, int x, int y) {
		Location tile = new Location(x, y, plane);
		int regionId = tile.getRegionId();
		Region region = getRegion(regionId);
		if (region == null) {
			return 0;
		}
		int baseLocalX = x - ((regionId >> 8) * 64);
		int baseLocalY = y - ((regionId & 0xff) * 64);
		return region.getRotation(tile.getPlane(), baseLocalX, baseLocalY);
	}

	public final void addGroundItem(Item item, final Location tile, final Player owner, long hiddenTime, boolean publiclyVisible) {
		if (item.getDefinitions().isStackable() && !publiclyVisible) {
			final Region region = getWorld().getRegion(tile.getRegionId());
			GroundItem g = region.getGroundItem(item.getId(), tile);
			if (g != null && !g.isPubliclyVisible()) {
				this.removeGroundItem(owner, g, false);
				item = new Item(item.getId(), item.getAmount() + g.getAmount());
			}
		}
		addGroundItem(item, tile, owner, hiddenTime, publiclyVisible, 180);
	}

	public final void addGroundItem(final Item item, final Location tile, final Player owner, long hiddenTime, boolean publiclyVisible, final int publicTime) {
		final GroundItem floorItem = new GroundItem(item, tile, owner, publiclyVisible);
		final Region region = getWorld().getRegion(tile.getRegionId());
		if (region.getFloorItems() == null)
			region.setFloorItems(new ArrayList<GroundItem>());
		region.getFloorItems().add(floorItem);
		if (!publiclyVisible && hiddenTime != -1) {
			if (owner != null)
				owner.getWriter().sendGroundItem(floorItem);
			GameCore.getSlowExecutor().schedule(new Runnable() {
				@Override
				public void run() {
					try {
						if (!region.getFloorItems().contains(floorItem))
							return;
						int regionId = tile.getRegionId();
						floorItem.setPubliclyVisible(true);
						for (Player player : worldPlayers) {
							if (player == null || player == owner || player.isFinished() || player.getLocation().getPlane() != tile.getPlane() || !player.getMapRegionsIds().contains(regionId))
								continue;
							player.getWriter().sendGroundItem(floorItem);
						}
						removeGroundItem(floorItem, publicTime);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}, hiddenTime, TimeUnit.SECONDS);
			return;
		}
		int regionId = tile.getRegionId();
		for (Player player : worldPlayers) {
			if (player == null || player.isFinished() || player.getLocation().getPlane() != tile.getPlane() || !player.getMapRegionsIds().contains(regionId))
				continue;
			player.getWriter().sendGroundItem(floorItem);
		}
		removeGroundItem(floorItem, publicTime);
	}

	public final void addGroundItem(final Item item, final Location loc) {
		final GroundItem floorItem = new GroundItem(item, loc, null, false);
		final Region region = getRegion(loc.getRegionId());
		if (region.getFloorItems() == null)
			region.setFloorItems(new ArrayList<GroundItem>());
		region.getFloorItems().add(floorItem);
		int regionId = loc.getRegionId();
		for (Player player : worldPlayers) {
			if (player == null || !player.isOnline() || player.getLocation().getPlane() != loc.getPlane() || !player.getMapRegionsIds().contains(regionId))
				continue;
			player.getWriter().sendGroundItem(floorItem);
		}
	}

	private final void removeGroundItem(final GroundItem floorItem, long publicTime) {
		if (publicTime < 0) {
			return;
		}
		GameCore.getSlowExecutor().schedule(new Runnable() {
			@Override
			public void run() {
				try {
					int regionId = floorItem.getLocation().getRegionId();
					Region region = getRegion(regionId);
					if (!region.getFloorItems().contains(floorItem))
						return;
					region.getFloorItems().remove(floorItem);
					for (Player player : World.getPlayers()) {
						if (player == null || player.isFinished() || player.getLocation().getPlane() != floorItem.getLocation().getPlane() || !player.getMapRegionsIds().contains(regionId))
							continue;
						player.getWriter().sendRemoveGroundItem(floorItem);
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, publicTime, TimeUnit.SECONDS);
	}

	public final boolean removeGroundItem(Player player, GroundItem floorItem) {
		return removeGroundItem(player, floorItem, true);
	}

	public final boolean removeGroundItem(Player player, GroundItem floorItem, boolean add) {
		int regionId = floorItem.getLocation().getRegionId();
		Region region = getRegion(regionId);
		if (region.getFloorItems() == null)
			return false;
		if (!region.getFloorItems().contains(floorItem))
			return false;
		if (!player.getInventory().hasFreeSlots() && (!floorItem.getDefinitions().isStackable() || !player.getInventory().containsItem(floorItem.getId(), 1)))
			return false;
		region.getFloorItems().remove(floorItem);
		if (add)
			player.getInventory().addItem(floorItem.getId(), floorItem.getAmount());
		if (!floorItem.isPubliclyVisible()) {
			player.getWriter().sendRemoveGroundItem(floorItem);
			return true;
		} else {
			for (Player p2 : World.getPlayers()) {
				if (p2 == null || !p2.isOnline() || p2.getLocation().getPlane() != floorItem.getLocation().getPlane() || !p2.getMapRegionsIds().contains(regionId))
					continue;
				p2.getWriter().sendRemoveGroundItem(floorItem);
			}
			return true;
		}
	}

	public void removeTimedObject(final WorldObject object, long time, final boolean clip) {
		final int regionId = object.getLocation().getRegionId();
		final WorldObject realObject = object == null ? null : new WorldObject(object.getId(), object.getType(), object.getRotation(), object.getLocation().getX(), object.getLocation().getY(), object.getLocation().getPlane());
		removeObject(object, clip);
		GameCore.getSlowExecutor().schedule(new Runnable() {
			@Override
			public void run() {
				try {
					getRegion(regionId).removeRemovedObject(object);
					if (clip) {
						int baseLocalX = object.getLocation().getX() - ((regionId >> 8) * 64);
						int baseLocalY = object.getLocation().getY() - ((regionId & 0xFF) * 64);
						getRegion(regionId).addMapObject(realObject, baseLocalX, baseLocalY);
					}
					for (Player p2 : worldPlayers) {
						if (p2 == null || !p2.isOnline() || p2.isFinished() || !p2.getMapRegionsIds().contains(regionId)) {
							continue;
						}
						p2.getWriter().sendSpawnObject(realObject);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}, time, TimeUnit.MILLISECONDS);
	}

	private void removeObject(WorldObject object, boolean clip) {
		int regionId = object.getLocation().getRegionId();
		getRegion(regionId).addRemoveObject(object);
		if (clip) {
			int baseLocalX = object.getLocation().getX() - ((regionId >> 8) * 64);
			int baseLocalY = object.getLocation().getY() - ((regionId & 0xFF) * 64);
			getRegion(regionId).removeMapObject(object, baseLocalX, baseLocalY);
		}
		synchronized (worldPlayers) {
			for (Player p2 : worldPlayers) {
				if (p2 == null || !p2.isOnline() || p2.isFinished() || !p2.getMapRegionsIds().contains(regionId)) {
					continue;
				}
				p2.getWriter().sendDestroyObject(object);
			}
		}
	}

	public void spawnTimedObject(final WorldObject object, long time) {
		spawnTimedObject(object, time, false);
	}

	public void spawnTimedObject(final WorldObject object, long time, final boolean clip) {
		final int regionId = object.getLocation().getRegionId();
		WorldObject mapObject = getRegion(regionId).getRealObject(object);
		final WorldObject realObject = mapObject == null ? null : new WorldObject(mapObject.getId(), mapObject.getType(), mapObject.getRotation(), object.getLocation().getX(), object.getLocation().getY(), object.getLocation().getPlane());
		spawnObject(object, clip);
		final int baseLocalX = object.getLocation().getX() - ((regionId >> 8) * 64);
		final int baseLocalY = object.getLocation().getY() - ((regionId & 0xFF) * 64);
		if (realObject != null && clip) {
			getRegion(regionId).removeMapObject(realObject, baseLocalX, baseLocalY);
		}
		GameCore.getSlowExecutor().schedule(new Runnable() {
			@Override
			public void run() {
				try {
					getRegion(regionId).removeObject(object);
					if (clip) {
						getRegion(regionId).removeMapObject(object, baseLocalX, baseLocalY);
						if (realObject != null) {
							int baseLocalX = object.getLocation().getX() - ((regionId >> 8) * 64);
							int baseLocalY = object.getLocation().getY() - ((regionId & 0xff) * 64);
							getRegion(regionId).addMapObject(realObject, baseLocalX, baseLocalY);
						}
					}
					for (Player p2 : worldPlayers) {
						if (p2 == null || !p2.isOnline() || p2.isFinished() || !p2.getMapRegionsIds().contains(regionId)) {
							continue;
						}
						if (realObject != null) {
							p2.getWriter().sendSpawnObject(realObject);
						} else {
							p2.getWriter().sendDestroyObject(object);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}, time, TimeUnit.MILLISECONDS);
	}

	private void spawnObject(WorldObject object, boolean clip) {
		int regionId = object.getLocation().getRegionId();
		getRegion(regionId).addObject(object);
		if (clip) {
			int baseLocalX = object.getLocation().getX() - ((regionId >> 8) * 64);
			int baseLocalY = object.getLocation().getY() - ((regionId & 0xFF) * 64);
			getRegion(regionId).addMapObject(object, baseLocalX, baseLocalY);
		}
		synchronized (worldPlayers) {
			for (Player p2 : worldPlayers) {
				if (p2 == null || !p2.isOnline() || p2.isFinished() || !p2.getMapRegionsIds().contains(regionId)) {
					continue;
				}
				p2.getWriter().sendSpawnObject(object);
			}
		}
	}

	public static void removeLobbyPlayer(Player player) {
		lobbyPlayers.remove(player);
	}

	public static void addLobbyPlayer(Player player) {
		lobbyPlayers.add(player);
	}

	public static EntityList<Player> getPlayers() {
		return worldPlayers;
	}

	public int getPlayerCount() {
		return worldPlayers.size();
	}

	public static World getWorld() {
		return world;
	}

	public static EntityList<NPC> getNpcs() {
		return npcs;
	}

	public static final void addNPC(NPC npc) {
		npcs.add(npc);
	}

	public static final void removeNPC(NPC npc) {
		npcs.remove(npc);
	}

	public WorldObject getObject(Location location) {
		int regionId = location.getRegionId();
		int baseLocalX = location.getX() - ((regionId >> 8) * 64);
		int baseLocalY = location.getY() - ((regionId & 0xff) * 64);
		return getRegion(regionId).getObject(location.getPlane(), baseLocalX, baseLocalY);
	}

	public static EntityList<Player> getLobbyPlayers() {
		return lobbyPlayers;
	}
}