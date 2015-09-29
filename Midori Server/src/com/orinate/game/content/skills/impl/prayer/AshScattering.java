package com.orinate.game.content.skills.impl.prayer;

import com.orinate.game.content.skills.Skill;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.update.masks.Graphics;
import com.orinate.game.model.visual.Animation;

/**
 * Handles prayer ash scattering.
 * 
 * @author _Jordan / Apollo <citellumrsps@gmail.com>
 * Feb 7, 2014, 2014, 10:42:48 PM
 * 
 */
public class AshScattering extends Skill {

	/**
	 * The {@link AshDefinitions} instance.
	 */
	private final AshDefinitions definitions;

	/**
	 * Represents the scatter animation id.
	 */
	private final static int SCATTER_ANIMATION = 445;

	/**
	 * Constructs a new {@link AshScattering} {@code Object}.
	 * 
	 * @param player
	 *            the player.
	 * @param definitions
	 *            the definitions of the ashes.
	 */
	public AshScattering(Player player, AshDefinitions definitions) {
		super(player);
		this.definitions = definitions;
	}

	@Override
	public boolean onSkillStart() {
		if (player.getAttributes().get("scattering") == Boolean.TRUE)
			player.getAttributes().remove("scattering");
		player.lock(3);
		return true;
	}

	@Override
	public boolean canProcess() {
		if (player.getAttributes().get("scattering") == Boolean.TRUE)
			return false;
		return true;
	}

	@Override
	public boolean onProcess() {
		player.getAnimator().animate(Animation.create(SCATTER_ANIMATION));
		player.setGraphics(new Graphics(definitions.getGraphic()));
		player.getWriter().sendGameMessage("You scatter the ashes in the wind.");
		player.getInventory().deleteItem(new Item(definitions.getItem()));
		player.getSkills().addXp(Skills.PRAYER, definitions.getExperience());
		player.getAttributes().set("scattering", Boolean.TRUE);
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
	 * Gets the definitions of the ashes.
	 * 
	 * @return the definitions of the ashes scattered.
	 */
	public AshDefinitions getDefinitions() {
		return definitions;
	}

	/**
	 * Gets the ashes definitions based on item click.
	 * 
	 * @param item
	 *            of the ashes.
	 * @return the definitions of the ashes.
	 */
	public static AshDefinitions getAshes(int item) {
		for (final AshDefinitions definitions : AshDefinitions.values()) {
			if (definitions.getItem() == item)
				return definitions;
		}
		return null;
	}

}
