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
import net.digitaltsunami.tmeter.record.ConsoleTimeRecorder;
import net.digitaltsunami.tmeter.record.NullTimeRecorder;

import org.junit.Test;

/**
 * @author dhagberg
 * 
 */
public class NamedTimeTrackerTest {

    private static final String TEST_TASK_NAME = "TEST_TASK_NAME";
    private static ConsoleTimeRecorder csvRecorder = new ConsoleTimeRecorder(TimerLogType.CSV);
    private static ConsoleTimeRecorder textRecorder = new ConsoleTimeRecorder(TimerLogType.TEXT);
    private NamedTimeTracker timeTrackerInst = new NamedTimeTracker("TEST_TRACKER");

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.NamedTimeTracker#startRecording(java.lang.String)}
     * .
     */
    @Test
    public void testStartRecording() {
        Timer t = timeTrackerInst.startRecording(TEST_TASK_NAME);
        assertEquals(TEST_TASK_NAME, t.getTaskName());
        assertEquals(TimerStatus.RUNNING, t.getStatus());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.NamedTimeTracker#isKeepList()} and
     * {@link net.digitaltsunami.tmeter.NamedTimeTracker#setKeepList(boolean)}.
     */
    @Test
    public void testKeepListSettings() {
        assertFalse("Keep list should default to false", timeTrackerInst.isKeepList());
        timeTrackerInst.setKeepList(true);
        assertTrue("Keep list should have been set to true", timeTrackerInst.isKeepList());
        // Return to default state
        timeTrackerInst.setKeepList(false);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.NamedTimeTracker#isTrackConcurrent()} and
     * {@link net.digitaltsunami.tmeter.NamedTimeTracker#setTrackConcurrent(boolean)}
     * .
     */
    @Test
    public void testTrackConcurrentSettings() {
        assertFalse("Track concurrent should default to false", timeTrackerInst.isTrackConcurrent());
        timeTrackerInst.setTrackConcurrent(true);
        assertTrue("Track concurrent should have been set to true", timeTrackerInst.isTrackConcurrent());
        // Restore to default
        timeTrackerInst.setTrackConcurrent(false);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.NamedTimeTracker#decrementConcurrent(java.lang.String)}
     */
    @Test
    public void testDecrementConcurrent() {
        timeTrackerInst.setTrackConcurrent(true);
        Timer t1 = timeTrackerInst.startRecording(TEST_TASK_NAME);
        Timer t2 = timeTrackerInst.startRecording(TEST_TASK_NAME);
        // Value set for second timer should be 2
        assertEquals(2, t2.getConcurrent());
        t1.stop();
        t2.stop();
        // Both timers stopped. Next value should be 1
        Timer t3 = timeTrackerInst.startRecording(TEST_TASK_NAME);
        assertEquals(1, t3.getConcurrent());
        timeTrackerInst.setTrackConcurrent(false);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.NamedTimeTracker#getLogType()} and
     * {@link net.digitaltsunami.tmeter.NamedTimeTracker#setDefaultTimeRecorder(TimeRecorder)}
     * .
     */
    @Test
    public void testLogTypeSettings() {
        assertEquals("TimeRecorder should default to NullTimeRecorder",
                NullTimeRecorder.getInstance(),
                timeTrackerInst.getDefaultTimeRecorder());
        timeTrackerInst.setDefaultTimeRecorder(csvRecorder);
        assertEquals("TimeRecorder should now be TEXT", csvRecorder,
                timeTrackerInst.getDefaultTimeRecorder());
        restoreDefaultTimeRecorder();
    }

    /**
     * Test method to ensure that log type is set properly in timer.
     */
    @Test
    public void testLogTypePropagation() {
        timeTrackerInst.setDefaultTimeRecorder(textRecorder);
        Timer t = timeTrackerInst.startRecording(TEST_TASK_NAME);
        String output = TestUtils.getTimerLogOutput(t, TimerLogType.TEXT, true);
        String text = t.toString();
        assertEquals(text, output);
        // Restore to default
        restoreDefaultTimeRecorder();
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.NamedTimeTracker#clear()}.
     */
    @Test
    public void testClear() {
        timeTrackerInst.setTrackConcurrent(true);
        timeTrackerInst.setKeepList(true);
        // Need to use clear to test clear later, but other tests may have
        // changed the values as this is testing a static class.
        timeTrackerInst.clear();
        timeTrackerInst.startRecording(TEST_TASK_NAME);
        Timer t2 = timeTrackerInst.startRecording(TEST_TASK_NAME);
        assertEquals(2, t2.getConcurrent());
        assertEquals("Current number of entries in timer list incorrect. ", 2,
                timeTrackerInst.getCurrentTimers().length);
        timeTrackerInst.clear();
        assertEquals("Failed to clear list of timers.", 0, timeTrackerInst.getCurrentTimers().length);
        Timer t3 = timeTrackerInst.startRecording(TEST_TASK_NAME);
        assertEquals("Concurrent count is invalid", 1, t3.getConcurrent());
        timeTrackerInst.setTrackConcurrent(false);
        timeTrackerInst.setKeepList(false);

    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.NamedTimeTracker#isTrackingDisabled()}.
     * {@link net.digitaltsunami.tmeter.NamedTimeTracker#setTrackingDisabled(boolean)}
     */
    @Test
    public void testTrackingDisabledSettings() {
        assertFalse("Tracking disabled should default to false", timeTrackerInst.isTrackingDisabled());
        timeTrackerInst.setTrackingDisabled(true);
        assertTrue("Tracking disabled should have been set to true",
                timeTrackerInst.isTrackingDisabled());
        // Restore to default
        timeTrackerInst.setTrackingDisabled(false);
    }

    /**
     * Test method for .
     */
    @Test
    public void testTrackingDisabled() {
        timeTrackerInst.setTrackingDisabled(true);
        Timer t = timeTrackerInst.startRecording(TEST_TASK_NAME);
        assertTrue("Should have returned an instance of TimerShell", t instanceof TimerShell);
        timeTrackerInst.setTrackingDisabled(false);
    }

    /**
     * 
     */
    @Test
    public void testTimerActionSettings() throws InterruptedException {
        // Create an action that will populate a container with processed
        // timer. This will show that the chain is set and operable.
        final List<Timer> listOfTimers = new ArrayList<Timer>();
        TimerAction action = new TimerAction() {
    
            @Override
            protected void processTimer(Timer timer) {
                listOfTimers.add(timer);
            }
        };
        timeTrackerInst.addCompletionAction(action);
        Timer t1 = timeTrackerInst.startRecording(TEST_TASK_NAME);
        t1.start();
        t1.stop();
        Thread.sleep(1000); // Sleep to allow actionChain to process the timer.
        assertTrue("Timer list does not contain the timer. ", listOfTimers.contains(t1));
    }

    /**
     * Old method for testing timer action settings. Replaced by testTimerActionSettings()
     */
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
        timeTrackerInst.setActionChain(chain);
        assertTrue(timeTrackerInst.getActionChain().getActions().contains(action));
        Timer t1 = timeTrackerInst.startRecording(TEST_TASK_NAME);
        t1.start();
        t1.stop();
        Thread.sleep(1000); // Sleep to allow actionChain to process the timer.
        assertTrue("Timer list does not contain the timer. ", listOfTimers.contains(t1));
        Timer t2 = timeTrackerInst.startRecording(TEST_TASK_NAME);
        t2.start();
        t2.stop();
        Thread.sleep(1000); // Sleep to allow actionChain to process the timer.
        assertTrue("Timer list does not contain the timer. ", listOfTimers.contains(t2));
    }

    /**
     * 
     */
    protected void restoreDefaultTimeRecorder() {
        timeTrackerInst.setDefaultTimeRecorder(NullTimeRecorder.getInstance());
    }

}
