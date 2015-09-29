package com.orinate.game.content.skills.impl.firemaking;

/**
 * @author Taylor
 */
public enum Fire {

	NORMAL(1511, 1, 300, 70755, 40, 20), ACHEY(2862, 1, 300, 70756, 40, 1), OAK(1521, 15, 450, 70757, 60, 1), WILLOW(1519, 30, 450, 70758, 90, 1), TEAK(6333, 35, 450, 70759, 105, 1), ARCTIC_PINE(10810, 42, 500, 70760, 125, 1), MAPLE(1517, 45, 500, 70761, 135, 1), MAHOGANY(6332, 50, 700, 70762, 157.5, 1), EUCALYPTUS(12581, 58, 700, 70763, 193.5, 1), YEW(1515, 60, 800, 70764, 202.5, 1), MAGIC(1513, 75, 900, 70765, 303.8, 1), CURSED_MAGIC(13567, 82, 1000, 70766, 303.8, 1);

	private final int logId;
	private final int levelReq;
	private final double exp;
	private final long spawnTime;
	private final int fireId;

	Fire(int logId, int levelReq, int spawnTime, int fireId, double exp, int time) {
		this.logId = logId;
		this.levelReq = levelReq;
		this.exp = exp;
		this.spawnTime = spawnTime;
		this.fireId = fireId;
	}

	/**
	 * @return the logId
	 */
	public int getLogId() {
		return logId;
	}

	/**
	 * @return the levelReq
	 */
	public int getLevelReq() {
		return levelReq;
	}

	/**
	 * @return the exp
	 */
	public double getExp() {
		return exp;
	}

	/**
	 * @return the fire
	 */
	public int getFireId() {
		return fireId;
	}

	/**
	 * @return the spawnTime
	 */
	public long getSpawnTime() {
		return (spawnTime * 100);
	}

	public static Fire forId(int itemId) {
		for (Fire fire : Fire.values()) {
			if (fire.getLogId() == itemId) {
				return fire;
			}
		}
		return null;
	}
}
