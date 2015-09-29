package com.orinate.game.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.orinate.game.World;
import com.orinate.game.core.tick.TickManager;
import com.orinate.game.model.npc.NPC;
import com.orinate.game.model.player.Player;
import com.orinate.game.task.WorldTasksManager;
import com.orinate.util.TimeUtil;
import com.orinate.util.misc.SlowThreadFactory;

/**
 * @author Tom
 * 
 */
public class GameCore extends Thread {

	/**
	 * The amount of game ticks elapsed.
	 */
	private static int ticks;

	private static ScheduledExecutorService slowExecutor;
	public static long LAST_CYCLE_CTM;
	private static final TickManager TICK_MANAGER = new TickManager();

	public GameCore() {
		this.setPriority(MAX_PRIORITY);
		this.setName("GameCore");
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		slowExecutor = availableProcessors >= 6 ? Executors.newScheduledThreadPool(availableProcessors >= 12 ? 4 : 2, new SlowThreadFactory()) : Executors.newSingleThreadScheduledExecutor(new SlowThreadFactory());
		TICK_MANAGER.loadDefaults();
	}

	@Override
	public void run() {
		try {
			while (true) {
				long currentTime = TimeUtil.currentTimeMillis();
				TICK_MANAGER.processAllTicks();
				WorldTasksManager.processTasks();
				for (Player player : World.getPlayers()) {
					if (player == null || !player.isOnline() || player.isFinished()) {
						continue;
					}
					player.process();
				}
				for (NPC npc : World.getNpcs()) {
					if (npc == null) {
						continue;
					}
					npc.process();
				}
				for (Player player : World.getPlayers()) {
					if (player == null || !player.isOnline() || player.isFinished()) {
						continue;
					}
					player.getUpdate().send();
					player.getNpcUpdate().send();
				}
				for (Player player : World.getPlayers()) {
					if (player == null || !player.isOnline() || player.isFinished()) {
						continue;
					}
					player.resetUpdate();
				}
				for (NPC npc : World.getNpcs()) {
					if (npc == null) {
						continue;
					}
					npc.resetUpdate();
				}
				ticks++;
				LAST_CYCLE_CTM = TimeUtil.currentTimeMillis();
				long sleepTime = 600 + currentTime - LAST_CYCLE_CTM;
				if (sleepTime <= 0) {
					continue;
				}
				Thread.sleep(sleepTime);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static ScheduledExecutorService getSlowExecutor() {
		return slowExecutor;
	}

	public static int getTicks() {
		return ticks;
	}

	public static void setTicks(int ticks) {
		GameCore.ticks = ticks;
	}

	/**
	 * @return the tickManager
	 */
	public static TickManager getTickManager() {
		return TICK_MANAGER;
	}
}
