package com.orinate.tool;

import java.io.FileOutputStream;
import java.io.PrintStream;

import com.orinate.cache.Cache;
import com.orinate.cache.parsers.NPCDefinitions;

/**
 * The npc list dumper
 * @author SonicForce41
 */
public class NPCListDumper {

	public static void main(String[] args) {
		try {
			System.setOut(new PrintStream(new FileOutputStream("./protocol-information/npcList.txt")));
			Cache.init();
			for (int i = 0; i < NPCDefinitions.getSize(); i++) {
				NPCDefinitions def = NPCDefinitions.forId(i);
				if (def != null) {
					System.out.print(i + " - " + def.name + " ("+def.combatLevel+"), options:");
					for (int index = 0; index < def.options.length; index++) {
						String action = def.options[index];
						if (action != null)
							System.out.print((def.options.length - 1) == index ? def.options[index] : def.options[index] + ", ");
					}
					System.out.println();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}