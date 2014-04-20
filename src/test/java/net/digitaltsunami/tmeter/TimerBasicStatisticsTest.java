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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test exercises the logic within {@link TimerBasicStatistics}. The tests
 * within the class rely on a CSV file containing timer entries. The file must
 * be named basicStatsTestTimers.csv and be available in the class path. The
 * times are for the "Query" task and have the following times in nanoseconds:
 * <ul>
 * <li>5,000,000 (5 milliseconds)</li>
 * <li>10,000,000 (10 milliseconds)</li>
 * <li>15,000,000 (15 milliseconds)</li>
 * </ul>
 * 
 * @author dhagberg
 * 
 */
public class TimerBasicStatisticsTest {

    private final static String TASK_NAME = "Query";
    private static long totalElapsedNanos;
    private static long minElapsedNanos = Long.MAX_VALUE;
    private static long maxElapsedNanos = Long.MIN_VALUE;
    private static TimerBasicStatistics stats;
    private static int timerCount;

    public static List<Timer> timers;

    @BeforeClass
    public static void createTimersFromCsv() throws IOException {
        BufferedReader timerCsv = new BufferedReader(new InputStreamReader(
                ClassLoader.getSystemResourceAsStream("basicStatsTestTimers.csv")));

        timers = new ArrayList<Timer>();
        stats = new TimerBasicStatistics(TASK_NAME);

        String line;
        line = timerCsv.readLine(); // skip past header
        while ((line = timerCsv.readLine()) != null) {
            Timer t = Timer.fromCsv(line);
            timers.add(t);
            if (TASK_NAME.equals(t.getTaskName())) {
                totalElapsedNanos += t.getElapsedNanos();
                minElapsedNanos = Math.min(minElapsedNanos, t.getElapsedNanos());
                maxElapsedNanos = Math.max(maxElapsedNanos, t.getElapsedNanos());
                stats.addTimer(t);
                timerCount++;
            }
        }
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#hashCode()}.
     */
    @Test
    public void testHashCode() {
        assertNotSame(0, stats.hashCode());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#TimerBasicStatistics(java.lang.String)}
     * .
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
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#TimerBasicStatistics(net.digitaltsunami.tmeter.Timer)}
     * .
     * 
     * @throws InterruptedException
     */
    @Test
    public void testTimerBasicStatisticsTimerConstructor() throws InterruptedException {
        Timer t = timers.get(1);
        TimerBasicStatistics testStat = new TimerBasicStatistics(t);
        assertEquals(t.getTaskName(), testStat.getTaskName());
        assertEquals(1, testStat.getCount());
        assertEquals(t.getElapsedNanos(), testStat.getTotalElapsedNanos());
        assertEquals(t.getElapsedNanos(), testStat.getMinElapsedNanos());
        assertEquals(t.getElapsedNanos(), testStat.getMaxElapsedNanos());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#addTimer(net.digitaltsunami.tmeter.Timer)}
     * .
     * 
     * @throws InterruptedException
     */
    @Test
    public void testAddTimer() throws InterruptedException {
        Timer t = timers.get(1);
        TimerBasicStatistics testStat = new TimerBasicStatistics(t.getTaskName());
        testStat.addTimer(t);
        assertEquals(1, testStat.getCount());
        assertEquals(t.getElapsedNanos(), testStat.getTotalElapsedNanos());
        assertEquals(t.getElapsedNanos(), testStat.getMinElapsedNanos());
        assertEquals(t.getElapsedNanos(), testStat.getMaxElapsedNanos());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getAverageElapsedNanos()}
     * and
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getAverageElapsed(java.util.concurrent.TimeUnit)}
     * 
     * @throws InterruptedException
     */
    @Test
    public void testGetAverageElapsed() throws InterruptedException {
        assertEquals(10000000, stats.getAverageElapsedNanos(), 0.1);
        assertEquals(10, stats.getAverageElapsed(TimeUnit.MILLISECONDS), 0.1);

        // Test that average works correctly with 0 entries.
        TimerBasicStatistics testStat = new TimerBasicStatistics(TASK_NAME);
        assertEquals(0.0, testStat.getAverageElapsedNanos(), 0.0);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getStdDevElapsedNanos()}
     * and
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getStdDevElapsed(java.util.concurrent.TimeUnit)}
     * 
     * @throws InterruptedException
     */
    @Test
    public void testGetStdDevElapsed() throws InterruptedException {
        assertEquals(5000000.0, stats.getStdDevElapsedNanos(), 0.1);
        assertEquals(5.0, stats.getStdDevElapsed(TimeUnit.MILLISECONDS), 0.1);

        TimerBasicStatistics testStat = new TimerBasicStatistics(TASK_NAME);

        // Test that average works correctly with 0 entries.
        assertEquals(0.0, testStat.getStdDevElapsedNanos(), 0.0);

        // Test that average works correctly with 1 entries.
        testStat.addTimer(timers.get(1));
        assertEquals(0.0, testStat.getStdDevElapsedNanos(), 0.0);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getVarianceElapsedNanos()}
     * and
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getVarianceElapsed(java.util.concurrent.TimeUnit)}
     * 
     * @throws InterruptedException
     */
    @Test
    public void testGetVarianceElapsed() throws InterruptedException {
        // Squaring expected std deviation
        assertEquals(5000000.0 * 5000000.0, stats.getVarianceElapsedNanos(), 0.1);
        assertEquals(25000000.0, stats.getVarianceElapsed(TimeUnit.MILLISECONDS), 0.1);

        TimerBasicStatistics testStat = new TimerBasicStatistics(TASK_NAME);

        // Test that average works correctly with 0 entries.
        assertEquals(0.0, testStat.getVarianceElapsedNanos(), 0.0);

        // Test that average works correctly with 1 entries.
        testStat.addTimer(timers.get(1));
        assertEquals(0.0, testStat.getVarianceElapsedNanos(), 0.0);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getCount()}.
     */
    @Test
    public void testGetCount() {
        assertEquals(timerCount, stats.getCount());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getTotalElapsedNanos()}
     * and
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getTotalElapsed(java.util.concurrent.TimeUnit)}
     */
    @Test
    public void testGetTotalElapsed() {
        assertEquals(totalElapsedNanos, stats.getTotalElapsedNanos());
        assertEquals(totalElapsedNanos / 1000000, stats.getTotalElapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getMinElapsedNanos()}
     * and
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getMinElapsed(java.util.concurrent.TimeUnit)}
     */
    @Test
    public void testGetMinElapsed() {
        assertEquals(minElapsedNanos, stats.getMinElapsedNanos());
        assertEquals(minElapsedNanos / 1000000, stats.getMinElapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getMaxElapsedNanos()}
     * and
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getMaxElapsed(java.util.concurrent.TimeUnit)}
     */
    @Test
    public void testGetMaxElapsed() {
        assertEquals(maxElapsedNanos, stats.getMaxElapsedNanos());
        assertEquals(maxElapsedNanos / 1000000, stats.getMaxElapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#compareTo(net.digitaltsunami.tmeter.TimerBasicStatistics)}
     * .
     */
    @Test
    public void testCompareTo() {
        TimerBasicStatistics testStats = new TimerBasicStatistics("A_TASK");
        assertTrue("A_TASK should be less than TEST_TASK", (testStats.compareTo(stats) < 0));
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject() {
        TimerBasicStatistics testStats = new TimerBasicStatistics(TASK_NAME);
        assertTrue("TEST_TASK should be equal than TEST_TASK", (stats.equals(testStats)));
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerBasicStatistics#getSnapshot()}.
     */
    @Test
    public void testGetSnapshot() {
        TimerBasicStatistics snapshot = stats.getSnapshot();
        assertNotSame("Snapshot is same object", stats, snapshot);
        assertEquals(stats.getTaskName(), snapshot.getTaskName());
        assertEquals(stats.getTotalElapsedNanos(), snapshot.getTotalElapsedNanos());
        assertEquals(stats.getMinElapsedNanos(), snapshot.getMinElapsedNanos());
        assertEquals(stats.getMaxElapsedNanos(), snapshot.getMaxElapsedNanos());
        assertEquals(stats.getAverageElapsedNanos(), snapshot.getAverageElapsedNanos(), .1);
        assertEquals(stats.getStdDevElapsedNanos(), snapshot.getStdDevElapsedNanos(), .1);
        assertEquals(stats.getVarianceElapsedNanos(), snapshot.getVarianceElapsedNanos(), .1);
    }
    
    /**
     * Simple test to ensure that toString does not fail
     */
    @Test
    public void testToString() {
        assertNotNull(stats.toString());
    }
}
