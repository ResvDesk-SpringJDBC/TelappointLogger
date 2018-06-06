package com.telappoint.common.utils;

/**
 * 
 * @author Balaji N
 *
 */
public enum PropertiesConstants {	
	EMAIL_PROP("mail.properties");
	
	private String propertyFileName;
	
	private PropertiesConstants(String propertyFileName) {
		this.setPropertyFileName(propertyFileName);
	}

	public String getPropertyFileName() {
		return propertyFileName;
	}

	public void setPropertyFileName(String propertyFileName) {
		this.propertyFileName = propertyFileName;
	}
}
