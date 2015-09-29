package com.orinate.util.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.logging.Logger;

import com.orinate.game.World;
import com.orinate.game.model.Location;
import com.orinate.game.model.npc.NPC;

/**
 * 
 * @author Tyler
 * 
 */
public class NPCSpawns {

	public static HashMap<Location, NPC> spawnedNPCS = new HashMap<Location, NPC>();
	private static final Logger logger = Logger.getLogger(NPCSpawns.class.getSimpleName());

	public static void init() throws Exception {
		File file = new File("./data/npc/spawns.txt");
		if (!file.exists()) {
			throw new Exception("No file found: " + file.getName());
		}
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = "";
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("//"))
				continue;
			String[] splitString = line.split(" - ");
			int npcId = Integer.valueOf(splitString[0]);
			String[] locationSplit = splitString[1].split(" ");
			Location location = new Location(Integer.valueOf(locationSplit[0]), Integer.valueOf(locationSplit[1]), Integer.valueOf(locationSplit[2]));
			spawnedNPCS.put(location, World.getWorld().spawnNpc(npcId, location));
		}
		reader.close();
		logger.info("Finished parsing " + spawnedNPCS.size() + " npc into the world.");
	}
}
