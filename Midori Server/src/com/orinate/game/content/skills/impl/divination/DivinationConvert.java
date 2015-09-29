package com.orinate.game.content.skills.impl.divination;

import com.orinate.game.content.skills.Skill;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.update.masks.Graphics;
import com.orinate.game.model.visual.Animation;

/**
 * 
 * @author Trenton
 * 
 */
public class DivinationConvert extends Skill {

	// TODO boon xp boosts and it's done

	public enum ConvertMode {
		CONVERT_TO_ENERGY, CONVERT_TO_XP, CONVERT_TO_MORE_XP;
	}

	private MemoryInfo info;
	private boolean enriched;
	private ConvertMode mode;

	public DivinationConvert(Player player, Object[] args) {
		super(player, args);
		setMode((ConvertMode) args[0]);
		checkAll(player);
	}

	public boolean checkAll(Player player) {
		for (Item item : player.getInventory().getItems().getItems()) {
			if (item == null)
				continue;
			for (MemoryInfo i : MemoryInfo.values()) {
				if (item.getId() == i.getMemoryId()) {
					info = i;
					enriched = false;
					return true;
				}
				if (item.getId() == i.getEnrichedMemoryId()) {
					info = i;
					enriched = true;
					return true;
				}
			}
		}
		player.getWriter().sendGameMessage("You don't have any memories to convert.");
		return false;
	}

	@Override
	public boolean onSkillStart() {
		if (!checkAll(player)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canProcess() {
		if (!checkAll(player)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean onProcess() {
		switch (mode) {
		case CONVERT_TO_ENERGY:
			player.getAnimator().animate(Animation.create(21232));
			player.setGraphics(new Graphics(4239));
			player.getSkills().addXp(Skills.DIVINATION, 1);
			player.getInventory().deleteItem(enriched ? info.getEnrichedMemoryId() : info.getMemoryId(), 1);
			player.getInventory().addItem(info.getEnergyId(), 3);
			break;
		case CONVERT_TO_XP:
			player.getAnimator().animate(Animation.create(21234));
			player.setGraphics(new Graphics(4240));
			player.getSkills().addXp(Skills.DIVINATION, enriched ? info.getXp() * 2 : info.getXp());
			player.getInventory().deleteItem(enriched ? info.getEnrichedMemoryId() : info.getMemoryId(), 1);
			break;
		case CONVERT_TO_MORE_XP:
			if (!player.getInventory().containsItem(info.getEnergyId(), 5)) {
				setMode(ConvertMode.CONVERT_TO_XP);
				player.getAnimator().animate(Animation.create(21234));
				player.setGraphics(new Graphics(4240));
				player.getSkills().addXp(Skills.DIVINATION, enriched ? info.getXp() * 2 : info.getXp());
				player.getInventory().deleteItem(enriched ? info.getEnrichedMemoryId() : info.getMemoryId(), 1);
				return true;
			}
			player.getAnimator().animate(Animation.create(21234));
			player.setGraphics(new Graphics(4240));
			player.getSkills().addXp(Skills.DIVINATION, enriched ? (info.getXp() * 2) + ((info.getXp() * 2) * 0.25) : (info.getXp()) + ((info.getXp()) * 0.25));
			if (player.getInventory().containsItem(info.getEnrichedMemoryId(), 1)) {
				player.getInventory().deleteItem(info.getEnrichedMemoryId(), 1);
			} else {
				player.getInventory().deleteItem(info.getMemoryId(), 1);
			}
			player.getInventory().deleteItem(info.getEnergyId(), 5);
			break;
		}
		return true;
	}

	@Override
	public void onSkillEnd() {

	}

	@Override
	public int getSkillDelay() {
		return 2;
	}

	public boolean isEnriched() {
		return enriched;
	}

	public void setEnriched(boolean enriched) {
		this.enriched = enriched;
	}

	public MemoryInfo getInfo() {
		return info;
	}

	public void setInfo(MemoryInfo info) {
		this.info = info;
	}

	public ConvertMode getMode() {
		return mode;
	}

	public void setMode(ConvertMode mode) {
		this.mode = mode;
	}

}
