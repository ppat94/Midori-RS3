package com.orinate.game.content.combat.ability;

import java.util.Random;

import com.orinate.cache.parsers.GeneralMapParser;
import com.orinate.cache.parsers.ItemDefinition;
import com.orinate.game.content.combat.AbilityEvent;
import com.orinate.game.content.combat.CombatStyle;
import com.orinate.game.content.skills.SkillRequirement;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.core.tick.Tick;
import com.orinate.game.core.tick.TickManager;
import com.orinate.game.core.tick.TickState;
import com.orinate.game.model.Entity;
import com.orinate.game.model.Location;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.npc.NPC;
import com.orinate.game.model.player.Equipment;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.update.masks.HitType;
import com.orinate.game.model.visual.Animation;
import com.orinate.game.model.visual.Animator.Priority;

/**
 * Represents an ability.
 * 
 * @author Emperor
 * 
 */
public abstract class Ability {

	/**
	 * The staff weapon type.
	 */
	public static final int STAFF = 7;

	/**
	 * The bow weapon type.
	 */
	public static final int BOW = 8;

	/**
	 * The crossbow weapon type.
	 */
	public static final int CROSSBOW = 9;

	/**
	 * The thrown weapon type.
	 */
	public static final int THROWN = 10;

	/**
	 * The client ability id.
	 */
	private final int clientId;

	/**
	 * The script id.
	 */
	private final int scriptId;

	/**
	 * The random instance.
	 */
	protected static final Random RANDOM = new Random();

	/**
	 * The fire delay.
	 */
	protected int fireDelay;

	/**
	 * The recharge ticks.
	 */
	protected int rechargeTicks;

	/**
	 * The tick count of when this ability can be used again.
	 */
	protected int nextUsage;

	/**
	 * The combat style.
	 */
	protected CombatStyle style;

	/**
	 * The ability t.
	 */
	protected AbilityType type;

	/**
	 * The ability event.
	 */
	protected AbilityEvent event;

	/**
	 * The ammunition item (for ranged combat).
	 */
	protected Item ammo;

	/**
	 * The skill requirement for this ability.
	 */
	protected final SkillRequirement requirement;

	/**
	 * Constructs a new {@code Ability} {@code Object}.
	 * 
	 * @param fireDelay
	 *            The fire delay.
	 * @param type
	 *            The ability type.
	 * @param adrenaline
	 *            The adrenaline needed.
	 */
	public Ability(int fireDelay, int rechargeTicks, AbilityType type, int clientId, int scriptId, CombatStyle style, SkillRequirement requirement) {
		this.fireDelay = fireDelay;
		this.rechargeTicks = rechargeTicks;
		this.type = type;
		this.clientId = clientId;
		this.scriptId = scriptId;
		this.style = style;
		this.requirement = requirement;
	}

	/**
	 * Creates a new instance.
	 * 
	 * @return The ability.
	 */
	public abstract Ability newInstance();

	/**
	 * Registers the ability.
	 * 
	 * @return {@code True} if successful.
	 */
	public abstract boolean register();

	/**
	 * Checks if the entity meets the requirements to use this ability.
	 * 
	 * @param e
	 *            The entity.
	 * @param target
	 *            The target to cast this on.
	 * @return {@code True} if so.
	 */
	public boolean meetsRequirements(Entity e, Entity target) {
		if (requirement != null && !requirement.meetsRequirement(e)) {
			if (e instanceof Player) {
				((Player) e).sendMessage("You need a " + Skills.SKILL_NAME[requirement.getSkillId()] + " level of " + requirement.getLevel() + " to use this ability.");
			}
			return false;
		}
		Item weapon = getWeapon(e, false);
		if (weapon.getDefinitions().getCombatStyle() != style) {
			if (e instanceof Player) {
				((Player) e).sendMessage("You need a " + style.name().toLowerCase() + " weapon to use this ability.");
			}
			System.out.println("Style: " + weapon.getDefinitions().getCombatStyle());
			return false;
		}
		return inRange(e, target, style.getDistance());
	}

	/**
	 * Gets the animation for the given script id.
	 * @param scriptId The animation.
	 * @return The animation.
	 */
	public Animation getAnimation(Entity e, int scriptId) {
		GeneralMapParser map = GeneralMapParser.getMap(scriptId);
		//Script=21271 {3619=18180 3596=18177, 3597=18177, 3598=18176, 3592=18178, 3593=18178, 3595=18179}
		int animId = -1;
		if (e instanceof NPC) {
			animId = (Integer) map.getValues().get((long) 3592);
		} else {
			Item weapon = getWeapon(e, false);
			if (weapon == null) {
				animId = (Integer) map.getValues().get((long) 3592);
			}
			else {
				String name = weapon.getName();
				if (Equipment.isTwoHandedWeapon(weapon)) {
					if (name.contains("sword")) {
						animId = (Integer) map.getValues().get((long) 3619);
					}
					else if (name.contains("maul")) {
						animId = (Integer) map.getValues().get((long) 3596);
					}
					else if (name.contains("halberd") || name.contains("spear")) {
						animId = (Integer) map.getValues().get((long) 3597);
					}
				}
				else {
					animId = (Integer) map.getValues().get((long) 3595);
				}
			}
		}
		return Animation.create(animId, Priority.HIGH);
	}

