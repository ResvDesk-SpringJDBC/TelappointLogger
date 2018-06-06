package com.telappoint.common.utils;

import java.util.Properties;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.telappoint.logger.CustomLogger;

/**
 * 
 * @author Balaji N
 *
 */
public class MailUtils {
	
	public static void sendEmail(CustomLogger customLogger,BaseEmailRequest emailRequest, Multipart multipart) throws Exception {
		if(multipart == null) {
			// email body part
			multipart = new MimeMultipart("alternative");
		} 
		MimeBodyPart emailBodyPart = new MimeBodyPart();
		emailBodyPart.setContent(emailRequest.getEmailBody(), "text/html; charset=utf-8");
		multipart.addBodyPart(emailBodyPart);
		
		JavaMailSenderImpl sender = new JavaMailSenderImpl();	
		if(emailRequest.isEmailThroughInternalServer() == false) {
			sender.setHost(PropertyUtils.getValueFromProperties("mail.smtp.hostname", PropertiesConstants.EMAIL_PROP.getPropertyFileName()));
			sender.setPort(Integer.valueOf(PropertyUtils.getValueFromProperties("mail.smtp.port", PropertiesConstants.EMAIL_PROP.getPropertyFileName())));
			sender.setUsername(PropertyUtils.getValueFromProperties("mail.smtp.user", PropertiesConstants.EMAIL_PROP.getPropertyFileName()));
			sender.setPassword(PropertyUtils.getValueFromProperties("mail.smtp.password", PropertiesConstants.EMAIL_PROP.getPropertyFileName()));
			sender.setJavaMailProperties(getSMTPMailProperties());
		} else {
			String mailHost = PropertyUtils.getValueFromProperties("internal.mail.hostname", PropertiesConstants.EMAIL_PROP.getPropertyFileName());
			sender.setHost(mailHost);
		}
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(emailRequest.getToAddress());
		helper.setFrom(emailRequest.getFromAddress());
		helper.setSubject(emailRequest.getSubject());
		helper.setText(emailRequest.getEmailBody());
		message.setContent(multipart);
		sender.send(message);
	}
	
	private static Properties getSMTPMailProperties() {
		Properties properties = new Properties();
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.smtp.auth", "false");
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.debug", "false");
		return properties;
	}
}
