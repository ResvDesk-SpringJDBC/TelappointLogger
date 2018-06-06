package com.telappoint.common.utils;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.telappoint.logger.TelAppointLogger;
/**
 * 
 * Functionality Uploaded
 * 
 * @author Balaji 
 *
 */

public class PropertyUtils {
	private static Map<String, Properties> propsMap = new HashMap<String, Properties>();
	private static final Object lock = new Object();

	/**
	 * @param fileName
	 * @return An Inputstream object
	 * @throws Exception
	 *             if specified file not found
	 * @desc Creates an InputStream Object from a given file name.
	 */
	public static InputStream getResourceAsStream(String fileName) throws Exception {
		InputStream propsIn = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		if (propsIn == null) {
			propsIn = FileUtils.class.getResourceAsStream(fileName);
		}
		if (propsIn == null) {
			propsIn = ClassLoader.getSystemResourceAsStream(fileName);
		}

		if (propsIn == null) {
			TelAppointLogger.logError(fileName + " not found");
			throw new Exception(fileName + " file is not found");
		}
		return propsIn;
	}
	
	/**
	 * @desc Refreshes a Properties Object from a given file name by assigning it to null.
	 * @param fileName
	 * @return A Properties object
	 * @throws OMSException
	 */
	public static Properties clearProperties(String fileName) throws Exception {
		Properties properties = new Properties();
		try {
			properties.load(getResourceAsStream(fileName));
			synchronized (lock) {
				propsMap.put(fileName, null);
			}
		} catch (IOException e) {
			TelAppointLogger.logError("Error:", e);
			throw new Exception(fileName+" file is not found");
		}
		return properties;
	}

	/**
	 * @param fileName
	 * @return A Properties object
	 * @throws java.lang.Exception
	 * @desc Creates a Properties Object from a given file name.
	 */
	public static Properties getProperties(String fileName) throws Exception {
		Properties properties = propsMap.get(fileName);
		if (properties != null) {
			return properties;
		}
		try {
			properties = new Properties();
			properties.load(getResourceAsStream(fileName));
			synchronized (lock) {
				propsMap.put(fileName, properties);
			}
		} catch (IOException e) {
			TelAppointLogger.logError("Error:", e);
			throw new Exception(fileName + " file is not found");
		}

		return properties;
	}

	public static String getValueFromProperties(String key, String fileName) throws Exception {
		Properties properties = getProperties(fileName);
		return (String) properties.get(key);
	}
}
