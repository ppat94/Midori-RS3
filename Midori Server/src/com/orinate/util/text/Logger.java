package com.orinate.util.text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.orinate.Constants;
import com.orinate.util.DateUtils;

/**
 * Logger used for the purpose of logging information
 * @author SonicForce41
 */
public class Logger {
	
	/**
	 * Send a log message to the Outputstream.
	 * Command Prompt or Eclipse Console.
	 * @param log
	 */
	public static void sendLog(String log) {
		System.out.println("["+Constants.SERVER_NAME+"] "+log);
	}
	
	/**
	 * Sends an exception to the  Eclipse Console or Command Prompt, if {@code Constants.DEBUG} is true.
	 * Logs the error into a file in the <b>Errors</b> folder
	 * @param e
	 */
	public static void sendError(Throwable e) {
		if (e.getMessage() != null && e.getMessage().contains("existing connection"))
			return;
		try {
			if (Constants.VERBOSE_MODE) {
				e.printStackTrace();
			}
			File file = new File("./data/errors/"+DateUtils.getDate()+".txt");
			if (!file.exists())
				file.createNewFile();
			e.printStackTrace(new PrintStream(new FileOutputStream("./data/errors/"+DateUtils.getDate()+".txt", true)));
		} catch (Throwable e1) {
			System.out.println("Error finding error printing file.");
		}
	}

}
