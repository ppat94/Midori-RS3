package com.orinate.game.core.tick;

/**
 * @author Taylor Moon
 * @since Jan 23, 2014
 */
public abstract class Tick {

	private int duration;

	private int delay;

	public Tick(int delay) {
		this.delay = delay;
		this.duration = delay;
	}

	/**
	 * Called when the game cycle has been executed.
	 * 
	 * @return The state of the tick once a cycle is completed.
	 */
	public abstract TickState onTick();

	/**
	 * @return the delay
	 */
	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	/**
	 * Decreases the delay.
	 * 
	 * @return The tick delay.
	 */
	public int decreaseDelay() {
		return delay--;
	}

	/**
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * @param duration
	 *            the duration to set
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}
}
