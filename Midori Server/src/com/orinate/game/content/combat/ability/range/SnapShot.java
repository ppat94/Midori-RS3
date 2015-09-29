package com.orinate.game.content.combat.ability.range;

import com.orinate.cache.parsers.ItemDefinition;
import com.orinate.game.content.combat.CombatStyle;
import com.orinate.game.content.combat.ability.Ability;
import com.orinate.game.content.combat.ability.AbilityRepository;
import com.orinate.game.content.combat.ability.AbilityType;
import com.orinate.game.content.skills.SkillRequirement;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.Entity;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.update.masks.Graphics;
import com.orinate.game.model.update.masks.HitType;
import com.orinate.game.model.visual.Animation;
import com.orinate.game.model.visual.Animator.Priority;

/**
 * Handles the snap shot ability.
 * 
 * @author Emperor
 * 
 */
public final class SnapShot extends Ability {

	/**
	 * The amount of shots.
	 */
	private int count = 0;

	/**
	 * Constructs a new {@code SnapShot} {@code Object}.
	 */
	public SnapShot() {
		super(0, 33, AbilityType.THRESHOLD, 117, 14669, CombatStyle.RANGE, new SkillRequirement(Skills.RANGE, 2));
	}

	@Override
	public Ability newInstance() {
		return new SnapShot();
	}

	@Override
	public boolean register() {
		return AbilityRepository.register(1452, 7, this);
	}

	@Override
	public boolean fire(Entity user, Entity target) {
		if (user instanceof Player && ammo != null) {
			Player p = (Player) user;
			int id = ammo.getDefinitions().getProjectileId();
			p.getWriter().sendProjectile(target, p.getCenterLocation(), target.getCenterLocation(), id, 40, 36, 90, 40, 5, 11, 1);
			if (ammo.getAmount() > 1) {
				p.getWriter().sendProjectile(target, p.getCenterLocation(), target.getCenterLocation(), id, 42, 36, 80, 40, 10, 11, 1);
			}
		}
		//int shots = ammo == null ? 1 : ammo.getAmount();
        int first = (int) (getMaximumDamage(user, false, Skills.RANGE) * (1.0 + (RANDOM.nextDouble() * 1.20)));
        int second = (int) (getMaximumDamage(user, false, Skills.RANGE) * (1.0 + (RANDOM.nextDouble() * 2.10)));
			double acc = getHitAccuracy(user, target, 0, Skills.RANGE);
			if (acc > 0.1) {
				target.getImpactHandler().impact(first, user, HitType.RANGE_DAMAGE, 1);
                target.getImpactHandler().impact(second, user, HitType.RANGE_DAMAGE, 1);
			}
		return true;
	}

	@Override
	public boolean meetsRequirements(Entity e, Entity target) {
		if (!super.meetsRequirements(e, target)) {
			return false;
		}
		Item weapon = getWeapon(e, false);
		ammo = getRangeAmmunition(e, false);
		count = 0;
		e.getAnimator().setPriority(Priority.HIGH);
		if (weapon != null && weapon.getDefinitions().getWeaponType() == ItemDefinition.CROSSBOW_WEAPON_TYPE && ((Player) e).getEquipment().isDual()) {
			e.getAnimator().animate(Animation.create(18532, Priority.HIGH));
        } else if (weapon != null && weapon.getDefinitions().getWeaponType() == ItemDefinition.BOW_WEAPON_TYPE) {
            e.getAnimator().animate(Animation.create(18481, Priority.HIGH));
            e.getAnimator().animate(null, new Graphics(3511));
		} else {
			e.getAnimator().animate(Animation.create(18533, Priority.HIGH));
		}
		return true;
	}

}
