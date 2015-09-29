package com.orinate.game.model.npc.drops;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import com.orinate.cache.parsers.ItemDefinition;
import com.orinate.cache.parsers.NPCDefinitions;
import com.orinate.game.model.npc.NPC;
import com.orinate.game.model.npc.drops.Drop.Rarity;
import com.orinate.game.model.player.Player;
import com.orinate.util.Utilities;

public class NPCDrops {

	private static final Logger logger = Logger.getLogger(NPCDrops.class.getName());

	private final static String PACKED_PATH = "data/npc/NPCDrops.txt";
	private static HashMap<Integer, Drop[]> npcDrops;
	final static Charset ENCODING = StandardCharsets.UTF_8;

	public static final void init() {
		loadPackedNPCDrops();
	}

	public static ArrayList<Drop> getDrops(int npcId) {
		ArrayList<Drop> drops = new ArrayList<Drop>();
		if (npcDrops.get(npcId) != null) {
			for (Drop drop : npcDrops.get(npcId)) {
				drops.add(drop);
			}
		}
		return drops;
	}

	public static ArrayList<Drop> generateDrop(Player killer, int npcId) {
		ArrayList<Drop> earnedDrops = new ArrayList<Drop>();
		ArrayList<Drop> drops = getDrops(npcId);

		if (drops == null || drops.isEmpty())
			return null;

		Drop finalDrop = null;
		Drop rareDrop = null;

		int roll = (Utilities.random(5000));
		ArrayList<Drop> possibleDrops = new ArrayList<Drop>();
		ArrayList<Drop> rareDropTable = new ArrayList<Drop>();

		if (hasAccessToRareTable(killer, npcId)) {
			int rareroll = (Utilities.random(5000));
			ArrayList<Drop> rareTable = NPCDrops.getDrops(50000);
			for (Drop rare : rareTable) {
				if (rareroll < 25 && rare.getRarity() == Rarity.ULTRA_RARE) {
					rareDropTable.add(rare);
				} else if (rareroll < 75 && rare.getRarity() == Rarity.VERY_RARE) {
					rareDropTable.add(rare);
				} else if (rareroll < 250 && rare.getRarity() == Rarity.RARE) {
					rareDropTable.add(rare);
				} else if (rareroll < 1000 && rare.getRarity() == Rarity.UNCOMMON) {
					rareDropTable.add(rare);
				} else if (rareroll < 4500 && rare.getRarity() == Rarity.COMMON) {
					rareDropTable.add(rare);
				}
			}
		}

		for (Drop drop : drops) {
			if (drop.getRarity() == Rarity.ALWAYS) {
				earnedDrops.add(drop);
			} else {
				if (roll < 25 && drop.getRarity() == Rarity.ULTRA_RARE) {
					possibleDrops.add(drop);
				} else if (roll < 75 && drop.getRarity() == Rarity.VERY_RARE) {
					possibleDrops.add(drop);
				} else if (roll < 250 && drop.getRarity() == Rarity.RARE) {
					possibleDrops.add(drop);
				} else if (roll < 1000 && drop.getRarity() == Rarity.UNCOMMON) {
					possibleDrops.add(drop);
				} else if (roll < 4500 && drop.getRarity() == Rarity.COMMON) {
					possibleDrops.add(drop);
				}
			}
		}
		if (rareDropTable.size() > 0)
			rareDrop = rareDropTable.get(Utilities.random(rareDropTable.size()));
		if (rareDrop != null) {
			// if (killer.getEquipment().getRingId() != -1 &&
			// ItemDefinitions.getItemDefinitions(killer.getEquipment().getRingId()).getName().toLowerCase().contains("ring of wealth"))
			// {
			// killer.getWriter().sendGameMessage("<col=FACC2E>Your ring of wealth shines brightly!");
			// }
			earnedDrops.add(rareDrop);
		}
		if (possibleDrops.size() > 0)
			finalDrop = possibleDrops.get(Utilities.random(possibleDrops.size()));
		if (finalDrop != null)
			earnedDrops.add(finalDrop);
		return earnedDrops;
	}

