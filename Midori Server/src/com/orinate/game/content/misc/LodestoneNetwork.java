package com.orinate.game.content.misc;

import java.io.Serializable;

import com.orinate.cache.parsers.AnimationParser;
import com.orinate.cache.parsers.ObjectDefinition;
import com.orinate.game.content.skills.Skill;
import com.orinate.game.model.Location;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.update.masks.Graphics;
import com.orinate.game.model.visual.Animation;
import com.orinate.game.model.visual.Animator;

/**
 * @author Tom
 * 
 */
public class LodestoneNetwork implements Serializable {

	private static final long serialVersionUID = -7067878971557130742L;

	public enum Lodestone {
		LUNAR_ISLE(69828, new Location(2085, 3915, 0)), TIRANNWN(84753, new Location(2254, 3150, 0)), EAGLES_PEEK(84749, new Location(2366, 3480, 0)), OO_GLOG(84752, new Location(2532, 2872, 0)), YANILLE(69841, new Location(2529, 3095, 0)), ARDOUGNE(69830, new Location(2634, 3349, 0)), SEERS_VILLAGE(69838, new Location(2689, 3483, 0)), FREMINIK_PROVINCE(84750, new Location(2712, 3678, 0)), KARAMJA(84751, new Location(2761, 3148, 0)), CATHERBY(69832, new Location(2831, 3452, 0)), TAVERLY(69839, new Location(2878, 3443, 0)), BURTHROPE(69831, new Location(2899, 3545, 0)), FALADOR(69835, new Location(2967, 3404, 0)), PORT_SARIM(69837, new Location(3011, 3216, 0)), EDGEVILLE(69834, new Location(3067, 3506, 0)), DRAYNOR_VILLAGE(69833, new Location(3105, 3299, 0)), WILDERNESS_VOLCANO(84754, new Location(3143, 3636, 0)), BANDIT_CAMP(69827, new Location(3214, 2955, 0)), LUMBRIDGE(69836, new Location(3233, 3222, 0)), VARROCK(69840, new Location(3214, 3377, 0)), AL_KHARID(69829, new Location(3297, 3185, 0)), CANIFIS(84748, new Location(3517, 3516, 0));

		private int objectId;
		private Location location;

		private Lodestone(int objectId, Location location) {
			this.objectId = objectId;
			this.location = location;
		}

		public int configId() {
			return ObjectDefinition.forId(objectId).configFileId;
		}

		public Location location() {
			return location;
		}

		public int unlockedValue() {
			return this.equals(Lodestone.BANDIT_CAMP) ? 15 : this.equals(Lodestone.LUNAR_ISLE) ? 190 : 1;
		}

		public static Lodestone forComponent(int componentId) {
			return LUMBRIDGE;
		}
	}

	private transient Player player;
	private boolean[] activated;
	private transient Lodestone previousLodestone;

	public LodestoneNetwork(Player player) {
		this.player = player;
		this.activated = new boolean[Lodestone.values().length];
	}

	public void teleport(final Lodestone lodestone) {
		Skill skill = new Skill(player) {

			private int ANIMATION_ID = 16385;
			private int GRAPHIC_ID = 3017;
			private AnimationParser parser;

			private int currentTime;

			@Override
			public boolean onSkillStart() {
				parser = AnimationParser.forId(ANIMATION_ID);
				player.setAnimation(Animation.create(ANIMATION_ID));
				player.setGraphics(new Graphics(GRAPHIC_ID));
				return true;
			}

			@Override
			public boolean canProcess() {
				if (currentTime == (parser.getDurationTicks() + 1)) {
					player.setNextLocation(lodestone.location);
					player.setFaceLocation(new Location(lodestone.location.getX(), lodestone.location.getY(), lodestone.location.getPlane()));
					player.setDirection(6);
				} else if (currentTime == (parser.getDurationTicks() + 2)) {
					player.getAnimator().animate(Animation.create(16386), new Graphics(3018));
				} else if (currentTime == (parser.getDurationTicks() + 7)) {
					player.setNextLocation(lodestone.location);
					player.getAnimator().animate(Animation.create(16393));
				} else if (currentTime == (parser.getDurationTicks() + 9)) {
					previousLodestone = lodestone;
					return false;
				}
				currentTime++;
				return true;
			}

			@Override
			public boolean onProcess() {
				return true;
			}

			@Override
			public void onSkillEnd() {
				player.getAnimator().animate(Animator.RESET_A, Animator.RESET_G);
			}

			@Override
			public int getSkillDelay() {
				return 0;
			}
		};
		player.getSkillManager().train(skill);
	}

	public static Lodestone getLodestoneByID(int componentId) {
		switch (componentId) {
		case 47:
			return Lodestone.LUMBRIDGE;
		case 53:
			return Lodestone.CANIFIS;
		}
		return null;
	}

	public void activateAll() {
		for (Lodestone lodestone : Lodestone.values()) {
			activated[lodestone.ordinal()] = true;
			refresh(lodestone);
		}
	}

	public void onLogin() {
		if (!activated[Lodestone.LUMBRIDGE.ordinal()]) {
			activate(Lodestone.LUMBRIDGE);
		}
		for (Lodestone lodestone : Lodestone.values()) {
			refresh(lodestone);
		}
	}

	public void activate(Lodestone lodestone) {
		if (activated[lodestone.ordinal()]) {
			return;
		}
		activated[lodestone.ordinal()] = true;
		refresh(lodestone);
	}

	private void refresh(Lodestone lodestone) {
		player.getWriter().sendConfigByFile(lodestone.configId(), activated[lodestone.ordinal()] ? lodestone.unlockedValue() : 0);
	}

	public Lodestone getPreviousLodestone() {
		return previousLodestone;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean isLocked(Lodestone lodestone) {
		return !activated[lodestone.ordinal()];
	}
}
