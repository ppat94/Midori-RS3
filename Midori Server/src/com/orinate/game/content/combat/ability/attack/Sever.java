package com.orinate.game.content.combat.ability.attack;

import com.orinate.game.content.combat.CombatStyle;
import com.orinate.game.content.combat.ability.Ability;
import com.orinate.game.content.combat.ability.AbilityRepository;
import com.orinate.game.content.combat.ability.AbilityType;
import com.orinate.game.content.skills.SkillRequirement;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.Entity;
import com.orinate.game.model.update.masks.Graphics;
import com.orinate.game.model.update.masks.HitType;
import com.orinate.game.model.visual.Animation;
import com.orinate.game.model.visual.Animator.Priority;

/**
 * Handles the sever ability.
 * 
 * @author Jack
 * 
 */
public final class Sever extends Ability {

	/**
	 * Constructs a new {@code Sever} {@code Object}.
	 */
	public Sever() {
		super(0, 50, AbilityType.BASIC, 49, 14679, CombatStyle.MELEE, new SkillRequirement(Skills.ATTACK, 45));
	}

	@Override
	public boolean fire(Entity user, Entity target) {
		double acc = getHitAccuracy(user, target, 0, Skills.ATTACK);
		if (acc > 0.1) {
			int max = (int) (getMaximumDamage(user, false, Skills.STRENGTH) * 1.88);
			target.getImpactHandler().impact(max, user, HitType.MELEE_DAMAGE);
		}
        user.getAnimator().animate(Animation.create(18144, Priority.HIGH));
		return true;
	}

	@Override
	public boolean register() {
		return AbilityRepository.register(1460, 3, this);
	}

	@Override
	public Ability newInstance() {
		return new Sever();
	}

}