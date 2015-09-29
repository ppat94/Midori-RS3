package com.orinate.tool;

import java.io.FileOutputStream;
import java.io.PrintStream;

import com.orinate.cache.Cache;
import com.orinate.cache.parsers.ObjectDefinition;

/**
 * The object list dumper
 * 
 * @author SonicForce41
 */
public class ObjectListDumper {

	public static void main(String[] args) {
		try {
			System.setOut(new PrintStream(new FileOutputStream("./protocol-information/objectList.txt")));
			Cache.init();
			for (int i = 0; i < ObjectDefinition.getSize(); i++) {
				ObjectDefinition def = ObjectDefinition.forId(i);
				if (def != null) {
					System.out.print(i + " - " + def.name + ", options:");
					for (int index = 0; index < def.actions.length; index++) {
						String action = def.actions[index];
						if (action != null)
							System.out.print((def.actions.length - 1) == index ? def.actions[index] : def.actions[index] + ", ");
					}
					System.out.println();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}