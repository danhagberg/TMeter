package net.digitaltsunami.tmeter.record;

import static org.junit.Assert.*;

import net.digitaltsunami.tmeter.TimerLogType;

import org.junit.Test;

public class ConsoleTimeRecorderTest {

    @Test
    public void testConsoleTimeRecorder() {
        ConsoleTimeRecorder ctr = new ConsoleTimeRecorder();
        assertEquals(TimerLogType.TEXT, ctr.getLogType());
    }

    @Test
    public void testConsoleTimeRecorderTimerLogType() {
        ConsoleTimeRecorder ctr = new ConsoleTimeRecorder(TimerLogType.CSV);
        assertEquals(TimerLogType.CSV, ctr.getLogType());
    }

}
