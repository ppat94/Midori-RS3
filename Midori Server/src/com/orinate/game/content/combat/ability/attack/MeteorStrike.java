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
 * Handles the meteor strike ability.
 * @author Emperor
 *
 */
public class MeteorStrike extends Ability {

	/**
	 * Constructs a new {@code MeteorStrike} {@code Object}.
	 */
	public MeteorStrike() {
		super(0, 100, AbilityType.ULTIMATE, 193, 14688, CombatStyle.MELEE, new SkillRequirement(Skills.ATTACK, 81));
	}
	
	@Override
	public boolean fire(Entity user, Entity target) {
		double acc = getHitAccuracy(user, target, 0, Skills.ATTACK);
            if ((acc > 0.1) && (target.getHitpoints() > (target.getMaxHitpoints() / 2))) {
                int hit = (int) (getMaximumDamage(user, false, Skills.STRENGTH) * (2.5 + (RANDOM.nextDouble() * 3.5)));
                target.getImpactHandler().impact(hit, user, HitType.MELEE_DAMAGE, 2);
            } else {
                ((Player) user).sendMessage("Target must have more than 50% health.");
                return true;
        }
        user.getAnimator().animate(Animation.create(18198, Animator.Priority.HIGH));
        user.getAnimator().animate(null, new Graphics(3583));
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

	@Override
	public boolean register() {
		return AbilityRepository.register(1460, 12, this);
	}

	@Override
	public Ability newInstance() {
		return new MeteorStrike();
	}
}