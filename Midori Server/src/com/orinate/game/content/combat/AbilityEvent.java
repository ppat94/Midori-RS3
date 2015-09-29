package com.orinate.game.content.combat;

import com.orinate.game.content.combat.ability.Ability;
import com.orinate.game.model.Entity;

/**
 * An event handling an ability.
 * 
 * @author Emperor
 * 
 */
public final class AbilityEvent {

	/**
	 * The amount of ticks passed.
	 */
	private int ticks;

	/**
	 * The entity using the ability.
	 */
	private Entity user;

	/**
	 * The target.
	 */
	private Entity target;

	/**
	 * The ability.
	 */
	private Ability ability;

	/**
	 * The combat movement object.
	 */
	private CombatMovement movement;

	/**
	 * Constructs a new {@code AbilityEvent} {@code Object}.
	 * 
	 * @param user
	 *            The entity using the ability.
	 * @param target
	 *            The target.
	 * @param movement
	 *            The combat movement.
	 * @param ability
	 *            The ability used.
	 */
	public AbilityEvent(Entity user, Entity target, CombatMovement movement, Ability ability) {
		this.user = user;
		this.target = target;
		this.movement = movement;
		this.ability = ability;
		if (ability != null) {
			ability.setEvent(this);
		}
	}

	/**
	 * Pulses the ability event.
	 * 
	 * @return {@code True} if the event should be removed from the schedule.
	 */
	public boolean pulse() {
		if (++ticks >= ability.getFireDelay()) {
			return fire();
		}
		return false;
	}

	/**
	 * Checks if this ability is overriden by the used ability.
	 * 
	 * @param a
	 *            The ability used.
	 * @return {@code False} if this ability can't be overriden.
	 */
	public boolean isOverriden(Ability a) {
		return a.override(ability);
	}

	/**
	 * Called on when the ability event finishes.
	 */
	public void destroy() {
		ability.destroy();
	}

	/**
	 * Fires the ability effect.
	 */
	public boolean fire() {
		return ability.fire(user, target);
	}

	/**
	 * Checks if the ability should be instantly fired.
	 * 
	 * @return {@code True} if so.
	 */
	public boolean isInstant() {
		return ability.getFireDelay() < 1;
	}

	/**
	 * Gets the ability.
	 * 
	 * @return The ability.
	 */
	public Ability getAbility() {
		return ability;
	}

	/**
	 * Sets the target.
	 * 
	 * @param target
	 *            The target.
	 */
	public void setTarget(Entity target) {
		this.target = target;
	}

	/**
	 * @return the movement
	 */
	public CombatMovement getMovement() {
		return movement;
	}

	/**
	 * @param movement
	 *            the movement to set
	 */
	public void setMovement(CombatMovement movement) {
		this.movement = movement;
	}

	/**
	 * @return the ticks
	 */
	public int getTicks() {
		return ticks;
	}

	/**
	 * @param ticks
	 *            the ticks to set
	 */
	public int setTicks(int ticks) {
		this.ticks = ticks;
		return ticks;
	}
}