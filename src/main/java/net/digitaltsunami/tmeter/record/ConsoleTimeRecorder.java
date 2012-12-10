package net.digitaltsunami.tmeter.record;

import net.digitaltsunami.tmeter.TimerLogType;

public class ConsoleTimeRecorder extends FileTimeRecorder {

    public ConsoleTimeRecorder() {
        super(System.out);
    }
    
    public ConsoleTimeRecorder(TimerLogType logType) {
        super(System.out, logType);
    }

}
