package com.orinate.game.content.combat.ability.attack;

import com.orinate.game.content.combat.CombatStyle;
import com.orinate.game.content.combat.ability.Ability;
import com.orinate.game.content.combat.ability.AbilityRepository;
import com.orinate.game.content.combat.ability.AbilityType;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.Entity;
import com.orinate.game.model.update.masks.HitType;
import com.orinate.game.model.visual.Animation;
import com.orinate.game.model.visual.Animator.Priority;

/**
 * Handles the slice ability.
 * 
 * @author Emperor
 * 
 */
public final class Slice extends Ability {

	/**
	 * Constructs a new {@code Slice} {@code Object}.
	 */
	public Slice() {
		super(0, 5, AbilityType.BASIC, 17, 14677, CombatStyle.MELEE, null);
	}

	@Override
	public boolean fire(Entity user, Entity target) {
		double acc = getHitAccuracy(user, target, 0, Skills.ATTACK);
            if (acc > 0.1) {
                int max = (int) (getMaximumDamage(user, false, Skills.STRENGTH) * 1.25);
                target.getImpactHandler().impact((int) (acc * max), user, HitType.MELEE_DAMAGE);
            }
            user.getAnimator().animate(Animation.create(18148, Priority.HIGH)); //18556
            return true;
	}

	@Override
	public boolean register() {
		return AbilityRepository.register(1460, 1, this);
	}

	@Override
	public Ability newInstance() {
		return new Slice();
	}

}