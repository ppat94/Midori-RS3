package com.orinate.game.model.npc;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.orinate.cache.parsers.AnimationParser;
import com.orinate.cache.parsers.NPCDefinitions;
import com.orinate.game.World;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.core.GameCore;
import com.orinate.game.model.Entity;
import com.orinate.game.model.Location;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.npc.combat.CombatDefinitions;
import com.orinate.game.model.npc.combat.CombatDefinitionsLoader;
import com.orinate.game.model.npc.drops.Drop;
import com.orinate.game.model.npc.drops.NPCDrops;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.visual.Animation;
import com.orinate.game.model.visual.Animator;
import com.orinate.game.task.WorldTask;
import com.orinate.game.task.WorldTasksManager;
import com.orinate.util.Utilities;

public class NPC extends Entity {

	private static final long serialVersionUID = -864928186379501129L;

	private transient String modifiedName;

	private int id;
	private Location respawnLocation;
	private boolean forcedAgressiveness;
	private CombatDefinitions combatDefinitions;
	private int nextTransformation;
	private int overridedCombatLevel;
	private String overridedName;
	private boolean movementLocked;

	public NPC(int id, Location tile) {
		this.id = id;
		this.modifiedName = null;
		this.movementLocked = false;
		this.location = new Location(tile.getX(), tile.getY(), tile.getPlane());
		this.respawnLocation = new Location(tile.getX(), tile.getY(), tile.getPlane());
		this.setSize(NPCDefinitions.forId(id).size);
		this.setSkills(new Skills(this));
		initEntity();
		World.addNPC(this);
		World.updateEntityRegion(this);
		loadMapRegions();
		combatDefinitions = CombatDefinitionsLoader.getCombatDefinitions(id);
		if (combatDefinitions != null) {
			setMaxHitpoints(combatDefinitions.getMaximumHitpoints());
		} else {
			setMaxHitpoints(1);
		}
		this.setHitpoints(getMaxHitpoints());
		this.resetMasks();
	}

	@Override
	public void reset() {
		super.reset();
	}

	@Override
	public void initEntity() {
		super.initEntity();
	}

	@Override
	public void process() {
		super.process();
		this.processNPC();
	}

	public boolean canRandomWalk() {
		return this.id != 18196;
	}

	public void processNPC() {
		if (isDead() || movementLocked || !canRandomWalk()) {
			return;
		}
		if (!hasWalkSteps()) {
			boolean shouldMove = false;
			for (int index = 0; index < 2; index++) {
				if ((Math.random() * 1000.0) < 100.0) {
					shouldMove = true;
					break;
				}
			}

			if (!shouldMove) {
				return;
			}

			int moveX = (int) Math.round((Math.random() * 10.00) - 5.0);
			int moveY = (int) Math.round((Math.random() * 10.00) - 5.0);

			resetWalkSteps();
			addWalkSteps(respawnLocation.getX() + moveX, respawnLocation.getY() + moveY, 5, true);
		}
	}

	@Override
	public void resetUpdate() {
		super.resetUpdate();
		resetMasks();
	}

	@Override
	public void processDeath() {
		final CombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getAnimator().animate(Animator.RESET_A);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					getAnimator().animate(Animation.create(defs.getDeathAnimation()));
				} else if (loop >= AnimationParser.forId(defs.getDeathAnimation()).getDurationTicks()) {
					drop();
					reset();
					setLocation(respawnLocation);
					finish();
					setRespawnTask();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public void setRespawnTask() {
		if (!isFinished()) {
			reset();
			setLocation(respawnLocation);
			finish();
		}
		GameCore.getSlowExecutor().schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawn();
				} catch (Throwable e) {
				}
			}
		}, getCombatDefinitions().getRespawnDelay() * 600, TimeUnit.MILLISECONDS);
	}

	public void spawn() {
		setFinished(false);
		reset();
		World.addNPC(this);
		setLastRegionId(0);
		World.updateEntityRegion(this);
		loadMapRegions();
	}

	@Override
	public void finish() {
		if (isFinished()) {
			return;
		}
		setFinished(true);
		World.updateEntityRegion(this);
		World.removeNPC(this);
	}

	public Player getPlayerByDamageDealt() {
		return World.getPlayers().get(0);
	}

	public void drop() {
		Player killer = getPlayerByDamageDealt();
		if (killer == null) {
			return;
		}

		ArrayList<Drop> drops = NPCDrops.generateDrop(killer, this.getId());

		if (drops == null || drops.isEmpty()) {
			return;
		}

		for (Drop drop : drops) {
			addDrop(killer, drop);
		}
	}

	public void addDrop(Player player, Drop drop) {
		World.getWorld().addGroundItem(new Item(drop.getItemId(), Utilities.random(drop.getMinAmount(), drop.getMaxAmount())), player.getLocation(), player, 60, false);
	}

	@Override
	public void loadMapRegions() {
		super.loadMapRegions();
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || nextTransformation != -1;
	}

	public void resetMasks() {
		this.nextTransformation = -1;
		this.overridedName = null;
		this.overridedCombatLevel = -1;
	}

	@Override
	public String toString() {
		return "[id=" + getId() + ", name=" + NPCDefinitions.forId(id).getName() + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void transformToNPC(int id) {
		this.setId(id);
		this.combatDefinitions = CombatDefinitionsLoader.getCombatDefinitions(id);
		this.nextTransformation = id;
	}

	public Location getRespawnLocation() {
		return respawnLocation;
	}

	public void setRespawnLocation(Location respawnLocation) {
		this.respawnLocation = respawnLocation;
	}

	public boolean isForcedAgressiveness() {
		return forcedAgressiveness;
	}

	public void setForcedAgressiveness(boolean forcedAgressiveness) {
		this.forcedAgressiveness = forcedAgressiveness;
	}

	public String getModifiedName() {
		return modifiedName;
	}

	public void setModifiedName(String modifiedName) {
		this.modifiedName = modifiedName;
	}

	public CombatDefinitions getCombatDefinitions() {
		return combatDefinitions;
	}

	public int getNextTransformation() {
		return nextTransformation;
	}

	public int getOverridedCombatLevel() {
		return overridedCombatLevel;
	}

	public void setOverridedCombatLevel(int overridedCombatLevel) {
		this.overridedCombatLevel = overridedCombatLevel;
	}

	public String getOverridedName() {
		return overridedName;
	}

	public void setOverridedName(String overridedName) {
		this.overridedName = overridedName;
	}

	public void setLocked(boolean locked) {
		this.movementLocked = locked;
	}
}
