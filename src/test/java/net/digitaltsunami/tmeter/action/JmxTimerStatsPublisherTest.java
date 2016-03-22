package net.digitaltsunami.tmeter.action;

import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.TimerBasicStatistics;
import net.digitaltsunami.tmeter.action.jmx.JmxTimerStats;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.sun.tools.internal.xjc.reader.gbind.Expression.EPSILON;
import static org.junit.Assert.*;

/**
 * Created by dhagberg on 3/19/16.
 */
public class JmxTimerStatsPublisherTest {

    private static final String TEST_TIMER_1 = "TEST_TIMER_1";
    private static final String TEST_TIMER_2 = "TEST_TIMER_2";
    private static final double EPSILON = 0.05;


    @Test
    public void testPublish() throws Exception {
        JmxTimerStatsPublisher pub = new JmxTimerStatsPublisher();
        TimerStatsPublisherAction pubAction = new TimerStatsPublisherAction(pub);
        Timer timer = new Timer(TEST_TIMER_1);
        timer.start();
        Thread.sleep(2);
        timer.stop();
        pubAction.processTimer(timer);
    }

    @Test
    public void testGetStatsBean() {
        JmxTimerStatsPublisher pub = new JmxTimerStatsPublisher();
        TimerBasicStatistics stats1 = new TimerBasicStatistics(TEST_TIMER_1);
        TimerBasicStatistics statsSameName = new TimerBasicStatistics(TEST_TIMER_1);
        TimerBasicStatistics stats2 = new TimerBasicStatistics(TEST_TIMER_2);

        JmxTimerStats bean1 = pub.getStatsBean(stats1);
        assertNotNull("Failed to create non-existing bean", bean1);

        JmxTimerStats cachedBean1 = pub.getStatsBean(stats1);
        assertNotNull("Failed to return existing bean", cachedBean1);
        assertSame("Failed to return same bean for task name", bean1, cachedBean1);

        cachedBean1 = pub.getStatsBean(statsSameName);
        assertSame("Failed to return same bean for task name", bean1, cachedBean1);

        JmxTimerStats bean2 = pub.getStatsBean(stats2);
        assertNotSame("Returned same bean for different task name", bean1, bean2);
    }

    @Test
    public void testReset() {
        JmxTimerStatsPublisher pub = new JmxTimerStatsPublisher();
        TimerBasicStatistics stats1 = new TimerBasicStatistics(TEST_TIMER_1);
        TimerBasicStatistics stats2 = new TimerBasicStatistics(TEST_TIMER_2);

        // These tests assume that caching works
        JmxTimerStats bean1 = pub.getStatsBean(stats1);
        assertNotNull("Failed to create existing bean", bean1);
        JmxTimerStats bean2 = pub.getStatsBean(stats2);
        assertNotNull("Failed to create existing bean", bean2);
        pub.reset(Arrays.asList(stats1, stats2));
        assertNotSame("Should have created new bean", bean1, pub.getStatsBean(stats1));
        assertNotSame("Should have created new bean", bean2, pub.getStatsBean(stats2));
    }

    @Test
    public void testPublishValues() throws InterruptedException {
        JmxTimerStatsPublisher pub = new JmxTimerStatsPublisher();
        Timer t = new Timer(TEST_TIMER_1);
        t.start();
        Thread.sleep(2);
        t.stop();
        TimerBasicStatistics stats1 = new TimerBasicStatistics(t);
        pub.publish(stats1);
        JmxTimerStats bean = pub.getStatsBean(stats1);
        assertEquals(stats1.getCount(), bean.getCount());
        assertEquals(stats1.getMaxElapsed(TimeUnit.MILLISECONDS), bean.getMaxElapsedMillis());
        assertEquals(stats1.getMinElapsed(TimeUnit.MILLISECONDS), bean.getMinElapsedMillis());
        assertEquals(stats1.getAverageElapsed(TimeUnit.MILLISECONDS), bean.getMeanElapsedMillis(), EPSILON);
        assertEquals(stats1.getStdDevElapsed(TimeUnit.MILLISECONDS), bean.getStdDevElapsedMillis(), EPSILON);
    }
}