package com.orinate.util.misc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom
 * 
 */
public class LobbyConfigs {

	private static Map<Integer, Integer> configs = new HashMap<>();

	public static void parse() throws Exception {
		try (BufferedReader reader = new BufferedReader(new FileReader("./data/lobby-configs.txt"))) {
			while (reader.ready()) {
				String line = reader.readLine();
				if (line == null) {
					continue;
				}

				String[] split = line.split(", ");

				int configID = Integer.valueOf(split[0]);
				int configValue = Integer.valueOf(split[1]);

				configs.put(configID, configValue);
			}
		}
	}

	public static Map<Integer, Integer> getConfigs() {
		return configs;
	}
}
