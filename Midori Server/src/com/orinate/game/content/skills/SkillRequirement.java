package com.orinate.game.content.skills;

import com.orinate.game.model.Entity;

/**
 * Represents a skill requirement.
 * @author Emperor
 *
 */
public final class SkillRequirement {

	/**
	 * The skill id.
	 */
	private final int skillId;
	
	/**
	 * The level required.
	 */
	private final int level;
	
	/**
	 * If the skill
	 */
	private final boolean checkStatic;
	
	/**
	 * Constructs a new {@code SkillRequirement} {@code Object}.
	 * @param skillId The skill id.
	 * @param level The level.
	 */
	public SkillRequirement(int skillId, int level) {
		this(skillId, level, false);
	}
	
	/**
	 * Constructs a new {@code SkillRequirement} {@code Object}.
	 * @param skillId The skill id.
	 * @param level The level.
	 * @param checkStatic If the requirement is for the static level.
	 */
	public SkillRequirement(int skillId, int level, boolean checkStatic) {
		this.skillId = skillId;
		this.level = level;
		this.checkStatic = checkStatic;
	}
	
	/**
	 * Checks if the entity meets the skill level requirement.
	 * @param e The entity.
	 * @return {@code True} if so.
	 */
	public boolean meetsRequirement(Entity e) {
		if (checkStatic) {
			return e.getSkills().getLevelForXp(skillId) >= level;
		}
		return e.getSkills().getLevel(skillId) >= level;
	}

	/**
	 * @return the skillId
	 */
	public int getSkillId() {
		return skillId;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return the checkStatic
	 */
	public boolean isCheckStatic() {
		return checkStatic;
	}
}