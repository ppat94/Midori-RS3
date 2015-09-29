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

/**
 * Handles the overpower ability.
 * @author Emperor
 *
 */
public class Overpower extends Ability {

	/**
	 * Constructs a new {@code Overpower} {@code Object}.
	 */
	public Overpower() {
		super(0, 50, AbilityType.ULTIMATE, 161, 14686, CombatStyle.MELEE, new SkillRequirement(Skills.ATTACK, 3));
	}
	
	@Override
	public boolean fire(Entity user, Entity target) {
		double acc = getHitAccuracy(user, target, 0, Skills.ATTACK);
		if (acc > 0.1) {
			int hit = (int) (getMaximumDamage(user, false, Skills.STRENGTH) * (2.0 + (RANDOM.nextDouble() * 2.0)));
			target.getImpactHandler().impact(hit, user, HitType.MELEE_DAMAGE, 2);
		}
		Animation anim = getAnimation(user, 21271);
		user.getAnimator().animate(anim, new Graphics(anim.getDefinition().getGFXId()));
		return true;
	}

	@Override
	public boolean register() {
		return AbilityRepository.register(1460, 10, this);
	}

	@Override
	public Ability newInstance() {
		return new Overpower();
	}
}