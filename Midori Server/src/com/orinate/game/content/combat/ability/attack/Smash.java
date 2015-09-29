package com.orinate.game.content.combat.ability.attack;

import com.orinate.game.content.combat.CombatStyle;
import com.orinate.game.content.combat.ability.Ability;
import com.orinate.game.content.combat.ability.AbilityRepository;
import com.orinate.game.content.combat.ability.AbilityType;
import com.orinate.game.content.skills.SkillRequirement;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.Entity;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.update.masks.Graphics;
import com.orinate.game.model.update.masks.HitType;
import com.orinate.game.model.visual.Animation;
import com.orinate.game.model.visual.Animator;

/**
 * Handles the Smash ability.
 * 
 * @author Emperor
 * 
 */
public final class Smash extends Ability {

	/**
	 * Constructs a new {@code Smash} {@code Object}.
	 */
	public Smash() {
		super(0, 17, AbilityType.BASIC, 81, 14681, CombatStyle.MELEE, new SkillRequirement(Skills.ATTACK, 25));
	}

	@Override
	public Ability newInstance() {
		return new Smash();
	}

	@Override
	public boolean register() {
		return AbilityRepository.register(1460, 5, this);
	}

	@Override
	public boolean fire(Entity user, Entity target) {
        user.getAnimator().animate(Animation.create(18159, Animator.Priority.HIGH));
		int max = (int) (getMaximumDamage(user, false, Skills.STRENGTH) * 1.25);
		if (getHitAccuracy(user, target, 0, Skills.ATTACK) > 0.1) {
			target.getImpactHandler().impact(max, user, HitType.MELEE_DAMAGE);
		}
		return true;
	}

	@Override
	public boolean meetsRequirements(Entity e, Entity target) {
		if (e instanceof Player && !((Player) e).getEquipment().is2h()) {
			((Player) e).sendMessage("This ability can only be used with two-handed weapons.");
			return false;
		}
		return super.meetsRequirements(e, target);
	}

}