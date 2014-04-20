package net.digitaltsunami.tmeter.record;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.TimerLogType;

import org.junit.Test;

public class FileTimeRecorderTest {

    @Test
    public void testRecord() {
        Timer timer = new Timer("TEST");
        // Set timer output to byte array so the value can be captured.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileTimeRecorder recorder = new FileTimeRecorder(new PrintStream(out),TimerLogType.CSV);
        timer.setTimeRecorder(recorder);
        timer.start();
        timer.stop();
        String outputLine = out.toString();
        assertTrue("Failed to write to recorder", outputLine.length() > 0);
    }
    
    @Test
    public void testDefaultLogType() {
        FileTimeRecorder recorder = new FileTimeRecorder(System.out);
        assertEquals(TimerLogType.TEXT, recorder.getLogType());
    }

    @Test
    public void testConsoleTimeRecorderTimerLogType() {
        FileTimeRecorder recorder = new FileTimeRecorder(System.out, TimerLogType.CSV);
        assertEquals(TimerLogType.CSV, recorder.getLogType());
    }
}
