package com.telappoint.logger;

/**
 * This class keeps track of all registered logging services.  When an
 * event is logged via the log method, the underlying logging services
 * are called.
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LogDispatcher implements LoggingService {

	protected Map listeners = new HashMap();

	public void addService(String name, LoggingService logger) {
		listeners.put(name, logger);
	}

	public void removeService(String name) {
		listeners.remove(name);
	}

	public void log(LogEvent event) {
		for (Iterator i = listeners.entrySet().iterator(); i.hasNext();) {
			Map.Entry e = (Map.Entry) i.next();
			LoggingService service = (LoggingService) e.getValue();
			service.log(event);

		}
	}

}
