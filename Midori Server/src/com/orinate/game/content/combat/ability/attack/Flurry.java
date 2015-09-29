package com.orinate.game.content.combat.ability.attack;

import com.orinate.game.content.combat.CombatStyle;
import com.orinate.game.content.combat.ability.Ability;
import com.orinate.game.content.combat.ability.AbilityRepository;
import com.orinate.game.content.combat.ability.AbilityType;
import com.orinate.game.content.skills.SkillRequirement;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.core.tick.Tick;
import com.orinate.game.core.tick.TickManager;
import com.orinate.game.core.tick.TickState;
import com.orinate.game.model.Entity;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.update.masks.Graphics;
import com.orinate.game.model.update.masks.HitType;
import com.orinate.game.model.visual.Animation;
import com.orinate.game.model.visual.Animator;

/**
 * Handles the flurry ability.
 * 
 * @author Emperor
 * 
 */
public final class Flurry extends Ability {

	/**
	 * Constructs a new {@code Flurry} {@code Object}.
	 */
	public Flurry() {
		super(0, 33, AbilityType.THRESHOLD, 129, 14684, CombatStyle.MELEE, new SkillRequirement(Skills.ATTACK, 37));
	}

	@Override
	public Ability newInstance() {
		return new Flurry();
	}

	@Override
	public boolean register() {
		return AbilityRepository.register(1460, 8, this);
	}

	@Override
	public boolean fire(Entity user, Entity target) {
        //Animation anim = getAnimation(user, 21256);
        //user.getAnimator().animate(anim, new Graphics(anim.getDefinition().getGFXId()));
        user.getAnimator().animate(Animation.create(18150, Animator.Priority.HIGH));
        user.getAnimator().animate(null, new Graphics(3589));
		//double mod = 1 + (RANDOM.nextDouble() * 1.501);
        int hit = (int) (getMaximumDamage(user, false, Skills.STRENGTH) * (RANDOM.nextDouble() * 0.8));
        int test = (int) (RANDOM.nextDouble() * 0.8);
		//int hit = (int) (getMaximumDamage(user, false, Skills.STRENGTH) * mod) / 6;
		if (getHitAccuracy(user, target, 0, Skills.ATTACK) > 0.1) {
            target.getImpactHandler().impact(hit, user, HitType.MELEE_DAMAGE);
            bleed(user, target, test, 3, 0);
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