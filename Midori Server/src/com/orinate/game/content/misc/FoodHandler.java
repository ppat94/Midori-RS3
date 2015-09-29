package com.orinate.game.content.misc;

import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.update.masks.Hit;
import com.orinate.game.model.update.masks.HitType;
import com.orinate.game.model.visual.Animation;
import com.orinate.util.TimeUtil;

/**
 * @author Tyler
 * 
 */
public class FoodHandler {

	private static final Animation EAT_ANIM = Animation.create(829);

	public static boolean eatFood(int itemId, int slotId, Player player) {
		Item item = new Item(itemId, 1);
		int hp = player.getSkills().getLevel(Skills.HITPOINTS);
		if (item.getDefinitions().getHealAmount(hp) < 0) {
			return false;
		}
		if (player.getFoodDelay() > TimeUtil.currentTimeMillis()) {
			return false;
		}
		int startingHp = player.getHitpoints();
		int amount = player.getHitpoints() + item.getDefinitions().getHealAmount(hp);
		player.getWriter().sendGameMessage("You eat the " + item.getDefinitions().name + ".");
		player.heal(amount);
		if (player.getHitpoints() > startingHp) {
			player.getWriter().sendGameMessage("It heals some health.");
			player.appendHit(new Hit(amount, HitType.HEALED_DAMAGE));
		}
		player.getAnimator().animate(EAT_ANIM);
		player.setFoodDelay((player.getFoodDelay() + 1800) + TimeUtil.currentTimeMillis());
		player.getInventory().deleteItem(slotId, item);
		return true;
	}
}
