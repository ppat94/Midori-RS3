package com.orinate.tool;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import com.orinate.cache.Cache;
import com.orinate.cache.parsers.ObjectDefinition;

/**
 * The object list dumper
 * 
 * @author SonicForce41
 */
public class TreeDump {

	public static void main(String[] args) {
		try {
			System.setOut(new PrintStream(new FileOutputStream("./protocol-information/stumpList.txt")));
			Cache.init();
			for (int i = 0; i < ObjectDefinition.getSize(); i++) {
				ObjectDefinition def = ObjectDefinition.forId(i);
				if (def != null) {
					if (def.name.toLowerCase().contains("tree stump")) {
						System.out.println(def.name + " : " + i + " :" + Arrays.toString(def.actions));
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}