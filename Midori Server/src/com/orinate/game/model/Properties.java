package com.orinate.game.model;

import com.orinate.game.content.combat.CombatStyle;
import com.orinate.game.model.player.Bonuses;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.update.masks.Bar;

/**
 * Holds an Entity's properties.
 * 
 * @author Emperor
 * 
 */
public final class Properties {

	/**
	 * The bonuses of the entity.
	 */
	private final Bonuses bonuses = new Bonuses();

	/**
	 * The weapon bonuses of the entity.
	 */
	private final Bonuses[] weaponBonuses = new Bonuses[2];

	/**
	 * The entity's adrenaline.
	 */
	private int adrenaline;

	/**
	 * Constructs a new {@code Properties} {@code Object}.
	 */
	public Properties() {
		/*
		 * empty.
		 */
	}

	/**
	 * Gets the damage bonus.
	 * 
	 * @param offhand
	 *            If the bonus to get is for the off-hand.
	 * @param style
	 *            The combat style used.
	 * @return The total damage bonus.
	 */
	public int getDamageBonus(boolean offhand, CombatStyle style) {
		int damage = bonuses.getDamage()[style.ordinal()];
		Bonuses b = weaponBonuses[offhand ? 1 : 0];
		if (b != null) {
			damage += b.getDamage()[style.ordinal()] / (offhand ? 2 : 1);
		}
		return damage;
	}

	/**
	 * Gets the accuracy bonus.
	 * 
	 * @param offhand
	 *            If the bonus to get is for the off-hand.
	 * @param style
	 *            The combat style used.
	 * @return The total accuracy bonus.
	 */
	public int getAccuracyBonus(boolean offhand, CombatStyle style) {
		int accuracy = bonuses.getAccuracy()[style.ordinal()];
		Bonuses b = weaponBonuses[offhand ? 1 : 0];
		if (b != null) {
			accuracy += b.getAccuracy()[style.ordinal()];
		}
		return accuracy;
	}

	/**
	 * Gets the critical hit bonus.
	 * 
	 * @param offhand
	 *            If the bonus to get is for the off-hand.
	 * @param style
	 *            The combat style used.
	 * @return The total critical hit bonus.
	 */
	public double getCriticalHitBonus(boolean offhand, CombatStyle style) {
		double critical = bonuses.getCritical()[style.ordinal()];
		Bonuses b = weaponBonuses[offhand ? 1 : 0];
		if (b != null) {
			critical += b.getCritical()[style.ordinal()];
		}
		return critical;
	}

	/**
	 * Drains the adrenaline bar.
	 * 
	 * @param drain
	 *            The amount to drain.
	 * @param e
	 *            The entity.
	 */
	public void drainAdrenaline(int drain, Entity e) {
		if (drain > adrenaline) {
			drain = adrenaline;
		}
		adrenaline -= drain;
		if (adrenaline > 100) {
			adrenaline = 100;
		}
		if (e instanceof Player) {
			((Player) e).getWriter().sendConfig(679, adrenaline * 10);
		}
		e.appendBar(Bar.ADRENALINE);
	}

	/**
	 * Sets the weapon bonus.
	 * 
	 * @param i
	 *            The index.
	 * @param bonus
	 *            The bonus to set.
	 */
	public void setWeaponBonus(int i, Bonuses bonus) {
		weaponBonuses[i] = bonus;
	}

	/**
	 * Gets the weapon bonuses.
	 * 
	 * @return The weapon bonuses.
	 */
	public Bonuses[] getWeaponBonuses() {
		return weaponBonuses;
	}

	/**
	 * @return the bonuses
	 */
	public Bonuses getBonuses() {
		return bonuses;
	}

	/**
	 * @return the adrenaline
	 */
	public int getAdrenaline() {
		return adrenaline;
	}

	/**
	 * @param adrenaline
	 *            the adrenaline to set
	 */
	public void setAdrenaline(int adrenaline) {
		this.adrenaline = adrenaline;
	}
}