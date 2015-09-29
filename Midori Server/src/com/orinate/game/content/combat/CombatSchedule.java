package com.orinate.game.content.combat;

import com.orinate.game.content.combat.ability.Ability;
import com.orinate.game.content.combat.ability.AbilityType;
import com.orinate.game.core.GameCore;
import com.orinate.game.model.Entity;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.player.Player;

/**
 * Handles combat events scheduling.
 * 
 * @author Emperor
 * 
 */
public final class CombatSchedule {

	/**
	 * The main-hand index.
	 */
	public static final int MAIN_HAND = 0;

	/**
	 * The off-hand index.
	 */
	public static final int OFF_HAND = 1;

	/**
	 * The entity.
	 */
	private final Entity entity;

	/**
	 * The entity the attacking entity is locked on.
	 */
	private Entity lock;

	/**
	 * The current victim.
	 */
	private Entity victim;

	/**
	 * The current events schedule.
	 */
	private AbilityEvent[] events = new AbilityEvent[5];

	/**
	 * The combat movement.
	 */
	private final CombatMovement movement;

	/**
	 * The combat styles the entity is using (0= main-hand, 1=off-hand).
	 */
	private AbilityEvent[] attacks = new AbilityEvent[2];

	/**
	 * The combat stance tick value.
	 */
	private int combatStance;

	/**
	 * The ability delay.
	 */
	private int abilityDelay;

	/**
	 * Constructs a new {@code CombatSchedule} {@code Object}.
	 * 
	 * @param entity
	 *            The entity.
	 */
	public CombatSchedule(Entity entity) {
		this.entity = entity;
		this.movement = new CombatMovement(entity);
		attacks[0] = new AbilityEvent(entity, null, movement, CombatStyle.MELEE.newInstance(false));
	}

	/**
	 * Uses an ability.
	 * 
	 * @param ability
	 *            The ability.
	 */
	public void useAbility(Ability ability) {
		if (!(entity instanceof Player)) {
			return;
		}
		Player p = (Player) entity;
		if (lock == null) {
			if (victim == null) {
				p.getWriter().sendGameMessage("You do not have an opponent.");
				return;
			}
			lock = victim;
		}
		if (abilityDelay > GameCore.getTicks()) {
			return;
		}
		if (ability == null || ability.getNextUsage() >= GameCore.getTicks()) {
			p.getWriter().sendGameMessage("This ability is not ready yet.");
			return;
		}
		if (entity.getProperties().getAdrenaline() < ability.getType().ordinal() * 50) {
			p.getWriter().sendGameMessage("You don't have enough adrenaline to use this ability.");
			return;
		}
		int drain = -8;
		if (ability.getType() == AbilityType.THRESHOLD) {
			drain = 15;
		} else if (ability.getType() == AbilityType.ULTIMATE) {
			drain = 100;
		}
		p.getProperties().drainAdrenaline(drain, entity);
		schedule(new AbilityEvent(entity, lock, movement, ability));
	}

	/**
	 * Updates the schedule.
	 */
	public void tick() {
		updateCombatStance();
		// TODO: Check if should be updated without victim.
		for (int i = 0; i < events.length; i++) {
			AbilityEvent event = events[i];
			if (event != null && event.pulse()) {
				event.destroy();
				events[i] = null;
			}
		}
		if (victim == null) {
			if (!isCombatStance() && entity.getProperties().getAdrenaline() > 0) {
				entity.getProperties().drainAdrenaline(5, entity);
			}
			pulseBaseAttacks();
			return;
		}
		combatStance = GameCore.getTicks() + 17;
		entity.setFaceEntity(victim);
		if (abilityDelay > GameCore.getTicks()) {
			return;
		}
		pulseBaseAttacks();
	}

	/**
	 * Updates the combat stance.
	 */
	private void updateCombatStance() {
		if (!isCombatStance() && lock != null) {
			lock = null;
			if (victim != null) {
				lock = victim;
				combatStance = GameCore.getTicks() + 17;
			}
			if (entity instanceof Player) {
				((Player) entity).getAppearance().refresh();
			}
		}
	}

