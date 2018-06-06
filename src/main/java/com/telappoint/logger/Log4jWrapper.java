package com.telappoint.logger;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.AsyncAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SMTPAppender;
import org.apache.log4j.rolling.RollingFileAppender;
import org.apache.log4j.rolling.TimeBasedRollingPolicy;

import com.telappoint.common.utils.PropertyUtils;

/**
 * 
 * @author Balaji
 * 
 */
public class Log4jWrapper {
	
	private static String defaultLogFile = null;
	private static String logFormat = null;
	private static String logFileBaseLocation = null;
	private static String logMaskPattern = null;
	private static String defaultLogLevel = null;
	private static String logMaskData = null;
	private static String logMaskReplaceText=null;
	private static String emailFrom="";
	private static String emailTO="";
	private static String emailHost="";
	private static String emailPort="";
	private static String emailProtocol="";
	private static String emailHostUserName="";
	private static String emailHostPassword="";
	
	private static TelAppointPatternLayout layout;
	private static String hn = "Err";
	private static Pattern sensitiveDataPattern;
	private static boolean maskSensitiveData;
	private static AsyncAppender asyncAppender;
	private static String version = "$Revision: 1.0.0";
	public static Log4jWrapper log4jNew;
	private static Object lock = new Object();
	private Map<String, Logger> listOfLoggers = new HashMap<String, Logger>();
	private Map<String, Boolean> emailTriggerSwitchMap = new Hashtable<String, Boolean>();

	private Log4jWrapper() {
	}
	
	static {
		try {
			defaultLogFile = PropertyUtils.getValueFromProperties("DEFAULT_LOG_FILE", "TFlogger.properties");
			logFormat = PropertyUtils.getValueFromProperties("LOG_FORMAT", "TFlogger.properties");
			logFileBaseLocation = PropertyUtils.getValueFromProperties("LOGFILE_BASE_LOCATION", "TFlogger.properties");
			logMaskPattern = PropertyUtils.getValueFromProperties("LOG_MASK_PATTERN", "TFlogger.properties");
			defaultLogLevel = PropertyUtils.getValueFromProperties("DEFAULT_LOG_LEVEL", "TFlogger.properties");
			logMaskData = PropertyUtils.getValueFromProperties("LOG_MASK_DATA", "TFlogger.properties");
			logMaskReplaceText = PropertyUtils.getValueFromProperties("LOG_MASK_REPLACE_TEXT", "TFlogger.properties");
			emailFrom = PropertyUtils.getValueFromProperties("EMAIL_FROM", "TFlogger.properties");
			emailTO = PropertyUtils.getValueFromProperties("EMAIL_TO", "TFlogger.properties");
			emailHost= PropertyUtils.getValueFromProperties("EMAIL_HOST", "TFlogger.properties");
			emailPort= PropertyUtils.getValueFromProperties("EMAIL_PORT", "TFlogger.properties");
			emailProtocol= PropertyUtils.getValueFromProperties("EMAIL_PROTOCOL", "TFlogger.properties");
			emailHostUserName= PropertyUtils.getValueFromProperties("EMAIL_HOST_USERNAME", "TFlogger.properties");
			emailHostPassword= PropertyUtils.getValueFromProperties("EMAIL_HOST_PASSWORD", "TFlogger.properties");
		} catch(Exception e) {
			TelAppointLogger.logError("Error while loading logger properties",e);
		}
	}

	public static Log4jWrapper getInstance() {
		if (log4jNew == null) {
			synchronized (lock) {
				if (log4jNew == null) {
					log4jNew = new Log4jWrapper();
					log4jNew.initLogger();
					TelAppointLogger.logInfo("Log4J wrapper initialized:" + version);
				}
			}
		}
		return log4jNew;
	}

	/**
	 * 
	 */
	private void initLogger() {
		createLogsBaseLocationIfReqd();
		if (LogUtils.isBlankOrNull(defaultLogFile))
			defaultLogFile = "itfrontdesk_default_log.txt";
		if (LogUtils.isBlankOrNull(logFormat))
			logFormat = "%d{MM-dd-yyyy HH:mm:ss z} %h [%t] %p %c - %m%n";
		layout = new TelAppointPatternLayout(logFormat);
		InetAddress host;
		try {
			host = InetAddress.getLocalHost();
			hn = host.getHostName();
		} catch (UnknownHostException e) {
			TelAppointLogger.logError("Error getting hostname for log4j");
		}
		if (!LogUtils.isBlankOrNull(logMaskPattern)) {
			try {
				sensitiveDataPattern = Pattern.compile(logMaskPattern);
				// This unchecked exception has to be caught so the logger
				// functionality remains intact
			} catch (PatternSyntaxException pttrnSyntaxEx) {
				TelAppointLogger.logError("Error in data masking pattern. Pleae check the pattern syntax.");
				sensitiveDataPattern = null;
			}
		}
		TelAppointLogger.logInfo("Log File(s) location is:" +logFileBaseLocation);
		setMaskingData();
		createAsyncAppender();
	}
	
