package com.orinate.game.model.player;

import com.orinate.game.model.update.masks.Graphics;
import com.orinate.game.model.visual.Animation;
import com.orinate.game.task.WorldTask;
import com.orinate.game.task.WorldTasksManager;

/**
 * Handles player emotes.
 * 
 * @author _Jordan / Apollo <citellumrsps@gmail.com>
 * Feb 4, 2014, 2014, 4:55:17 PM
 * 
 */
public class EmoteHandler {

	/**
	 * Represents an enumeration of player emote types.
	 * 
	 * @author _Jordan / Apollo <citellumrsps@gmail.com>
	 * Feb 4, 2014, 2014, 4:55:37 PM
	 * 
	 */
	public enum Emotes {

		YES(855, -1, 0), NO(856, -1, 1), BOW(858, -1, 2), ANGRY(859, -1, 3), THINK(857, -1, 4), WAVE(863, -1, 5), SHRUG(2113, -1, 6), CHEER(862, -1, 7), BECKON(864, -1, 8), LAUGH(861, -1, 9), JUMP_FOR_JOY(2109, -1, 10), YAWN(2111, -1, 11), DANCE(866, -1, 12), JIG(2106, -1, 13), TWIRL(2107, -1, 14), HEADBANG(2108, -1, 15), CRY(860, -1, 16), BLOW_KISS(1374, 1702, 17), PANIC(2105, -1, 18), RASPBERRY(2110, -1, 19), CLAP(865, -1, 20), SALUTE(2112, -1, 21), GOBLIN_BOW(2127, -1, 22), GOBLIN_SALUTE(2128, -1, 23), GLASS_BOX(1131, -1, 24), CLIMB_ROPE(1130, -1, 25), LEAN(1129, -1, 26), GLASS_WALL(1128, -1, 27), IDEA(4275, -1, 28), STOMP(1745, -1, 29), FLAP(4280, -1, 30), SLAP_HEAD(4276, -1, 31), ZOMBIE_WALK(3544, -1, 32), ZOMBIE_DANCE(3543, -1, 33), ZOMBIE_HAND(7272, 1244, 34), SCARED(2836, -1, 35), BUNNY_HOP(6111, -1, 36), CAPE(-1, -1, 37), SNOWMAN_DANCE(7531, -1, 38), AIR_GUITAR(2414, 1537, 39), SAFETY_FIRST(8770, 1553, 40), EXPLORE(9990, 1734, 41), TRICK(10530, 1864, 42), FREEZE(11044, 1973, 43), GIVE_THANKS(-1, -1, 44), AROUND_THE_WORLD(11542, 2037, 45), DRAMATIC_POINT(12658, 780, 46), FAINT(14165, -1, 47), PUPPET_MASTER(14869, 2837, 48), TASK_MASTER(15033, 2930, 49), SEAL_OF_APPROVAL(-1, -1, 50), CAT_FIGHT(2252, -1, 51), TALK_TO_THE_HAND(2416, -1, 52), SHAKE_HANDS(2303, -1, 53), HIGH_FIVE(2312, -1, 54), FACEPALM(2254, -1, 55), SURRENDER(2360, -1, 56), LEVITATE(2327, -1, 57), MUSCLEMAN_POSE(2566, -1, 58), ROFL(2347, -1, 59), BREATHE_FIRE(2238, 358, 60), STORM(2563, 365, 61), SNOW(2417, 364, 62), INVOKE_SPRING(15357, 1391, 63), HEAD_IN_THE_SAND(12926, 1761, 64), HULA_HOOP(12928, -1, 65), DISAPPEAR(12929, -1, 66), GHOST(12932, 1762, 67), BRING_IT(12934, -1, 68), PALM_FIST(12931, -1, 69), LIVING_ON_BORROWED_TIME(-1, -1, 93), TROUBADOUR_DANCE(15424, -1, 94), EVIL_LAUGH(15535, -1, 95), GOLF_CLAP(15520, -1, 96), LOLCANO(15532, 2191, 97), INFERNO_POWER(15529, 2197, 98), DIVINE_POWER(15524, 2195, 99), YOURE_DEAD(14195, -1, 100), SCREAM(15526, -1, 101), TORNADO(15530, 2196, 102), CHAOTIC_COOKERY(15604, 2239, 103), ROFLCOPTER(16373, 3010, 104), NATURES_MIGHT(16376, 3011, 105), INNER_POWER(16382, 3014, 106), WEREWOLF_TRANSFORMATION(-1, -1, 107), CELEBRATE(16913, -1, 108), BREAKDANCE(17079, -1, 109), MAHJARRAT(17103, 3222, 110), BREAK_WIND(17076, 3226, 111), BACK_FLIP(17101, -1, 112), GRAVEDIGGER(17077, 3219, 113), FROG_TRANSFORMATION(17080, 3220, 114), MEXICAN_WAVE(17163, -1, 115), SPORTSMAN(17166, -1, 116), SUNBATHE(17213, 3257, 117), KICK_SAND(17186, 3252, 118), DEFAULT(-1, -1, -1);

