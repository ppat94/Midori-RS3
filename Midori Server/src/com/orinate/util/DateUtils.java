package com.orinate.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Utilities relating to {@link Date}
 * 
 * @author SonicForce41
 */
public class DateUtils {

	/**
	 * Gets the today's day as a String
	 * 
	 * @return
	 */
	public static String getDate() {
		String dateStamp = new SimpleDateFormat("MM-dd-yyyy").format(Calendar.getInstance().getTime());
		return dateStamp;
	}

}
