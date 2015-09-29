package com.orinate.game.content.skills.impl.fishing;

import com.orinate.game.content.skills.Skill;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.content.skills.impl.fishing.FishDefinitions.CaptureDefinition;
import com.orinate.game.content.skills.impl.fishing.FishDefinitions.SpotDefinitions;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.visual.Animation;
import com.orinate.game.task.WorldTask;
import com.orinate.game.task.WorldTasksManager;
import com.orinate.util.Utilities;

/**
 * The fishing {@link Skill}
 * @author SonicForce41
 */
public class Fishing extends Skill {
	
	/**
	 * The {@link SpotDefinitions}
	 */
	private SpotDefinitions definitions;
	
	/**
	 * The maximum length from the {@link SpotDefinitions} for {@link CatchDefinition}s
	 */
	private int maxLength;
	
	/**
	 * The current catch being attempted
	 */
	private int currentCatch;

	/**
	 * Constructs a new {@code Fishing.java} {@code Object}.
	 * @param player the {@link Player}
	 * @param definitions the {@link SpotDefinitions}
	 */
	public Fishing(Player player, SpotDefinitions definitions) {
		super(player);
		this.definitions = definitions;
		this.maxLength = getMaxFishable(player, definitions, definitions.getCaptureDefinitions().length - 1);
	}

	@Override
	public boolean onSkillStart() {
		currentCatch = Utilities.getRandom(maxLength);
		if (player.getSkills().getLevel(Skills.FISHING) < definitions.getCaptureDefinitions()[currentCatch].getLevel()) {
			player.getWriter().sendGameMessage("You need a fishing level of "
					+definitions.getCaptureDefinitions()[currentCatch].getLevel()+" to fish here.");
			return false;
		}
		if (!player.getInventory().containsItem(definitions.getToolId(), 1)) {
			Item tool = new Item(definitions.getToolId());
			if (tool != null) {
				player.getWriter().sendGameMessage("You don't have a "+tool.getName().toLowerCase()
						+" to fish here.");
				return false;
			}
		}
		if (definitions.getBaitId() != -1 && !player.getInventory().containsItem(definitions.getBaitId(), 1)) {
			Item bait = new Item(definitions.getBaitId());
			if (bait != null) {
				player.getWriter().sendGameMessage("You don't have enough "+bait.getName().toLowerCase()
						+"s to fish here.");
				return false;
			}
		}
		if (!player.getInventory().hasFreeSlots()) {
			player.getWriter().sendGameMessage("You don't have enough inventory space.");
			return false;
		}
		return true;
	}

	@Override
	public boolean canProcess() {
		currentCatch = Utilities.getRandom(maxLength);
		if (definitions.getBaitId() != -1 && !player.getInventory().containsItem(definitions.getBaitId(), 1)) {
			Item bait = new Item(definitions.getBaitId());
			if (bait != null) {
				player.getWriter().sendGameMessage("You don't have enough "+bait.getName().toLowerCase()
						+"s to fish here.");
				return false;
			}
		}
		if (!player.getInventory().hasFreeSlots()) {
			player.getWriter().sendGameMessage("You don't have enough inventory space.");
			return false;
		}
		return true;
	}

	@Override
	public boolean onProcess() {
		if (!player.getInventory().hasFreeSlots()) {
			return false;
		}
		player.getWriter().sendGameMessage("You attempt to capture a fish...");
		player.setAnimation(definitions.getAnimation());
		if (getRandomizer(player, definitions.getCaptureDefinitions()[currentCatch]) 
				&& player.getInventory().hasFreeSlots())
			onSkillEnd();
		return true;
	}

	@Override
	public void onSkillEnd() {
		if (!player.getInventory().hasFreeSlots()) {
			player.setAnimation(Animation.create(-1));
			return;
		}
		if (player.getAnimation() == null)
			return;
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				final Item item = new Item(definitions.getCaptureDefinitions()[currentCatch].getId());
				player.getInventory().addItem(item);
				player.getSkills().addXp(Skills.FISHING, definitions.getCaptureDefinitions()[currentCatch].getExperience());
				if (definitions.getBaitId() != -1)
					player.getInventory().deleteItem(new Item(definitions.getBaitId()));
				if (definitions.getCaptureDefinitions()[currentCatch] == CaptureDefinition.ANCHOVIES ||
						definitions.getCaptureDefinitions()[currentCatch] == CaptureDefinition.SHRIMP)
					player.getWriter().sendGameMessage("You catch some "+item.getName().toLowerCase().replace("raw ", "")+".");
				else
					player.getWriter().sendGameMessage("You catch a "+item.getName().toLowerCase().replace("raw ", "")+".");
			}
		}, 2);
	}

	@Override
	public int getSkillDelay() {
		return 5;
	}
	
	/**
	 * Method checks if the player can recieve reward
	 * @param player the Player
	 * @param fish the Definition
	 * @return can/+not get reward
	 */
	public static boolean getRandomizer(Player player, CaptureDefinition fish) {
		return ((Utilities.getRandom(3) * player.getSkills().getLevel(Skills.FISHING)) / 3) > (fish.getLevel() / 2);
	}
	
	/**
	 * Method calculates the highest capture possible
	 * @param player the Player
	 * @param spotDef the Definition
	 * @param defLength the length of Definition
	 * @return the value of highest fish
	 */
	public static int getMaxFishable(Player player, SpotDefinitions spotDef, int defLength) {
		for (; defLength > 0; defLength--) {
			CaptureDefinition fishDef = spotDef.getCaptureDefinitions()[defLength];
			if (fishDef != null) {
				if (fishDef.getLevel() > player.getSkills().getLevel(Skills.FISHING))
					continue;
				if (fishDef.getLevel() <= player.getSkills().getLevel(Skills.FISHING))
					return defLength;
			}
		}
		return -1;
	}

	/**
	 * Gets the spotDefinitions
	 * @return the spotDefinitions
	 */
	public SpotDefinitions getSpotDefinitions() {
		return definitions;
	}

}