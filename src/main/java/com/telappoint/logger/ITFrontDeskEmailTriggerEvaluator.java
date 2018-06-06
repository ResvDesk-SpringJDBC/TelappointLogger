package com.telappoint.logger;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;

public class ITFrontDeskEmailTriggerEvaluator implements TriggeringEventEvaluator {
	@Override
	public boolean isTriggeringEvent(LoggingEvent loggingEvent) {
		String categoryName = loggingEvent.getLogger().getName();
		if (!LogUtils.isBlankOrNull(categoryName) && !Log4jWrapper.getInstance().getEmailTriggerMap().isEmpty()
				&& Log4jWrapper.getInstance().getEmailTriggerMap().get(categoryName) == true)
			return true;
		return false;
	}
}