package com.telappoint.logger;

/**
 * Represents an event to be logged.
 *
 */

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogEvent {

	protected static String DELIMITER = ", ";
	protected static String CARET = "^";

	protected static DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
	protected static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss z");

	protected static String host;

	static {
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			host = "Unknown";
		}
	}

	// Log event properties
	protected Date time;
	protected LogEventType type;
	protected String message;
	protected Throwable exception;
	protected Object source;

	protected String internalString;

	public LogEvent(LogEventType type, String message) {
		this.source = null;
		fillInDetails(type, message, null);
	}

	public LogEvent(Object source, LogEventType type, String message) {
		this.source = source;
		fillInDetails(type, message, null);
	}

	public LogEvent(LogEventType type, String message, Throwable exception) {
		this.source = null;
		fillInDetails(type, message, exception);
	}

	public LogEvent(Object source, LogEventType type, String message, Throwable exception) {
		this.source = source;
		fillInDetails(type, message, exception);
	}

	private void fillInDetails(LogEventType type, String message, Throwable exception) {
		this.time = new Date();
		this.type = type;
		this.message = message;
		this.exception = exception;

		createString();
	}

	/**
	 * Format a LogEvent exceptionect for output. This method is only called
	 * once.
	 *
	 * Sample output:
	 *
	 * "05-17-2000, 09:05:35 PST, HostName, <I>, Here is some information
	 * "05-17-2000, 09:05:35 PST, HostName, <W>, This could be a warning message
	 * "05-17-2000, 09:05:35 PST, HostName, <E>, An error occured "05-17-2000,
	 * 09:05:35 PST, HostName, <D>, SomeClassName, A debug statement for
	 * developers
	 */
	private void createString() {

		StringBuffer buffer = new StringBuffer(256);

		buffer.append(dateFormat.format(time));
		buffer.append(DELIMITER);

		buffer.append(timeFormat.format(time));
		buffer.append(DELIMITER);

		buffer.append("<");
		buffer.append(type.toString());
		buffer.append(">");
		buffer.append(DELIMITER);

		buffer.append(host);
		buffer.append(DELIMITER);

		if (source != null) {
			buffer.append(source.getClass().getName());
			buffer.append(DELIMITER);
		}

		buffer.append(message);

		// Print exception (if it exists) to the buffer - do some IO
		// gymnastics to make this happen

		if (exception != null) {

			buffer.append(DELIMITER);

			StringWriter sWriter = new StringWriter(512);
			PrintWriter pWriter = new PrintWriter(sWriter);

			exception.printStackTrace(pWriter);

			StringReader stringReader = new StringReader(sWriter.toString());
			BufferedReader bufReader = new BufferedReader(stringReader);

			String line;

			try {
				while ((line = bufReader.readLine()) != null) {
					buffer.append(line);
					buffer.append(CARET);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			pWriter.close();
			sWriter = null;
			pWriter = null;
		}

		internalString = buffer.toString();

		buffer = null;
	}

	public Date getTime() {
		return time;
	}

	public LogEventType getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	public Throwable getException() {
		return exception;
	}

	/**
	 * Concreate LoggingServices will call this method to log an event.
	 *
	 * @return A formatted log event
	 */
	public String toString() {

		return internalString;
	}
}
