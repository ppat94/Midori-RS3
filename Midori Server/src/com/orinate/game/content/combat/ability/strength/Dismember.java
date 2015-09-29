package com.orinate.game.content.combat.ability.strength;

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
import com.orinate.network.packet.impl.ClickInterfaceListener;

/**
 * Handles the dismember ability.
 * 
 * @author Jack
 * 
 */
public final class Dismember extends Ability {

	/**
	 * Constructs a new {@code Dismember} {@code Object}.
	 */
	public Dismember() {
		super(0, 50, AbilityType.BASIC, 18, 14698, CombatStyle.MELEE, new SkillRequirement(Skills.STRENGTH, 14));
	}

	@Override
	public boolean fire(Entity user, Entity target) {
		double acc = getHitAccuracy(user, target, 0, Skills.ATTACK);
            if (acc > 0.1) {
                int max = (int) (getMaximumDamage(user, false, Skills.STRENGTH) * 1.25);
                target.getImpactHandler().impact(max, user, HitType.MELEE_DAMAGE);
            }
            user.getAnimator().animate(Animation.create(18590, Priority.HIGH));
            target.getAnimator().animate(null, new Graphics(3581));
            return true;
	}

	@Override
	public boolean register() {
		return AbilityRepository.register(1460, 1, this);
	}

	@Override
	public Ability newInstance() {
		return new Dismember();
	}

}