package com.telappoint.logger;

/**
 * This class provides a logging service which writes to a file.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogFile implements LoggingService {

	private PrintWriter pw = null;
	/**
	 * Specifies the file name for the LogFile logging service.
	 */
	private static String fileName = ".." + File.separator + "logs"; 

	// Primes the calendar calculations
	Calendar currentCalendar = Calendar.getInstance();
	int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
	int currentMonth = currentCalendar.get(Calendar.MONTH);
	int currentYear = currentCalendar.get(Calendar.YEAR);

	public LogFile() throws IOException {
		openFile();
	}

	public LogFile(String fileName) throws IOException {
		// Supplied file name overides everything
		if (fileName != null) {
			LogFile.fileName = fileName;
		}
		openFile();
	}
	public void log(LogEvent event) {
		try {
			checkName();
			pw.println(event.toString());
		} catch (Throwable t) {
			System.err.println("***LogFile: Exception logging object: " + event);
			t.printStackTrace(System.err);
		}
	}

	/**
	 * This method was added to support the LogServer class.
	 */
	public void log(String str) {
		try {
			checkName();
			pw.println(str);
		} catch (Throwable t) {
			System.err.println("***LogFile: Exception logging str: " + str);
			t.printStackTrace();
		}
	}

	/**
	 * Opens a file, appending if it exists.
	 */
	private void openFile() throws IOException {
		System.out.println(makeCompleteFileName());
		pw = new PrintWriter(new BufferedWriter(new FileWriter(makeCompleteFileName(), true)), true);
	}

	/**
	 * Close a file.
	 */
	private void closeFile() throws IOException {
		try {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		} catch (Throwable t) {
			System.err.println("***LogFile: Exception closing logfile: " + fileName);
			t.printStackTrace(System.err);
		}
	}

	/**
	 * Create a complete file name.
	 */
	private String makeCompleteFileName() {
		StringBuffer str = new StringBuffer();
		str.append(fileName + File.separator + "resvdeskrestws");
		return (str.toString() + makeDate() + ".log");
	}

	/**
	 * Create a date formatted like yyyyMMdd.
	 */
	private String makeDate() {

		Date today = currentCalendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String formattedDate = formatter.format(today);
		return formattedDate;
	}

	/**
	 * Check if the file names needs to be updated based on the current day.
	 */
	private void checkName() throws IOException {
		Calendar now = Calendar.getInstance();
		if (now.get(Calendar.DAY_OF_MONTH) != currentDay || now.get(Calendar.MONTH) != currentMonth || now.get(Calendar.YEAR) != currentYear) {
			currentCalendar = now;
			currentDay = now.get(Calendar.DAY_OF_MONTH);
			currentMonth = now.get(Calendar.MONTH);
			currentYear = now.get(Calendar.YEAR);
			closeFile();
			openFile();
		}
	}
}