	/**
	 * Starts attacking again when needed.
	 */
	public void startAttack(Entity user, Entity target) {
		if (target != null && user.getCombatSchedule().getVictim() != target) {
			user.getCombatSchedule().attack(target);
		}
	}

	/**
	 * Starts a bleed effect.
	 * 
	 * @param attacker
	 *            The attacking entity.
	 * @param victim
	 *            The victim.
	 * @param damage
	 *            The amount of damage.
	 * @param cycles
	 *            The amount of cycles.
	 * @param moveModifier
	 *            The amount to multiply the damage with if the target moves.
	 */
	public static void bleed(final Entity attacker, final Entity victim, final int damage, final int cycles, final double moveModifier) {
		final Location position = victim.getLocation();
		TickManager.register(new Tick(2) {
			int cycle = 0;

			@Override
			public TickState onTick() {
				int hit = damage;
				if (!victim.getLocation().equals(position)) {
					hit *= moveModifier;
				}
				victim.getImpactHandler().impact(hit, attacker, HitType.MELEE_DAMAGE);
				if (++cycle == cycles) {
					return TickState.DESTROYED;
				}
				return TickState.ALIVE;
			}
		});
	}

	/**
	 * Gets the weapon used.
	 * 
	 * @param e
	 *            The entity.
	 * @param offhand
	 *            If we should get the offhand weapon.
	 * @return The weapon.
	 */
	public static Item getWeapon(Entity e, boolean offhand) {
		if (e instanceof Player) {
			return ((Player) e).getEquipment().get(offhand ? 5 : 3);
		}
		return null;
	}

	/**
	 * Checks if the entity has a weapon of the given weapon type.
	 * @param e The entity.
	 * @param weaponType The weapon type.
	 * @return {@code True} if so.
	 */
	public static boolean hasWeapon(Entity e, boolean offhand, int...weaponType) {
		if (e instanceof NPC) {
			return true;
		}
		Item weapon = getWeapon(e, offhand);
		if (weapon == null) {
			return false;
		}
		int current = weapon.getDefinitions().getWeaponType();
		for (int type : weaponType) {
			if (type == current) {
				return true;
			}
		}
		return weaponType.length < 1;
	}

	/**
	 * Gets the range ammunition used.
	 * 
	 * @param e
	 *            The entity.
	 * @param offhand
	 *            If we should get ammo for the offhand weapon.
	 * @return The ammunition.
	 */
	public static Item getRangeAmmunition(Entity e, boolean offhand) {
		Item weapon = getWeapon(e, offhand);
		if (weapon != null && !weapon.getDefinitions().isChargeBow()) {
			int amount = weapon.getDefinitions().isDarkBow() ? 2 : 1;
			if (weapon.getDefinitions().getWeaponType() != ItemDefinition.THROWN_WEAPON_TYPE) {
				weapon = ((Player) e).getEquipment().get(Equipment.SLOT_ARROWS);
				if (weapon == null) {
					return null;
				}
				if (amount > weapon.getAmount()) {
					amount = weapon.getAmount();
				}
			}
			return new Item(weapon.getId(), amount);
		}
		return weapon;
	}

	/**
	 * Gets the hit accuracy.
	 * 
	 * @param user
	 *            The attacking entity.
	 * @param target
	 *            The target entity.
	 * @param handIndex
	 *            The hand index (0=main-hand, 1=off-hand).
	 * @return The hit accuracy rate (0.0 - 1.0).
	 */
	public double getHitAccuracy(Entity user, Entity target, int handIndex, int skillId) {
		double accuracy = RANDOM.nextInt(1 + getMaximumAccuracy(user, handIndex == 1, skillId));
		double defence = 1 + RANDOM.nextInt(1 + getMaximumDefence(target, skillId));
		return accuracy / (accuracy + defence);
	}

	/**
	 * Gets the maximum accuracy bonus.
	 * 
	 * @param e
	 *            The entity.
	 * @param offhand
	 *            If we should get off-hand bonus.
	 * @param skillId
	 *            The skill id.
	 * @return The maximum accuracy.
	 */
	public int getMaximumAccuracy(Entity e, boolean offhand, int skillId) {
		CombatStyle style = getStyle(skillId);
		double accuracy = e.getProperties().getAccuracyBonus(offhand, style);
		int level = e.getSkills().getLevelForXp(skillId);
		accuracy += (level * 12.25) * (1 + (e.getSkills().getLevel(skillId) - level) * 0.08);
		// TODO: Prayer boost
		if (e instanceof Player) {
			accuracy *= ((Player) e).getEquipment().getAccuracyRatio(style);
		}
		return (int) accuracy;
	}

