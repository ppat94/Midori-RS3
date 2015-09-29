package com.orinate.game.model.npc.combat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.logging.Logger;

import com.google.gson.Gson;

public class CombatDefinitionsLoader {

	public static HashMap<Integer, CombatDefinitions> combatDefinitions = new HashMap<Integer, CombatDefinitions>();
	private static final Logger logger = Logger.getLogger(CombatDefinitionsLoader.class.getName());

	public static CombatDefinitions getCombatDefinitions(int id) {
		if (combatDefinitions.get(id) == null) {
			CombatDefinitions def = new CombatDefinitions(id);
			def.setAggressive(false);
			def.setDeathAnimation(-1);
			def.setMaximumHitpoints(1);
			return def;
		}
		return combatDefinitions.get(id);
	}

	public static void init() {
		try {
			BufferedReader in = new BufferedReader(new FileReader("data/npc/NPCCombatDefinitions.txt"));
			int loaded = 0;
			while (true) {
				String line = in.readLine();
				if (line == null)
					break;
				if (line.startsWith("//"))
					continue;
				if (line.isEmpty())
					continue;

				Gson gson = new Gson();
				CombatDefinitions def = gson.fromJson(line, CombatDefinitions.class);

				if (def != null) {
					combatDefinitions.put(def.getNpcId(), def);
					loaded++;
				}
			}
			logger.info("Parsed " + loaded + " NPC combat definitions...");
			in.close();
		} catch (Exception e) {

		}
	}

}