	private void createLogsBaseLocationIfReqd() {
		if (logFileBaseLocation == null) {
			logFileBaseLocation = getDefaultLogLocation();
		} else if (!new File(logFileBaseLocation).exists()) {
			File logLocationFromConfig = new File(logFileBaseLocation);
			boolean dirCreated = logLocationFromConfig.mkdirs();
			if (!dirCreated) {
				logFileBaseLocation = getDefaultLogLocation();
			} else {
				logFileBaseLocation = logLocationFromConfig.getAbsolutePath() + File.separator;
			}
		} else {
			logFileBaseLocation = logFileBaseLocation + File.separator;
		}
	}

	private int getEffectiveSMTPPortNumber() {
		if (LogUtils.isBlankOrNull(emailPort))
			return 25;
		try {
			return Integer.parseInt(emailPort);

		} catch (Exception _ex) {
			TelAppointLogger.logError("Email Port from Config is not a valid number. Defaulting to 25");
			return 25;
		}

	}

	private void setMaskingData() {
		maskSensitiveData = isMaskingEnabled();
		if (maskSensitiveData) {
			TelAppointLogger.logInfo("#### Data Masking is enabled #### ");
		} else
			TelAppointLogger.logInfo("#### Data Masking is disabled ####");
	}

	synchronized Logger getLog4jLogger(String fileName, String category, String logLevel) {
		Logger log4jLogger = null;
		String logFileName = getLogFileName(fileName);

		if (LogUtils.isBlankOrNull(logFileName)) {
			TelAppointLogger.logInfo("Log path [" + fileName + "] does NOT have file name. Default filename [" + defaultLogFile + "] will be used for category [" + category + "]");
			logFileName = defaultLogFile;
		}

		if (LogUtils.isBlankOrNull(category)) {
			TelAppointLogger.logInfo("Category is BLANK. Logfile name will be used as category");
			category = logFileName;
		}

		Logger existingLogger = returnLoggerIfPresent(category, logFileName);
		if (existingLogger != null) {
			log4jLogger = existingLogger;
		} else
			log4jLogger = configureLogger(category, logLevel, logFileName);

		TelAppointLogger
				.logInfo("Category [" + category + "] will be logged in file [" + logFileBaseLocation + logFileName + "] with level [" + log4jLogger.getLevel() + "] " + listOfLoggers.size());

		return log4jLogger;
	}

	private Logger returnLoggerIfPresent(String category, String logFileName) {

		Logger loggerFromMap = listOfLoggers.get(logFileName);
		if (loggerFromMap != null) {
			TelAppointLogger.logInfo("Returning existing category [" + loggerFromMap.getName() + "] for [" + category + "] as the log file[" + logFileName + "] is same ");
			return loggerFromMap;
		}
		return null;
	}

	
	private String getDefaultLogLocation() {
		String defaultLogLoc = "." + File.separator + "logs" + File.separator;
		TelAppointLogger.logError("############ WARNING::USING DEFAULT LOCATION FOR LOG FILES !!!!  ############");
		return defaultLogLoc;
	}

	private Logger configureLogger(String category, String logLevel, String logFileName) {
		Logger log4jLogger = LogManager.getLogger(category);
		log4jLogger.setAdditivity(false);
		Level returnedLevel = getLogLevel(logLevel);
		log4jLogger.setLevel(returnedLevel);
		RollingFileAppender rolling = createDailyRollingFileAppender(logFileName);
		
		log4jLogger.addAppender(rolling);
		log4jLogger.log(returnedLevel, "Logger Initialized=>");
		if (asyncAppender != null) {
			log4jLogger.addAppender(asyncAppender);
			emailTriggerSwitchMap.put(category, true);
		} else
			emailTriggerSwitchMap.put(category, false);
		listOfLoggers.put(logFileName, log4jLogger);
		return log4jLogger;
	}

	private void createAsyncAppender() {	
		SMTPAppender smtpAppender = createEmailAppender();
		if (smtpAppender != null) {
			asyncAppender = new AsyncAppender();
			asyncAppender.addAppender(smtpAppender);
			asyncAppender.activateOptions();
		}
	}

