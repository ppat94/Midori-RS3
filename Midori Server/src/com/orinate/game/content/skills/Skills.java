package com.orinate.game.content.skills;

import java.io.Serializable;

import com.orinate.game.model.Entity;
import com.orinate.game.model.player.Player;

public class Skills implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final double XP_CAP = 200000000;
	public static final int ATTACK = 0, DEFENCE = 1, STRENGTH = 2, HITPOINTS = 3, RANGE = 4, PRAYER = 5, MAGIC = 6, COOKING = 7, WOODCUTTING = 8, FLETCHING = 9, FISHING = 10, FIREMAKING = 11, CRAFTING = 12, SMITHING = 13, MINING = 14, HERBLORE = 15, AGILITY = 16, THIEVING = 17, SLAYER = 18, FARMING = 19, RUNECRAFTING = 20, CONSTRUCTION = 22, HUNTER = 21, SUMMONING = 23, DUNGEONEERING = 24, DIVINATION = 25;

	public static final String[] SKILL_NAME = { "Attack", "Defence", "Strength", "Constitution", "Ranged", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Hunter", "Construction", "Summoning", "Dungeoneering", "Divination" };

	private short level[];
	private double xp[];

	private transient Entity entity;

	public Skills(Entity entity) {
		this.entity = entity;
		this.level = new short[26];
		this.xp = new double[26];
		for (int i = 0; i < level.length; i++) {
			this.level[i] = 1;
			this.xp[i] = 0;
		}
		this.level[3] = 10;
		this.xp[3] = 1184;
	}

	public void sendSkills() {
		for (int skill = 0; skill < level.length; skill++)
			refresh(skill);
	}

	public void restoreSkills() {
		for (int skill = 0; skill < level.length; skill++) {
			level[skill] = (short) getLevelForXp(skill);
			refresh(skill);
		}
	}

	public void set(int skill, int newLevel) {
		level[skill] = (short) newLevel;
		refresh(skill);
	}

	public void setLevelXP(int skill, int newLevel) {
		level[skill] = (short) newLevel;
		xp[skill] = getXPForLevel(level[skill]);
		refresh(skill);
	}

	public int drainLevel(int skill, int drain) {
		int drainLeft = drain - level[skill];
		if (drainLeft < 0) {
			drainLeft = 0;
		}
		level[skill] -= drain;
		if (level[skill] < 0) {
			level[skill] = 0;
		}
		refresh(skill);
		return drainLeft;
	}

	public static int getXPForLevel(int level) {
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			if (lvl >= level) {
				return output;
			}
			output = (int) Math.floor(points / 4);
		}
		return 0;
	}

	public int getLevelForXp(int skill) {
		double exp = xp[skill];
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= (skill == DUNGEONEERING ? 120 : 99); lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if ((output - 1) >= exp) {
				return lvl;
			}
		}
		return skill == DUNGEONEERING ? 120 : 99;
	}

	public int getTotalLevel() {
		int totalLevel = 0;
		for (int i = 0; i < level.length; i++) {
			totalLevel += getLevelForXp(i);
		}
		return totalLevel;
	}

	public void refresh(int skill) {
		if (entity instanceof Player) {
			((Player) entity).getWriter().sendSkillLevel(skill);
		}
	}

	public void addXp(int skill, double exp) {
		int oldLevel = getLevelForXp(skill);
		xp[skill] += exp;
		if (xp[skill] > XP_CAP) {
			xp[skill] = XP_CAP;
		}
		int newLevel = getLevelForXp(skill);
		int levelDiff = newLevel - oldLevel;
		if (newLevel > oldLevel) {
			level[skill] += levelDiff;
		}
		refresh(skill);
	}

	public int getCombatLevel() {
		int attack = getLevelForXp(0);
		int defence = getLevelForXp(1);
		int strength = getLevelForXp(2);
		int ranged = getLevelForXp(4);
		int magic = getLevelForXp(6);
		int combatLevel = 2;
		combatLevel += defence;
		combatLevel += getHighest(attack, strength, ranged, magic);
		return combatLevel;
	}

	public int getHighest(int... levels) {
		int largest = levels[0];
		for (int x = 0; x < levels.length; x++) {
			if (levels[x] > largest) {
				largest = levels[x];
			}
		}
		return largest;
	}

	public short[] getLevels() {
		return level;
	}

	public double[] getXp() {
		return xp;
	}

	public int getLevel(int skill) {
		return level[skill];
	}

	public double getXp(int skill) {
		return xp[skill];
	}

	public void refresh() {
		for (int skillId = 0; skillId <= 25; skillId++)
			refresh(skillId);
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}
}
