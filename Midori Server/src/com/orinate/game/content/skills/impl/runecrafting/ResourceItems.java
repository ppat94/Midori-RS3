package com.orinate.game.content.skills.impl.runecrafting;

/**
 * Handles enumerations of runecrafting resource items.
 * 
 * @author _Jordan / Apollo <citellumrsps@gmail.com>
 * Feb 1, 2014, 2014, 6:03:32 PM
 * 
 */
public class ResourceItems {

	/**
	 * Represents an enumeration of the runecrafting tiara types.
	 * 
	 * @author _Jordan / Apollo <citellumrsps@gmail.com>
	 * Feb 1, 2014, 2014,  6:04:03 PM
	 * 
	 */
	public static enum TiaraDefinitions {
		
		AIR_TIARA(5527, 2452), MIND_TIARA(5529, 2453), WATER_TIARA(5531, 2454), EARTH_TIARA(5535, 2455), FIRE_TIARA(5537, 2456), BODY_TIARA(5533, 2457), COSMIC_TIARA(5539, 2458), CHAOS_TIARA(5543, 2459), ASTRAL_TIARA(9106, 17010), NATURE_TIARA(5541, 2460), LAW_TIARA(5545, 2459), DEATH_TIARA(5547, -1), BLOOD_TIARA(5549, -1);

		/**
		 * Represents the item id of the tiara.
		 */
		private final int item;

		/**
		 * Represents the object id of the temple.
		 */
		private final int object;

		/**
		 * Constructs a new {@code TiaraDefinitions} {@code Object}.
		 * 
		 * @param item
		 *            the item of the tiara.
		 * @param object
		 *            the object of the temple.
		 */
		TiaraDefinitions(int item, int object) {
			this.item = item;
			this.object = object;
		}

		/**
		 * Gets the item.
		 * 
		 * @return the item
		 */
		public int getItem() {
			return item;
		}

		/**
		 * Gets the object of the temple.
		 * 
		 * @return the object of the temple.
		 */
		public int getObject() {
			return object;
		}
	}

	/**
	 * Represents an enumeration of the runecrafting talisman types.
	 * 
	 * @author _Jordan / Apollo <citellumrsps@gmail.com>
	 * Feb 1, 2014, 2014,  6:05:48 PM
	 * 
	 */
	public enum TalismanDefinitions {
		
		AIR_TALISMAN(1438, 2452), MIND_TALISMAN(1448, 2453), WATER_TALISMAN(1444, 2454), EARTH_TALISMAN(1440, 2455), FIRE_TALISMAN(1442, 2456), BODY_TALISMAN(1446, 2457), COSMIC_TALISMAN(1454, 2458), CHAOS_TALISMAN(1452, 2461), NATURE_TALISMAN(1462, 2460), LAW_TALISMAN(1458, 2459), DEATH_TALISMAN(1456, -1), BLOOD_TALISMAN(1450, -1);

		/**
		 * Represents the item id of the talisman.
		 */
		private final int item;

		/**
		 * Represents the object id of the temple.
		 */
		private final int object;

		/**
		 * Constructs a new {@code TalismanDefinitions} {@code Object}.
		 * 
		 * @param item
		 *            the item of the talisman.
		 * @param object
		 *            the object of the temple.
		 */
		TalismanDefinitions(int item, int object) {
			this.item = item;
			this.object = object;
		}

		/**
		 * Gets the item.
		 * 
		 * @return the item
		 */
		public int getItem() {
			return item;
		}

		/**
		 * Gets the object id of the temple.
		 * 
		 * @return the object
		 */
		public int getObject() {
			return object;
		}
	}

	/**
	 * Represents an enumeration of the runecrafting talisman staff types.
	 * 
	 * @author _Jordan / Apollo <citellumrsps@gmail.com>
	 * Feb 1, 2014, 2014,  6:16:22 PM
	 * 
	 */
	public enum TalismanStaffDefinitions {
		
		AIR_STAFF(13630, 2452), MIND_STAFF(13631, 2453), WATER_STAFF(13632, 2454), EARTH_STAFF(13633, 2455), FIRE_STAFF(13634, 2456), BODY_STAFF(13635, 2457), COSMIC_STAFF(13636, 2458), CHAOS_STAFF(13637, 2461), NATURE_STAFF(13638, 2460), LAW_STAFF(13639, 2459), DEATH_STAFF(13640, -1), BLOOD_STAFF(13641, -1);

		/**
		 * Represents the item id of the talisman staff.
		 */
		private final int item;

		/**
		 * Represents the object id of the temple.
		 */
		private final int object;

		/**
		 * Constructs a new {@code TalismanStaffDefinitions} {@code Object}.
		 * 
		 * @param item
		 *            the item of the talisman staff.
		 * @param object
		 *            the object of the temple.
		 */
		TalismanStaffDefinitions(int item, int object) {
			this.item = item;
			this.object = object;
		}

		/**
		 * Gets the item.
		 * 
		 * @return the item
		 */
		public int getItem() {
			return item;
		}

		/**
		 * Gets the object id of the temple.
		 * 
		 * @return the object of the temple.
		 */
		public int getObject() {
			return object;
		}
	}

}