	/**
	 * Sets the default attacks.
	 */
	public void setDefaultAttacks() {
		for (int i = 0; i < 2; i++) {
			boolean offhand = i == 1;
			CombatStyle style = entity.getCombatStyle(offhand);
			setDefaultAttack(i, style == null ? null : style.newInstance(offhand));
		}
	}

	/**
	 * Updates the base attacks.
	 */
	private void pulseBaseAttacks() {
		try {
			for (AbilityEvent e : attacks) {
				if (e != null) {
					e.pulse();
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Sets a default attack.
	 * 
	 * @param index
	 *            The index (0=main-hand, 1=off-hand).
	 * @param attack
	 *            The default attack.
	 */
	public void setDefaultAttack(int index, DefaultAttack attack) {
		if (attack == null) {
			attacks[index] = null;
			return;
		}
		if (index == 1) {
			attack.setOffhand(true);
		}
		if (attacks[index] != null) {
			DefaultAttack a = (DefaultAttack) attacks[index].getAbility();
			if (a != null) {
				attack.cooldown = getDefaultDelay(index == 1);
				attack.ticks = attack.cooldown - (a.cooldown - a.ticks);
			}
		}
		attacks[index] = new AbilityEvent(entity, victim, movement, attack);
	}

	/**
	 * Schedules an ability event.
	 * 
	 * @param event
	 *            The event.
	 */
	public void schedule(AbilityEvent event) {
		if (abilityDelay > GameCore.getTicks()) {
			return;
		}
		Ability a = event.getAbility();
		event.setTarget(lock);
		a.startAttack(entity, lock);
		if (!a.meetsRequirements(entity, lock)) {
			return;
		}
		if (entity instanceof Player) {
			((Player) entity).getWriter().sendCS2Script(6570, 1, 1, a.getRechargeDuration(), 0, event.getAbility().getScriptId());
			((Player) entity).getWriter().sendCS2Script(6066, 14881); // 14882 =
																		// 4
																		// ticks
		}
		event.getAbility().setNextUsage(GameCore.getTicks() + event.getAbility().getRechargeDuration());
		abilityDelay = GameCore.getTicks() + 3;
		checkOverride(event.getAbility());
		if (event.isInstant()) {
			if (event.fire()) {
				event.destroy();
				return;
			}
		}
		for (int i = 0; i < events.length; i++) {
			if (events[i] == null) {
				events[i] = event;
				return;
			}
		}
	}

	/**
	 * Checks if this ability overrides any other.
	 * 
	 * @param a
	 *            The ability used.
	 */
	private void checkOverride(Ability a) {
		for (int i = 0; i < events.length; i++) {
			AbilityEvent event = events[i];
			if (event != null && !event.isOverriden(a)) {
				event.destroy();
				events[i] = null;
			}
		}
	}

	/**
	 * Attacks the entity.
	 * 
	 * @param e
	 *            The entity to attack.
	 */
	public void attack(Entity e) {
		this.victim = e;
		for (AbilityEvent a : attacks) {
			if (a != null) {
				a.setTarget(e);
			}
		}
		this.lock = e;
	}

	/**
	 * Stops the attacking action.
	 */
	public void end() {
		movement.reset();
		for (AbilityEvent a : attacks) {
			if (a != null) {
				a.setTarget(null);
			}
		}
		// for (AbilityEvent a : events) {
		// if (a != null) {
		// a.destroy();
		// }
		// }
		// events = new AbilityEvent[5];
		entity.setFaceEntity(null);
		this.victim = null;
	}

	/**
	 * Gets the default delay.
	 * 
	 * @param offhand
	 *            If the attack is for the off-hand.
	 * @return The default delay in ticks.
	 */
	public int getDefaultDelay(boolean offhand) {
		if (entity instanceof Player) {
			Player p = (Player) entity;
			Item item = p.getEquipment().get(offhand ? 5 : 3);
			if (item != null) {
				return item.getDefinitions().getSpeed();
			}
			return 4;
		}
		return offhand ? 6 : 4;
	}

	/**
	 * Checks if the entity is in combat stance.
	 * 
	 * @return {@code true} if so.
	 */
	public boolean isCombatStance() {
		return combatStance >= GameCore.getTicks();
	}

	/**
	 * Gets the current victim.
	 * 
	 * @return The victim.
	 */
	public Entity getVictim() {
		return victim;
	}
}