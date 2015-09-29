package com.orinate.game.model.update.masks;

/**
 * Enumeration used for storing the hit types.
 * 
 * @author Tyler Telis <tyler@xlitersps.com>
 * @since Aug 9, 2013
 * @version 1.0
 */
public enum HitType {
    // 'A' stands for ability.
    // 29 == 100k range dmg, 30 == 150k range dmg, 31 == 200k range dmg and so on until 47 == 1m range dmg. These are used for party demon.
	MISSED(8), REGULAR_DAMAGE(3), MELEE_DAMAGE(48), RANGE_DAMAGE(49), MAGIC_DAMAGE(50), REFLECTED_DAMAGE(4), ABSORB_DAMAGE(5), POISON_DAMAGE(6), DESEASE_DAMAGE(7), HEALED_DAMAGE(9), CANNON_DAMAGE(13), INSTANT_KILL(54), DOUBLE_HIT(32767), HIDDEN_HIT(32766),
    RANGE_CRITICAL(53), MELEE_CRITICAL(51), MAGE_CRITICAL(52), A_MAGE_DAMAGE(16), A_MELEE_DAMAGE(14), A_RANGE_DAMAGE(15);
	private int hitMask;

	/**
	 * Constructs a new {@code HitType} instance.
	 * 
	 * @param hitMask
	 *            The hit mask id.
	 */
	private HitType(int hitMask) {
		this.hitMask = hitMask;
	}

	/**
	 * Gets the hit mask id.
	 * 
	 * @return The hit mask id.
	 */
	public int getCode() {
		return hitMask;
	}
}
