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

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

/**
 * @author dhagberg
 *
 */
public class TimerBasicStatisticsTest {

    private final static String TASK_NAME = "TEST_TASK";
    private long totalElapsedNanos;
    private long minElapsedNanos = Long.MAX_VALUE;
    private long maxElapsedNanos = Long.MIN_VALUE;
    private TimerBasicStatistics stats;
    private final static int NBR_TIMERS = 3; 
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        stats = new TimerBasicStatistics(TASK_NAME);
        Timer t; 
        for (int i = 0; i < NBR_TIMERS; i++) {
            t = new Timer(TASK_NAME, false, TimerLogType.NONE);
            Thread.sleep(1);
	        t.stop();
	        stats.addTimer(t);
	        totalElapsedNanos += t.getElapsedNanos();
	        minElapsedNanos = Math.min(minElapsedNanos, t.getElapsedNanos());
	        maxElapsedNanos = Math.max(maxElapsedNanos, t.getElapsedNanos());
        }
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerBasicStatistics#hashCode()}.
     */
    @Test
    public void testHashCode() {
        assertNotSame(0, stats.hashCode());
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerBasicStatistics#TimerBasicStatistics(java.lang.String)}.
     */
    @Test
    public void testTimerBasicStatisticsTaskNameConstructor() {
        TimerBasicStatistics testStat = new TimerBasicStatistics(TASK_NAME);
        assertEquals(TASK_NAME, testStat.getTaskName());
        assertEquals(0, testStat.getCount());
        assertEquals(0, testStat.getTotalElapsedNanos());
        assertEquals(Long.MAX_VALUE, testStat.getMinElapsedNanos());
        assertEquals(Long.MIN_VALUE, testStat.getMaxElapsedNanos());
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerBasicStatistics#TimerBasicStatistics(net.digitaltsunami.tmeter.Timer)}.
     * @throws InterruptedException 
     */
    @Test
    public void testTimerBasicStatisticsTimerConstructor() throws InterruptedException {
        Timer t = new Timer(TASK_NAME);
        Thread.sleep(0,50000);
        t.stop();
        TimerBasicStatistics testStat = new TimerBasicStatistics(t);
        assertEquals(TASK_NAME, testStat.getTaskName());
        assertEquals(1, testStat.getCount());
        assertEquals(t.getElapsedNanos(), testStat.getTotalElapsedNanos());
        assertEquals(t.getElapsedNanos(), testStat.getMinElapsedNanos());
        assertEquals(t.getElapsedNanos(), testStat.getMaxElapsedNanos());
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerBasicStatistics#addTimer(net.digitaltsunami.tmeter.Timer)}.
     * @throws InterruptedException 
     */
    @Test
    public void testAddTimer() throws InterruptedException {
        Timer t = new Timer(TASK_NAME);
        Thread.sleep(0,50000);
        t.stop();
        TimerBasicStatistics testStat = new TimerBasicStatistics(TASK_NAME);
        testStat.addTimer(t);
        assertEquals(1, testStat.getCount());
        assertEquals(t.getElapsedNanos(), testStat.getTotalElapsedNanos());
        assertEquals(t.getElapsedNanos(), testStat.getMinElapsedNanos());
        assertEquals(t.getElapsedNanos(), testStat.getMaxElapsedNanos());
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getAverageElapsedNanos()} and
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getAverageElapsed(java.util.concurrent.TimeUnit)}
     * @throws InterruptedException 
     */
    @Test
    public void testGetAverageElapsed() throws InterruptedException {
        assertEquals(totalElapsedNanos/NBR_TIMERS, stats.getAverageElapsedNanos(), 0.1);
        assertEquals((totalElapsedNanos/1000000/NBR_TIMERS), stats.getAverageElapsed(TimeUnit.MILLISECONDS), 0.1);
        
        
        // Test that average works correctly with 0 entries.
        TimerBasicStatistics testStat = new TimerBasicStatistics(TASK_NAME);
        assertEquals(0.0, testStat.getAverageElapsedNanos(), 0.0);
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getCount()}.
     */
    @Test
    public void testGetCount() {
        assertEquals(NBR_TIMERS, stats.getCount());
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getTotalElapsedNanos()} and
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getTotalElapsed(java.util.concurrent.TimeUnit)}
     */
    @Test
    public void testGetTotalElapsed() {
        assertEquals(totalElapsedNanos, stats.getTotalElapsedNanos());
        assertEquals(totalElapsedNanos/1000000, stats.getTotalElapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getMinElapsedNanos()} and
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getMinElapsed(java.util.concurrent.TimeUnit)}
     */
    @Test
    public void testGetMinElapsed() {
        assertEquals(minElapsedNanos, stats.getMinElapsedNanos());
        assertEquals(minElapsedNanos/1000000, stats.getMinElapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getMaxElapsedNanos()} and
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getMaxElapsed(java.util.concurrent.TimeUnit)}
     */
    @Test
    public void testGetMaxElapsed() {
        assertEquals(maxElapsedNanos, stats.getMaxElapsedNanos());
        assertEquals(maxElapsedNanos/1000000, stats.getMaxElapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerBasicStatistics#compareTo(net.digitaltsunami.tmeter.TimerBasicStatistics)}.
     */
    @Test
    public void testCompareTo() {
        TimerBasicStatistics testStats = new TimerBasicStatistics("A_TASK");
        assertTrue("A_TASK should be less than TEST_TASK", (testStats.compareTo(stats) < 0));
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerBasicStatistics#equals(java.lang.Object)}.
     */
    @Test
    public void testEqualsObject() {
        TimerBasicStatistics testStats = new TimerBasicStatistics(TASK_NAME);
        assertTrue("TEST_TASK should be equal than TEST_TASK", (stats.equals(testStats)));
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getSnapshot()}.
     */
    @Test
    public void testGetSnapshot() {
        TimerBasicStatistics snapshot = stats.getSnapshot();
        assertNotSame("Snapshot is same object", stats, snapshot);
        assertEquals(stats.getTaskName(), snapshot.getTaskName());
        assertEquals(stats.getTotalElapsedNanos(), snapshot.getTotalElapsedNanos());
        assertEquals(stats.getMinElapsedNanos(), snapshot.getMinElapsedNanos());
        assertEquals(stats.getMaxElapsedNanos(), snapshot.getMaxElapsedNanos());
    }
}
