package net.digitaltsunami.tmeter.record;

import net.digitaltsunami.tmeter.TimerLogType;

/**
 * Record all timer output to console (stdout). 
 * @author dhagberg
 *
 */
public class ConsoleTimeRecorder extends FileTimeRecorder {

    /**
     * Create a recorder that will log to console using the default log type of {@link TimerLogType#TEXT}.
     */
    public ConsoleTimeRecorder() {
        this(TimerLogType.TEXT);
    }
    
    /**
     * Create a recorder that will log to console using the provided log type.
     */
    public ConsoleTimeRecorder(TimerLogType logType) {
        super(System.out, logType);
    }

}
