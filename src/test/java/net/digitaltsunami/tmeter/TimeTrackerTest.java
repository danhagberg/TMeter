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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.digitaltsunami.tmeter.Timer.TimerStatus;
import net.digitaltsunami.tmeter.action.ActionChain;
import net.digitaltsunami.tmeter.action.TimerAction;

import org.junit.Test;

/**
 * @author dhagberg
 * 
 */
public class TimeTrackerTest {

    private static final String TEST_TASK_NAME = "TEST_TASK_NAME";

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimeTracker#startRecording(java.lang.String)}
     * .
     */
    @Test
    public void testStartRecording() {
        Timer t = TimeTracker.startRecording(TEST_TASK_NAME);
        assertEquals(TEST_TASK_NAME, t.getTaskName());
        assertEquals(TimerStatus.RUNNING, t.getStatus());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimeTracker#isKeepList()} and
     * {@link net.digitaltsunami.tmeter.TimeTracker#setKeepList(boolean)}.
     */
    @Test
    public void testKeepListSettings() {
        assertFalse("Keep list should default to false", TimeTracker.isKeepList());
        TimeTracker.setKeepList(true);
        assertTrue("Keep list should have been set to true", TimeTracker.isKeepList());
        // Return to default state
        TimeTracker.setKeepList(false);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimeTracker#isTrackConcurrent()} and
     * {@link net.digitaltsunami.tmeter.TimeTracker#setTrackConcurrent(boolean)}
     * .
     */
    @Test
    public void testTrackConcurrentSettings() {
        assertFalse("Track concurrent should default to false", TimeTracker.isTrackConcurrent());
        TimeTracker.setTrackConcurrent(true);
        assertTrue("Track concurrent should have been set to true", TimeTracker.isTrackConcurrent());
        // Restore to default
        TimeTracker.setTrackConcurrent(false);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimeTracker#decrementConcurrent(java.lang.String)}
     */
    @Test
    public void testDecrementConcurrent() {
        TimeTracker.setTrackConcurrent(true);
        Timer t1 = TimeTracker.startRecording(TEST_TASK_NAME);
        Timer t2 = TimeTracker.startRecording(TEST_TASK_NAME);
        // Value set for second timer should be 2
        assertEquals(2, t2.getConcurrent());
        t1.stop();
        t2.stop();
        // Both timers stopped. Next value should be 1
        Timer t3 = TimeTracker.startRecording(TEST_TASK_NAME);
        assertEquals(1, t3.getConcurrent());
        TimeTracker.setTrackConcurrent(false);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimeTracker#getLogType()} and
     * {@link net.digitaltsunami.tmeter.TimeTracker#setLogType(net.digitaltsunami.tmeter.TimerLogType)}
     * .
     */
    @Test
    public void testLogTypeSettings() {
        assertEquals("LogType should default to NONE", TimerLogType.NONE, TimeTracker.getLogType());
        TimeTracker.setLogType(TimerLogType.TEXT);
        assertEquals("LogType should now be TEXT", TimerLogType.TEXT, TimeTracker.getLogType());
        // Restore to default
        TimeTracker.setLogType(TimerLogType.NONE);
    }

    /**
     * Test method to ensure that log type is set properly in timer.
     */
    @Test
    public void testLogTypePropagation() {
        TimeTracker.setLogType(TimerLogType.TEXT);
        Timer t = TimeTracker.startRecording(TEST_TASK_NAME);
        String output = TestUtils.getTimerLogOutput(t, true);
        String text = t.toString();
        assertEquals(text, output);
        // Restore to default
        TimeTracker.setLogType(TimerLogType.NONE);
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimeTracker#clear()}.
     */
    @Test
    public void testClear() {
        TimeTracker.setTrackConcurrent(true);
        TimeTracker.setKeepList(true);
        // Need to use clear to test clear later, but other tests may have
        // changed the values as this is testing a static class.
        TimeTracker.clear();
        Timer t1 = TimeTracker.startRecording(TEST_TASK_NAME);
        Timer t2 = TimeTracker.startRecording(TEST_TASK_NAME);
        assertEquals(2, t2.getConcurrent());
        assertEquals("Current number of entries in timer list incorrect. ", 2,
                TimeTracker.getCurrentTimers().length);
        TimeTracker.clear();
        assertEquals("Failed to clear list of timers.", 0, TimeTracker.getCurrentTimers().length);
        Timer t3 = TimeTracker.startRecording(TEST_TASK_NAME);
        assertEquals("Concurrent count is invalid", 1, t3.getConcurrent());
        TimeTracker.setTrackConcurrent(false);
        TimeTracker.setKeepList(false);

    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimeTracker#isTrackingDisabled()}.
     * {@link net.digitaltsunami.tmeter.TimeTracker#setTrackingDisabled(boolean)}
     */
    @Test
    public void testTrackingDisabledSettings() {
        assertFalse("Tracking disabled should default to false", TimeTracker.isTrackingDisabled());
        TimeTracker.setTrackingDisabled(true);
        assertTrue("Tracking disabled should have been set to true",
                TimeTracker.isTrackingDisabled());
        // Restore to default
        TimeTracker.setTrackingDisabled(false);
    }

    /**
     * Test method for .
     */
    @Test
    public void testTrackingDisabled() {
        TimeTracker.setTrackingDisabled(true);
        Timer t = TimeTracker.startRecording(TEST_TASK_NAME);
        assertTrue("Should have returned an instance of TimerShell", t instanceof TimerShell);
        TimeTracker.setTrackingDisabled(false);
    }

    @Test
    public void testActionChainSettings() throws InterruptedException {
        // Create an action that will populate a container with processed
        // timer. This will show that the chain is set and operable.
        final List<Timer> listOfTimers = new ArrayList<Timer>();
        TimerAction action = new TimerAction() {

            @Override
            protected void processTimer(Timer timer) {
                listOfTimers.add(timer);
            }
        };
        ActionChain chain = new ActionChain(action);
        TimeTracker.setActionChain(chain);
        assertEquals(chain, TimeTracker.getActionChain());
        Timer t1 = TimeTracker.startRecording(TEST_TASK_NAME);
        t1.start();
        t1.stop();
        Thread.sleep(1000); // Sleep to allow actionChain to process the timer.
        assertTrue("Timer list does not contain the timer. ", listOfTimers.contains(t1));
        Timer t2 = TimeTracker.startRecording(TEST_TASK_NAME);
        t2.start();
        t2.stop();
        Thread.sleep(1000); // Sleep to allow actionChain to process the timer.
        assertTrue("Timer list does not contain the timer. ", listOfTimers.contains(t2));
    }

}
