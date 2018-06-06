package com.telappoint.logger;

public interface LoggingService {

    /**
    * Logs an event to a service.
    *
    * @param message  error message
    */
    public void log(LogEvent event);


}