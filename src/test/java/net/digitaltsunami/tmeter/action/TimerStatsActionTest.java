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
package net.digitaltsunami.tmeter.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;

import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.TimerBasicStatistics;

import org.junit.Before;
import org.junit.Test;

/**
 * @author dhagberg
 * 
 */
public class TimerStatsActionTest {
    private static final String TEST_TIMER = "TEST_TIMER";
    private Timer timer;
    private TimerStatsAction action;

    @Before
    public void setup() throws InterruptedException {
        timer = new Timer(TEST_TIMER);
        action = new TimerStatsAction();
        Thread.sleep(0, 10000);
        timer.stop();
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.TimerStatsAction#processTimer(net.digitaltsunami.tmeter.Timer)}
     * .
     * 
     * @throws InterruptedException
     */
    @Test
    public void testProcessTimer() throws InterruptedException {
        // Ensure simple processing works.
        action.processTimer(timer);
        TimerBasicStatistics timerStat = action.getTimerStatistics(TEST_TIMER);
        assertEquals(1, timerStat.getCount());
        assertEquals(timer.getElapsedNanos(), timerStat.getTotalElapsedNanos());

        // Ensure that processTimer is aggregating correctly based on task name.
        Timer timer2 = new Timer(TEST_TIMER);
        timer2.start();
        Thread.sleep(0, 10000);
        timer2.stop();
        action.processTimer(timer2);
        timerStat = action.getTimerStatistics(TEST_TIMER);
        assertEquals(2, timerStat.getCount());
        assertEquals(timer.getElapsedNanos() + timer2.getElapsedNanos(),
                timerStat.getTotalElapsedNanos());

        // Ensure that processTimer differentiates timers based on task name.
        Timer diffTaskTimer = new Timer(TEST_TIMER + "_2");
        diffTaskTimer.start();
        Thread.sleep(0, 30000);
        diffTaskTimer.stop();
        action.processTimer(diffTaskTimer);
        timerStat = action.getTimerStatistics(TEST_TIMER + "_2");
        assertEquals(1, timerStat.getCount());
        assertEquals(diffTaskTimer.getElapsedNanos(), timerStat.getTotalElapsedNanos());

    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.TimerStatsAction#getTimerStatistics(String)}
     * .
     */
    @Test
    public void testGetTimerStatisticsForTask() {
        assertNull("Action should not yet contain the stats instance",
                action.getTimerStatistics(TEST_TIMER));
        action.processTimer(timer);
        assertNotNull("processTimer should have created a TimerBasicStatistics instance",
                action.getTimerStatistics(TEST_TIMER));
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.TimerStatsAction#getTimerStatisticsSnapshot(String)}
     * .
     */
    @Test
    public void testGetTimerStatisticsSnapshotForTask() {
        action.processTimer(timer);

        TimerBasicStatistics live = action.getTimerStatistics(TEST_TIMER);
        TimerBasicStatistics snapshot = action.getTimerStatisticsSnapshot(TEST_TIMER);
        assertEquals(1, live.getCount());
        assertEquals(1, snapshot.getCount());

        action.processTimer(timer);

        assertEquals(2, live.getCount());
        assertEquals(1, snapshot.getCount());
    }
    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.TimerStatsAction#getTimerStatisticsSnapshot(String)}
     * .
     */
    @Test
    public void testGetTimerStatisticsSnapshotForTaskNotFound() {
        TimerBasicStatistics snapshot = action.getTimerStatisticsSnapshot(TEST_TIMER);
        assertNull("Should have returned null as timer has not been processed", snapshot);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.TimerStatsAction#getAllTimerStatistics()}
     * .
     * 
     * @throws InterruptedException
     */
    @Test
    public void testGetAllTimerStatistics() throws InterruptedException {
        // Add two tasks to the stats collection
        assertNull("Action should not yet contain the stats instance",
                action.getTimerStatistics(TEST_TIMER));
        action.processTimer(timer);
        Timer timer2 = new Timer(TEST_TIMER + "_2");
        timer2.start();
        Thread.sleep(0, 30000);
        timer2.stop();
        action.processTimer(timer2);

        Collection<TimerBasicStatistics> allStats = action.getAllTimerStatistics();
        assertEquals(2, allStats.size());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.TimerStatsAction#getAllTimerStatisticsSnapshot()}
     * 
     * @throws InterruptedException
     */
    @Test
    public void testGetAllTimerStatisticsSnapshot() throws InterruptedException {
        // Add two tasks to the stats collection
        action.processTimer(timer);
        Timer timer2 = new Timer(TEST_TIMER + "_2");
        timer2.start();
        Thread.sleep(0, 30000);
        timer2.stop();
        action.processTimer(timer2);

        // Get both live and snapshot collections of the stats.
        Collection<TimerBasicStatistics> allStats = action.getAllTimerStatistics();
        Collection<TimerBasicStatistics> allStatsSnapshot = action.getAllTimerStatisticsSnapshot();

        // Ensure both items returned for snapshot.
        assertEquals(2, allStatsSnapshot.size());

        // Process the timers again to update the stats.
        action.processTimer(timer);
        action.processTimer(timer2);

        // Live stats should have been updated.
        for (TimerBasicStatistics stat : allStats) {
            assertEquals(2, stat.getCount());
        }
        // Snapshot stats should not have been updated.
        for (TimerBasicStatistics stat : allStatsSnapshot) {
            assertEquals(1, stat.getCount());
        }
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.TimerStatsAction#reset()}.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testReset() throws InterruptedException {
        // Ensure simple processing works.
        action.processTimer(timer);

        // Add another timer to make sure that we have a collection that can be
        // cleared.
        Timer diffTaskTimer = new Timer(TEST_TIMER + "_2");
        diffTaskTimer.start();
        Thread.sleep(0, 30000);
        diffTaskTimer.stop();
        action.processTimer(diffTaskTimer);

        assertEquals(2, action.getAllTimerStatistics().size());
        action.reset();
        assertEquals(0, action.getAllTimerStatistics().size());

    }
}
