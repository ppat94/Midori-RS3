package com.orinate.game.content.combat;

import com.orinate.game.content.combat.ability.Ability;
import com.orinate.game.content.combat.ability.AbilityType;
import com.orinate.game.model.Entity;

/**
 * Handles the default attack.
 * 
 * @author Emperor
 * 
 */
public abstract class DefaultAttack extends Ability {

	/**
	 * The tick count.
	 */
	protected int ticks = 0;

	/**
	 * The cooldown ticks.
	 */
	protected int cooldown = 0;

	/**
	 * If the default attack is for the off-hand.
	 */
	protected boolean offhand;

	/**
	 * Constructs a new {@code DefaultAttack} {@code Object}.
	 * 
	 * @param style
	 *            The combat style.
	 * @param offhand
	 *            If the attack is off-hand.
	 */
	public DefaultAttack(CombatStyle style, boolean offhand) {
		super(offhand ? 2 : 0, 0, AbilityType.BASIC, 0, 0, style, null);
		this.offhand = offhand;
	}

	@Override
	public boolean fire(Entity user, Entity victim) {
		ticks++;
		if (victim == null || !event.getMovement().interact(victim, getStyle())) {
			event.setTicks(0);
			return false;
		}
		user.resetWalkSteps();
		if (ticks >= cooldown) {
			if (!offhand) {
				user.getProperties().drainAdrenaline(-8, user);
			}
			System.out.println("Cooldown: " + cooldown + ", " + ticks);
			if (swing(user, victim)) {
				this.ticks = 0;
			}
			cooldown = user.getCombatSchedule().getDefaultDelay(offhand);
		}
		return false;
	}

	/**
	 * Starts a combat swing.
	 * 
	 * @param entity
	 *            The entity attacking.
	 * @param victim
	 *            The entity under attack.
	 * @return {@code True} if the swing could be executed.
	 */
	public abstract boolean swing(Entity entity, Entity victim);

	/**
	 * 
	 * Visualizes the combat swing.
	 * 
	 * @param entity
	 *            The entity.
	 * @param victim
	 *            The victim.
	 */
	public abstract void visualize(Entity entity, Entity victim);

	/**
	 * Sets the offhand flag.
	 * 
	 * @param offhand
	 *            The offhand flag.
	 */
	public void setOffhand(boolean offhand) {
		this.offhand = offhand;
	}
}