	public static boolean hasAccessToRareTable(Player killer, int npcId) {
		int chance = Utilities.random(1000);
		// boolean isSlayer = false;
		// SlayerMonsters[] slayerMonsters = SlayerMonsters.values();
		// for (SlayerMonsters slayer : slayerMonsters) {
		// if
		// (getDefinitions().name.toLowerCase().equals(NPCDefinitions.getNPCDefinitions(slayer.getId()).name))
		// {
		// chance -= slayer.getRequirement();
		// isSlayer = true;
		// }
		// }
		if (/* !isSlayer && */NPCDefinitions.forId(npcId).combatLevel < 30)
			return false;
		chance -= NPCDefinitions.forId(npcId).combatLevel / 4;
		if (killer.getEquipment().getRingId() != -1 && ItemDefinition.forId(killer.getEquipment().getRingId()).getName().toLowerCase().contains("ring of wealth")) {
			chance -= 10;
		}
		if (chance <= 10) {
			if (killer.getEquipment().getRingId() != -1 && ItemDefinition.forId(killer.getEquipment().getRingId()).getName().toLowerCase().contains("ring of wealth")) {
				killer.getWriter().sendGameMessage("<col=FACC2E>Your ring of wealth shines brightly!");
			}
			return true;
		}
		return false;
	}

	public static boolean hasAccessToRareTable(Player killer, NPC npc) {
		int chance = Utilities.getRandom(1000);
		if (/* !isSlayer && */NPCDefinitions.forId(npc.getId()).combatLevel < 30)
			return false;
		chance -= NPCDefinitions.forId(npc.getId()).combatLevel / 4;
		if (killer.getEquipment().getRingId() != -1 && ItemDefinition.forId(killer.getEquipment().getRingId()).getName().toLowerCase().contains("ring of wealth")) {
			chance -= 10;
		}
		if (chance <= 10) {
			if (killer.getEquipment().getRingId() != -1 && ItemDefinition.forId(killer.getEquipment().getRingId()).getName().toLowerCase().contains("ring of wealth")) {
				killer.getWriter().sendGameMessage("<col=FACC2E>Your ring of wealth shines brightly!");
			}
			return true;
		}
		return false;
	}

	private Map<Integer, ArrayList<Drop>> dropMapx = null;

	public Map<Integer, ArrayList<Drop>> getDropArray() {
		if (dropMapx == null)
			dropMapx = new LinkedHashMap<Integer, ArrayList<Drop>>();
		for (int i : npcDrops.keySet()) {
			int npcId = i;
			ArrayList<Drop> temp = new ArrayList<Drop>();
			for (Drop mainDrop : npcDrops.get(npcId)) {
				temp.add(mainDrop);
			}
			dropMapx.put(i, temp);
		}
		return dropMapx;
	}

	private static void loadPackedNPCDrops() {
		int lineNumber = 0;
		try {
			npcDrops = new HashMap<Integer, Drop[]>();
			Path path = Paths.get(PACKED_PATH);
			try (Scanner scanner = new Scanner(path, ENCODING.name())) {
				ArrayList<Drop> drops = new ArrayList<Drop>();
				int npcId = -1;
				int dropNumber = 0;
				int npcCount = 0;
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					lineNumber++;
					if (line.isEmpty() && !drops.isEmpty() && npcId != -1) {
						dropNumber += drops.size();
						npcCount++;
						Drop[] npcDraps = new Drop[drops.size()];
						npcDrops.put(npcId, drops.toArray(npcDraps));
						drops.clear();
						npcId = -1;
					} else {
						String[] subs = line.split(":");
						String[] info = subs[1].split("-");

						Rarity rarity = null;
						int itemId = 0;
						int minAmount = 0;
						int maxAmount = 0;

						npcId = Integer.parseInt(subs[0]);
						itemId = Integer.parseInt(info[0]);
						switch (info[1]) {
						case "ALWAYS":
							rarity = Rarity.ALWAYS;
							break;
						case "COMMON":
							rarity = Rarity.COMMON;
							break;
						case "UNCOMMON":
							rarity = Rarity.UNCOMMON;
							break;
						case "RARE":
							rarity = Rarity.RARE;
							break;
						case "VERYRARE":
							rarity = Rarity.VERY_RARE;
							break;
						case "ULTRARARE":
							rarity = Rarity.ULTRA_RARE;
							break;
						}
						minAmount = Integer.parseInt(info[2]);
						maxAmount = Integer.parseInt(info[3]);

						if (minAmount > maxAmount) {
							int temp = minAmount;
							minAmount = maxAmount;
							maxAmount = temp;
						}

						if (npcId == -1 || itemId == 0 || minAmount == 0 || maxAmount == 0 || rarity == null) {
							System.out.println("INVALID NPC DROP ON LINE: " + lineNumber);
							return;
						}

						drops.add(new Drop(rarity, itemId, minAmount, maxAmount));
					}
				}
				logger.info("Parsed " + dropNumber + " drops for " + npcCount + " NPCs...");
			}
		} catch (Throwable e) {
			logger.warning("Error parsing NPC drops at line: " + lineNumber);
		}
	}

	public HashMap<Integer, Drop[]> getDropMap() {
		return npcDrops;
	}
}