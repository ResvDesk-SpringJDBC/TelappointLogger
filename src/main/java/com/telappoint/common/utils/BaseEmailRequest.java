package com.telappoint.common.utils;

/**
 * 
 * @author Balaji
 *
 */
public class BaseEmailRequest {
	// email properties
	private String fromAddress;
	private String toAddress;
	private String replyAddress;
	private String subject;
	private String emailBody;
	
	private boolean emailThroughInternalServer = true;

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getReplyAddress() {
		return replyAddress;
	}

	public void setReplyAddress(String replyAddress) {
		this.replyAddress = replyAddress;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getEmailBody() {
		return emailBody;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	public boolean isEmailThroughInternalServer() {
		return emailThroughInternalServer;
	}

	public void setEmailThroughInternalServer(boolean emailThroughInternalServer) {
		this.emailThroughInternalServer = emailThroughInternalServer;
	}
}