		/**
		 * Represents the animation of the emote.
		 */
		private final int animation;

		/**
		 * Represents the graphic of the emote.
		 */
		private final int graphic;

		/**
		 * Represents the slot of the emote.
		 */
		private final int slot;

		/**
		 * Constructs a new {@code Emotes} {@code Object}.
		 * 
		 * @param animation
		 *            the animation of the emote.
		 * @param graphic
		 *            the graphic of the emote.
		 * @param slot
		 *            the slot of the emote.
		 */
		Emotes(int animation, int graphic, int slot) {
			this.animation = animation;
			this.graphic = graphic;
			this.slot = slot;
		}

		/**
		 * Gets the animation.
		 * 
		 * @return the animation of the emote.
		 */
		public int getAnimation() {
			return animation;
		}

		/**
		 * Gets the graphic.
		 * 
		 * @return the graphic of the emote.
		 */
		public int getGraphic() {
			return graphic;
		}

		/**
		 * Gets the slot.
		 * 
		 * @return the slot of the emote.
		 */
		public int getSlot() {
			return slot;
		}

	}

	/**
	 * Represents an enumeration of player skillcape emote types.
	 * 
	 * @author _Jordan / Apollo <citellumrsps@gmail.com>
	 * Feb 4, 2014, 2014, 8:42:06 PM
	 * 
	 */
	public enum SkillcapeEmotes {

		ATTACK(9747, 9748, 4959, 823), DEFENCE(9753, 9754, 4961, 824), STRENGTH(9750, 9751, 4981, 828), CONSTITUTION(9768, 9769, 14242, 2745), RANGING(9756, 9757, 4973, 832), MAGIC(9762, 9763, 4939, 813), PRAYER(9759, 9760, 4979, 829), COOKING(9801, 9802, 4955, 821), WOODCUTTING(9807, 9808, 4957, 822), FLETCHING(9783, 9784, 4937, 812), FISHING(9798, 9799, 4951, 819), FIREMAKING(9804, 9805, 4975, 831), CRAFTING(9780, 9781, 4949, 818), SMITHING(9795, 9796, 4943, 815), MINING(9792, 9793, 4941, 814), HERBLORE(9774, 9775, 4969, 835), AGILITY(9771, 9772, 4977, 830), THIEVING(9777, 9778, 4965, 826), SLAYER(9786, 9787, 4967, 1656), FARMING(9810, 9811, 4963, 825), RUNECRAFT(9765, 9766, 4947, 817), CONSTRUCT(9789, 9790, 4953, 820), SUMMONING(12169, 12170, 8525, 1515), HUNTER(9948, 9949, 5158, 907), QUESTING(9813, -1, 4945, 816), DUNG(18508, 18509, -1, -1), DUNG_MASTER(19709, 19710, -1, -1), CLASSIC(20765, -1, -1, -1), MAX(20767, -1, -1, -1), COMPLETIONIST(20769, 20771, -1, -1), DIVINATION(29185, 29186, 21241, 4254), DEFAULT(-1, -1, -1, -1);

