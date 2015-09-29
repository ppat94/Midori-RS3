package com.orinate.game.content.combat.ability.attack;

import com.orinate.game.content.combat.AfterMath;
import com.orinate.game.content.combat.CombatStyle;
import com.orinate.game.content.combat.ability.Ability;
import com.orinate.game.content.combat.ability.AbilityRepository;
import com.orinate.game.content.combat.ability.AbilityType;
import com.orinate.game.content.skills.SkillRequirement;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.Entity;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.update.masks.HitType;
import com.orinate.game.model.visual.Animation;
import com.orinate.game.model.visual.Animator.Priority;

/**
 * Handles the backhand ability.
 * 
 * @author Emperor
 * 
 */
public final class Backhand extends Ability {

	/**
	 * Constructs a new {@code Backhand} {@code Object}.
	 */
	public Backhand() {
		super(0, 25, AbilityType.BASIC, 97, 14682, CombatStyle.MELEE, new SkillRequirement(Skills.ATTACK, 15, true));
	}

	@Override
	public Ability newInstance() {
		return new Backhand();
	}

	@Override
	public boolean register() {
		return AbilityRepository.register(1460, 6, this);
	}

	@Override
	public boolean fire(Entity user, Entity target) {
		user.getAnimator().animate(Animation.create(18154, Priority.HIGH));
		int max = (int) (getMaximumDamage(user, false, Skills.STRENGTH) * 1.88);
		if (getHitAccuracy(user, target, 0, Skills.ATTACK) > 0.1 && !AfterMath.stun(target)) {
			target.getImpactHandler().impact(max, user, HitType.MELEE_DAMAGE);
		}
		return true;
	}
	
	@Override
	public boolean meetsRequirements(Entity e, Entity target) {
		if (requirement != null && !requirement.meetsRequirement(e)) {
			if (e instanceof Player) {
				((Player) e).sendMessage("You need a " + Skills.SKILL_NAME[requirement.getSkillId()] + " level of " + requirement.getLevel() + " to use this ability.");
			}
			return false;
		}
		return inRange(e, target, style.getDistance());
	}

}