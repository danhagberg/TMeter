package net.digitaltsunami.tmeter.record;

import java.io.PrintStream;

import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.TimerLogType;

/**
 * Record all timers to the provided output stream.
 * 
 * @author dhagberg
 * 
 */
public class FileTimeRecorder implements TimeRecorder {

    protected final PrintStream out;
    protected final TimerLogType logType;

    /**
     * Create a new FileTimeRecorder with the output stream to which all timers.
     * @param out print stream for timers. 
     */
    public FileTimeRecorder(PrintStream out) {
        this(out, TimerLogType.TEXT);
    }

    /**
     * Create a new FileTimeRecorder with the output stream to which all timers
     * will be written and the format in which they will be written.
     * 
     * @param out print stream for timers. 
     * @param logType format used to write timers to output stream.
     */
    public FileTimeRecorder(PrintStream out, TimerLogType logType) {
        super();
        this.out = out;
        this.logType = logType;
    }

    @Override
    public void record(Timer timer) {
        switch (logType) {
        case TEXT:
            out.println(timer.toString());
            break;

        case CSV:
            out.println(timer.toCsv());
            break;

        default:
            break;
        }
    }

    public TimerLogType getLogType() {
        return this.logType;
    }

}
