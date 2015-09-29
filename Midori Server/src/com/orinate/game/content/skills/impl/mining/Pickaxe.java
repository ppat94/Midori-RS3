package com.orinate.game.content.skills.impl.mining;

/**
 * 
 * @author Trenton
 * 
 */
public enum Pickaxe {

	INFERNAL_ADZE(13661, 61, 10222, 13), DRAGON(15259, 61, 12190, 13), RUNITE(1275, 41, 624, 10), ADAMANT(1271, 31, 1271, 7), MITHRIL(1273, 21, 629, 5), STEEL(1269, 6, 627, 3), IRON(1267, 1, 626, 2), BRONZE(1265, 1, 625, 1);

	private Pickaxe(int item, int level, int emote, int quality) {
		this.item = item;
		this.level = level;
		this.emote = emote;
		this.quality = quality;
	}

	private int item;
	private int level;
	private int emote;
	private int quality;

	public int getItem() {
		return item;
	}

	public int getLevel() {
		return level;
	}

	public int getEmote() {
		return emote;
	}

	public int getQuality() {
		return quality;
	}
}
