package com.orinate.game.model.region;

import com.orinate.game.model.Location;
import com.orinate.game.model.container.Item;
import com.orinate.game.model.player.Player;

/**
 * 
 * @author Trenton
 * 
 */
public class GroundItem extends Item {

	private static final long serialVersionUID = -565779043612803151L;

	private Location location;
	private boolean publiclyVisible;
	private Player dropOwner;

	public GroundItem(int id, int amount) {
		super(id, amount);
	}

	public GroundItem(Item item, Location location, Player owner, boolean publiclyVisible) {
		super(item.getId(), item.getAmount());
		this.location = location;
		this.dropOwner = owner;
		this.publiclyVisible = publiclyVisible;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public boolean isPubliclyVisible() {
		return publiclyVisible;
	}

	public void setPubliclyVisible(boolean publiclyVisible) {
		this.publiclyVisible = publiclyVisible;
	}

	public Player getDropOwner() {
		return dropOwner;
	}

	public void setDropOwner(Player dropOwner) {
		this.dropOwner = dropOwner;
	}

}
