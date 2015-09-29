package com.orinate.game.model;

import java.io.Serializable;

/**
 * @author Tom
 * 
 */
public class Location implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final int[] REGION_SIZES = new int[] { 104, 120, 136, 168 };

	private short x, y;
	private byte plane;

	public Location(int x, int y, int plane) {
		this.x = (short) x;
		this.y = (short) y;
		this.plane = (byte) plane;
	}

	public Location(Location tile) {
		this.x = tile.x;
		this.y = tile.y;
		this.plane = tile.plane;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Location)) {
			return false;
		}
		Location l = (Location) o;
		return l.getX() == x && l.getY() == y && l.getPlane() == plane;
	}

	public Location(int hash) {
		this.x = (short) (hash >> 14 & 0x3fff);
		this.y = (short) (hash & 0x3fff);
		this.plane = (byte) (hash >> 28);
	}

	public void moveLocation(int xOffset, int yOffset, int planeOffset) {
		x += xOffset;
		y += yOffset;
		plane += planeOffset;
	}

	public void set(Location tile) {
		setLocation(tile.x, tile.y, tile.plane);
	}

	public void setLocation(int x, int y, int plane) {
		this.x = (short) x;
		this.y = (short) y;
		this.plane = (byte) plane;
	}

	public int getX() {
		return x;
	}

	public int getXInRegion() {
		return x & 0x3F;
	}

	public int getYInRegion() {
		return y & 0x3F;
	}

	public int getY() {
		return y;
	}

	public int getPlane() {
		if (plane > 3)
			return 3;
		return plane;
	}

	public int getChunkX() {
		return (x >> 3);
	}

	public int getChunkY() {
		return (y >> 3);
	}

	public int getRegionX() {
		return (x >> 6);
	}

	public int getRegionY() {
		return (y >> 6);
	}

	public int getRegionId() {
		return ((getRegionX() << 8) + getRegionY());
	}

	public int getLocalX(Location tile, int mapSize) {
		return x - 8 * (tile.getChunkX() - (REGION_SIZES[mapSize] >> 4));
	}

	public int getLocalY(Location tile, int mapSize) {
		return y - 8 * (tile.getChunkY() - (REGION_SIZES[mapSize] >> 4));
	}

	public int getOtherLocalX() {
		return getOtherLocalX(this);
	}

	public int getOtherLocalX(Location tile) {
		return x - ((tile.getChunkX() - 6) << 3);
	}

	public int getOtherLocalY() {
		return getOtherLocalY(this);
	}

	public int getOtherLocalY(Location tile) {
		return y - ((tile.getChunkY() - 6) << 3);
	}

	public int getLocalX(Location tile) {
		return getLocalX(tile, 0);
	}

	public int getLocalY(Location tile) {
		return getLocalY(tile, 0);
	}

	public int getLocalX() {
		return getLocalX(this);
	}

	public int getLocalY() {
		return getLocalY(this);
	}

	public int getRegionHash() {
		return getRegionY() + (getRegionX() << 8) + (plane << 16);
	}

	public int getTileHash() {
		return y + (x << 14) + (plane << 28);
	}

	public boolean withinDistance(Location tile, int distance) {
		if (tile.plane != plane)
			return false;
		int deltaX = tile.x - x, deltaY = tile.y - y;
		return deltaX <= distance && deltaX >= -distance && deltaY <= distance && deltaY >= -distance;
	}

	public boolean withinDistance(Location tile) {
		if (tile.plane != plane)
			return false;
		return Math.abs(tile.x - x) <= 14 && Math.abs(tile.y - y) <= 14;
	}

	public int getCoordFaceX(int sizeX) {
		return getCoordFaceX(sizeX, -1, -1);
	}

	public static final int getCoordFaceX(int x, int sizeX, int sizeY, int rotation) {
		return x + ((rotation == 1 || rotation == 3 ? sizeY : sizeX) - 1) / 2;
	}

	public static final int getCoordFaceY(int y, int sizeX, int sizeY, int rotation) {
		return y + ((rotation == 1 || rotation == 3 ? sizeX : sizeY) - 1) / 2;
	}

	public int getCoordFaceX(int sizeX, int sizeY, int rotation) {
		return x + ((rotation == 1 || rotation == 3 ? sizeY : sizeX) - 1) / 2;
	}

	public int getCoordFaceY(int sizeY) {
		return getCoordFaceY(-1, sizeY, -1);
	}

	public int getCoordFaceY(int sizeX, int sizeY, int rotation) {
		return y + ((rotation == 1 || rotation == 3 ? sizeX : sizeY) - 1) / 2;
	}

	public Location transform(int x, int y, int plane) {
		return new Location(this.x + x, this.y + y, this.plane + plane);
	}

	public boolean matches(Location other) {
		return x == other.x && y == other.y && plane == other.plane;
	}

	public boolean differentMap(Location tile) {
		return distanceFormula(getChunkX(), getChunkY(), tile.getChunkX(), tile.getChunkY()) >= 4;
	}

	private final double distanceFormula(int x, int y, int x2, int y2) {
		return Math.sqrt(Math.pow(x2 - x, 2) + Math.pow(y2 - y, 2));
	}

	public int distance(Location tile) {
		return (int) distanceFormula(x, y, tile.x, tile.y);
	}

	public Location translate(int diffX, int diffY, int diffZ) {
		return new Location(x + diffX, y + diffY, plane + diffZ);
	}

	public boolean lazyMatches(Location tile) {
		return x == tile.x && y == tile.y;
	}

	public String toString() {
		return "X: " + x + ", Y: " + y + ", Z: " + plane;
	}

	public Location copy() {
		return new Location(x, y, plane);
	}

	public Location difference(int diffX, int diffY, int diffZ) {
		return new Location((x - diffX), (y - diffY), (plane - diffZ));
	}
}
