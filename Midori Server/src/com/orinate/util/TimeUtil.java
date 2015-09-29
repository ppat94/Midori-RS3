package com.orinate.util;

/**
 * @author Tom
 * 
 */
public class TimeUtil {

	private static long timeCorrection;
	private static long lastTimeUpdate;

	public static synchronized long currentTimeMillis() {
		long curTime = System.currentTimeMillis();
		if (curTime < lastTimeUpdate) {
			timeCorrection += lastTimeUpdate - curTime;
		}
		lastTimeUpdate = curTime;
		return curTime + timeCorrection;
	}
}