	/**
	 * Gets the maximum defence bonus.
	 * 
	 * @param e
	 *            The entity.
	 * @param skillId
	 *            The skill id.
	 * @return The maximum defence.
	 */
	public int getMaximumDefence(Entity e, int skillId) {
		double defence = 1 + e.getProperties().getBonuses().getArmour();
		int level = e.getSkills().getLevelForXp(Skills.DEFENCE);
		defence += (level * 12.25) * (1 + (e.getSkills().getLevel(Skills.DEFENCE) - level) * 0.08);
		// TODO: Prayer boost
		if (e instanceof Player) {
			defence *= ((Player) e).getEquipment().getDefenceRatio(getStyle(skillId));
		}
		return (int) defence << 1;
	}

	/**
	 * Gets the damage bonus.
	 * 
	 * @param e
	 *            The entity.
	 * @param offhand
	 *            If we should get off-hand bonus.
	 * @param skillId
	 *            The skill id.
	 * @return The maximum damage.
	 */
	public int getMaximumDamage(Entity e, boolean offhand, int skillId) {
		double damage = e.getProperties().getDamageBonus(offhand, getStyle(skillId));
		int level = e.getSkills().getLevelForXp(skillId);
		damage += level * (1 + (e.getSkills().getLevel(skillId) - level) * 0.08);
		if (skillId == Skills.RANGE && e instanceof Player) {
			if (ammo != null && ammo.getDefinitions().equipSlotId == Equipment.SLOT_ARROWS) {
				damage += ammo.getDefinitions().getDamageBonus(CombatStyle.RANGE);
			}
		}
		// TODO: Prayer boost
		return (int) damage;
	}

	/**
	 * Gets the combat style for the given skill id.
	 * 
	 * @param skillId
	 *            The skill id.
	 * @return The combat style.
	 */
	private CombatStyle getStyle(int skillId) {
		if (skillId > Skills.RANGE) {
			return CombatStyle.MAGIC;
		} else if (skillId == Skills.RANGE) {
			return CombatStyle.RANGE;
		}
		return CombatStyle.MELEE;
	}

	/**
	 * Handles the defending.
	 * 
	 * @param e
	 *            The defending entity.
	 */
	public void defend(Entity e) {
		e.getAnimator().animate(e.getCombatAnimation(2));
	}

	/**
	 * Fires the ability effect.
	 * 
	 * @param user
	 *            The entity using this ability.
	 * @param target
	 *            The target to cast this on.
	 * @return {@code True} if the ability has finished.
	 */
	public abstract boolean fire(Entity user, Entity target);

	/**
	 * Checks if this ability overrides the ability (parameter).
	 * 
	 * @param ability
	 *            The ability to override.
	 * @return {@code True} if this ability overrides the other.
	 */
	public boolean override(Ability ability) {
		return true;
	}

	/**
	 * Called when the ability gets destroyed.
	 */
	public void destroy() {
		/*
		 * empty.
		 */
	}

	/**
	 * Checks if the attacking entity is in range of the victim.
	 * 
	 * @param attacker
	 *            The attacking entity.
	 * @param victim
	 *            The victim.
	 * @param distance
	 *            The distance.
	 * @return {@code True} if so.
	 */
	public boolean inRange(Entity attacker, Entity victim, int distance) {
		int offset = victim.getSize() >> 1;
		Location center = victim.getLocation().transform(offset, offset, 0);
		if (!attacker.getLocation().withinDistance(center, offset + distance)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the delay before firing the ability effect.
	 * 
	 * @return The delay, in game ticks.
	 */
	public int getFireDelay() {
		return fireDelay;
	}

	/**
	 * Gets the recharge duration.
	 * 
	 * @return The duration, in game ticks.
	 */
	public int getRechargeDuration() {
		return rechargeTicks;
	}

	/**
	 * Gets the ability type.
	 * 
	 * @return The ability type.
	 */
	public AbilityType getType() {
		return type;
	}

	/**
	 * @return the event
	 */
	public AbilityEvent getEvent() {
		return event;
	}

	/**
	 * @param event
	 *            the event to set
	 */
	public void setEvent(AbilityEvent event) {
		this.event = event;
	}

	/**
	 * @return the clientId
	 */
	public int getClientId() {
		return clientId;
	}

	/**
	 * @return the nextUsage
	 */
	public int getNextUsage() {
		return nextUsage;
	}

	/**
	 * @param nextUsage
	 *            the nextUsage to set
	 */
	public void setNextUsage(int nextUsage) {
		this.nextUsage = nextUsage;
	}

	/**
	 * Gets the combat style of this ability.
	 * 
	 * @return The style.
	 */
	public CombatStyle getStyle() {
		return style;
	}

	/**
	 * @return the scriptId
	 */
	public int getScriptId() {
		return scriptId;
	}

	/**
	 * @return the requirement
	 */
	public SkillRequirement getRequirement() {
		return requirement;
	}

}