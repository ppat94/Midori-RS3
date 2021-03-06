package com.orinate.util.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A class the wraps a generic <code>HashMap</code> and provides different
 * methods to ease the process of getting and storing data.
 * 
 * @author Lazaro
 */
public class Attributes {

	private Map<Object, Object> attributes = new HashMap<Object, Object>();
	private Map<Object, Long> monitors = new HashMap<Object, Long>();

	/**
	 * Gets an attribute from the attribute map based upon the key specified.
	 * 
	 * @param key
	 *            The key to retrieve the value.
	 * @param <T>
	 *            The type of the value.
	 * @return The value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(Object key) {
		if (attributes.get(key) == null) {
			return (T) monitors.get(key);
		}
		return (T) attributes.get(key);
	}

	/**
	 * Gets a byte from the attribute map.
	 * 
	 * @param key
	 *            The key of the attribute to retrieve the value back later.
	 * @return The value.
	 */
	public byte getByte(Object key) {
		Number n = get(key);
		if (n == null) {
			return (byte) 0;
		}
		return n.byteValue();
	}

	/**
	 * Gets a double from the attribute map.
	 * 
	 * @param key
	 *            The key of the attribute to retrieve the value back later.
	 * @return The value.
	 */
	public double getDouble(Object key) {
		Number n = get(key);
		if (n == null) {
			return 0;
		}
		return n.doubleValue();
	}

	/**
	 * Gets a integer from the attribute map.
	 * 
	 * @param key
	 *            The key of the attribute to retrieve the value back later.
	 * @return The value.
	 */
	public int getInt(Object key) {
		Number n = get(key);
		if (n == null) {
			return 0;
		}
		return n.intValue();
	}

	/**
	 * Gets a long from the attribute map.
	 * 
	 * @param key
	 *            The key of the attribute to retrieve the value back later.
	 * @return The value.
	 */
	public long getLong(Object key) {
		Number n = get(key);
		if (n == null) {
			return 0;
		}
		return n.longValue();
	}

	/**
	 * Gets a short from the attribute map.
	 * 
	 * @param key
	 *            The key of the attribute to retrieve the value back later.
	 * @return The value.
	 */
	public short getShort(Object key) {
		Number n = get(key);
		if (n == null) {
			return (short) 0;
		}
		return n.shortValue();
	}

	/**
	 * Gets a boolean from the attribute map.
	 * 
	 * @param key
	 *            The key of the attribute to retrieve the value back later.
	 * @return The value.
	 */
	public boolean is(Object key) {
		Boolean b = get(key);
		if (b == null) {
			return false;
		}
		return b;
	}

	/**
	 * Sets an attribute.
	 * 
	 * @param key
	 *            The key of the attribute to retrieve the value back later.
	 * @param value
	 *            The value to set.
	 */
	public void set(Object key, Object value) {
		attributes.put(key, value);
	}

	/**
	 * If an attribute is set.
	 * 
	 * @param key
	 *            The attribute key.
	 */
	public boolean isSet(Object key) {
		return attributes.containsKey(key);
	}

	/**
	 * Un-sets an attribute.
	 * 
	 * @param key
	 *            The key of the attribute to un-set.
	 */
	public void remove(Object key) {
		attributes.remove(key);
	}

	public void monitor(Object key, long time) {
		monitors.put(key, System.currentTimeMillis() + time);
	}

	public void checkMonitors() {
		if (monitors.isEmpty()) {
			return;
		}
		for (Entry<Object, Long> monitor : monitors.entrySet()) {
			if (monitor.getValue() > System.currentTimeMillis()) {
				continue;
			}
			monitors.remove(monitor.getKey());
		}
	}
}
