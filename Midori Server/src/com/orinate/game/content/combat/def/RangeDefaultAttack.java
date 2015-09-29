package com.orinate.game.content.combat.def;

import com.orinate.cache.parsers.ItemDefinition;
import com.orinate.game.World;
import com.orinate.game.content.combat.CombatStyle;
import com.orinate.game.content.combat.DefaultAttack;
import com.orinate.game.content.combat.ability.Ability;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.Entity;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.player.Equipment;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.update.masks.HitType;

/**
 * Handles the range default attacks.
 * 
 * @author Emperor
 * 
 */
public final class RangeDefaultAttack extends DefaultAttack {

	/**
	 * Constructs a new {@code RangeDefaultAttack} {@code Object}.
	 * 
	 * @param offhand
	 *            If the attack is for the off-hand.
	 */
	public RangeDefaultAttack(boolean offhand) {
		super(CombatStyle.RANGE, offhand);
	}

	@Override
	public boolean swing(Entity entity, Entity victim) {
		ammo = getRangeAmmunition(entity, offhand);
		int shots = useAmmunition(entity, victim, offhand, ammo);
		if (shots < 0) {
			entity.getCombatSchedule().end();
			return false;
		}
		visualize(entity, victim);
		if (shots > 1 && entity instanceof Player) {
			if (ammo != null) {
				((Player) entity).getWriter().sendProjectile(victim, entity.getCenterLocation(), victim.getCenterLocation(), ammo.getDefinitions().getProjectileId(), 42, 36, 68, 40, 10, 11, 1);
			}
		}
		for (int i = 0; i < shots; i++) {
			int hit = 0;
			double acc = getHitAccuracy(entity, victim, offhand ? 1 : 0, Skills.RANGE);
			if (acc > 0.1) {
				int max = getMaximumDamage(entity, offhand, Skills.RANGE);
				hit = (int) (acc * max);
			}
			victim.getImpactHandler().impact(hit, entity, HitType.RANGE_DAMAGE, 1);
		}
		return true;
	}

	/**
	 * Uses the ammunition.
	 * 
	 * @param entity
	 *            The entity.
	 * @param victim
	 *            The victim entity.
	 * @param offhand
	 *            If the attack is for the off-hand.
	 * @param ammo
	 *            The ammunition item.
	 * @return The amount of attacks to do.
	 */
	public static int useAmmunition(Entity entity, Entity victim, boolean offhand, Item ammo) {
		if (!(entity instanceof Player)) {
			return 1;
		}
		Player p = (Player) entity;
		Item weapon = getWeapon(entity, offhand);
		if (weapon == null || ammo == null) {
			return -1; // Shouldn't happen.
		}
		if (!weapon.getDefinitions().isChargeBow()) {
			Item item = p.getEquipment().get(ammo.getDefinitions().equipSlotId);
			if (item == null) {
				p.getWriter().sendGameMessage("You're out of ammunition!");
				return -1;
			}
			if (ammo.getDefinitions().getName().contains("bolt") != (weapon.getDefinitions().getWeaponType() == ItemDefinition.CROSSBOW_WEAPON_TYPE)) {
				p.getWriter().sendGameMessage("You can't use this kind of ammo with this bow.");
				return -1;
			}
			item = new Item(item.getId(), item.getAmount() - ammo.getAmount());
			if (item.getAmount() < 1) {
				item = null;
			}
			p.getEquipment().getItems().set(ammo.getDefinitions().equipSlotId, item);
			if (ammo.getDefinitions().equipSlotId != Equipment.SLOT_ARROWS) {
				p.getAppearance().refresh();
			}
			World.getWorld().addGroundItem(ammo, victim.getLocation(), p, 60, false);
			if (item == null) {
				p.getEquipment().refresh(ammo.getDefinitions().equipSlotId);
			} else {
				p.getWriter().sendUpdateItems(94, p.getEquipment().getItems(), ammo.getDefinitions().equipSlotId);
			}
		}
		return ammo.getAmount();
	}

	@Override
	public void visualize(Entity entity, Entity victim) {
		entity.getAnimator().animate(entity.getCombatAnimation(offhand ? 1 : 0));
		if (entity instanceof Player) {
			Player p = (Player) entity;
			if (ammo != null) {
				int id = ammo.getDefinitions().getProjectileId();
				p.getWriter().sendProjectile(victim, entity.getCenterLocation(), victim.getCenterLocation(), id, 40, 36, 75, isThrownWeapon(ammo) ? 28 : 40, 5, 11, 1);
			}
		} else {
			// TODO: Entity projectile settings.
		}
	}

	/**
	 * Checks if the item is a thrown weapon.
	 * 
	 * @param item
	 *            The item to check.
	 * @return {@code True} if so.
	 */
	public static boolean isThrownWeapon(Item item) {
		return item != null && item.getDefinitions().getWeaponType() == ItemDefinition.THROWN_WEAPON_TYPE;
	}

	@Override
	public Ability newInstance() {
		return new RangeDefaultAttack(offhand);
	}

	@Override
	public boolean register() {
		return false;
	}

	@Override
	public boolean meetsRequirements(Entity e, Entity target) {
		return true;
	}

}