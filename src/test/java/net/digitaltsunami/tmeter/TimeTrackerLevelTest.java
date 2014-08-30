package net.digitaltsunami.tmeter;

import static org.junit.Assert.*;
import net.digitaltsunami.tmeter.Timer.TimerStatus;
import net.digitaltsunami.tmeter.level.TimerLevelTestData;

import org.junit.Before;
import org.junit.Test;

/**
 * Test TimeTracker in relation to level filtering.
 * <p>
 * Note: These tests use the assertion that {@link TimerShell} will be returned
 * when a timer is not created due to being filtered out. If that changes, then
 * a new means of determining this must be created.
 * 
 * @author dhagberg
 * 
 */
public class TimeTrackerLevelTest {

    private static final String TEST_TASK_NAME = "TEST_TASK";
    @Before
    public void setup() {
        TimeTracker.clearTimerLevels();
    }
     

    @Test
    public void testStartRecordingNoFilterNoLevel() {
        Timer t = TimeTracker.startRecording(TEST_TASK_NAME);
        assertTrue("No filter and no level passed in. Should have recorded timer",isTimeEnabled(t));
    }

    @Test
    public void testStartRecordingNoFilterWithLevel() {
        Timer t = TimeTracker.startRecording(TimerLevelTestData.cat1x, TEST_TASK_NAME);
        assertFalse("No filter, but level passed in. Should not have recorded timer",isTimeEnabled(t));
    }

    @Test
    public void testStartRecordingWithFilterNoLevel() {
        TimeTracker.enableTimerLevel(TimerLevelTestData.cat1x);
        Timer t = TimeTracker.startRecording(TEST_TASK_NAME);
        assertTrue("Filter enabled, but no level passed in. Should have recorded timer",isTimeEnabled(t));
    }

    @Test
    public void testStartRecordingWithFilterWithLevel() {
        TimeTracker.enableTimerLevel(TimerLevelTestData.cat1x);
        Timer t = TimeTracker.startRecording(TimerLevelTestData.cat1x, TEST_TASK_NAME);
        assertTrue("Filter enabled and matching level passed in. Should have recorded timer",isTimeEnabled(t));
    }

    @Test
    public void testStartRecordingWithFilterWithDiffLevel() {
        TimeTracker.enableTimerLevel(TimerLevelTestData.cat1x);
        Timer t = TimeTracker.startRecording(TimerLevelTestData.cat1y, TEST_TASK_NAME);
        assertFalse("Filter enabled and non-matching level passed in. Should not have recorded timer",isTimeEnabled(t));
    }

    @Test
    public void testStartRecordingAddToFilter() {
        TimeTracker.enableTimerLevel(TimerLevelTestData.cat1x);
        Timer t = TimeTracker.startRecording(TimerLevelTestData.cat1y, TEST_TASK_NAME);
        assertFalse("Filter enabled and non-matching level passed in. Should not have recorded timer",isTimeEnabled(t));
        TimeTracker.enableTimerLevel(TimerLevelTestData.cat1y);
        t = TimeTracker.startRecording(TimerLevelTestData.cat1y, TEST_TASK_NAME);
        assertTrue("Filter enabled and matching level passed in. Should have recorded timer",isTimeEnabled(t));
    }

    @Test
    public void testStartRecordingAddMultipleToFilter() {
        TimeTracker.enableTimerLevels(TimerLevelTestData.cat1x, TimerLevelTestData.cat1y);
        Timer t = TimeTracker.startRecording(TimerLevelTestData.cat1x, TEST_TASK_NAME);
        assertTrue("Filter enabled and matching level passed in. Should have recorded timer",isTimeEnabled(t));
        t = TimeTracker.startRecording(TimerLevelTestData.cat1y, TEST_TASK_NAME);
        assertTrue("Filter enabled and matching level passed in. Should have recorded timer",isTimeEnabled(t));
    }

