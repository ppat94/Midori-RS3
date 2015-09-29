package com.orinate.game.model.update.masks;

/**
 * Class used for handling hits.
 * 
 * @author Tyler Telis <tyler@xlitersps.com>
 * @since Aug 9, 2013
 * @version 1.0
 */
public class Hit {

	private Bar[] bars;
	private HitType type;
	private int damage;
	private int customType;
	private int secondaryDamage;
	private int secondaryType;
	private int delay;

	/**
	 * Constructs a new {@code Hit} instance.
	 * 
	 * @param damage
	 *            The damage to send.
	 * @param type
	 *            The {@code HitType} to send.
	 */
	public Hit(int damage, HitType type) {
		this(damage, 1, type);
	}

	/**
	 * Constructs a new {@code Hit} instance.
	 * 
	 * @param damage
	 *            The amount of damage to send.
	 * @param type
	 *            The {@code HitType} instance to send.
	 */
	public Hit(int damage, int delay, HitType type, Bar... bars) {
		this.damage = damage;
		this.delay = delay;
		this.type = type;
		this.bars = bars;
	}

	/**
	 * Constructs a new {@code Hit} instance.
	 * 
	 * @param damage
	 *            The amount of damage to send.
	 * @param type
	 *            The {@code HitType} instance to send.
	 */
	public Hit(int damage, int type, int secondaryDamage, int secondaryType, int delay, Bar... bars) {
		this.damage = damage;
		this.delay = delay;
		this.type = HitType.DOUBLE_HIT;
		this.customType = type;
		this.secondaryType = secondaryType;
		this.secondaryDamage = secondaryDamage;
		this.bars = bars;
	}

	/**
	 * @return the type
	 */
	public HitType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(HitType type) {
		this.type = type;
	}

	/**
	 * @return the damage
	 */
	public int getDamage() {
		return damage;
	}

	/**
	 * @param damage
	 *            the damage to set
	 */
	public void setDamage(int damage) {
		this.damage = damage;
	}

	/**
	 * @return the delay
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * @param delay
	 *            the delay to set
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}

	/**
	 * @return the bars
	 */
	public Bar[] getBars() {
		return bars;
	}

	/**
	 * @return the secondayDamage
	 */
	public int getSecondayDamage() {
		return secondaryDamage;
	}

	/**
	 * @param secondayDamage
	 *            the secondayDamage to set
	 */
	public void setSecondayDamage(int secondayDamage) {
		this.secondaryDamage = secondayDamage;
	}

	/**
	 * @return the secondaryType
	 */
	public int getSecondaryType() {
		return secondaryType;
	}

	/**
	 * @param secondaryType
	 *            the secondaryType to set
	 */
	public void setSecondaryType(int secondaryType) {
		this.secondaryType = secondaryType;
	}

	/**
	 * @return the customType
	 */
	public int getCustomType() {
		return customType;
	}

	/**
	 * @param customType
	 *            the customType to set
	 */
	public void setCustomType(int customType) {
		this.customType = customType;
	}
}
