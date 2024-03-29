/**
 * 
 */
package com.lars_albrecht.general.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lalbrecht
 * 
 *         TODO register global exception handler
 * 
 * @see "http://stackoverflow.com/questions/1548487/java-global-exception-handler"
 * 
 */
public class Debug implements UncaughtExceptionHandler {

	final public static Integer											LEVEL_ALL	= 0;
	final public static Integer											LEVEL_TRACE	= 1;
	final public static Integer											LEVEL_DEBUG	= 2;
	final public static Integer											LEVEL_INFO	= 3;
	final public static Integer											LEVEL_WARN	= 4;
	final public static Integer											LEVEL_ERROR	= 5;
	final public static Integer											LEVEL_FATAL	= 6;
	final public static Integer											LEVEL_OFF	= 7;

	public static Integer												loglevel	= Debug.LEVEL_DEBUG;

	private static final ConcurrentHashMap<Integer, ArrayList<String>>	logList		= new ConcurrentHashMap<Integer, ArrayList<String>>();
	public static ConcurrentHashMap<String, Long>						timerMap	= new ConcurrentHashMap<String, Long>();

	public static String getFormattedTime(final String name) {
		final Long time = Debug.getTime(name);
		if (time != null) {
			TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
			final SimpleDateFormat sdfTime = new SimpleDateFormat();
			sdfTime.applyPattern("HH:mm:ss");
			return sdfTime.format(time);
		} else {
			return null;
		}
	}

	public static ArrayList<String> getFormattedTimes() {
		final ArrayList<String> timerList = new ArrayList<String>();
		for (final Map.Entry<String, Long> entry : Debug.timerMap.entrySet()) {
			if (entry.getKey().endsWith("_start")
					&& Debug.timerMap.containsKey(entry.getKey().substring(0, entry.getKey().indexOf("_start")) + "_end")) {
				timerList.add(Debug.getFormattedTime(entry.getKey().substring(0, entry.getKey().indexOf("_start"))) + " - "
						+ entry.getKey().substring(0, entry.getKey().indexOf("_start")));
			}
		}

		return timerList;
	}

	/**
	 * Returns a list of logs for the current level. All items smaller than the
	 * current level will be ignored.
	 * 
	 * @param level
	 * @return ArrayList<String>
	 */
	private static ArrayList<String> getListForLogLevel(final Integer level) {
		final ArrayList<String> resultList = new ArrayList<String>();
		for (int i = 7; i >= level; i--) {
			if (Debug.logList.get(i) != null) {
				resultList.addAll(Debug.logList.get(i));
			}
		}

		return resultList;
	}

	public static ArrayList<String> getLogForCurrentLevel() {
		return Debug.logList.get(Debug.loglevel);
	}

	public static ArrayList<String> getLogForLevel(final Integer level) {
		return Debug.logList.get(level);
	}

	public static Long getTime(final String name) {
		if (Debug.timerMap.containsKey(name + "_end") && Debug.timerMap.containsKey(name + "_start")) {
			return Debug.timerMap.get(name + "_end") - Debug.timerMap.get(name + "_start");
		}
		return null;
	}

	public static Debug getUncaughtExceptionHandler() {
		return new Debug();
	}

	public static Boolean inDebugLevel(final Integer level) {
		return level >= Debug.loglevel;
	}

	public static void log(final Integer level, final String msg) {
		ArrayList<String> tempList = null;
		if (Debug.logList.containsKey(level) && (Debug.logList.get(level) != null)) {
			tempList = Debug.logList.get(level);
		} else {
			tempList = new ArrayList<String>();
		}

		// @see
		// "http://www.java-examples.com/get-synchronized-list-java-arraylist-example"
		final List<String> list = Collections.synchronizedList(tempList);
		final String caller = (Thread.currentThread().getStackTrace().length >= 2 ? Thread.currentThread().getStackTrace()[2]
				.getMethodName() : "none");
		list.add(msg);
		Debug.logList.put(level, tempList);
		if (level >= Debug.loglevel) {
			String timeStr = null;
			TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
			final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.GERMAN);
			timeStr = df.format(new Date().getTime());
			final String pre = "->\t" + timeStr + " | ";
			if (Debug.loglevel >= Debug.LEVEL_ERROR) {
				System.err.println(pre + caller + "\tMSG: " + msg);
			} else {
				System.out.println(pre + caller + "\tMSG: " + msg);
			}
		}
	}

	public static void printLogForCurrentLevel() {
		for (final String s : Debug.getListForLogLevel(Debug.loglevel)) {
			System.out.println(s);
		}
	}

	public static void printLogForLevel(final Integer level) {
		for (final String s : Debug.getListForLogLevel(level)) {
			System.out.println(s);
		}
	}

	/**
	 * 
	 * @param level
	 */
	public static void saveLogToFileForLevel(final Integer level) {
		File file = null;
		FileWriter writer = null;
		final ArrayList<String> logList = Debug.getListForLogLevel(level);
		file = new File("log_" + level + "-" + new SimpleDateFormat("yyyy-M-d").format(new Date()) + ".txt");
		try {
			writer = new FileWriter(file, true);
			if ((writer != null)) {
				writer.write("BEGIN log with loglevel " + level + " @ " + new SimpleDateFormat("y-m-d").format(new Date()));
				writer.write("\n\n");
				if ((logList != null) && (logList.size() > 0)) {
					for (final String s : logList) {
						if (s != null) {
							writer.write(s);
							writer.write(System.getProperty("line.separator"));
						}
					}
				}
				if (Debug.getFormattedTimes().size() > 0) {
					writer.write("\n\nTimes\n\n");
					for (final String s : Debug.getFormattedTimes()) {
						if (s != null) {
							writer.write(s);
							writer.write(System.getProperty("line.separator"));
						}
					}
				}
				writer.write("End of log");
				writer.write("\n\n");
				writer.flush();
				writer.close();
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static void startTimer(final String name) {
		Debug.timerMap.put(name + "_start", System.currentTimeMillis());
	}

	public static void stopTimer(final String name) {
		Debug.timerMap.put(name + "_end", System.currentTimeMillis());
	}

	@Override
	public void uncaughtException(final Thread thread, final Throwable throwable) {
		Debug.log(Debug.LEVEL_FATAL, "UncaughtException thrown (" + throwable.getMessage() + " - " + throwable.toString() + ") in Thread "
				+ thread.getName() + " (" + thread.getId() + ") ");
		throwable.printStackTrace();
	}
}
