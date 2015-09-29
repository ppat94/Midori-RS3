package com.orinate.game.model.npc.combat;

import com.orinate.cache.parsers.NPCDefinitions;

/**
 * 
 * @author Trenton
 * 
 */
public class CombatDefinitions {

	// DO NOT MODIFY OR RENAME ANY VALUE IN THIS CLASS OR NPC DEFINITIONS WILL
	// NOT LOAD

	/*
	 * ======Weakness IDs====== 0: Monsters with no weaknesses 1: Monsters weak
	 * to water 2: Monsters weak to earth 3: Monsters weak to air 4: Monsters
	 * weak to fire 5: Monsters weak to arrows 6: Monsters weak to bolts 7:
	 * Monsters weak to thrown weapons 8: Monsters weak to slashing weapons 9:
	 * Monsters weak to stabbing weapons 10: Monsters weak to crushing weapons
	 */

	private int npcId;
	private int maximumHitpoints;
	private int attack;
	private int defence;
	private int magic;
	private int ranged;
	private int weakness;
	private int deathAnimation;
	private int meleeAttackAnimation;
	private int rangedAttackAnimation;
	private String slayerTask;
	private float combatXp;
	private boolean aggressive;
	private boolean poisonous;

	public CombatDefinitions(int npcId) {
		this.npcId = npcId;
		maximumHitpoints = 200;
		attack = 1;
		defence = 1;
		magic = 1;
		ranged = 1;
		weakness = 0;
		aggressive = false;
		slayerTask = null;
		poisonous = false;
	}

	public int getMaximumHitpoints() {
		return maximumHitpoints;
	}

	public void setMaximumHitpoints(int maximumHitpoints) {
		this.maximumHitpoints = maximumHitpoints;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getDefence() {
		return defence;
	}

	public void setDefence(int defence) {
		this.defence = defence;
	}

	public int getMagic() {
		return magic;
	}

	public void setMagic(int magic) {
		this.magic = magic;
	}

	public int getRanged() {
		return ranged;
	}

	public void setRanged(int ranged) {
		this.ranged = ranged;
	}

	public int getWeakness() {
		return weakness;
	}

	public void setWeakness(int weakness) {
		this.weakness = weakness;
	}

	public boolean isAggressive() {
		return aggressive;
	}

	public void setAggressive(boolean agressive) {
		this.aggressive = agressive;
	}

	public int getMeleeAttackAnimation() {
		return meleeAttackAnimation;
	}

	public void setMeleeAttackAnimation(int meleeAttackAnimation) {
		this.meleeAttackAnimation = meleeAttackAnimation;
	}

	public int getRangedAttackAnimation() {
		return rangedAttackAnimation;
	}

	public void setRangedAttackAnimation(int rangedAttackAnimation) {
		this.rangedAttackAnimation = rangedAttackAnimation;
	}

	public String getSlayerTask() {
		return slayerTask;
	}

	public void setSlayerTask(String slayerTask) {
		this.slayerTask = slayerTask;
	}

	public boolean isPoisonous() {
		return poisonous;
	}

	public void setPoisonous(boolean poisonous) {
		this.poisonous = poisonous;
	}

	public int getDeathAnimation() {
		return deathAnimation;
	}

	public void setDeathAnimation(int deathAnimation) {
		this.deathAnimation = deathAnimation;
	}

	public float getCombatXp() {
		if (combatXp > 0)
			return combatXp;
		else
			return ((NPCDefinitions.forId(npcId).combatLevel + maximumHitpoints) / 2) / 10;
	}

	public void setCombatXp(float combatXp) {
		this.combatXp = combatXp;
	}

	/**
	 * @return the npcId
	 */
	public int getNpcId() {
		return npcId;
	}

	public int getRespawnDelay() {
		return 20;
	}

}