		/**
		 * Represents the untrimmed item id of the skillcape.
		 */
		private final int untrimmed;

		/**
		 * Represents the trimmed item id of the skillcape.
		 */
		private final int trimmed;

		/**
		 * Represents the animation of the emote.
		 */
		private final int animation;

		/**
		 * Represents the graphic id of the emote.
		 */
		private final int graphic;

		SkillcapeEmotes(int untrimmed, int trimmed, int animation, int graphic) {
			this.untrimmed = untrimmed;
			this.trimmed = trimmed;
			this.animation = animation;
			this.graphic = graphic;
		}

		/**
		 * Gets the untrimmed skillcape item.
		 * 
		 * @return the untrimmed skillcape.
		 */
		public int getUntrimmed() {
			return untrimmed;
		}

		/**
		 * Gets the trimmed skillcape item.
		 * 
		 * @return the trimmed skillcape.
		 */
		public int getTrimmed() {
			return trimmed;
		}

		/**
		 * Gets the animation of the skillcape emote.
		 * 
		 * @return the animation of the cape.
		 */
		public int getAnimation() {
			return animation;
		}

		/**
		 * Gets the graphic of the skillcape emote.
		 * 
		 * @return the graphic of the cape.
		 */
		public int getGraphic() {
			return graphic;
		}

	}

	/**
	 * Handles the buttons of the player emotes.
	 * 
	 * @param player
	 *            the player.
	 * @param slotId
	 *            the slot.
	 */
	public static void handleButton(final Player player, int slotId) {
		switch (slotId) {
		case 37:
			final int capeId = player.getEquipment().getCapeId();
			final SkillcapeEmotes cape = getSkillcape(capeId);
			switch (capeId) {
			case 20769://completionist
			case 20771:
				WorldTasksManager.schedule(new WorldTask() {
					private int step;

					@Override
					public void run() {
						if (step == 0) {
							player.getAnimator().animate(Animation.create(356));
							player.setGraphics(new Graphics(307));
						} else if (step == 2) {
							player.getAppearance().setCustomRender(capeId == 20769 ? 1830 : 3372);
							player.getAnimator().animate(Animation.create(1174));
							player.setGraphics(new Graphics(1443));
						} else if (step == 8) {
							player.getAppearance().setCustomRender(-1);
							player.getAnimator().animate(Animation.create(1175));
							stop();
						}
						step++;
					}

				}, 0, 1);
				System.out.println("Hello");
				break;
			}
			player.getAnimator().animate(Animation.create(cape.getAnimation()));
			player.setGraphics(new Graphics(cape.getGraphic()));
			return;
		}
		final Emotes emote = getEmote(slotId);
		player.getAnimator().animate(Animation.create(emote.getAnimation()));
		player.setGraphics(new Graphics(emote.getGraphic()));
	}

	/**
	 * Gets the emote definitions based on slot id.
	 * 
	 * @param slotId
	 *            the slot of the emote.
	 * @return the emote definitions.
	 */
	public static Emotes getEmote(int slotId) {//overkill
		for (final Emotes emote : Emotes.values()) {
			if (emote.getSlot() == slotId)
				return emote;
		}
		return Emotes.DEFAULT;
	}

	/**
	 * Gets the skillcape emote definitions based on the cape worn.
	 * 
	 * @param capeId
	 *            the skillcape.
	 * @return the cape definitions.
	 */
	public static SkillcapeEmotes getSkillcape(int capeId) {
		for (final SkillcapeEmotes cape : SkillcapeEmotes.values()) {
			if (cape.getUntrimmed() == capeId || cape.getTrimmed() == capeId)
				return cape;
		}
		return SkillcapeEmotes.DEFAULT;
	}

}
