package com.telappoint.logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

public class TelAppointPatternLayout extends PatternLayout {

	protected String host;

	public TelAppointPatternLayout(String format) {
		super(format);
	}

	protected String getHostname() {
		if (host == null) {
			try {
				InetAddress addr = InetAddress.getLocalHost();
				this.host = addr.getHostName();
			} catch (UnknownHostException e) {
				this.host = "HostError";
			}
		}
		return host;
	}

	@Override
	protected PatternParser createPatternParser(String pattern) {
		return new PatternParser(pattern) {

			@Override
			protected void finalizeConverter(char c) {
				PatternConverter pc = null;

				switch (c) {
				case 'h':
					pc = new PatternConverter() {
						@Override
						protected String convert(LoggingEvent event) {
							return getHostname();
						}
					};
					break;
				}

				if (pc == null)
					super.finalizeConverter(c);
				else
					addConverter(pc);
			}
		};
	}
}
