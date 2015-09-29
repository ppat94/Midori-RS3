package com.orinate.game.content.combat.def;

import com.orinate.game.content.combat.CombatStyle;
import com.orinate.game.content.combat.DefaultAttack;
import com.orinate.game.content.combat.ability.Ability;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.Entity;
import com.orinate.game.model.update.masks.HitType;

/**
 * Handles a melee default attack.
 * 
 * @author Emperor
 * 
 */
public final class MeleeDefaultAttack extends DefaultAttack {

	/**
	 * Constructs a new {@code MeleeDefaultAttack} {@code Object}.
	 */
	public MeleeDefaultAttack(boolean offhand) {
		super(CombatStyle.MELEE, offhand);
	}

	@Override
	public boolean swing(Entity entity, Entity victim) {
		visualize(entity, victim);
		int hit = 0;
		double acc = getHitAccuracy(entity, victim, offhand ? 1 : 0, Skills.ATTACK);
		if (acc > 0.1) {
			int max = getMaximumDamage(entity, offhand, Skills.STRENGTH);
			hit = (int) (acc * max);
		}
		victim.getImpactHandler().impact(hit, entity, HitType.MELEE_DAMAGE);
		return true;
	}

	@Override
	public void visualize(Entity entity, Entity victim) {
		entity.getAnimator().animate(entity.getCombatAnimation(offhand ? 1 : 0));
	}

	@Override
	public boolean meetsRequirements(Entity e, Entity target) {
		return true;
	}

	@Override
	public boolean register() {
		return false;
	}

	@Override
	public Ability newInstance() {
		return new MeleeDefaultAttack(offhand);
	}

}