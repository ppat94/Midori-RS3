package com.orinate.game.content.skills.impl.firemaking;

import com.orinate.game.World;
import com.orinate.game.content.skills.Skill;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.region.WorldObject;
import com.orinate.game.model.visual.Animation;

/**
 * @author Taylor
 */
public class Firemaking extends Skill {

	public static final int TINDER_BOX = 590;
	private static final Animation LIGHTING_ANIMATION = Animation.create(16700);

	public Firemaking(Player player, Object... args) {
		super(player, args);
	}

	@Override
	public boolean onSkillStart() {
		Fire fire = (Fire) args[0];
		if (!player.getInventory().containsOneItem(TINDER_BOX)) {
			player.getWriter().sendGameMessage("You need a tinderbox to light a fire.");
			return false;
		}
		if (player.getSkills().getLevel(Skills.FIREMAKING) < fire.getLevelReq()) {
			player.getWriter().sendGameMessage("You do not have the required level to light this.");
			return false;
		}
		if (!World.isPermitted(player.getLocation().getPlane(), player.getLocation().getX(), player.getLocation().getY())) {
			player.getWriter().sendGameMessage("You can't light a fire here.");
			return false;
		}
		if (World.getWorld().getRegion(player.getLocation().getRegionId()).containsObject(((Fire) args[0]).getFireId(), player.getLocation())) {
			player.getWriter().sendGameMessage("There is already a fire here.");
			return false;
		}
		player.getInventory().deleteItem(fire.getLogId(), 1);
		// World.getWorld().addGroundItem(new Item(fire.getLogId(), 1),
		// player.getLocation());
		if (player.getAttributes().get("quick_fire") == null) {
			player.setAnimation(LIGHTING_ANIMATION);
		}
		return true;
	}

	@Override
	public boolean canProcess() {
		if (!(args[0] instanceof Fire)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean onProcess() {
		player.getWriter().sendGameMessage("The fire catches and the logs begin to burn.");
		// GroundItem item =
		// World.getWorld().getRegion(player.getLocation().getRegionId()).getGroundItem(((Fire)
		// args[0]).getLogId(), player.getLocation(), player);
		// if (item == null) {
		// player.getWriter().sendGameMessage("Fuck you kid.");
		// return false;
		// }
		// Location location = item.getLocation();
		// World.getWorld().removeGroundItem(player, item);
		World.getWorld().spawnTimedObject(new WorldObject(((Fire) args[0]).getFireId(), 10, 1, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getPlane()), ((Fire) args[0]).getSpawnTime());
		if (!player.addWalkSteps(player.getLocation().getX() - 1, player.getLocation().getY(), 1, true)) {
			if (!player.addWalkSteps(player.getLocation().getX() + 1, player.getLocation().getY(), 1, true)) {
				if (!player.addWalkSteps(player.getLocation().getX(), player.getLocation().getY() + 1, 1, true)) {
					player.addWalkSteps(player.getLocation().getX(), player.getLocation().getY() - 1, 1, true);
				}
			}
		}
		player.setFaceLocation(player.getLastLocation());
		player.getSkills().addXp(Skills.FIREMAKING, ((Fire) args[0]).getExp());
		player.getAttributes().monitor("quick_fire", 1800);
		return false;
	}

	@Override
	public void onSkillEnd() {
	}

	@Override
	public int getSkillDelay() {
		return player.getAttributes().get("quick_fire") == null ? 1800 : 0;
	}
}
