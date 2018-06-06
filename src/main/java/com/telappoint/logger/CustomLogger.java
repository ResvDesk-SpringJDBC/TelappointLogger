package com.telappoint.logger;

import java.io.IOException;

/**
 * 
 * @author Balaji N
 *
 */
public class CustomLogger extends Logger {

	public CustomLogger(String fileName, boolean pathProvided, String category, String logLevel) throws IOException, Exception {
		super(fileName, category == null ? "default" : "["+category+"]", logLevel);
	}

	public CustomLogger(String fileName) throws IOException, Exception {
		super(fileName);
	}

	public CustomLogger(String fileName, boolean pathProvided) throws IOException, Exception {
		this(fileName, pathProvided, null, null);
	}

	public void trace(Object request, String msg) {
		trace(request.toString() + ":: " + msg);
	}

	public void debug(Object request, String msg) {
		debug(request.toString() + ":: " + msg);

	}

	public void info(Object request, String msg) {
		info(request.toString() + ":: " + msg);
	}

	public void warn(Object request, String msg) {
		warn(request.toString() + ":: " + msg);
	}

	public void error(Object request, String msg) {
		error(request.toString() + ":: " + msg);
	}

	/**
	 * 
	 * @param request
	 * @param msg
	 * @param e
	 *            - This is optional.
	 */
	public void error(Object request, String msg, Exception e) {
		error(request.toString() + ":: " + msg, e);

	}

	/**
	 * Please use this with caution as for every fatal log, an email will be
	 * sent out.
	 * 
	 * @param request
	 * @param msg
	 * @param e
	 *            - This is optional.
	 */
	public void fatal(Object request, String msg, Exception e) {
		fatal(request.toString() + ":: " + msg, e);
	}
}
