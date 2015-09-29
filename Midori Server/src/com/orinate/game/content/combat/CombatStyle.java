package com.orinate.game.content.combat;

import com.orinate.game.content.combat.def.MeleeDefaultAttack;
import com.orinate.game.content.combat.def.RangeDefaultAttack;
import com.orinate.game.model.Entity;
import com.orinate.game.model.Location;

/**
 * Represents the combat styles.
 * 
 * @author Emperor
 * 
 */
public enum CombatStyle {

	/**
	 * The melee combat style.
	 */
	MELEE(new MeleeDefaultAttack(false), 1, 0, 1, 2, 3, 4, 5, 6, 7) {
		@Override
		public boolean canInteract(Entity e, Entity target, Location l) {
			return l != null && e.getLocation().equals(l);
		}
	},

	/**
	 * The magic combat style.
	 */
	MAGIC(new RangeDefaultAttack(false), 12) {
		@Override
		public boolean canInteract(Entity e, Entity target, Location l) {
			if (e.getLocation().equals(target.getLocation())) {
				return false;
			}
			// TODO: Check for projectile clipping.
			return e.getLocation().distance(target.getLocation()) < 12;
		}
	},

	/**
	 * The range combat style.
	 */
	RANGE(new RangeDefaultAttack(false), 10, 8, 9, 10) {
		@Override
		public boolean canInteract(Entity e, Entity target, Location l) {
			if (e.getLocation().equals(target.getLocation())) {
				return false;
			}
			// TODO: Check for projectile clipping.
			return e.getLocation().distance(target.getLocation()) < 12;
		}
	};

	/**
	 * The default attack of the combat style.
	 */
	private final DefaultAttack defaultAttack;
	
	/**
	 * The combat distance.
	 */
	private final int distance;

	/**
	 * The weapon types.
	 */
	private final int[] types;
	
	/**
	 * Constructs a new {@code CombatStyle} {@code Object}.
	 * 
	 * @param defaultAttack
	 *            The default attack of this style.
	 */
	private CombatStyle(DefaultAttack defaultAttack, int distance, int...types) {
		this.defaultAttack = defaultAttack;
		this.distance = distance;
		this.types = types;
	}

	/**
	 * Constructs a new default attack object for this combat style.
	 * 
	 * @param offhand
	 *            If the attack is for the off-hand.
	 * @return The default attack instance.
	 */
	public DefaultAttack newInstance(boolean offhand) {
		DefaultAttack attack = (DefaultAttack) defaultAttack.newInstance();
		attack.offhand = offhand;
		return attack;
	}

	/**
	 * Checks if the entity can interact with the target.
	 * 
	 * @param e
	 *            The attacking entity.
	 * @param target
	 *            The target.
	 * @param l
	 *            The interaction location.
	 * @return {@code True} if so.
	 */
	public boolean canInteract(Entity e, Entity target, Location l) {
		return false;
	}

	/**
	 * Gets the combat distance.
	 * @return The distance.
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * Gets the weapon types array.
	 * @return The weapon types.
	 */
	public int[] getTypes() {
		return types;
	}

}