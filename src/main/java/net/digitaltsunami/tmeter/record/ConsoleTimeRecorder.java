package net.digitaltsunami.tmeter.record;

import net.digitaltsunami.tmeter.TimerLogType;

/**
 * Record all timer output to console (stdout). 
 * <p>
 * Output of timer data is specfied by providing a {@link TimerLogType} value,
 * with the default being {@link TimerLogType#TEXT}
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

    /* (non-Javadoc)
     * @see net.digitaltsunami.tmeter.record.FileTimeRecorder#prepareForShutdown()
     */
    @Override
    public void prepareForShutdown() {
        // We do not want to close the file for System.out, so don't call super.
    }

}
