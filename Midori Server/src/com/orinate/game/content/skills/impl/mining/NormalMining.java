package com.orinate.game.content.skills.impl.mining;

import com.orinate.cache.parsers.ItemDefinition;
import com.orinate.game.World;
import com.orinate.game.content.skills.Skill;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.region.WorldObject;
import com.orinate.game.model.visual.Animation;
import com.orinate.util.Utilities;

/**
 * 
 * @author Trenton
 * 
 */
public class NormalMining extends Skill {

	private Pickaxe pickaxe;
	private MinableRock rockInfo;
	private WorldObject rockObject;
	private boolean started = false;

	public NormalMining(Player player, Object[] args) {
		super(player, args);
		this.rockObject = (WorldObject) args[0];
		this.rockInfo = (MinableRock) args[1];
		setPickaxe();
	}

	private boolean check() {
		if (!hasPickaxe()) {
			player.getWriter().sendGameMessage("You need a pickaxe to be able to mine.");
			return false;
		}
		if (!player.getInventory().hasFreeSlots() && rockInfo.getOreId() != -1) {
			player.getWriter().sendGameMessage("Not enough space in your inventory.");
			return false;
		}
		if (!canMine(rockInfo)) {
			player.getWriter().sendGameMessage("You need " + rockInfo.getLevel() + " Mining to mine this.");
			return false;
		}
		return true;
	}

	@Override
	public boolean onSkillStart() {
		if (!check())
			return false;
		player.getWriter().sendGameMessage("You swing your pickaxe at the rock.", true);
		return true;
	}

	@Override
	public boolean onProcess() {
		if (started) {
			addOre();
		} else {
			started = true;
		}
		if (!check())
			return false;
		player.getAnimator().animate(Animation.create(pickaxe.getEmote()));
		return checkIfRockExists();
	}

	@Override
	public boolean canProcess() {
		return true;
	}

	@Override
	public void onSkillEnd() {
		player.getAnimator().animate(Animation.create(-1));
	}

	@Override
	public int getSkillDelay() {
		return generateDelay();
	}

	private int generateDelay() {
		int summoningInvisibleBoost = 0;
		int mineTimer = rockInfo.getOreBaseTime() - (player.getSkills().getLevel(Skills.MINING) + summoningInvisibleBoost) - Utilities.getRandom(pickaxe.getQuality());
		if (mineTimer < 1 + rockInfo.getOreRandomTime())
			mineTimer = 1 + Utilities.getRandom(rockInfo.getOreRandomTime());
		return mineTimer;
	}

	private boolean canMine(MinableRock rock) {
		if (player.getSkills().getLevel(Skills.MINING) < rock.getLevel())
			return false;
		return true;
	}

	private boolean setPickaxe() {
		int miningLevel = player.getSkills().getLevel(Skills.MINING);
		for (Pickaxe pick : Pickaxe.values()) {
			if (player.getEquipment().getWeaponId() == pick.getItem() && miningLevel >= pick.getLevel()) {
				this.pickaxe = pick;
				return true;
			}
			if (player.getInventory().containsOneItem(pick.getItem()) && miningLevel >= pick.getLevel()) {
				this.pickaxe = pick;
				return true;
			}
		}
		return false;
	}

	private void addOre() {
		double xp = rockInfo.getXp();
		double boost = getMiningSuitBoost();
		player.getSkills().addXp(Skills.MINING, xp * boost);
		if (rockInfo.getOreId() != -1) {
			// TODO juju mining boost once herblore habitat is added.
			// just adds a smithed bar to your inv and gives smithing xp for the
			// bar
			player.getInventory().addItem(rockInfo.getOreId(), 1);
			player.getWriter().sendGameMessage("You mine some " + ItemDefinition.forId(rockInfo.getOreId()).getName().toLowerCase() + ".", true);
		}
		player.setAnimation(Animation.create(-1));
		World.getWorld().spawnTimedObject(new WorldObject(rockInfo.getMinedOutObjectId(), rockObject.getType(), rockObject.getRotation(), rockObject.getX(), rockObject.getY(), rockObject.getPlane()), rockInfo.getRespawnDelay() * 600, false);
	}

	private boolean hasPickaxe() {
		return setPickaxe();
	}

	private boolean checkIfRockExists() {
		return World.getWorld().getRegion(rockObject.getLocation().getRegionId()).containsObject(rockObject.getId(), rockObject.getLocation());
	}

	private double getMiningSuitBoost() {
		double boost = 1.0;
		if (player.getEquipment().getHeadId() == 20789)
			boost += 0.01;
		if (player.getEquipment().getBodyId() == 20791)
			boost += 0.01;
		if (player.getEquipment().getLegsId() == 20790)
			boost += 0.01;
		if (player.getEquipment().getFeetId() == 20788)
			boost += 0.01;
		if (player.getEquipment().getHandsId() == 20787)
			boost += 0.01;
		if (boost == 1.05)
			boost += 0.01;
		return boost;
	}

	public int getEmoteId() {
		return pickaxe.getEmote();
	}

	public int getPickaxeTime() {
		return pickaxe.getQuality();
	}

	public static void handle(Player player, WorldObject object, String objectName) {
		switch (objectName) {
		case "copper ore rocks":
			player.getSkillManager().train(new NormalMining(player, new Object[] { object, MinableRock.COPPER }));
			return;
		case "tin ore rocks":
			player.getSkillManager().train(new NormalMining(player, new Object[] { object, MinableRock.TIN }));
			return;
		case "clay rocks":
			player.getSkillManager().train(new NormalMining(player, new Object[] { object, MinableRock.CLAY }));
			return;
		case "granite rocks":
			player.getSkillManager().train(new NormalMining(player, new Object[] { object, MinableRock.GRANITE }));
			return;
		case "sandstone rocks":
			player.getSkillManager().train(new NormalMining(player, new Object[] { object, MinableRock.SANDSTONE }));
			return;
		case "iron ore rocks":
			player.getSkillManager().train(new NormalMining(player, new Object[] { object, MinableRock.IRON }));
			return;
		case "coal rocks":
			player.getSkillManager().train(new NormalMining(player, new Object[] { object, MinableRock.COAL }));
			return;
		case "gold ore rocks":
			player.getSkillManager().train(new NormalMining(player, new Object[] { object, MinableRock.GOLD }));
			return;
		case "silver ore rocks":
			player.getSkillManager().train(new NormalMining(player, new Object[] { object, MinableRock.SILVER }));
			return;
		case "mithril ore rocks":
			player.getSkillManager().train(new NormalMining(player, new Object[] { object, MinableRock.MITHRIL }));
			return;
		case "adamant ore rocks":
			player.getSkillManager().train(new NormalMining(player, new Object[] { object, MinableRock.ADAMANT }));
			return;
		case "runite ore rocks":
			player.getSkillManager().train(new NormalMining(player, new Object[] { object, MinableRock.RUNITE }));
			return;
		}
	}

	public Pickaxe getPickaxe() {
		return pickaxe;
	}

	public MinableRock getRock() {
		return rockInfo;
	}

	public WorldObject getRockObject() {
		return rockObject;
	}
}
