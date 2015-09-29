package com.orinate.game.content.combat.ability.attack;

import com.orinate.game.content.combat.CombatStyle;
import com.orinate.game.content.combat.ability.Ability;
import com.orinate.game.content.combat.ability.AbilityRepository;
import com.orinate.game.content.combat.ability.AbilityType;
import com.orinate.game.content.skills.SkillRequirement;
import com.orinate.game.model.update.masks.Graphics;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.Entity;
import com.orinate.game.model.update.masks.HitType;

/**
 * Handles the slaughter ability.
 * 
 * @author Emperor
 * 
 */
public final class Slaughter extends Ability {

	/**
	 * Constructs a new {@code Slaughter} {@code Object}.
	 */
	public Slaughter() {
		super(0, 50, AbilityType.THRESHOLD, 113, 14683, CombatStyle.MELEE, new SkillRequirement(Skills.ATTACK, 2));
	}

	@Override
	public Ability newInstance() {
		return new Slaughter();
	}

	@Override
	public boolean register() {
		return AbilityRepository.register(1460, 7, this);
	}

	@Override
	public boolean fire(Entity user, Entity target) {
		user.getAnimator().animate(getAnimation(user, 21267), new Graphics(3581));
		double mod = 1 + (RANDOM.nextDouble() * 1.501);
		int hit = (int) (getMaximumDamage(user, false, Skills.STRENGTH) * mod) / 5;
		if (getHitAccuracy(user, target, 0, Skills.ATTACK) > 0.1) {
            target.getImpactHandler().impact(hit, user, HitType.MELEE_DAMAGE);
			bleed(user, target, hit, 5, 3.0);
		}
		return true;
	}
}