package net.digitaltsunami.tmeter;

import static org.junit.Assert.*;

import org.junit.Test;

public class TimeTrackerInstanceTest {

    private static final String TEST_NAME_1 = "TEST_TT_1";
    private static final String TEST_NAME_2 = "TEST_TT_2";

    @Test
    public void testNamedTimeTracker() {
        NamedTimeTracker timeTracker = TimeTracker.named(TEST_NAME_1);
        assertSame("Should have been the same instance",  timeTracker, TimeTracker.named(TEST_NAME_1));
    }

    @Test
    public void testNamedTimeTrackerDifferentName() {
        NamedTimeTracker timeTracker = TimeTracker.named(TEST_NAME_1);
        assertSame("Should have been the same instance",  timeTracker, TimeTracker.named(TEST_NAME_1));
        assertNotSame("Should not have been the same instance",  timeTracker, TimeTracker.named(TEST_NAME_2));
    }
    
    @Test
    public void testNamedTimeTrackerSetValue() {
        TimeTracker.named(TEST_NAME_1).setKeepList(false);
        TimeTracker.named(TEST_NAME_2).setKeepList(false);
        assertFalse(TimeTracker.named(TEST_NAME_1).isKeepList());
        assertFalse(TimeTracker.named(TEST_NAME_2).isKeepList());
        TimeTracker.named(TEST_NAME_1).setKeepList(true);
        assertTrue("Value should have been set",  TimeTracker.named(TEST_NAME_1).isKeepList());
        assertFalse("Value should not have been set",  TimeTracker.named(TEST_NAME_2).isKeepList());
    }

}
