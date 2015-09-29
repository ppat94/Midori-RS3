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
 * Handles the massacre ability.
 * @author Emperor
 *
 */
public class Massacre extends Ability {

	/**
	 * Constructs a new {@code Massacre} {@code Object}.
	 */
	public Massacre() {
		super(0, 100, AbilityType.ULTIMATE, 177, 14687, CombatStyle.MELEE, new SkillRequirement(Skills.ATTACK, 66));
	}
	
	@Override
	public boolean fire(Entity user, Entity target) {
		double acc = getHitAccuracy(user, target, 0, Skills.ATTACK);
		if (acc > 0.1) {
            int max = (int) ((getMaximumDamage(user, false, Skills.STRENGTH) + getMaximumDamage(user, true, Skills.STRENGTH)) * 1.57);
            int one = (int) (getMaximumDamage(user, true, Skills.STRENGTH) * 1.57);
			target.getImpactHandler().impact(max, user, HitType.MELEE_DAMAGE, 1);
            bleed(user, target, one / 2, 4, 0);
		}
        user.getAnimator().animate(Animation.create(18201, Animator.Priority.HIGH));
        user.getAnimator().animate(null, new Graphics(3593));
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

	@Override
	public boolean register() {
		return AbilityRepository.register(1460, 11, this);
	}

	@Override
	public Ability newInstance() {
		return new Massacre();
	}
}