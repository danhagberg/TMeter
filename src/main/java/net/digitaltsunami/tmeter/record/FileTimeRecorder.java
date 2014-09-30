package net.digitaltsunami.tmeter.record;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.TimerLogType;

/**
 * Record all timers to the provided output stream.
 * <p>
 * Output of timer data is specfied by providing a {@link TimerLogType} value,
 * with the default being {@link TimerLogType#TEXT}
 * 
 * @author dhagberg
 * 
 */
public class FileTimeRecorder implements TimeRecorder {

    protected final PrintStream out;
    protected final TimerLogType logType;

    /**
     * Create a new FileTimeRecorder with the output stream to which all timers will be written.
     * 
     * @param out
     *            print stream for timers.
     */
    public FileTimeRecorder(PrintStream out) {
        this(out, TimerLogType.TEXT);
    }

    /**
     * Create a new FileTimeRecorder with the output stream to which all timers
     * will be written and the format in which they will be written.
     * 
     * @param out
     *            print stream for timers.
     * @param logType
     *            format used to write timers to output stream.
     */
    public FileTimeRecorder(PrintStream out, TimerLogType logType) {
        super();
        this.out = out;
        this.logType = logType;
    }

    /**
     * Create a new FileTimeRecorder with the output stream to which all timers will be written.
     * 
     * @param fileName
     *            Name of file to open for recording. Will be overritten if already exists.
     * @throws FileNotFoundException if provided file could not be opened.
     */
    public FileTimeRecorder(String fileName) throws FileNotFoundException {
        this(fileName, TimerLogType.TEXT);
    }

    /**
     * Create a new FileTimeRecorder with the output stream to which all timers
     * will be written and the format in which they will be written.
     * 
     * @param fileName
     *            Name of file to open for recording. Will be overritten if already exists.
     * @param logType
     *            format used to write timers to output stream.
     * @throws FileNotFoundException if provided file could not be opened.
     * 
     * TODO: Not yet ready as there is no way to gaurantee closure of file.
     */
    private FileTimeRecorder(String fileName, TimerLogType logType) throws FileNotFoundException {
        super();
        PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(fileName)));
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

    @Override
    public void prepareForShutdown() {
        out.close();
    }

}
