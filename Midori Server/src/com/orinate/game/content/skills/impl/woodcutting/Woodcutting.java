package com.orinate.game.content.skills.impl.woodcutting;

import com.orinate.cache.parsers.ItemDefinition;
import com.orinate.game.World;
import com.orinate.game.content.skills.Skill;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.Location;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.region.WorldObject;
import com.orinate.game.model.visual.Animation;
import com.orinate.util.Utilities;

/**
 * Handles the woodcutting skill.
 * 
 * @author _Jordan / Apollo <citellumrsps@gmail.com>
 * @author Tom Feb 2, 2014, 2014, 7:37:10 PM
 * 
 */
public class Woodcutting extends Skill {

	/**
	 * The {@code WorldObject} instance.
	 */
	private final WorldObject tree;

	/**
	 * The {@code TreeDefinition} instance.
	 */
	private final TreeDefinition definition;

	/**
	 * The {@code HatchetDefinition} instance.
	 */
	private final HatchetDefinition hatchet;

	/**
	 * Constructs a new {@code Woodcutting} {@code Object}.
	 * 
	 * @param player
	 *            the player.
	 * @param tree
	 *            the tree clicked.
	 * @param definition
	 *            the definitions of the tree clicked.
	 */
	public Woodcutting(Player player, WorldObject tree, TreeDefinition definition) {
		super(player);
		this.tree = tree;
		this.definition = definition;
		this.hatchet = getHatchet();
	}

	@Override
	public boolean canProcess() {
		if (!checkTreeExists()) {
			return false;
		}
		if (!player.getInventory().hasFreeSlots()) {
			player.getWriter().sendGameMessage("Not enough free space in your inventory.");
			return false;
		}
		player.getAnimator().animate(Animation.create(hatchet.getAnimation()));
		return true;
	}

	@Override
	public boolean onSkillStart() {
		if (hatchet == null) {
			player.getWriter().sendGameMessage("You do not have a hatchet which you have the Woodcutting level to use.");
			return false;
		}
		if (player.getSkills().getLevel(Skills.WOODCUTTING) < definition.getLevel()) {
			player.getWriter().sendGameMessage("You do not have the required level to chop down this tree.");
			return false;
		}
		if (!player.getInventory().hasFreeSlots()) {
			player.getWriter().sendGameMessage("Not enough free space in your inventory.");
			return false;
		}
		if (!checkTreeExists()) {
			return false;
		}
		player.getWriter().sendGameMessage("You swing your hatchet at the tree...");
		return true;
	}

	@Override
	public boolean onProcess() {
		player.getSkills().addXp(Skills.WOODCUTTING, (definition.getExperience()));
		player.getInventory().addItem(definition.getLogId(), 1);
		player.getWriter().sendGameMessage("You get some " + ItemDefinition.forId(definition.getLogId()).name.toLowerCase() + ".");

		if (Utilities.random(definition.getLifeProbability()) == 0) {
			long time = definition.getRespawnDelay() * 600;
			World.getWorld().spawnTimedObject(new WorldObject(definition.getStumpId(), tree.getType(), tree.getRotation(), tree.getLocation().getX(), tree.getLocation().getY(), tree.getLocation().getPlane()), time);

			if (tree.getLocation().getPlane() < 3) {
				WorldObject object = getObject();
				if (object != null) {
					World.getWorld().removeTimedObject(object, time, false);
				}
			}

			player.getAnimator().animate(Animation.create(-1));
			return false;
		}
		return true;
	}

	@Override
	public int getSkillDelay() {
		int summoningBonus = 0;
		int timer = definition.getBaseTime() - (player.getSkills().getLevel(Skills.WOODCUTTING) + summoningBonus) - Utilities.random(hatchet.getBaseTime());
		if (timer < 1 + definition.getRandomTime()) {
			timer = 1 + Utilities.random(definition.getRandomTime());
		}
		return timer;
	}

	@Override
	public void onSkillEnd() {
		player.getSkillManager().setDelay(3);
	}

	/**
	 * Gets the hatchet the player is using.
	 * 
	 * @return the hatchet to use.
	 */
	private HatchetDefinition getHatchet() {
		HatchetDefinition bestHatchet = null;
		for (HatchetDefinition def : HatchetDefinition.values()) {
			if (player.getEquipment().getWeaponId() == def.getItemId() || player.getInventory().containsItem(def.getItemId(), 1)) {
				if (player.getSkills().getLevelForXp(Skills.WOODCUTTING) < def.getRequiredLevel()) {
					continue;
				}
				if (bestHatchet == null || def.getRequiredLevel() > bestHatchet.getRequiredLevel()) {
					bestHatchet = def;
				} else {
					continue;
				}
			}
		}
		return bestHatchet;
	}

	/**
	 * Gets the tree object.
	 * 
	 * @return the object.
	 */
	private WorldObject getObject() {
		WorldObject object = World.getWorld().getObject(new Location(tree.getX() - 1, tree.getY() - 1, tree.getPlane() + 1));
		if (object == null) {
			object = World.getWorld().getObject(new Location(tree.getX(), tree.getY() - 1, tree.getPlane() + 1));
			if (object == null) {
				object = World.getWorld().getObject(new Location(tree.getX() - 1, tree.getY(), tree.getPlane() + 1));
				if (object == null) {
					object = World.getWorld().getObject(new Location(tree.getX(), tree.getY(), tree.getPlane() + 1));
				}
			}
		}
		return object;
	}

	/**
	 * Checks if the tree selected exists.
	 * 
	 * @return the tree.
	 */
	private boolean checkTreeExists() {
		return World.getWorld().getRegion(tree.getLocation().getRegionId()).containsObject(tree.getId(), tree.getLocation());
	}

	/**
	 * Gets the tree based on the object.
	 * 
	 * @param object
	 *            the object id.
	 * @return nothing.
	 */
	public static TreeDefinition getTree(int object) {
		switch (object) {
		case 1276:
		case 1277:
		case 1278:
		case 1280:
		case 1330:
		case 1331:
		case 1332:
		case 2410:
		case 2411:
		case 3033:
		case 3034:
		case 3036:
		case 3881:
		case 3882:
		case 3883:
		case 5904:
		case 14308:
		case 14309:
		case 16265:
		case 37477:
		case 37478:
		case 37652:
		case 38782:
		case 38783:
		case 38784:
		case 38785:
		case 38786:
		case 38787:
		case 38788:
		case 38760:
		case 70060:
		case 70063:
			return TreeDefinition.NORMAL_TREE;
		case 38732:
		case 38731:
			return TreeDefinition.OAK_TREE;
		case 38616:
		case 58006:
			return TreeDefinition.WILLOW_TREE;
		case 1307:
		case 4674:
		case 46277:
		case 51843:
			return TreeDefinition.MAPLE_TREE;
		case 38755:
			return TreeDefinition.YEW_TREE;
		case 1306:
		case 37823:
		case 63176:
			return TreeDefinition.MAGIC_TREE;
		case 46320:
		case 46322:
		case 46324:
			return TreeDefinition.IVY_TREE;
		}
		return null;
	}
}
