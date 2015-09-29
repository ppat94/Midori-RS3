package com.orinate.game.model.visual;

import com.orinate.game.core.GameCore;
import com.orinate.game.model.Entity;
import com.orinate.game.model.update.masks.Graphics;

/**
 * Handles the animating of an Entity.
 * 
 * @author Emperor
 */
public final class Animator {

	/**
	 * The reset animation.
	 */
	public static final Animation RESET_A = Animation.create(-1);

	/**
	 * The reset graphics.
	 */
	public static final Graphics RESET_G = new Graphics(-1);

	/**
	 * The entity.
	 */
	private Entity entity;

	/**
	 * The current animation.
	 */
	private Animation animation;

	/**
	 * The current graphics.
	 */
	private Graphics graphics;

	/**
	 * Current priority.
	 */
	private Priority priority = Priority.LOW;

	/**
	 * The current animation delay.
	 */
	private int ticks;

	/**
	 * Constructs a new {@code Animator} {@Code Object}.
	 * 
	 * @param entity
	 *            The entity.
	 */
	public Animator(Entity entity) {
		this.entity = entity;
	}

	/**
	 * Represents the priorities.
	 * 
	 * @author Emperor
	 */
	public static enum Priority {

		/**
		 * Lowest priority.
		 */
		LOW,

		/**
		 * Medium priority (override low priority)
		 */
		MID,

		/**
		 * High priority (override all)
		 */
		HIGH,

		/**
		 * Extra priority only to be used when really needed. (overrides death
		 * animation etc).
		 */
		VERY_HIGH;
	}

	/**
	 * Starts an animation.
	 * 
	 * @param animation
	 *            The animation.
	 * @return {@code True} if succesful.
	 */
	public boolean animate(Animation animation) {
		return animate(animation, null);
	}

	/**
	 * Starts a graphic.
	 * 
	 * @param graphic
	 *            The graphic.
	 * @return {@code True} if succesful.
	 */
	public boolean graphics(Graphics graphic) {
		return animate(null, graphic);
	}

	/**
	 * Starts an animation (and if animation is null or has succesfully started
	 * > start graphic)
	 * 
	 * @param animation
	 *            The animation.
	 * @param graphic
	 *            The graphic.
	 * @return {@code True} if successfully started.
	 */
	public boolean animate(Animation animation, Graphics graphic) {
		if (animation != null) {
			if (ticks > GameCore.getTicks() && priority.ordinal() > animation.getPriority().ordinal()) {
				return false;
			}
			this.animation = animation;
			ticks = GameCore.getTicks() + 1;
			entity.setAnimation(animation);
			priority = animation.getPriority();
		}
		if (graphic != null) {
			this.graphics = graphic;
			entity.setGraphics(graphic);
		}
		return true;
	}

	/**
	 * @return the animation.
	 */
	public Animation getAnimation() {
		return animation;
	}

	/**
	 * @param animation
	 *            the animation to set.
	 */
	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	/**
	 * @return the graphics.
	 */
	public Graphics getGraphics() {
		return graphics;
	}

	/**
	 * @param graphics
	 *            the graphics to set.
	 */
	public void setGraphics(Graphics graphics) {
		this.graphics = graphics;
	}

	/**
	 * Gets the priority.
	 * 
	 * @return The priority.
	 */
	public Priority getPriority() {
		return priority;
	}

	/**
	 * Sets the priority.
	 * 
	 * @param priority
	 *            The priority to set.
	 */
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
}