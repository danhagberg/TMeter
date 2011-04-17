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
public class TimerTest {
    private static final String TASK_NAME = "TestTimer";
    private Timer timer;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        timer = new Timer(TASK_NAME);
        timer.start();

    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.Timer#Timer(java.lang.String)}.
     */
    @Test
    public void testTimerString() {
        assertEquals(TASK_NAME, timer.getTaskName());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.Timer#Timer(java.lang.String, boolean)}
     * .
     */
    @Test
    public void testTimerStringBoolean() {
        Timer notStarted = new Timer(TASK_NAME, true, TimerLogType.NONE);
        assertEquals(TimerStatus.INITIALIZED, notStarted.getStatus());
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.Timer#start()}.
     */
    @Test
    public void testStart() {
        Timer notStarted = new Timer(TASK_NAME, true, TimerLogType.NONE);
        assertEquals(TimerStatus.INITIALIZED, notStarted.getStatus());
        notStarted.start();
        assertEquals(TimerStatus.RUNNING, notStarted.getStatus());
        assertTrue("Start Time should be greater than zero",
                notStarted.getStartTimeMillis() > 0);
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.Timer#stop()}.
     */
    @Test
    public void testStop() {
        assertEquals(TimerStatus.RUNNING, timer.getStatus());
        long elapsedNanos = timer.stop();
        assertEquals(TimerStatus.STOPPED, timer.getStatus());
        assertTrue("Elapsed time nanoseconds should be creater than zero",
                timer.getElapsedNanos() > 0);
        assertEquals(
                "Elapsed nanos returned by stop is not the same as recorded within the timer",
                timer.getElapsedNanos(), elapsedNanos);
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.Timer#getThreadName()}.
     */
    @Test
    public void testGetThreadName() {
        assertEquals(Thread.currentThread().getName(), timer.getThreadName());
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.Timer#getTaskName()}.
     */
    @Test
    public void testGetTaskName() {
        assertEquals(TASK_NAME, timer.getTaskName());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.Timer#getStartTimeMillis()}.
     */
    @Test
    public void testGetStartTimeMillis() {
        assertTrue("Start Time should be greater than zero",
                timer.getStartTimeMillis() > 0);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.Timer#getStartTimeNanos()}.
     * being called before it is started.
     */
    @Test(expected = IllegalStateException.class)
    public void testGetStartTimeNanosInvalid() {
        Timer notStarted = new Timer(TASK_NAME, true, TimerLogType.NONE);
        assertEquals(TimerStatus.INITIALIZED, notStarted.getStatus());
        notStarted.getStartTimeNanos();
        fail("Should have thrown IllegalStateExcpeption");
    }
    
    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.Timer#getStartTimeMillis()}.
     * being called before it is started.
     */
    @Test(expected = IllegalStateException.class)
    public void testGetStartTimeMillisInvalid() {
        Timer notStarted = new Timer(TASK_NAME, true, TimerLogType.NONE);
        assertEquals(TimerStatus.INITIALIZED, notStarted.getStatus());
        notStarted.getStartTimeMillis();
        fail("Should have thrown IllegalStateExcpeption");
    }
    
    /**
     * Test method for 
     * {@link net.digitaltsunami.tmeter.Timer#getStopTimeNanos()}.
     * being called at the wrong time.
     */
    @Test(expected = IllegalStateException.class)
    public void testGetStopTimeNanosInvalid() {
        Timer notStopped = new Timer(TASK_NAME, true, TimerLogType.NONE);
        notStopped.start();
        assertEquals(TimerStatus.RUNNING, notStopped.getStatus());
        notStopped.getStopTimeNanos();
        fail("Should have thrown IllegalStateExcpeption");
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.Timer#getElapsedNanos()}.
     * being called before it is started.
     */
    @Test(expected = IllegalStateException.class)
    public void testGetElapsedNanosInvalid() {
        Timer notStarted = new Timer(TASK_NAME, true, TimerLogType.NONE);
        assertEquals(TimerStatus.INITIALIZED, notStarted.getStatus());
        notStarted.getElapsedNanos();
        fail("Should have thrown IllegalStateExcpeption");
    }
    
    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.Timer#getElapsedMillis()}.
     * being called before it is started.
     */
    @Test(expected = IllegalStateException.class)
    public void testGetElapsedMillisInvalid() {
        Timer notStarted = new Timer(TASK_NAME, true, TimerLogType.NONE);
        assertEquals(TimerStatus.INITIALIZED, notStarted.getStatus());
        notStarted.getElapsedMillis();
        fail("Should have thrown IllegalStateExcpeption");
    }
    

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.Timer#getStopTimeNanos()}.
     */
    @Test
    public void testGetStopTimeNanos() {
        timer.stop();
        assertTrue("Stop time nanos should be > or = start time nanos",
                timer.getStopTimeNanos() >= timer.getStartTimeNanos());
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.Timer#getConcurrent()} .
     */
    @Test
    public void testGetConcurrent() {
        timer.setConcurrent(1);
        assertEquals(1, timer.getConcurrent());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.Timer#setConcurrent(int)}.
     */
    @Test
    public void testSetConcurrent() {
        timer.setConcurrent(2);
        assertEquals(2, timer.getConcurrent());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.Timer#getElapsedNanos(boolean)}.
     */
    @Test
    public void testGetElapsedNanosRunningSnapshot() {
        assertTrue("Elapsed nanos snapshot should be greater than 0",
                timer.getElapsedNanos(true) > 0);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.Timer#getElapsedNanos(boolean)}.
     */
    @Test
    public void testGetElapsedNanosRunningNotSnapshot() {
        assertEquals(-1, timer.getElapsedNanos(false));
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.Timer#getElapsedNanos(boolean)}.
     */
    @Test
    public void testGetElapsedNanosSnapshotStopped() {
        timer.stop();
        assertEquals(timer.getElapsedNanos(), timer.getElapsedNanos(true));
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.Timer#getElapsedNanos()}
     * .
     */
    @Test
    public void testGetElapsedNanos() {
        // Should return -1 prior if currently running.
        assertEquals(-1, timer.getElapsedNanos());
        long elapsedNanos = timer.stop();
        assertTrue("Elaped nanos should be greater than zero",
                timer.getElapsedNanos() > 0);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.Timer#getElapsedMillis()}.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testGetElapsedMillis() throws InterruptedException {
        // Should return -1 prior if currently running.
        assertEquals(-1, timer.getElapsedMillis());
        Thread.sleep(1);
        timer.stop();
        assertTrue("Elaped millis should be greater than zero",
                timer.getElapsedMillis() > 0);
        assertEquals(timer.getElapsedNanos() / 1000000, timer.getElapsedMillis());
    }

    /**
     * Test that notes attached to the timer are processed correctly.
     */
    @Test
    public void testNotes() {
        timer.setNotes("a", "b", 1);
        Object[] notes = timer.getNotes();
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
        // Must be after timer has stopped otherwise it will produce a different
        // value.
        String csvLine = timer.toCsv();

        assertEquals("CSV output should have been written.", csvLine, output);
    }

    /**
     * Test that output is written in text format when specified.
     */
    @Test
    public void testOutputText() {
        timer.setLogType(TimerLogType.TEXT);
        String output = TestUtils.getTimerLogOutput(timer, true);
        // Must be after timer has stopped otherwise it will produce a different
        // value.
        String textLine = timer.toString();

        assertEquals("Text output should have been written.", textLine, output);
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
     * Test that notes attached to the timer are output correctly.
     */
    @Test
    public void testNotesOutputCsv() {
        timer.setNotes("a", "b", 1);
        timer.setLogType(TimerLogType.CSV);
        String suffix = ",a,b,1";
        String output = TestUtils.getTimerLogOutput(timer, true);

        assertTrue("CSV output of timer should end with timer notes.", output.endsWith(suffix));
    }

    /**
     * Test that notes attached to the timer are output correctly.
     */
    @Test
    public void testNotesOutputText() {
        timer.setNotes("a", "b", 1);
        timer.setLogType(TimerLogType.TEXT);
        String suffix = "Notes: a,b,1";
        String output = TestUtils.getTimerLogOutput(timer, true);

        assertTrue("TEXT output of timer should end with timer notes.", output.endsWith(suffix));
    }
    
    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.Timer#isRunning()}.
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
     * {@link net.digitaltsunami.tmeter.Timer#isStopped()}.
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
