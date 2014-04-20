package net.digitaltsunami.tmeter.action;

import static org.junit.Assert.*;

import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.record.CountTimeRecorder;

import org.junit.Test;

public class TimeRecorderActionTest {

    @Test
    public void testProcessTimer() {
        CountTimeRecorder tr = new CountTimeRecorder();
        TimeRecorderAction tra = new TimeRecorderAction(tr);
        Timer timer = new Timer("test");
        tra.processTimer(timer);
        assertEquals(1, tr.getCount());
    }

}
