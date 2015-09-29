package com.orinate.game.model.update.masks;

import com.orinate.util.Utilities;

public class GlowColor {

	private int color;
	private int delay;
	private int duration;

	public GlowColor(int color, int duration) {
		this.color = color;
		this.delay = 0;
		this.duration = duration;
	}

	public GlowColor(int color, int delay, int duration) {
		this.color = color;
		this.delay = delay;
		this.duration = duration;
	}

	public static GlowColor generateRandom() {
		return new GlowColor(Utilities.random(254) + (Utilities.random(254) << 8) + (Utilities.random(254) << 16) + (Utilities.random(254) << 24), 100);
	}

	public int getColor() {
		return color;
	}

	public int getDuration() {
		return duration;
	}

	public int getDelay() {
		return delay;
	}
}
