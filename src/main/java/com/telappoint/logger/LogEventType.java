package com.telappoint.logger;

/**
 * Represents the different kinds of log events.
 *
 */

public class LogEventType {

    public static final LogEventType EXCEPTION = new LogEventType("X");
    public static final LogEventType ERROR     = new LogEventType("E");
    public static final LogEventType INFO      = new LogEventType("I");
    public static final LogEventType DEBUG     = new LogEventType("D");

    protected String name;

    public LogEventType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