    @Test
    public void testStartRecordingDisableLevel() {
        TimeTracker.enableTimerLevels(TimerLevelTestData.cat1x, TimerLevelTestData.cat1y);
        Timer t = TimeTracker.startRecording(TimerLevelTestData.cat1x, TEST_TASK_NAME);
        assertTrue("Filter enabled and matching level passed in. Should have recorded timer",isTimeEnabled(t));
        t = TimeTracker.startRecording(TimerLevelTestData.cat1y, TEST_TASK_NAME);
        assertTrue("Filter enabled and matching level passed in. Should have recorded timer",isTimeEnabled(t));
        t = TimeTracker.startRecording(TimerLevelTestData.cat1z, TEST_TASK_NAME);
        assertFalse("Filter enabled and non-matching level passed in. Should not have recorded timer",isTimeEnabled(t));
        
        TimeTracker.disableTimerLevel(TimerLevelTestData.cat1y);
        t = TimeTracker.startRecording(TimerLevelTestData.cat1y, TEST_TASK_NAME);
        assertFalse("Level disabled. Should not have recorded timer",isTimeEnabled(t));
        
        t = TimeTracker.startRecording(TimerLevelTestData.cat1x, TEST_TASK_NAME);
        assertTrue("Cat y removed, but Cat x still enabled. Should have recorded timer",isTimeEnabled(t));
        
        TimeTracker.disableTimerLevel(TimerLevelTestData.cat1x);
        t = TimeTracker.startRecording(TimerLevelTestData.cat1x, TEST_TASK_NAME);
        assertFalse("Level disabled. Should not have recorded timer",isTimeEnabled(t));
        
        t = TimeTracker.startRecording(TimerLevelTestData.cat1z, TEST_TASK_NAME);
        assertFalse("All filters disabled. Should not have recorded timer",isTimeEnabled(t));
    }
    
    @Test
    public void testEnableLevelReturnTypes() {
        assertNull("Should have returned null as this timer level was not enabled", TimeTracker.enableTimerLevel(TimerLevelTestData.cat1x));
        assertEquals("Should have returned current value as this timer level was already enabled", TimerLevelTestData.cat1x,TimeTracker.enableTimerLevel(TimerLevelTestData.cat1x));
        assertNull("Should have returned null as this timer level was not enabled", TimeTracker.enableTimerLevel(TimerLevelTestData.normal));
        assertEquals("Should have returned current value as this timer level was already enabled", TimerLevelTestData.normal,TimeTracker.enableTimerLevel(TimerLevelTestData.verbose));
    }

    @Test
    public void testDisableLevelReturnType() {
        assertFalse("Should have returned false as this timer level was not enabled", TimeTracker.disableTimerLevel(TimerLevelTestData.cat1x));
        TimeTracker.enableTimerLevel(TimerLevelTestData.cat1x);
        assertTrue("Should have returned true as this timer level was enabled", TimeTracker.disableTimerLevel(TimerLevelTestData.cat1x));
    }
    @Test
    public void testDisableLevelReturnTypes() {
        TimeTracker.enableTimerLevel(TimerLevelTestData.cat1x);
        TimeTracker.enableTimerLevel(TimerLevelTestData.verbose);
        TimeTracker.enableTimerLevel(TimerLevelTestData.warn);
        assertTrue("Should have removed at least one of the levels", TimeTracker.disableTimerLevels(TimerLevelTestData.cat1x, TimerLevelTestData.verbose));
        Timer t = TimeTracker.startRecording(TimerLevelTestData.cat1x, TEST_TASK_NAME);
        assertFalse("Cat1x disabled. Should not have recorded timer",isTimeEnabled(t));
        t = TimeTracker.startRecording(TimerLevelTestData.verbose, TEST_TASK_NAME);
        assertFalse("Verbose disabled. Should not have recorded timer",isTimeEnabled(t));
        t = TimeTracker.startRecording(TimerLevelTestData.warn, TEST_TASK_NAME);
        assertTrue("Warn was not disabled and should have recorded timer.", isTimeEnabled(t));
    }

    /**
     * @param t
     * @return
     */
    protected boolean isTimeEnabled(Timer t) {
        return !("TimerShell".equals(t.toString()));
    }
}