	/**
	 * @return
	 */
	private SMTPAppender createEmailAppender() {
		SMTPAppender smtpAppender = null;
		if (sendEmailOnFatalErrors()) {
			smtpAppender = new SMTPAppender();
			smtpAppender.setName("itfrontdeskemaillog");
			smtpAppender.setTo(emailTO);
			smtpAppender.setBufferSize(1);
			smtpAppender.setFrom(emailFrom);
			smtpAppender.setLayout(layout);
			smtpAppender.setSMTPHost(emailHost);
			smtpAppender.setSMTPPort(getEffectiveSMTPPortNumber());
			smtpAppender.setSMTPProtocol(emailProtocol);
			//smtpAppender.setSMTPDebug(true);
			smtpAppender.setThreshold(Level.FATAL);
			smtpAppender.setEvaluator(new ITFrontDeskEmailTriggerEvaluator());
			smtpAppender.setSubject(hn + " : ERROR in itfrontdesk");
			if (!LogUtils.isBlankOrNull(emailHostUserName) && !LogUtils.isBlankOrNull(emailHostPassword)) {
				smtpAppender.setSMTPUsername(emailHostUserName);
				smtpAppender.setSMTPPassword(emailHostPassword);
			}
			smtpAppender.activateOptions();
		}
		return smtpAppender;
	}

	/**
	 * @param logFileName
	 * @return
	 */
	private RollingFileAppender createDailyRollingFileAppender(String logFileName) {
		TimeBasedRollingPolicy tbrp = new TimeBasedRollingPolicy();
		// Files will roll over daily.
		tbrp.setFileNamePattern(logFileBaseLocation + logFileName + "_%d{yyyyMMdd}.log"); 
		tbrp.activateOptions();
		RollingFileAppender rolling = new RollingFileAppender();
		rolling.setName("itfrontdeskdailylog");
		rolling.setLayout(layout);
		rolling.setRollingPolicy(tbrp);
		// rolling.setFile(logFileBaseLocation + logFileName);
		rolling.activateOptions();
		return rolling;
	}

	private String getLogFileName(String fileName) {
		if (fileName != null) {
			File f = new File(fileName);
			String logFileName = f.getName();
			if ("".equals(logFileName))
				return null;
			else
				return logFileName;
		}
		return null;
	}

	private Level getLogLevel(String logLevel) {
		if (!LogUtils.isBlankOrNull(logLevel)) {
			Level level = getLog4jLevel(logLevel);
			if (level == null) {
				level = getDefaultLevel();
			}
			return level;
		} else {
			TelAppointLogger.logInfo("Log level not supplied. Default log level [" + defaultLogLevel + "] will be set.");
			return getDefaultLevel();
		}
	}

	private Level getDefaultLevel() {
		Level dl = getLog4jLevel(defaultLogLevel);
		return (dl == null ? Level.DEBUG : dl);
	}

	private Level getLog4jLevel(String level) {
		if ("INFO".equalsIgnoreCase(level))
			return Level.INFO;
		if ("DEBUG".equalsIgnoreCase(level))
			return Level.DEBUG;
		if ("WARN".equalsIgnoreCase(level))
			return Level.WARN;
		if ("ERROR".equalsIgnoreCase(level))
			return Level.ERROR;
		if ("TRACE".equalsIgnoreCase(level))
			return Level.TRACE;
		if ("FATAL".equalsIgnoreCase(level))
			return Level.FATAL;
		return null;
	}

	Map<String, Logger> getAllITfrontDeskLoggers() {
		return Collections.unmodifiableMap(listOfLoggers);
	}

	public boolean maskSensitiveData() {
		return maskSensitiveData;
	}

	Pattern getMaskSensitiveDataPatern() {
		return sensitiveDataPattern;
	}

	String getMaskSensitiveDataReplacmentText() {
		return logMaskReplaceText;
	}

	static String getDefaultLogLevel() {
		return defaultLogLevel;
	}

	public boolean sendEmailOnFatalErrors() {
		if (LogUtils.isBlankOrNull(emailTO) || LogUtils.isBlankOrNull(emailFrom) || LogUtils.isBlankOrNull(emailHost)) {
			TelAppointLogger.logError("############ WARNING!!! - EMAIL CONFIG PARAMETERS FOR LOGGER NOT FOUND. EMAIL WILL NOT BE SENT ON FATAL ERRORS  #############");
			return false;
		}
		return true;
	}

	public Map<String, Boolean> getEmailTriggerMap() {
		return Collections.unmodifiableMap(emailTriggerSwitchMap);
	}

	void toggleEmailTriggerConfig(String logCatgeory, boolean emailOnOff) {
		emailTriggerSwitchMap.put(logCatgeory, emailOnOff);
	}

	void toggleDataMasking() {
		maskSensitiveData = !maskSensitiveData;
		TelAppointLogger.logInfo("Toggling data masking. Data masking is now " + (maskSensitiveData ? "ENABLED" : "DISABLED"));
	}

	public boolean isMaskingEnabled() {
		boolean isMaskingOn = (sensitiveDataPattern != null && !LogUtils.isBlankOrNull(logMaskData) && ("Y".equalsIgnoreCase(logMaskData) || "true".equalsIgnoreCase(logMaskData))
				&& !LogUtils.isBlankOrNull(logMaskPattern) && !LogUtils.isBlankOrNull(logMaskReplaceText));
		return isMaskingOn;
	}
}
