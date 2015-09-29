package com.orinate.game.content.skills.impl.firemaking;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

import com.orinate.game.content.skills.Skill;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.update.masks.Graphics;
import com.orinate.game.model.visual.Animation;

/**
 * @author Taylor
 */
public class Bonfire extends Skill {

	public static enum Log {

		LOG(1511, 3098, 1, 50, 6), OAK(1521, 3099, 15, 75, 12), WILLOW(1519, 3101, 30, 112.5, 18), MAPLE(1517, 3100, 45, 157, 36), EUCALYPTUS(12581, 3112, 58, 241, 48), YEWS(1515, 3111, 60, 252, 54), MAGIC(1513, 3135, 75, 378, 60), BLISTERWOOD(21600, 3113, 76, 378, 60), CURSED_MAGIC(13567, 3116, 82, 378, 60);

		private final int logId;
		private final int graphicId;
		private final int levelReq;
		private final int boostTime;
		private final double exp;

		private Log(int logId, int gfxId, int level, double exp, int boostTime) {
			this.logId = logId;
			this.graphicId = gfxId;
			this.levelReq = level;
			this.exp = exp;
			this.boostTime = boostTime;
		}

		public int getLogId() {
			return logId;
		}

		/**
		 * @return the graphicId.
		 */
		public int getGrapgicId() {
			return graphicId;
		}

		/**
		 * @return the level
		 */
		public int getLevelReq() {
			return levelReq;
		}

		/**
		 * @return the boostTime
		 */
		public int getBoostTime() {
			return boostTime;
		}

		/**
		 * @return the xp
		 */
		public double getExp() {
			return exp;
		}

		public static Log forId(int id) {
			for (Log log : Log.values()) {
				if (log.getLogId() == id) {
					return log;
				}
			}
			return null;
		}
	}

	// private final WorldObject fire;
	private Queue<Log> logs;
	private Log log;
	private int count;

	public Bonfire(Player player, Object... args) {
		super(player, args);
		// fire = (WorldObject) args[0];
	}

	@Override
	public boolean onSkillStart() {
		Log[] possibleLogs = new Log[28];
		for (int i = 0; i < 28; i++) {
			possibleLogs[i] = Log.MAGIC;
		}
		if (possibleLogs.length == 0) {
			player.getWriter().sendGameMessage("You do not have any logs to add to this fire.");
			return false;
		}
		this.logs = new ArrayDeque<Log>();
		this.logs.addAll(Arrays.asList(possibleLogs));
		if (player.getSkills().getLevel(Skills.FIREMAKING) < logs.peek().getLevelReq()) {
			player.getWriter().sendGameMessage("You need level " + logs.peek().getLevelReq() + " to add logs to this bonfire.");
			return false;
		}
		player.setCustomRenderAnim(2498);
		return true;
	}

	@Override
	public boolean canProcess() {
		if (logs.isEmpty()) {
			return false;
		}
		// if (!player.getInventory().containsOneItem(logs.peek().getLogId())) {
		// return false;
		// }
		// if (World.getWorld().getObject(fire.getLocation()) == null) {
		// return false;
		// }
		return true;
	}

	@Override
	public boolean onProcess() {
		log = logs.poll();
		player.getInventory().deleteItem(log.getLogId(), 1);
		player.setAnimation(Animation.create(16703));
		player.setGraphics(new Graphics(log.getGrapgicId()));
		if (count++ == 4 && player.getLastBonfire() == 0) {
			player.setLastBonfire(log.getBoostTime() * 100);
			int percentage = (int) (getBoost() * 100 - 100);
			player.getWriter().sendGameMessage("<col=00ff00>The bonfire's warmth increases your maximum health by " + percentage + "%. This will last " + log.boostTime + " minutes.");
			player.getEquipment().updateBonuses();
		}
		player.getSkills().addXp(Skills.FIREMAKING, log.getExp());
		return true;
	}

	@Override
	public void onSkillEnd() {
		player.setAnimation(Animation.create(16702));
		player.setCustomRenderAnim(-1);
	}

	@Override
	public int getSkillDelay() {
		return 6;
	}

	private double getBoost() {
		int level = player.getSkills().getLevel(Skills.FIREMAKING);
		if (level >= 90)
			return 1.1;
		if (level >= 80)
			return 1.09;
		if (level >= 70)
			return 1.08;
		if (level >= 60)
			return 1.07;
		if (level >= 50)
			return 1.06;
		if (level >= 40)
			return 1.05;
		if (level >= 30)
			return 1.04;
		if (level >= 20)
			return 1.03;
		if (level >= 10)
			return 1.02;
		return 1.01;

	}
}
