package com.orinate.game.content.combat.ability;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * The ability repository.
 * 
 * @author Emperor
 * 
 */
public final class AbilityRepository {

	/**
	 * The mapping.
	 */
	private static final Map<Integer, Ability> MAPPING = new HashMap<>();

	/**
	 * The abilities mapping.
	 */
	private final Map<Integer, Ability> abilities = new HashMap<>();

	/**
	 * Constructs a new {@code AbilityRepository}.
	 */
	public AbilityRepository() {
		/*
		 * empty.
		 */
	}

	/**
	 * Loads the abilities.
	 */
	public void load() {
		for (int key : MAPPING.keySet()) {
			abilities.put(key, MAPPING.get(key).newInstance());
		}
	}

	/**
	 * Initializes the ability repository.
	 */
	public static void init() {
		for (File f : new File("./src/com/orinate/game/content/combat/ability/").listFiles()) {
			if (f.isDirectory()) {
				for (File file : f.listFiles()) {
					try {
						if (!((Ability) Class.forName("com.orinate.game.content.combat.ability." + f.getName() + "." + file.getName().replace(".java", "")).newInstance()).register()) {
							continue;
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Gets the ability.
	 * 
	 * @param interfaceId
	 *            The interface id.
	 * @param childId
	 *            The child id.
	 * @return The ability.
	 */
	public Ability getAbility(int interfaceId, int childId) {
		return getAbility(interfaceId << 16 | childId);
	}

	/**
	 * Gets the ability.
	 * 
	 * @param abilityId
	 *            The ability id.
	 * @return The ability.
	 */
	public Ability getAbility(int abilityId) {
		return MAPPING.get(abilityId);
	}

	/**
	 * Registers an ability.
	 * 
	 * @param interfaceId
	 *            The interface id.
	 * @param childId
	 *            The child id.
	 * @param ability
	 *            The ability.
	 */
	public static boolean register(int interfaceId, int childId, Ability ability) {
		return MAPPING.put(interfaceId << 16 | childId, ability) == null;
	}

	/**
	 * Gets the ability mapping.
	 * 
	 * @return the mapping.
	 */
	public static Map<Integer, Ability> getMapping() {
		return MAPPING;
	}
}