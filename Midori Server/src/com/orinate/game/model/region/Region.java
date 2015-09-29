package com.orinate.game.model.region;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.alex.io.InputStream;
import com.orinate.cache.Cache;
import com.orinate.cache.parsers.ClientScriptMap;
import com.orinate.cache.parsers.ObjectDefinition;
import com.orinate.game.core.GameCore;
import com.orinate.game.model.Location;
import com.orinate.util.Utilities;

/**
 * @author Tom
 * @author Taylor
 */
public class Region {

	public static final int[] OBJECT_SLOTS = new int[] { 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3 };

	private int regionId;
	private int loadStage;
	private boolean objectsLoaded;
	private List<Integer> playersIndexes;
	private List<Integer> npcsIndexes;
	private List<GroundItem> floorItems;
	private RegionMap map;
	private RegionMap clippedMap;
	private WorldObject[][][][] objects;
	private CopyOnWriteArrayList<WorldObject> removedObjects;
	private CopyOnWriteArrayList<WorldObject> spawnedObjects;

	public Region(int regionId) {
		this.regionId = regionId;
		loadMusicFromCache();
	}

	public void checkLoadMap() {
		if (loadStage == 0) {
			loadStage = 1;

			GameCore.getSlowExecutor().execute(new Runnable() {
				@Override
				public void run() {
					try {
						loadRegionMap();
						loadStage = 2;

						if (!objectsLoaded) {
							// TODO: Load objects.
							objectsLoaded = true;
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
		}
	}

	protected void loadRegionMap() {
		int regionX = (regionId >> 8) * 64;
		int regionY = (regionId & 0xff) * 64;
		int landArchiveId = getArchiveId(((regionX >> 3) / 8), ((regionY >> 3) / 8));
		byte[] landContainerData = landArchiveId == -1 ? null : Cache.STORE.getIndexes()[5].getFile(landArchiveId, 0);
		int mapArchiveId = getArchiveId(((regionX >> 3) / 8), ((regionY >> 3) / 8));
		byte[] mapContainerData = mapArchiveId == -1 ? null : Cache.STORE.getIndexes()[5].getFile(mapArchiveId, 3);
		byte[][][] mapSettings = mapContainerData == null ? null : new byte[4][64][64];
		if (mapContainerData != null) {
			InputStream mapStream = new InputStream(mapContainerData);
			for (int plane = 0; plane < 4; plane++) {
				for (int x = 0; x < 64; x++) {
					for (int y = 0; y < 64; y++) {
						int value = mapStream.readUnsignedByte();
						if ((value & 0x1) != 0) {
							mapStream.readByte();
							mapStream.readUnsignedSmart();
						}
						if ((value & 0x2) != 0) {
							mapSettings[plane][x][y] = (byte) mapStream.readByte();
						}
						if ((value & 0x4) != 0)
							mapStream.readUnsignedSmart();
						if ((value & 0x8) != 0)
							mapStream.readByte();
					}
				}
			}
			for (int plane = 0; plane < 4; plane++) {
				for (int x = 0; x < 64; x++) {
					for (int y = 0; y < 64; y++) {
						if ((mapSettings[plane][x][y] & 1) == 1) {
							int z = plane;
							if ((mapSettings[1][x][y] & 2) == 2) {
								z--;
							}
							if (z >= 0 && z <= 3) {
								getForceRegionMap().clipTile(z, x, y);
							}
						}
					}
				}
			}
		}
		if (landContainerData != null) {
			InputStream landStream = new InputStream(landContainerData);
			int objectId = -1;
			int incr;
			while ((incr = landStream.readSmart2()) != 0) {
				objectId += incr;
				int location = 0;
				int incr2;
				while ((incr2 = landStream.readUnsignedSmart()) != 0) {
					location += incr2 - 1;
					int localX = (location >> 6 & 0x3f);
					int localY = (location & 0x3f);
					int plane = location >> 12;
					int objectData = landStream.readUnsignedByte();
					int type = objectData >> 2;
					int rotation = objectData & 0x3;
					if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64)
						continue;
					int objectPlane = plane;
					if (mapSettings != null && (mapSettings[1][localX][localY] & 2) == 2)
						objectPlane--;
					if (objectPlane < 0 || objectPlane >= 4 || plane < 0 || plane >= 4)
						continue;
					addObject(new WorldObject(objectId, type, rotation, localX + regionX, localY + regionY, objectPlane), objectPlane, localX, localY);
				}
			}
		}
	}

	public void addRemoveObject(WorldObject object) {
		if (removedObjects == null) {
			removedObjects = new CopyOnWriteArrayList<>();
		}
		removedObjects.add(object);
	}

	private void addObject(WorldObject object, int plane, int localX, int localY) {
		addMapObject(object, localX, localY);
		if (objects == null) {
			objects = new WorldObject[4][64][64][];
		}
		WorldObject[] tileObjects = objects[plane][localX][localY];
		if (tileObjects == null) {
			objects[plane][localX][localY] = new WorldObject[] { object };
		} else {
			WorldObject[] newTileObjects = new WorldObject[tileObjects.length + 1];
			newTileObjects[tileObjects.length] = object;
			System.arraycopy(tileObjects, 0, newTileObjects, 0, tileObjects.length);
			objects[plane][localX][localY] = newTileObjects;
		}
	}

	public void addMapObject(WorldObject object, int x, int y) {
		if (map == null) {
			map = new RegionMap(regionId, false);
		}
		if (clippedMap == null) {
			clippedMap = new RegionMap(regionId, true);
		}
		int plane = object.getLocation().getPlane();
		int type = object.getType();
		int rotation = object.getRotation();
		if (x < 0 || y < 0 || x >= map.getMasks()[plane].length || y >= map.getMasks()[plane][x].length) {
			return;
		}
		ObjectDefinition objectDef = ObjectDefinition.forId(object.getId());
		if (type == 22 ? objectDef.clipType != 0 : objectDef.clipType == 0) {
			return;
		}
		if (type >= 0 && type <= 3) {
			map.addWall(plane, x, y, type, rotation, objectDef.projectileClipped, true);
			if (objectDef.projectileClipped) {
				clippedMap.addWall(plane, x, y, type, rotation, objectDef.projectileClipped, true);
			}
		} else if (type >= 9 && type <= 21) {
			int sizeX;
			int sizeY;
			if (rotation != 1 && rotation != 3) {
				sizeX = objectDef.sizeX;
				sizeY = objectDef.sizeY;
			} else {
				sizeX = objectDef.sizeY;
				sizeY = objectDef.sizeX;
			}
			map.addObject(plane, x, y, sizeX, sizeY, objectDef.projectileClipped, true);
			if (objectDef.projectileClipped) {
				clippedMap.addObject(plane, x, y, sizeX, sizeY, objectDef.projectileClipped, true);
			}
		}
	}

	public WorldObject getObject(int objectId, Location location) {
		int absX = (regionId >> 8) * 64;
		int absY = (regionId & 0xff) * 64;
		int localX = location.getX() - absX;
		int localY = location.getY() - absY;
		if (localX < 0 || localY < 0 || localX >= 64 || localY >= 64) {
			return null;
		}
		WorldObject removedObject = getRemovedObject(location);
		if (removedObject != null && removedObject.getId() == objectId) {
			return null;
		}
		WorldObject[] mapObjects = getObjects(location.getPlane(), localX, localY);
		if (mapObjects == null) {
			return null;
		}
		for (WorldObject object : mapObjects) {
			if (object.getId() == objectId) {
				return object;
			}
		}
		return null;
	}

	public WorldObject getRealObject(WorldObject spawnObject) {
		int absX = (regionId >> 8) * 64;
		int absY = (regionId & 0xFF) * 64;
		int localX = spawnObject.getLocation().getX() - absX;
		int localY = spawnObject.getLocation().getY() - absY;
		WorldObject[] mapObjects = getObjects(spawnObject.getLocation().getPlane(), localX, localY);
		if (mapObjects == null) {
			return null;
		}
		for (WorldObject object : mapObjects) {
			if (object.getType() == spawnObject.getType()) {
				return object;
			}
		}
		return null;
	}

	public WorldObject[] getObjects(int plane, int x, int y) {
		checkLoadMap();
		if (objects == null) {
			return null;
		}
		return objects[plane][x][y];
	}

	public WorldObject getRemovedObject(Location tile) {
		if (removedObjects == null) {
			return null;
		}
		for (WorldObject object : removedObjects) {
			if (object.getLocation().getX() == tile.getX() && object.getLocation().getY() == tile.getY() && object.getLocation().getPlane() == tile.getPlane()) {
				return object;
			}
		}
		return null;
	}

	public RegionMap getForceRegionMap() {
		if (map == null) {
			map = new RegionMap(regionId, false);
		}
		return map;
	}

	public RegionMap getForceRegionMapClipped() {
		if (clippedMap == null) {
			clippedMap = new RegionMap(regionId, true);
		}
		return clippedMap;
	}

	private int getArchiveId(int regionX, int regionY) {
		return regionX | regionY << 7;
	}

	public RegionMap getRegionMap() {
		return map;
	}

	public boolean isObjectsLoaded() {
		return objectsLoaded;
	}

	public int getLoadMapStage() {
		return loadStage;
	}

	public int getRegionId() {
		return regionId;
	}

	public int getMask(int plane, int localX, int localY) {
		if (map == null || getLoadMapStage() != 2) {
			return -1;
		}
		return map.getMasks()[plane][localX][localY];
	}

	public void addPlayerIndex(int index) {
		if (playersIndexes == null) {
			playersIndexes = new CopyOnWriteArrayList<Integer>();
		}
		playersIndexes.add(index);
	}

	public void addNPCIndex(int index) {
		if (npcsIndexes == null) {
			npcsIndexes = new CopyOnWriteArrayList<Integer>();
		}
		npcsIndexes.add(index);
	}

	public void removePlayerIndex(Integer index) {
		if (playersIndexes == null) {
			return;
		}
		playersIndexes.remove(index);
	}

	public boolean removeNPCIndex(Object index) {
		if (npcsIndexes == null) {
			return false;
		}
		return npcsIndexes.remove(index);
	}

	public List<Integer> getPlayersIndexes() {
		return playersIndexes;
	}

	public List<Integer> getNpcsIndexes() {
		return npcsIndexes;
	}

	public int getRotation(int plane, int localX, int localY) {
		return 0;
	}

	public GroundItem getGroundItem(int id, Location tile) {
		if (floorItems == null) {
			return null;
		}
		for (GroundItem item : floorItems) {
			if (item == null)
				continue;
			if (id == item.getId()) {
				return item;
			}
		}
		return null;
	}

	public List<GroundItem> getFloorItems() {
		return floorItems;
	}

	public void setFloorItems(List<GroundItem> floorItems) {
		this.floorItems = floorItems;
	}

	public void addObject(WorldObject object) {
		if (spawnedObjects == null) {
			spawnedObjects = new CopyOnWriteArrayList<>();
		}
		spawnedObjects.add(object);
	}

	public void removeMapObject(WorldObject object, int x, int y) {
		if (map == null) {
			map = new RegionMap(regionId, false);
		}
		if (clippedMap == null) {
			clippedMap = new RegionMap(regionId, true);
		}
		int plane = object.getLocation().getPlane();
		int type = object.getType();
		int rotation = object.getRotation();
		if (x < 0 || y < 0 || x >= map.getMasks()[plane].length || y >= map.getMasks()[plane][x].length) {
			return;
		}
		ObjectDefinition objectDef = ObjectDefinition.forId(object.getId());
		if (type == 22 ? objectDef.clipType != 0 : objectDef.clipType == 0) {
			return;
		}
		if (type >= 0 && type <= 3) {
			map.removeWall(plane, x, y, type, rotation, objectDef.projectileClipped, true);
			if (objectDef.projectileClipped) {
				clippedMap.removeWall(plane, x, y, type, rotation, objectDef.projectileClipped, true);
			}
		} else if (type >= 9 && type <= 21) {
			int sizeX;
			int sizeY;
			if (rotation != 1 && rotation != 3) {
				sizeX = objectDef.sizeX;
				sizeY = objectDef.sizeY;
			} else {
				sizeX = objectDef.sizeY;
				sizeY = objectDef.sizeX;
			}
			map.removeObject(plane, x, y, sizeX, sizeY, objectDef.projectileClipped, true);
			if (objectDef.projectileClipped) {
				clippedMap.removeObject(plane, x, y, sizeX, sizeY, objectDef.projectileClipped, true);
			}
		}
	}

	public void removeObject(WorldObject object) {
		if (spawnedObjects == null) {
			return;
		}
		spawnedObjects.remove(object);
	}

	public void removeRemovedObject(WorldObject object) {
		if (removedObjects == null) {
			return;
		}
		removedObjects.remove(object);
	}

	public boolean containsObject(int id, Location tile) {
		int absX = (regionId >> 8) * 64;
		int absY = (regionId & 0xff) * 64;
		int localX = tile.getX() - absX;
		int localY = tile.getY() - absY;
		if (localX < 0 || localY < 0 || localX >= 64 || localY >= 64) {
			return false;
		}
		WorldObject spawnedObject = getSpawnedObject(tile);
		if (spawnedObject != null) {
			return spawnedObject.getId() == id;
		}
		WorldObject removedObject = getRemovedObject(tile);
		if (removedObject != null && removedObject.getId() == id) {
			return false;
		}
		WorldObject[] mapObjects = getObjects(tile.getPlane(), localX, localY);
		if (mapObjects == null) {
			return false;
		}
		for (WorldObject object : mapObjects) {
			if (object.getId() == id) {
				return true;
			}
		}
		return false;
	}

	private WorldObject getSpawnedObject(Location tile) {
		if (spawnedObjects == null) {
			return null;
		}
		for (WorldObject object : spawnedObjects) {
			if (object.getLocation().getX() == tile.getX() && object.getLocation().getY() == tile.getY() && object.getLocation().getPlane() == tile.getPlane()) {
				return object;
			}
		}
		return null;
	}

	public WorldObject getObject(int plane, int x, int y) {
		WorldObject[] objects = getObjects(plane, x, y);
		if (objects == null) {
			return null;
		}
		return objects[0];
	}
	
	private int[] musicIds;

	public int getMusicId() {
		if (musicIds == null)
			return -1;
		if (musicIds.length == 1)
			return musicIds[0];
		return musicIds[Utilities.getRandom(musicIds.length - 1)];
	}

	public static final String getMusicName3(int regionId) {
		switch (regionId) {
		case 13152: //crucible
			return "Steady";	
		case 13151: //crucible
			return "Hunted";	
		case 12895: //crucible
			return "Target";	
		case 12896: //crucible
			return "I Can See You";	
		case 11575: //burthope
			return "Spiritual";
		case 18512:
		case 18511:
		case 19024:
			return "Tzhaar City III";
		case 18255: //fight pits
			return "Tzhaar Supremacy III";
		case 14948:
			return "Dominion Lobby III";
		default:
			return null;
		}
	}

	public static final String getMusicName2(int regionId) {
		switch (regionId) {
		case 13152: //crucible
			return "I Can See You";	
		case 13151: //crucible
			return "You Will Know Me";	
		case 12895: //crucible
			return "Steady";	
		case 12896: //crucible
			return "Hunted";	
		case 12853:
			return "Cellar Song";
		case 11573: //taverley
			return "Taverley Lament";
			
		case 11575: //burthope
			return "Taverley Adventure";
			/*
			 * kalaboss
			 */
		case 13626:
		case 13627:
		case 13882:
		case 13881:
			return "Daemonheim Fremenniks";
		case 18512:
		case 18511:
		case 19024:
			return "Tzhaar City II";
		case 18255: //fight pits
			return "Tzhaar Supremacy II";
		case 14948:
			return "Dominion Lobby II";
		default:
			return null;
		}
	}

	public static final String getMusicName1(int regionId) {
		switch (regionId) {
		case 15967://Runespan
			return "Runespan";
		case 15711://Runespan
			return "Runearia";
		case 15710://Runespan
			return "Runebreath";
		case 13152: //crucible
			return "Hunted";	
		case 13151: //crucible
			return "Target";	
		case 12895: //crucible
			return "I Can See You";	
		case 12896: //crucible
			return "You Will Know Me";	
		case 12597:
			return "Spirit";
		case 13109:
			return "Medieval";
		case 13110:
			return "Honkytonky Parade";
		case 10658:
			return "Espionage";
		case 13899: //water altar
			return "Zealot";
		case 10039:
			return "Legion";
		case 11319: //warriors guild
			return "Warriors' Guild";
		case 11575: //burthope
			return "Spiritual";
		case 11573: //taverley
			return "Taverley Ambience";
		case 7473:
			return "The Waiting Game";
		case 18512:
		case 18511:
		case 19024:
			return "Tzhaar City I";
		case 18255: //fight pits
			return "Tzhaar Supremacy I";
		case 14672:
		case 14671:
		case 14415:
		case 14416:
			return "Living Rock";
		case 11157: //Brimhaven Agility Arena
			return "Aztec";
		case 15446:
		case 15957:
		case 15958:
			return "Dead and Buried";
		case 12848:
			return "Arabian3";
		case 12954:
		case 12442:
		case 12441:
			return "Scape Cave";
		case 12185:
		case 11929:
			return "Dwarf Theme";
		case 12184:
			return "Workshop";
		case 6992:
		case 6993: //mole lair
			return "The Mad Mole";
		case 9776: // castle wars
			return "Melodrama";
		case 10029:
		case 10285:
			return "Jungle Hunt";
		case 14231: // barrows under
			return "Dangerous Way";
		case 12856: // chaos temple
			return "Faithless";
		case 13104:
		case 12847: // arround desert camp
		case 13359:
		case 13102:
			return "Desert Voyage";
		case 13103:
			return "Lonesome";
		case 12589: // granite mine
			return "The Desert";
		case 13407: //crucible entrance
		case 13360: // dominion tower outside
			return "";
		case 14948:
			return "Dominion Lobby I";
		case 11836: // lava maze near kbd entrance
			return "Attack3";
		case 12091: // lava maze west
			return "Wilderness2";
		case 12092: // lava maze north
			return "Wild Side";
		case 9781:
			return "Gnome Village";
		case 11339: // air altar
			return "Serene";
		case 11083: // mind altar
			return "Miracle Dance";
		case 10827: // water altar
			return "Zealot";
		case 10571: // earth altar
			return "Down to Earth";
		case 10315: // fire altar
			return "Quest";
		case 8523: // cosmic altar
			return "Stratosphere";
		case 9035: // chaos altar
			return "Complication";
		case 8779: // death altar
			return "La Mort";
		case 10059: // body altar
			return "Heart and Mind";
		case 9803: // law altar
			return "Righteousness";
		case 9547: // nature altar
			return "Understanding";
		case 9804: // blood altar
			return "Bloodbath";
		case 13107:
			return "Arabian2";
		case 13105:
			return "Al Kharid";
		case 12342:
			return "Forever";
		case 10806:
			return "Overture";
		case 10899:
			return "Karamja Jam";
		case 13623:
			return "The Terrible Tower";
		case 12374:
			return "The Route of All Evil";
		case 9802:
			return "Undead Dungeon";
		case 10809: // east rellekka
			return "Borderland";
		case 10553: // Rellekka
			return "Rellekka";
		case 10552: // south
			return "Saga";
		case 10296: // south west
			return "Lullaby";
		case 10828: // south east
			return "Legend";
		case 9275:
			return "Volcanic Vikings";
		case 11061:
		case 11317:
			return "Fishing";
		case 9551:
			return "TzHaar!";
		case 12345:
			return "Eruption";
		case 12089:
			return "Dark";
		case 12446:
		case 12445:
			return "Wilderness";
		case 12343:
			return "Dangerous";
		case 14131:
			return "Dance of the Undead";
		case 11844:
		case 11588:
			return "The Vacant Abyss";
		case 13363: // duel arena hospital
			return "Shine";
		case 13362: // duel arena
			return "Duel Arena";
		case 12082: // port sarim
			return "Sea Shanty2";
		case 12081: // port sarim south
			return "Tomorrow";
		case 11602:
			return "Strength of Saradomin";
		case 12590:
			return "Bandit Camp";
		case 10329:
			return "The Sound of Guthix";
		case 9033:
			return "Attack5";
			// godwars
		case 11603:
			return "Zamorak Zoo";
		case 11346:
			return "Armadyl Alliance";
		case 11347:
			return "Armageddon";
		case 13114:
			return "Wilderness";
			// black kngihts fortess
		case 12086:
			return "Knightmare";
			// tzaar
		case 9552:
			return "Fire and Brimstone";
			// kq
		case 13972:
			return "Insect Queen";
			// clan wars free for all:
		case 11094:
			return "Clan Wars";
			/*
			 * tutorial island
			 */
		case 12336:
			return "Newbie Melody";
			/*
			 * darkmeyer
			 */
		case 14644:
			return "Darkmeyer";
			/*
			 * kalaboss
			 */
		case 13626:
		case 13627:
		case 13882:
		case 13881:
			return "Daemonheim Entrance";
			/*
			 * Lumbridge, falador and region.
			 */
		case 11574: // heroes guild
			return "Splendour";
		case 12851:
			return "Autumn Voyage";
		case 12338: // draynor and market
			return "Unknown Land";
		case 12339: // draynor up
			return "Start";
		case 12340: // draynor mansion
			return "Spooky";
		case 12850: // lumbry castle
			return "Harmony";
		case 12849: // east lumbridge swamp
			return "Yesteryear";
		case 12593: // at Lumbridge Swamp.
			return "Book of Spells";
		case 12594: // on the path between Lumbridge and Draynor.
			return "Dream";
		case 12595: // at the Lumbridge windmill area.
			return "Flute Salad";
		case 12854: // at Varrock Palace.
			return "Adventure";
		case 12853: // at varrock center
			return "Garden";
		case 12852: // varock mages
			return "Expanse";
		case 13108:
			return "Still Night";
		case 12083:
			return "Wander";
		case 11828:
			return "Fanfare";
		case 11829:
			return "Scape Soft";
		case 11577:
			return "Mad Eadgar";
		case 10293: // at the Fishing Guild.
			return "Mellow";
		case 11824:
			return "Mudskipper Melody";
		case 11570:
			return "Wandar";
		case 12341:
			return "Barbarianims";
		case 12855:
			return "Crystal Sword";
		case 12344:
			return "Dark";
		case 12599:
			return "Doorways";
		case 12598:
			return "The Trade Parade";
		case 11318:
			return "Ice Melody";
		case 12600:
			return "Scape Wild";
		case 10032: // west yannile:
			return "Big Chords";
		case 10288: // east yanille
			return "Magic Dance";
		case 11826: // Rimmington
			return "Long Way Home";
		case 11825: // rimmigton coast
			return "Attention";
		case 11827: // north rimmigton
			return "Nightfall";
			/*
			 * Camelot and region.
			 */
		case 11062:
		case 10805:
			return "Camelot";
		case 10550:
			return "Talking Forest";
		case 10549:
			return "Lasting";
		case 10548:
			return "Wonderous";
		case 10547:
			return "Baroque";
		case 10291:
		case 10292:
			return "Knightly";
		case 11571: // crafting guild
			return "Miles Away";
		case 11595: // ess mine
			return "Rune Essence";
		case 10294:
			return "Theme";
		case 12349:
			return "Mage Arena";
		case 13365: // digsite
			return "Venture";
		case 13364: // exams center
			return "Medieval";
		case 13878: // canifis
			return "Village";
		case 13877: // canafis south
			return "Waterlogged";
			/*
			 * Mobilies Armies.
			 */
		case 9516:
			return "Command Centre";
		case 12596: // champions guild
			return "Greatness";
		case 10804: // legends guild
			return "Trinity";
		case 11601:
			return "Zaros Zeitgeist"; // zaros godwars
		default:
			return null;
		}
	}

	private static final int getMusicId(String musicName) {
		if (musicName == null)
			return -1;
		if (musicName.equals(""))
			return -2;
		int musicIndex = (int) ClientScriptMap.getMap(1345).getKeyForValue(
				musicName);
		return ClientScriptMap.getMap(1351).getIntValue(musicIndex);
	}

	public void loadMusicFromCache() {
		int musicId1 = getMusicId(getMusicName1(regionId));
		if (musicId1 != -1) {
			int musicId2 = getMusicId(getMusicName2(regionId));
			if (musicId2 != -1) {
				int musicId3 = getMusicId(getMusicName3(regionId));
				if (musicId3 != -1)
					musicIds = new int[] { musicId1, musicId2, musicId3 };
				else
					musicIds = new int[] { musicId1, musicId2 };
			} else
				musicIds = new int[] { musicId1 };
		}
	}
}
