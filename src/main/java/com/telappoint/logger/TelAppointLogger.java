package com.telappoint.logger;

/**
 * This class logs messages to concrete logging services.  Each logging
 * service implements the LoggingService interface and should register
 * itself in the common.properties file.  The LogDispatcher class keeps
 * track of the registered LoggingServices and calls them to log the
 * event.
 */

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.StringTokenizer;

public class TelAppointLogger {

	/**
	 * Specifies the class names of the logging services to be loaded
	 */

	public static final String NEWLINE = "^";
	public static boolean DEBUG = true;
	public static boolean INFO = true;

	private static LogDispatcher dispatcher = new LogDispatcher();

	static {
		String classNames = "com.telappoint.logger.LogFile";
		// Parse class names and instantiate classes
		if (classNames != null) {
			StringTokenizer st = new StringTokenizer(classNames, ",");
			while (st.hasMoreTokens()) {
				String className = st.nextToken().trim();
				try {
					LoggingService instance = (LoggingService) Class.forName(className).newInstance();
					dispatcher.addService(className, instance);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}

	} // end of static block

	/**
	 * Log an exception message and stack trace.
	 *
	 * @param obj
	 *            exception object for stack trace
	 * @param text
	 *            message
	 */
	// This functions is no more needed but is retained just because it was
	// being used
	// in too many places
	public static void logException(Throwable e, String text) {
		String tmp = text + " Stack Trace : " + getStackTrace(e);
		dispatcher.log(new LogEvent(LogEventType.EXCEPTION, tmp));
	}

	// Added for backward compatibility for upgrades which has class files
	// compiled with older version of
	public static void logException(Exception e, String text) {
		logException((Throwable) e, text);
	}

	/**
	 * Log an error message.
	 *
	 * @param text
	 *            error message
	 */
	public static void logError(String text) {
		dispatcher.log(new LogEvent(LogEventType.ERROR, text));
	}

	public static void logError(String text, SQLException sqle) {

		String tmp = text + " Error Code :" + sqle.getErrorCode() + " Error State :" + sqle.getSQLState() + " Stack Trace : " + getStackTrace(sqle);

		dispatcher.log(new LogEvent(LogEventType.EXCEPTION, tmp));
	}

	public static void logError(String text, Exception e) {

		String tmp = text + " : Stack Trace : " + getStackTrace(e);
		dispatcher.log(new LogEvent(LogEventType.ERROR, tmp));
	}

	public static void logError(String text, Throwable t) {
		String tmp = text + " : Stack Trace : " + getStackTrace(t);
		dispatcher.log(new LogEvent(LogEventType.ERROR, tmp));
	}

	/**
	 * Log a debug message.
	 *
	 * @param text
	 *            debug message
	 */
	public static void logDebug(String text) {
		if (TelAppointLogger.DEBUG)
			dispatcher.log(new LogEvent(LogEventType.DEBUG, text));
	}

	/**
	 * Log a debug message.
	 *
	 * @param source
	 *            object calling this method
	 * @param text
	 *            debug message
	 */
	public static void logDebug(Object source, String text) {
		if (TelAppointLogger.DEBUG)
			dispatcher.log(new LogEvent(source, LogEventType.DEBUG, text));
	}

	/**
	 * Log infomational message.
	 *
	 * @param text
	 *            information message
	 */
	public static void logInfo(String text) {
		if (TelAppointLogger.INFO)
			dispatcher.log(new LogEvent(LogEventType.INFO, text));
	}

	public static void logInfo(String text, Exception e) {
		if (TelAppointLogger.INFO) {
			String tmp = text + " : Stack Trace : " + getStackTrace(e);
			dispatcher.log(new LogEvent(LogEventType.INFO, tmp));
		}
	}

	private static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}

	public static void logInfoExt(String text) {
		text = "Thread[" + Thread.currentThread().getName() + "] " + text;
		dispatcher.log(new LogEvent(LogEventType.INFO, text));
	}

	public static void logInfoExt(String text, Exception e) {
		String tmp = "Thread[" + Thread.currentThread().getName() + "] " + text + " : Stack Trace : " + getStackTrace(e);
		dispatcher.log(new LogEvent(LogEventType.INFO, tmp));
	}

	public static void logErrorExt(String text, Exception e) {
		String tmp = "Thread[" + Thread.currentThread().getName() + "] " + text + " : Stack Trace : " + getStackTrace(e);
		dispatcher.log(new LogEvent(LogEventType.ERROR, tmp));
	}

	public static void logErrorExt(String text, Throwable e) {
		String tmp = "Thread[" + Thread.currentThread().getName() + "] " + text + " : Stack Trace : " + getStackTrace(e);
		dispatcher.log(new LogEvent(LogEventType.ERROR, tmp));
	}

	public static void logErrorExt(String text) {
		text = "Thread[" + Thread.currentThread().getName() + "] " + text;
		dispatcher.log(new LogEvent(LogEventType.ERROR, text));
	}

	public static void logDebugExt(String text) {
		String tmp = "Thread[" + Thread.currentThread().getName() + "] " + text;
		dispatcher.log(new LogEvent(LogEventType.DEBUG, tmp));
	}

	public static void logDebugExt(String text, Exception e) {
		String tmp = "Thread[" + Thread.currentThread().getName() + "] " + text + " : Stack Trace : " + getStackTrace(e);
		dispatcher.log(new LogEvent(LogEventType.DEBUG, tmp));
	}
}
