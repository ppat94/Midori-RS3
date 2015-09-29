package com.orinate.game.model.player;

/**
 * Class holding all the bonuses for an item (or entity).
 * 
 * @author Emperor
 * 
 */
public final class Bonuses {

	/**
	 * The lifepoints bonus.
	 */
	private int lifepoints;

	/**
	 * The armour bonus.
	 */
	private int armour;

	/**
	 * The prayer bonus.
	 */
	private int prayer;

	/**
	 * The damage bonuses (for all combat styles).
	 */
	private int[] damage = new int[3];

	/**
	 * The accuracy bonuses (for all combat styles).
	 */
	private int[] accuracy = new int[3];

	/**
	 * The critical hit chances (for all combat styles).
	 */
	private double[] critical = new double[3];

	/**
	 * Constructs a new @ code Bonuses} {@code Object}.
	 */
	public Bonuses() {
		/*
		 * empty.
		 */
	}

	/**
	 * Resets the bonuses.
	 * 
	 * @return This instance, for chaining.
	 */
	public Bonuses reset() {
		lifepoints = 0;
		armour = 0;
		prayer = 0;
		for (int i = 0; i < 3; i++) {
			damage[i] = 0;
			accuracy[i] = 0;
			critical[i] = 0;
		}
		return this;
	}

	/**
	 * Updates the bonuses with the specified item bonuses.
	 * 
	 * @param b
	 *            The bonuses to update with.
	 */
	public void update(Bonuses b) {
		lifepoints += b.lifepoints;
		armour += b.armour;
		prayer += b.prayer;
		for (int i = 0; i < 3; i++) {
			damage[i] += b.damage[i];
			accuracy[i] += b.accuracy[i];
			critical[i] += b.critical[i];
		}
	}

	/**
	 * @return the lifepoints
	 */
	public int getLifepoints() {
		return lifepoints;
	}

	/**
	 * @param lifepoints
	 *            the lifepoints to set
	 */
	public void setLifepoints(int lifepoints) {
		this.lifepoints = lifepoints;
	}

	/**
	 * @return the armour
	 */
	public int getArmour() {
		return armour;
	}

	/**
	 * @param armour
	 *            the armour to set
	 */
	public void setArmour(int armour) {
		this.armour = armour;
	}

	/**
	 * @return the prayer
	 */
	public int getPrayer() {
		return prayer;
	}

	/**
	 * @param prayer
	 *            the prayer to set
	 */
	public void setPrayer(int prayer) {
		this.prayer = prayer;
	}

	/**
	 * @return the damage
	 */
	public int[] getDamage() {
		return damage;
	}

	/**
	 * @param damage
	 *            the damage to set
	 */
	public void setDamage(int[] damage) {
		this.damage = damage;
	}

	/**
	 * @return the accuracy
	 */
	public int[] getAccuracy() {
		return accuracy;
	}

	/**
	 * @param accuracy
	 *            the accuracy to set
	 */
	public void setAccuracy(int[] accuracy) {
		this.accuracy = accuracy;
	}

	/**
	 * @return the critical
	 */
	public double[] getCritical() {
		return critical;
	}

	/**
	 * @param critical
	 *            the critical to set
	 */
	public void setCritical(double[] critical) {
		this.critical = critical;
	}
}