package com.orinate.game.core.tick;

/**
 * @author Taylor Moon
 * @since Jan 23, 2014
 */
public enum TickState {

	/**
	 * Represents a destroyed tick; a destroyed tick will no longer be alive and
	 * cannot be resumed or recovered.
	 */
	DESTROYED,

	/**
	 * Represents an alive tick; alive ticks will proceed to run on the engine's
	 * fixed schedule. An alive tick is virtualy ignored.
	 */
	ALIVE;

}