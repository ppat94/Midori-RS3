package com.orinate.game.content.skills.impl.divination;

import com.orinate.game.model.Location;
import com.orinate.game.model.npc.NPC;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.visual.Animation;
import com.orinate.game.task.WorldTask;
import com.orinate.game.task.WorldTasksManager;
import com.orinate.util.Utilities;

/**
 * 
 * @author Trenton
 * 
 */
@SuppressWarnings("serial")
public class Wisp extends NPC {

	private int life;
	private WispInfo info;
	private boolean isSpring;
	private boolean usedUp;

	public Wisp(int id, Location tile) {
		super(id, tile);
		this.info = WispInfo.forNpcId(id);
		isSpring = false;
	}

	@Override
	public void spawn() {
		super.spawn();
		setUsedUp(false);
		setSpring(false);
	}

	@Override
	public void processDeath() {
		resetWalkSteps();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					getAnimator().animate(Animation.create(21203)); // 21210 =
																	// enriched
																	// death
				} else if (loop >= 1) {
					setUsedUp(true);
					transformToNPC(info.getNpcId());
					setRespawnTask();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	@Override
	public void process() {
		super.process();
		if (isSpring()) {
			if (life > 0)
				life--;
			if (life <= 0 && !isDead()) {
				setHitpoints(0);
				processDeath();
			}
		}
	}

	public void harvest(Player player) {
		if (!DivinationHarvest.checkAll(player, info))
			return;
		if (!isSpring()) {
			transformToNPC(info.getSpringNpcId());
			life = Utilities.random(18, 60);
			setLocked(true);
			setSpring(true);
		}
		player.getSkillManager().train(new DivinationHarvest(player, new Object[] { this, info }));
	}

	public boolean isSpring() {
		return this.isSpring;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public void setSpring(boolean isSpring) {
		this.isSpring = isSpring;
	}

	public boolean isUsedUp() {
		return usedUp;
	}

	public void setUsedUp(boolean usedUp) {
		this.usedUp = usedUp;
	}
}
