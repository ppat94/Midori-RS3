package com.orinate.game.content.skills;

import com.orinate.game.model.player.Player;

/**
 * Class used for representing a specific skill.
 * 
 * @author Tyler
 * 
 */
public abstract class Skill {

	protected Player player;
	protected Object[] args;

	/**
	 * Constructs a new {@code Skill} instance.
	 * 
	 * @param args
	 *            The {@code Object} arguments.
	 */
	public Skill(Player player, Object... args) {
		this.player = player;
		this.args = args;
	}

	public abstract boolean onSkillStart();

	public abstract boolean canProcess();

	public abstract boolean onProcess();

	public abstract void onSkillEnd();

	public abstract int getSkillDelay();

	public Object[] getArgs() {
		return args;
	}
}
