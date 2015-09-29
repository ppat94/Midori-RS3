package com.orinate.game.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import com.orinate.game.World;
import com.orinate.game.content.combat.CombatSchedule;
import com.orinate.game.content.combat.CombatStyle;
import com.orinate.game.content.combat.ImpactHandler;
import com.orinate.game.content.skills.Skills;
import com.orinate.game.core.GameCore;
import com.orinate.game.model.player.Player;
import com.orinate.game.model.update.masks.Bar;
import com.orinate.game.model.update.masks.GlowColor;
import com.orinate.game.model.update.masks.Graphics;
import com.orinate.game.model.update.masks.Hit;
import com.orinate.game.model.visual.Animation;
import com.orinate.game.model.visual.Animator;
import com.orinate.game.model.visual.Animator.Priority;
import com.orinate.util.Utilities;
import com.orinate.util.misc.Attributes;

/**
 * @author Tom
 * 
 */
public abstract class Entity implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient int index;
	private transient Location nextLocation;
	private transient Location lastLoadedLocation;
	private transient boolean teleporting;
	private transient int lastRegionId;
	private transient ConcurrentLinkedQueue<Object[]> walkSteps;
	private transient int nextWalkDirection, nextRunDirection;
	private transient boolean firstStep;
	private transient Location lastLocation;
	private transient CopyOnWriteArrayList<Integer> mapRegionsIds;
	private transient CombatSchedule combatSchedule;
	private transient List<Hit> cachedHits;
	private transient List<Bar> cachedBars;
	private transient boolean finished;
	private transient GlowColor glowColor;
	private transient Animation animation;
	private transient Graphics graphics;
	private transient Graphics graphics1;
	private transient Graphics graphics2;
	private transient Graphics graphics3;
	private transient int faceEntity;
	private transient int lastFaceEntity;
	private transient int direction;
	private transient Location faceLocation;
	private transient Animator animator;
	private transient int size;
	private transient Properties properties;
	private transient ImpactHandler impactHandler;
	private transient Attributes attributes;
	private transient int lock;

	protected Location location;
	private int hitpoints;
	private int maxHitpoints;
	private boolean running;
	private Skills skills;

	public Entity() {
		this.location = new Location(3222, 3222, 0);
	}

	public void initEntity() {
		attributes = new Attributes();
		mapRegionsIds = new CopyOnWriteArrayList<Integer>();
		walkSteps = new ConcurrentLinkedQueue<Object[]>();
		combatSchedule = new CombatSchedule(this);
		impactHandler = new ImpactHandler(this);
		properties = new Properties();
		animator = new Animator(this);
		cachedBars = new ArrayList<Bar>();
		cachedHits = new ArrayList<Hit>();
		lastFaceEntity = -1;
		faceEntity = -2;
		combatSchedule.setDefaultAttacks();
	}

	public abstract void finish();

	public abstract void processDeath();

	public void process() {
		combatSchedule.tick();
		processMovement();
		attributes.checkMonitors();
	}

	/**
	 * Gets the combat animation for the given index.
	 * 
	 * @param index
	 *            The index (0 = mainhand attack, 1 = offhand attack, 2 =
	 *            defence, 3 = death).
	 * @return The combat animation.
	 */
	public Animation getCombatAnimation(int index) {
		return Animation.create(18226, Priority.MID);
	}

	/**
	 * Gets the current combat style.
	 * 
	 * @return The combat style.
	 */
	public CombatStyle getCombatStyle(boolean offhand) {
		return CombatStyle.MELEE;
	}

	private void processMovement() {
		lastLocation = location.copy();
		nextWalkDirection = nextRunDirection = -1;
		if (lastFaceEntity >= 0) {
			Entity target = lastFaceEntity >= 32768 ? World.getPlayers().get(lastFaceEntity - 32768) : World.getNpcs().get(lastFaceEntity);
			if (target != null) {
				direction = Utilities.getFaceDirection(target.getLocation().getCoordFaceX(target.getSize()) - getLocation().getX(), target.getLocation().getCoordFaceY(target.getSize()) - getLocation().getY());
			}
		}
		if (nextLocation != null) {
			setLocation(nextLocation);
			nextLocation = null;
			teleporting = true;
			World.updateEntityRegion(this);
			if (needMapUpdate()) {
				loadMapRegions();
			}
			resetWalkSteps();
			return;
		}
		teleporting = false;
		if (walkSteps.isEmpty()) {
			return;
		}
		for (int stepCount = 0; stepCount < (running ? 2 : 1); stepCount++) {
			Object[] nextStep = getNextWalkStep();
			if (nextStep == null) {
				break;
			}
			int dir = (int) nextStep[0];
			if (stepCount == 0) {
				nextWalkDirection = dir;
			} else {
				nextRunDirection = dir;
			}
			location.moveLocation(Utilities.DIRECTION_DELTA_X[dir], Utilities.DIRECTION_DELTA_Y[dir], 0);
		}
		if (needMapUpdate()) {
			loadMapRegions();
		}
	}

	private Object[] getNextWalkStep() {
		Object[] step = walkSteps.poll();
		if (step == null) {
			return null;
		}
		return step;
	}

	public boolean addWalkSteps(final int destX, final int destY, int maxStepsCount, boolean check) {
		int[] lastTile = getLastWalkTile();
		int myX = lastTile[0];
		int myY = lastTile[1];
		int stepCount = 0;
		while (true) {
			stepCount++;
			if (myX < destX)
				myX++;
			else if (myX > destX)
				myX--;
			if (myY < destY)
				myY++;
			else if (myY > destY)
				myY--;
			if (!addWalkStep(myX, myY, lastTile[0], lastTile[1], check))
				return false;
			if (stepCount == maxStepsCount)
				return true;
			lastTile[0] = myX;
			lastTile[1] = myY;
			if (lastTile[0] == destX && lastTile[1] == destY)
				return true;
		}
	}

	public void reset() {
		setHitpoints(getMaxHitpoints());
		resetWalkSteps();
	}

	private boolean addWalkStep(int nextX, int nextY, int lastX, int lastY, boolean check) {
		int dir = Utilities.getMoveDirection(nextX - lastX, nextY - lastY);
		if (dir == -1) {
			return false;
		}
		if (check && !World.getWorld().checkWalkStep(location.getPlane(), lastX, lastY, dir, size)) {
			return false;
		}
		walkSteps.add(new Object[] { dir, nextX, nextY, check });
		return true;
	}

	/**
	 * Gets the center location of this entity.
	 * 
	 * @return The center location.
	 */
	public Location getCenterLocation() {
		int s = getSize() >> 1;
		return location.transform(s, s, 0);
	}

	private int[] getLastWalkTile() {
		Object[] objects = walkSteps.toArray();
		if (objects.length == 0) {
			return new int[] { location.getX(), location.getY() };
		}
		Object step[] = (Object[]) objects[objects.length - 1];
		return new int[] { (int) step[1], (int) step[2] };
	}

	public void resetWalkSteps() {
		walkSteps.clear();
	}

	public boolean needMasksUpdate() {
		return (nextWalkDirection == -1 && faceLocation != null) || faceEntity != -2 || glowColor != null || graphics != null || animation != null || cachedBars.size() > 0 || cachedHits.size() > 0 || graphics1 != null || graphics2 != null || graphics3 != null;
	}

	public void appendHit(Hit... hits) {
		for (Hit hit : hits) {
			if (hit == null) {
				continue;
			}
			this.cachedHits.add(hit);
		}
	}

	public void appendBar(Bar... bars) {
		for (Bar bar : bars) {
			if (bar == null) {
				continue;
			}
			this.cachedBars.add(bar);
		}
	}

	public void resetUpdate() {
		setGlowColor(null);
		graphics = null;
		graphics1 = null;
		graphics2 = null;
		graphics3 = null;
		faceEntity = -2;
		if (nextWalkDirection == -1) {
			faceLocation = null;
		}
		setAnimation(null);
		this.cachedBars.clear();
		this.cachedHits.clear();
	}

	public void loadMapRegions() {
		mapRegionsIds.clear();
		int chunkX = location.getChunkX();
		int chunkY = location.getChunkY();
		int mapHash = Location.REGION_SIZES[0] >> 4;
		int minRegionX = (chunkX - mapHash) / 8;
		int minRegionY = (chunkY - mapHash) / 8;
		for (int xCalc = minRegionX < 0 ? 0 : minRegionX; xCalc <= ((chunkX + mapHash) / 8); xCalc++) {
			for (int yCalc = minRegionY < 0 ? 0 : minRegionY; yCalc <= ((chunkY + mapHash) / 8); yCalc++) {
				int regionId = yCalc + (xCalc << 8);
				World.getWorld().getRegion(regionId, (this instanceof Player));
				mapRegionsIds.add(regionId);
			}
		}
		lastLoadedLocation = location.copy();
	}

	public boolean needMapUpdate() {
		int lastMapRegionX = lastLoadedLocation.getChunkX();
		int lastMapRegionY = lastLoadedLocation.getChunkY();
		int regionX = location.getChunkX();
		int regionY = location.getChunkY();
		int size = ((Location.REGION_SIZES[0] >> 3) / 2) - 1;
		return Math.abs(lastMapRegionX - regionX) >= size || Math.abs(lastMapRegionY - regionY) >= size;
	}

	public boolean isDead() {
		return hitpoints <= 0;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Location getLastLocation() {
		return lastLocation;
	}

	public Location getNextLocation() {
		return nextLocation;
	}

	public void setNextLocation(Location nextLocation) {
		this.nextLocation = nextLocation;
	}

	public boolean isTeleporting() {
		return teleporting;
	}

	public int getNextWalkDirection() {
		return nextWalkDirection;
	}

	public int getNextRunDirection() {
		return nextRunDirection;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
		this.setFirstStep(true);
	}

	public boolean isFirstStep() {
		return firstStep;
	}

	public void setFirstStep(boolean firstStep) {
		this.firstStep = firstStep;
	}

	public GlowColor getGlowColor() {
		return glowColor;
	}

	public void setGlowColor(GlowColor glowColor) {
		this.glowColor = glowColor;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void heal(int amount) {
		this.hitpoints += amount;
		if (this instanceof Player) {
			((Player) this).getWriter().sendConfig(659, (getHitpoints() * 40) * 2);
		}
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public Graphics getGraphics() {
		return graphics;
	}

	public void setGraphics(Graphics graphics) {
		if (graphics == null) {
			this.graphics = null;
			return;
		}
		if (this.graphics3 != null) {
			return;
		}
		if (this.graphics2 != null) {
			this.graphics3 = graphics;
			return;
		}
		if (this.graphics1 != null) {
			this.graphics2 = graphics;
			return;
		}
		if (this.graphics != null) {
			this.graphics1 = graphics;
			return;
		}
		this.graphics = graphics;
	}

	public boolean hasWalkSteps() {
		return !walkSteps.isEmpty();
	}

	public CopyOnWriteArrayList<Integer> getMapRegionsIds() {
		return mapRegionsIds;
	}

	public int getLastRegionId() {
		return lastRegionId;
	}

	public void setLastRegionId(int lastRegionId) {
		this.lastRegionId = lastRegionId;
	}

	public Location getLastLoadedLocation() {
		return lastLoadedLocation;
	}

	public Graphics getGraphics1() {
		return graphics1;
	}

	public Graphics getGraphics2() {
		return graphics2;
	}

	public Graphics getGraphics3() {
		return graphics3;
	}

	public int getHitpoints() {
		return hitpoints;
	}

	public void setHitpoints(int hitpoints) {
		this.hitpoints = hitpoints;
	}

	public void removeHitpoints(int hitpoints) {
		this.hitpoints -= hitpoints;
	}

	public int getMaxHitpoints() {
		return maxHitpoints;
	}

	public List<Bar> getCachedBars() {
		return cachedBars;
	}

	public List<Hit> getCachedHits() {
		return cachedHits;
	}

	public Animator getAnimator() {
		return animator;
	}

	public CombatSchedule getCombatSchedule() {
		return combatSchedule;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public int getFaceEntity() {
		return faceEntity;
	}

	public void resetFaceEntity() {
		faceEntity = -1;
		lastFaceEntity = -1;
	}

	public void setFaceEntity(Entity entity) {
		if (entity == null) {
			faceEntity = -1;
			lastFaceEntity = -1;
		} else {
			faceEntity = entity.getClientIndex();
			lastFaceEntity = faceEntity;
		}
	}

	public int getClientIndex() {
		return index + (this instanceof Player ? 32768 : 0);
	}

	public int getLastFaceEntity() {
		return lastFaceEntity;
	}

	public void setLastFaceEntity(int lastFaceEntity) {
		this.lastFaceEntity = lastFaceEntity;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public Location getFaceLocation() {
		return faceLocation;
	}

	public void setFaceLocation(Location faceLocation) {
		if (faceLocation == null) {
			this.faceLocation = null;
			return;
		}
		if (faceLocation.getX() == getLocation().getX() && faceLocation.getY() == getLocation().getY()) {
			return;
		}
		this.faceLocation = faceLocation;
		if (nextLocation != null) {
			direction = Utilities.getFaceDirection(faceLocation.getX() - nextLocation.getX(), faceLocation.getY() - nextLocation.getY());
		} else {
			direction = Utilities.getFaceDirection(faceLocation.getX() - getLocation().getX(), faceLocation.getY() - getLocation().getY());
		}
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setMaxHitpoints(int maxHitpoints) {
		this.maxHitpoints = maxHitpoints;
	}

	public int getRenderAnim() {
		return 2699;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public ImpactHandler getImpactHandler() {
		return impactHandler;
	}

	public void setImpactHandler(ImpactHandler impactHandler) {
		this.impactHandler = impactHandler;
	}

	public Skills getSkills() {
		return skills;
	}

	public void setSkills(Skills skills) {
		this.skills = skills;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void lock(int count) {
		this.lock = GameCore.getTicks() + count;
	}

	public void lock() {
		lock(Integer.MAX_VALUE);
	}

	public void unlock() {
		this.lock = -1;
	}

	public boolean isLocked() {
		return lock >= GameCore.getTicks();
	}
}