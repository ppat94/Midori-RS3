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

/**
 * Handles the Havoc ability.
 * 
 * @author Emperor
 * 
 */
public final class Havoc extends Ability {

	/**
	 * Constructs a new {@code Havoc} {@code Object}.
	 */
	public Havoc() {
		super(0, 17, AbilityType.BASIC, 65, 14680, CombatStyle.MELEE, new SkillRequirement(Skills.ATTACK, 7));
	}

	@Override
	public Ability newInstance() {
		return new Havoc();
	}

	@Override
	public boolean register() {
		return AbilityRepository.register(1460, 4, this);
	}

	@Override
	public boolean fire(Entity user, Entity target) {
		user.getAnimator().animate(getAnimation(user, 21264), new Graphics(3579)); //Animation.create(18142, Priority.HIGH)
		int max = getMaximumDamage(user, false, Skills.STRENGTH) + getMaximumDamage(user, true, Skills.STRENGTH);
		if (getHitAccuracy(user, target, 0, Skills.ATTACK) > 0.1) {
			target.getImpactHandler().impact(max, user, HitType.MELEE_DAMAGE);
		}
		return true;
	}

	@Override
	public boolean meetsRequirements(Entity e, Entity target) {
		if (e instanceof Player && !((Player) e).getEquipment().isDual()) {
			((Player) e).sendMessage("This ability can only be used while dual wielding.");
			return false;
		}
		return super.meetsRequirements(e, target);
	}

}