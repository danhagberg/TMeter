package net.digitaltsunami.tmeter.action;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.TimerBasicStatistics;

import org.junit.Before;
import org.junit.Test;

public class TimerStatsPublisherActionTest {
    private static final String TEST_TIMER_1 = "TEST_TIMER_1";
    private static final String TEST_TIMER_2 = "TEST_TIMER_2";
    TimerStatsPublisherAction pubAction;
    TimerStatsPublisher pub;
    Map<String,AtomicInteger> timerCounts = new HashMap<String, AtomicInteger>();
    protected Collection<TimerBasicStatistics> statAtReset;
    @Before 
    public void setup() {
        pub = new TimerStatsPublisher() {
            
            @Override
            public void reset(Collection<TimerBasicStatistics> stats) {
                statAtReset = stats;
            }
            
            @Override
            public void publish(TimerBasicStatistics stats) {
                AtomicInteger cnt = timerCounts.get(stats.getTaskName());
                if (cnt==null) {
                    timerCounts.put(stats.getTaskName(), new AtomicInteger(1));
                }
                else {
                    cnt.incrementAndGet();
                }
            }
        };
        pubAction = new TimerStatsPublisherAction(pub);
    }

    @Test
    public void testProcessTimer() {
        Timer t = null;
        t = new Timer(TEST_TIMER_1);
        t.stop();
        pubAction.timerComplete(t);
        assertEquals("Publisher should have been called once for timer1", 1, timerCounts.get(TEST_TIMER_1).get());
        t = new Timer(TEST_TIMER_1);
        t.stop();
        pubAction.timerComplete(t);
        assertEquals("Publisher should have been called twice for timer1", 2, timerCounts.get(TEST_TIMER_1).get());
        t = new Timer(TEST_TIMER_2);
        t.stop();
        pubAction.timerComplete(t);
        assertEquals("Publisher should have been called once for timer2", 1, timerCounts.get(TEST_TIMER_2).get());
        assertEquals("Publisher should have remained unchanged for timer1", 2, timerCounts.get(TEST_TIMER_1).get());
    }

    @Test
    public void testReset() {
        Timer t = null;
        t = new Timer(TEST_TIMER_1);
        t.stop();
        pubAction.timerComplete(t);
        t = new Timer(TEST_TIMER_1);
        t.stop();
        pubAction.timerComplete(t);
        t = new Timer(TEST_TIMER_2);
        t.stop();
        pubAction.timerComplete(t);
        pubAction.reset();
        assertEquals("Stats should have entries for 2 timers", 2, statAtReset.size());
    }

}
