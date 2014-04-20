package net.digitaltsunami.tmeter.record;

import static org.junit.Assert.*;

import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.action.ActionChainShutdownType;

import org.junit.Test;

public class QueuedTimeRecorderTest {

    @Test
    public void testRecord() throws InterruptedException {
        CountTimeRecorder ctr = new CountTimeRecorder();
        QueuedTimeRecorder qtr = new QueuedTimeRecorder(ctr);
        Timer t = new Timer("TestQueuedTimeRecorder");
        t.setTimeRecorder(qtr);
        t.start();
        t.stop();
        Thread.sleep(100);
        assertEquals(1, ctr.getCount());
    }

    @Test
    public void testShutdown() throws InterruptedException {
        CountTimeRecorder ctr = new CountTimeRecorder();
        QueuedTimeRecorder qtr = new QueuedTimeRecorder(ctr);
        Timer t = new Timer("TestQueuedTimeRecorder");
        t.setTimeRecorder(qtr);
        t.start();
        t.stop();
        Thread.sleep(100);
        qtr.shutdown();
        assertEquals(1, ctr.getCount());
        qtr.shutdown();
        t = new Timer("TestQueuedTimeRecorder");
        t.setTimeRecorder(qtr);
        t.start();
        t.stop();
        Thread.sleep(100);
        // Should not have recorded the second timer.
        assertEquals(1, ctr.getCount());
    }
    
    @Test
    public void testShutdownOption() throws InterruptedException {
        CountTimeRecorder ctr = new CountTimeRecorder();
        QueuedTimeRecorder qtr = new QueuedTimeRecorder(ctr, ActionChainShutdownType.TERMINATE_AFTER_COMPLETION);
        Timer t = new Timer("TestQueuedTimeRecorder");
        t.setTimeRecorder(qtr);
        t.start();
        t.stop();
        Thread.sleep(100);
        qtr.shutdown();
        assertEquals(1, ctr.getCount());
        qtr.shutdown();
        t = new Timer("TestQueuedTimeRecorder");
        t.setTimeRecorder(qtr);
        t.start();
        t.stop();
        Thread.sleep(100);
        // Should not have recorded the second timer.
        assertEquals(1, ctr.getCount());
    }

}
