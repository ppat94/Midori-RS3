package com.orinate.game.content.combat;

import com.orinate.game.model.Entity;
import com.orinate.game.model.update.masks.Bar;
import com.orinate.game.model.update.masks.Hit;
import com.orinate.game.model.update.masks.HitType;
import com.orinate.game.task.WorldTask;
import com.orinate.game.task.WorldTasksManager;

/**
 * Impact handling class.
 * 
 * @author Emperor
 * 
 */
public final class ImpactHandler {

	/**
	 * The entity.
	 */
	private final Entity entity;

	/**
	 * Constructs a new {@code Entity} {@code Object}.
	 * 
	 * @param entity
	 *            The entity.
	 */
	public ImpactHandler(Entity entity) {
		this.entity = entity;
	}

	/**
	 * Handles an impact.
	 * 
	 * @param hit
	 *            The hit being received.
	 * @param source
	 *            The impact-dealing entity.
	 * @param type
	 *            The type of hit.
	 * @param ticks
	 *            The amount of ticks before applying the hit.
	 */
	public void impact(final int hit, final Entity source, final HitType type, int ticks) {
		if (ticks < 1) {
			impact(hit, source, type);
			return;
		}
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				impact(hit, source, type);
				stop();
			}
		}, ticks);
		return;
	}

	/**
	 * Handles an impact.
	 * 
	 * @param hit
	 *            The hit.
	 * @param source
	 *            The impact-dealing entity.
	 * @param type
	 *            The hit type.
	 */
	public void impact(int hit, Entity source, HitType type) {
		if (hit > entity.getHitpoints()) {
			hit = entity.getHitpoints();
			entity.setHitpoints(entity.getMaxHitpoints());
			// entity.processDeath();
			source.getCombatSchedule().end();
		} else {
			entity.removeHitpoints(hit);
		}
		if (hit < 1) {
			type = HitType.MISSED;
		}
		entity.appendHit(new Hit(hit, 5, type, Bar.HITPOINTS));
		entity.appendBar(Bar.HITPOINTS);
	}

	/**
	 * @return the entity
	 */
	public Entity getEntity() {
		return entity;
	}
}