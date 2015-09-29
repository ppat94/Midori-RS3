package com.orinate.game.content.skills;

import com.orinate.game.model.player.Player;

/**
 * Used for managing and processing all current skill actions.
 * 
 * @author Tyler
 * 
 */
public class SkillManager {

	/**
	 * The {@code Player} instance.
	 */
	private Player player;

	/**
	 * The {@code Skill} instance.
	 */
	private transient Skill currentSkill;
	private transient int skillDelay;

	/**
	 * Constructs a new {@code Player} instance.
	 * 
	 * @param player
	 */
	public SkillManager(Player player) {
		this.player = player;
		this.skillDelay = 0;
	}

	/**
	 * Processes the current skill action.
	 * 
	 * @return If we can process the skill action.
	 */
	public void process() {
		if (player == null || currentSkill == null) {
			return;
		}
		if (!currentSkill.canProcess()) {
			stopTraining();
			return;
		}
		if (skillDelay > 0) {
			skillDelay--;
			return;
		}
		if (skillDelay == -1) {
			stopTraining();
			return;
		}
		if (!currentSkill.onProcess()) {
			stopTraining();
			return;
		}
		skillDelay = currentSkill.getSkillDelay();
	}

	/**
	 * Trains the current {@code Skill} instance.
	 * 
	 * @param currentSkill
	 *            The {@code Skill} instance to train.
	 */
	public void train(Skill currentSkill) {
		this.stopTraining();
		if (!currentSkill.onSkillStart()) {
			stopTraining();
			return;
		}
		skillDelay = 2;
		this.currentSkill = currentSkill;
	}

	/**
	 * Stops the training of the current {@code Skill} instance.
	 */
	public void stopTraining() {
		if (currentSkill == null) {
			return;
		}
		currentSkill.onSkillEnd();
		currentSkill = null;
	}

	/**
	 * Sets the delay of the current skill.
	 * 
	 * @param newDelay
	 *            The new delay to set.
	 */
	public void setDelay(int newDelay) {
		this.skillDelay = newDelay;
	}
}
