package com.orinate.game.model.visual;

import com.orinate.cache.parsers.AnimationParser;
import com.orinate.game.model.visual.Animator.Priority;

/**
 * Represents an animation.
 * 
 * @author Emperor
 * 
 */
public final class Animation {

	/**
	 * The animation id.
	 */
	private final int id;

	/**
	 * The speed.
	 */
	private final int speed;

	/**
	 * The priority.
	 */
	private final Priority priority;

	/**
	 * The animation definition.
	 */
	private final AnimationParser definition;

	/**
	 * Constructs a new {@code Animation} {@code Object}.
	 * 
	 * @param id
	 *            The animation id.
	 * @param speed
	 *            The speed.
	 * @param priority
	 *            The priority.
	 */
	private Animation(int id, int speed, Priority priority) {
		this.id = id;
		this.speed = speed;
		this.priority = priority;
		this.definition = id > 0 ? AnimationParser.forId(id) : null;
	}

	/**
	 * Creates a new animation.
	 * 
	 * @param id
	 *            The animation id.
	 * @return The animation object.
	 */
	public static Animation create(int id) {
		return new Animation(id, 0, Priority.MID);
	}

	/**
	 * Creates a new animation.
	 * 
	 * @param id
	 *            The animation id.
	 * @param speed
	 *            The speed.
	 * @return The animation object.
	 */
	public static Animation create(int id, int speed) {
		return new Animation(id, speed, Priority.MID);
	}

	/**
	 * Creates a new animation.
	 * 
	 * @param id
	 *            The animation id.
	 * @param priority
	 *            The priority flag.
	 * @return The animation object.
	 */
	public static Animation create(int id, Priority priority) {
		return new Animation(id, 0, priority);
	}

	/**
	 * Creates a new animation.
	 * 
	 * @param id
	 *            The animation id.
	 * @param speed
	 *            The speed.
	 * @param priority
	 *            The priority flag.
	 * @return The animation object.
	 */
	public static Animation create(int id, int speed, Priority priority) {
		return new Animation(id, speed, priority);
	}

	/**
	 * Gets the duration of this animation.
	 * 
	 * @return The duration.
	 */
	public int getDuration() {
		if (definition != null) {
			return definition.getDuration();
		}
		return 0;
	}

	/**
	 * Gets the animation ids array.
	 * 
	 * @return The animation ids.
	 */
	public int[] getIds() {
		return new int[] { id, id, id, id };
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * @return the priority
	 */
	public Priority getPriority() {
		return priority;
	}

	/**
	 * @return the definition
	 */
	public AnimationParser getDefinition() {
		return definition;
	}

}