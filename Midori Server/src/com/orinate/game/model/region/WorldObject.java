package com.orinate.game.model.region;

import com.orinate.game.model.Location;

/**
 * @author Tom
 * 
 */
public class WorldObject {

	private int id;
	private int type;
	private int rotation;
	private Location location;

	public WorldObject(int id, int type, int rotation, int x, int y, int plane) {
		this.id = id;
		this.type = type;
		this.rotation = rotation;
		this.location = new Location(x, y, plane);
	}

	public WorldObject(WorldObject object) {
		this(object.getId(), object.getType(), object.getRotation(), object.getLocation().getX(), object.getLocation().getY(), object.getLocation().getPlane());
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public int getRotation() {
		return rotation;
	}

	public Location getLocation() {
		return location;
	}

	public int getX() {
		return location.getX();
	}

	public int getY() {
		return location.getY();
	}

	public int getPlane() {
		return location.getPlane();
	}
}
