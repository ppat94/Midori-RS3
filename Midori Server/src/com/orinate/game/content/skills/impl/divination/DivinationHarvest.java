package com.orinate.game.content.skills.impl.divination;

import com.orinate.game.content.skills.Skill;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.npc.NPC;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.update.masks.Graphics;
import com.orinate.game.model.visual.Animation;
import com.orinate.util.Utilities;

/**
 * 
 * @author Trenton
 * 
 */
public class DivinationHarvest extends Skill {

	private Wisp wisp;
	private WispInfo info;

	public DivinationHarvest(Player player, Object[] args) {
		super(player, args);
		this.wisp = (Wisp) args[0];
		this.info = (WispInfo) args[1];
	}

	public static boolean checkAll(Player player, WispInfo info) {
		if (player.getSkills().getLevel(Skills.DIVINATION) < info.getLevel()) {
			player.getWriter().sendGameMessage("You need a Divination level of " + info.getLevel() + " to harvest from this spring.");
			return false;
		}
		if (player.getInventory().getFreeSlots() == 0) {
			player.getWriter().sendGameMessage("You don't have enough space in your inventory.");
			player.setFaceEntity(null);
			return false;
		}
		return true;
	}

	public NPC getWisp() {
		return wisp;
	}

	public WispInfo getInfo() {
		return info;
	}

	@Override
	public boolean onSkillStart() {
		if (!checkAll(player, info)) {
			return false;
		}
		player.getAnimator().animate(Animation.create(21231));
		player.setFaceEntity(wisp);
		return true;
	}

	@Override
	public boolean canProcess() {
		if (wisp == null || wisp.isUsedUp())
			return false;
		if (!checkAll(player, info)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean onProcess() {
		player.getInventory().addItem(info.getEnergyId(), player.getBoon(info.ordinal()) ? 2 : 1);
		if (Utilities.getRandom(30) >= 10) {
			if (info != WispInfo.PALE && (Utilities.getRandom(30) <= 5 + (player.getSkills().getLevel(Skills.DIVINATION) - info.getLevel()))) {
				player.getSkills().addXp(Skills.DIVINATION, info.getHarvestXp() * 2);
				player.setGraphics(new Graphics(4236));
				player.getInventory().addItem(info.getEnrichedMemoryId(), 1);
			} else {
				player.getSkills().addXp(Skills.DIVINATION, info.getHarvestXp());
				player.setGraphics(new Graphics(4235));
				player.getInventory().addItem(info.getMemoryId(), 1);
			}
		} else {
			player.getSkills().addXp(Skills.DIVINATION, info.getHarvestXp());
		}
		return true;
	}

	@Override
	public void onSkillEnd() {
		player.getAnimator().animate(Animation.create(21229));
	}

	@Override
	public int getSkillDelay() {
		return 3;
	}

}
