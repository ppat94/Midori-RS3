package com.orinate.game.content.skills.impl.prayer;

import com.orinate.game.content.skills.Skill;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.visual.Animation;

/**
 * Handles prayer bone burying.
 * 
 * @author _Jordan / Apollo <citellumrsps@gmail.com>
 * Feb 5, 2014, 2014, 10:31:30 PM
 * 
 */
public class BoneBurying extends Skill {

	/**
	 * The {@code BoneDefinitions} instance.
	 */
	private final BoneDefinitions definitions;

	/**
	 * Represents the bone burying animation.
	 */
	private final static int BURY_ANIMATION = 18008;

	/**
	 * Constructs a new {@code BoneBurying} {@code Object}.
	 * 
	 * @param player
	 *            the player.
	 * @param definitions
	 *            the definitions of the bone.
	 */
	public BoneBurying(Player player, BoneDefinitions definitions) {
		super(player);
		this.definitions = definitions;
	}

	@Override
	public boolean onSkillStart() {
		if (player.getAttributes().get("burying") == Boolean.TRUE)
			player.getAttributes().remove("burying");
		player.lock(3);
		player.getWriter().sendGameMessage("You dig a hole in the ground...");
		player.getInventory().deleteItem(new Item(definitions.getItem()));
		return true;
	}

	@Override
	public boolean canProcess() {
		if (player.getAttributes().get("burying") == Boolean.TRUE)
			return false;
		player.getAnimator().animate(Animation.create(BURY_ANIMATION));
		return true;
	}

	@Override
	public boolean onProcess() {
		player.getSkills().addXp(Skills.PRAYER, definitions.getExperience());
		player.getWriter().sendGameMessage("You bury the " + new Item(definitions.getItem()).getName().toLowerCase() + ".");
		player.getAttributes().set("burying", Boolean.TRUE);
		return true;
	}

	@Override
	public void onSkillEnd() {
	}

	@Override
	public int getSkillDelay() {
		return 0;
	}

	/**
	 * Gets the prayer bone definitions.
	 * 
	 * @return the definitions of the bone.
	 */
	public BoneDefinitions getDefinitions() {
		return definitions;
	}

	/**
	 * Gets the bone selected.
	 * 
	 * @param item
	 *            the bone.
	 * @return the bone definitions.
	 */
	public static BoneDefinitions getBone(int item) {
		for (final BoneDefinitions definitions : BoneDefinitions.values()) {
			if (definitions.getItem() == item)
				return definitions;
		}
		return null;
	}

}
