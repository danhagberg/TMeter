/* __copyright_begin__
   Copyright 2011 Dan Hagberg

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
__copyright_end__ */
/**
 * 
 */
package net.digitaltsunami.tmeter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import net.digitaltsunami.tmeter.Timer.TimerStatus;

import org.junit.Before;
import org.junit.Test;

/**
 * @author dhagberg
 * 
 */
public class TimerShellTest {
    private static final String TASK_NAME = "TestTimer";
    private Timer timer;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        timer = new TimerShell(TASK_NAME);
        timer.start();

    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerShell#TimerShell(java.lang.String)}.
     */
    @Test
    public void testTimerString() {
        assertEquals(TASK_NAME, timer.getTaskName());
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerShell#start()}.
     */
    @Test
    public void testStart() {
        assertEquals(TimerStatus.RUNNING, timer.getStatus());
        assertEquals(0, timer.getStartTimeMillis());
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerShell#stop()}.
     */
    @Test
    public void testStop() {
        assertEquals(TimerStatus.RUNNING, timer.getStatus());
        long elapsedNanos = timer.stop();
        assertEquals(TimerStatus.STOPPED, timer.getStatus());
        assertEquals(0, timer.getElapsedNanos());
        assertEquals(
                "Elapsed nanos returned by stop is not the same as recorded within the timer",
                timer.getElapsedNanos(), elapsedNanos);
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerShell#getTaskName()}.
     */
    @Test
    public void testGetTaskName() {
        assertEquals(TASK_NAME, timer.getTaskName());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerShell#getStartTimeMillis()}.
     */
    @Test
    public void testGetStartTimeMillis() {
        assertTrue("Start Time should be zero",
                timer.getStartTimeMillis() == 0);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerShell#getStartTimeNanos()}.
     */
    @Test
    public void testGetStartTimeNanos() {
        assertTrue("Start Time should be zero",
                timer.getStartTimeNanos() == 0);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerShell#getStopTimeNanos()}.
     */
    @Test
    public void testGetStopTimeNanos() {
        timer.stop();
        assertTrue("Stop time nanos should = 0", timer.getStopTimeNanos() == 0);
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerShell#getConcurrent()} .
     */
    @Test
    public void testGetConcurrent() {
        timer.setConcurrent(1);
        assertEquals(0, timer.getConcurrent());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerShell#setConcurrent(int)}.
     */
    @Test
    public void testSetConcurrent() {
        timer.setConcurrent(2);
        assertEquals(0, timer.getConcurrent());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerShell#getElapsedNanos(boolean)}.
     */
    @Test
    public void testGetElapsedNanosRunningSnapshot() {
        assertTrue("Elapsed nanos snapshot should be 0",
                timer.getElapsedNanos(true) == 0);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerShell#getElapsedNanos(boolean)}.
     */
    @Test
    public void testGetElapsedNanosRunningNotSnapshot() {
        assertEquals(-1, timer.getElapsedNanos(false));
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerShell#getElapsedNanos(boolean)}.
     */
    @Test
    public void testGetElapsedNanosSnapshotStopped() {
        timer.stop();
        assertEquals(timer.getElapsedNanos(), timer.getElapsedNanos(true));
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerShell#getElapsedNanos()}
     * .
     */
    @Test
    public void testGetElapsedNanos() {
        // Should return -1 prior if currently running.
        assertEquals(-1, timer.getElapsedNanos());
        long elapsedNanos = timer.stop();
        assertEquals(0, timer.getElapsedNanos());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerShell#getElapsedMillis()}.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testGetElapsedMillis() throws InterruptedException {
        // Should return -1 prior if currently running.
        assertEquals(-1, timer.getElapsedMillis());
        Thread.sleep(1);
        timer.stop();
        assertEquals(0, timer.getElapsedMillis());
    }

    /**
     * Test that notes attached to the timer are processed correctly.
     */
    @Test
    public void testNotes() {
        timer.setNotes("a", "b", 1);
        Object[] notes = timer.getNotes().getNotes();
        assertEquals(3, notes.length);
        assertEquals("a", notes[0].toString());
        assertEquals("b", notes[1].toString());
        assertEquals(1, notes[2]);
    }

    /**
     * Test that output is written in csv format when specified.
     */
    @Test
    public void testOutputCsv() {
        timer.setLogType(TimerLogType.CSV);
        String output = TestUtils.getTimerLogOutput(timer, true);
        assertEquals("CSV output should not have been written.", 0, output.length());
    }

    /**
     * Test that output is written in text format when specified.
     */
    @Test
    public void testOutputText() {
        timer.setLogType(TimerLogType.TEXT);
        String output = TestUtils.getTimerLogOutput(timer, true);
        assertEquals("TEXT output should not have been written.", 0, output.length());
    }

    /**
     * Test that output is not written if NONE format specified.
     */
    @Test
    public void testOutputNone() {
        timer.setLogType(TimerLogType.NONE);
        String output = TestUtils.getTimerLogOutput(timer, true);

        assertEquals("Should not have written anything to file.", 0, output.length());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerShell#isRunning()}.
     */
    @Test
    public void testIsRunning() {
        Timer testTimer = new Timer(TASK_NAME, true, TimerLogType.NONE);
        assertFalse("Timer has not yet been started.  isRunning should report false", testTimer.isRunning());
        testTimer.start();
        assertTrue("Timer has been started.  isRunning should report true", testTimer.isRunning());
        testTimer.stop();
        assertFalse("Timer has been stopped.  isRunning should report false", testTimer.isRunning());
    }
    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerShell#isStopped()}.
     */
    @Test
    public void testIsStopped() {
        Timer testTimer = new Timer(TASK_NAME, true, TimerLogType.NONE);
        assertFalse("Timer has not yet been started.  isStopped should report false", testTimer.isStopped());
        testTimer.start();
        assertFalse("Timer has been started and is still running.  isStopped should report false", testTimer.isStopped());
        testTimer.stop();
        assertTrue("Timer has been stopped.  isRunning should report true", testTimer.isStopped());
    }
}
