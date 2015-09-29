package com.orinate.util.world;

/**
 * @author Sean
 */
public final class WorldEntry {

	/**
	 * The activity (What type of World it is)
	 */
	private String activity;
	/**
	 * The country
	 */
	private int country;
	/**
	 * The flag (What type of server it is)
	 */
	private int flag;
	/**
	 * The IP Address of the world
	 */
	private String ip;
	/**
	 * The location
	 */
	private int location;
	/**
	 * The region of the server
	 */
	private String region;
	/**
	 * The World id
	 */
	private int worldId;

	/**
	 * If this world is members
	 */
	private boolean members;

	/**
	 * The WorldList constructor.
	 * 
	 * @param worldId
	 *            The WorldId
	 * @param location
	 *            The location
	 * @param flag
	 *            The flag
	 * @param activity
	 *            The activity
	 * @param ip
	 *            The IP
	 * @param region
	 *            The region
	 * @param country
	 *            The country
	 */
	public WorldEntry(int worldId, int location, int flag, String activity, String ip, String region, int country, boolean members) {
		this.worldId = worldId;
		this.location = location;
		this.flag = flag;
		this.activity = activity;
		this.ip = ip;
		this.region = region;
		this.country = country;
		this.members = members;
	}

	/**
	 * Gets the activity
	 * 
	 * @return The activity
	 */
	public String getActivity() {
		return activity;
	}

	/**
	 * Gets the country
	 * 
	 * @return The country
	 */
	public int getCountry() {
		return country;
	}

	/**
	 * The flag
	 * 
	 * @return Gets the flag
	 */
	public int getFlag() {
		return flag;
	}

	/**
	 * Gets the IP Address
	 * 
	 * @return The Ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Gets the Location
	 * 
	 * @return The location
	 */
	public int getLocation() {
		return location;
	}

	/**
	 * Gets the region
	 * 
	 * @return The region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * Gets the world Id
	 * 
	 * @return The WorldId
	 */
	public int getWorldId() {
		return worldId;
	}

	/**
	 * @return the members
	 */
	public boolean isMembers() {
		return members;
	}
}
