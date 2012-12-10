package net.digitaltsunami.tmeter.record;

import java.io.PrintStream;

import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.TimerLogType;

public class FileTimeRecorder implements TimeRecorder {
    
    protected PrintStream out;
    protected TimerLogType logType;
    
    public FileTimeRecorder(PrintStream out) {
        this(out, TimerLogType.TEXT);
    }
    
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

    public void setLogType(TimerLogType logType) {
        this.logType = logType;
    }

    public TimerLogType getLogType() {
        return this.logType;
    }

}
