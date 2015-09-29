package com.orinate.game.content.skills.impl.fishing;

import com.orinate.game.model.npc.NPC;
import com.orinate.game.model.visual.Animation;

/**
 * The Definitions for {@link Fishing}
 * @author SonicForce41
 */
public class FishDefinitions {

	/**
	 * The capture reward definitions
	 * @author SonicForce41
	 */
	public enum CaptureDefinition {

		CRAYFISH(13435, 1, 10),
		SHRIMP(317, 1, 10),
		KARAMBWANJI(3150, 5, 5),
		SARDINES(327, 5, 20),
		HERRING(345, 10, 30), 
		ANCHOVIES(321, 15, 40),
		MACKEREL(353, 16, 20),
		OYSTER(407, 30, 0),
		SEAWEED(401, 30, 0),
		TROUT(335, 20, 50),
		COD(341, 23, 45),
		PIKE(349, 25, 60),
		SLIMY_EEL(3379, 28, 65),
		SALMON(331, 30, 70),
		FROG_SPAWNS(5004, 33, 75),
		TUNA(359, 35, 80),
		CAVE_EEL(5001, 38, 80),
		RAINBOW_FISH(3939, 38, 80),
		LOBSTER(377, 40, 90),
		BASS(363, 46, 100),
		SWORDFISH(371, 50, 100),
		LAVA_EEL(2148, 53, 60),
		MONKFISH(7944, 62, 120),
		KARAMBWAN(3142, 65, 105),
		SHARK(383, 76, 110),
		SEA_TURTLE(395, 79, 38),
		MANTA(389, 81, 46),
		CAVEFISH(15264, 85, 300),
		ROCKTAIL(15270, 90, 380),
		TIGER_SHARK(21520, 95, 80);

		/**
		 * The item id
		 */
		private int id;

		/**
		 * The level needed to capture this fish
		 */
		private int level;

		/**
		 * The experience gained
		 */
		private double experience;

		/**
		 * Constructs a new {@code CaptureDefinition.java} {@code Object}.
		 * @param itemId the item id
		 * @param level the level
		 * @param exp the exp
		 */
		CaptureDefinition(int id, int level, double exp) {
			this.id = id;
			this.level = level;
			this.experience = exp;
		}

		/**
		 * Gets the item Id
		 * @return
		 */
		public int getId() {
			return id;
		}

		/**
		 * Gets the level needed to fish this
		 * @return
		 */
		public int getLevel() {
			return level;
		}

		/**
		 * Gets the experience gained when the fish is captured
		 * @return
		 */
		public double getExperience() {
			return experience;
		}

	}

	/**
	 * The fishing spot definitions
	 * @author SonicForce41
	 */
	public enum SpotDefinitions {
		
		CRAY_CAGE(14907, "cage", 13431, -1, Animation.create(619), CaptureDefinition.CRAYFISH),
		SEA_NET(327, "net", 303, -1, Animation.create(621), CaptureDefinition.SHRIMP, CaptureDefinition.ANCHOVIES),
		SEA_BAIT(327, "bait", 307, 313, Animation.create(622), CaptureDefinition.SARDINES, CaptureDefinition.HERRING),
		RIVER_BAIT(328, "bait", 307, 313, Animation.create(622), CaptureDefinition.PIKE),
		RIVER_LURE(328, "lure", 309, 314, Animation.create(622), CaptureDefinition.TROUT, CaptureDefinition.SALMON),
		RIVER_LURE_TWO(329, "lure", 309, 314, Animation.create(622), CaptureDefinition.TROUT, CaptureDefinition.SALMON),
		BIG_NET(313, "net", 305, -1, Animation.create(621), CaptureDefinition.SEAWEED, CaptureDefinition.OYSTER, CaptureDefinition.MACKEREL, CaptureDefinition.COD, CaptureDefinition.BASS),
		HARPOON(312, "harpoon", 311, -1, Animation.create(618), CaptureDefinition.TUNA, CaptureDefinition.SWORDFISH, CaptureDefinition.SHARK),
		HARPOON_TWO(313, "harpoon", 311, -1, Animation.create(618), CaptureDefinition.TUNA, CaptureDefinition.SWORDFISH, CaptureDefinition.SHARK),
		CAGE(6267, "cage", 301, -1, Animation.create(619), CaptureDefinition.LOBSTER),
		CAGE_TWO(312, "cage", 301, -1, Animation.create(619), CaptureDefinition.LOBSTER),
		SEA_NET2(952, "net", 303, -1, Animation.create(621), CaptureDefinition.SHRIMP);
		
		/**
		 * The fishing npc id.
		 */
		private int id;
		
		/**
		 * The option name
		 */
		private String option;
		
		/**
		 * The fishing tool id.
		 */
		private int toolId;
		
		/**
		 * The bait id.
		 */
		private int baitId;
		
		/**
		 * The animation
		 */
		private Animation animation;
		
		/**
		 * The CaptureDefinition
		 */
		private CaptureDefinition[] captures;
		
		/**
		 * Constructor representing a fishing spot
		 * @param id the spot id
		 * @param toolId the tool used for fishing in this spot
		 * @param baitId the bait needed for fishing
		 * @param animation the animation
		 * @param captureDefs the capture definitions
		 */
		private SpotDefinitions(int id, String option, int toolId, int baitId, Animation animation, CaptureDefinition... captureDefs) {
			this.id = id;
			this.option = option;
			this.toolId = toolId;
			this.baitId = baitId;
			this.animation = animation;
			this.captures = captureDefs;
		}
		
		/**
		 * Gets the {@link SpotDefinitions} by the {@link NPC} and the option text
		 * @param npc the {@link NPC}
		 * @param option the option text
		 * @return
		 */
		public static SpotDefinitions getDefinition(NPC npc, String option) {
			for (SpotDefinitions definition : SpotDefinitions.values()) {
				if (npc.getId() == definition.getId() && option.equalsIgnoreCase(definition.getOption()))
					return definition;
			}
			return null;
		}
		
		/**
		 * Method returns the id of spot
		 * @return the spot id
		 */
		public final int getId() {
			return id;
		}
		
		/**
		 * Method returns the value of option
		 * @return the option value
		 */
		public final String getOption() {
			return option;
		}
		
		/**
		 * Method returns the tool used for fishing
		 * @return the tool id
		 */
		public final int getToolId() {
			return toolId;
		}
		
		/**
		 * Method returns the bait id
		 * @return the bait id
		 */
		public final int getBaitId() {
			return baitId;
		}
		
		/**
		 * Method returns the Animation
		 * @return the animation
		 */
		public final Animation getAnimation() {
			return animation;
		}
		
		/**
		 * Method returns the CaptureDefinitions
		 * @return the capture definitions
		 */
		public final CaptureDefinition[] getCaptureDefinitions() {
			return captures;
		}
		
	}

}