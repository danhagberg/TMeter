package net.digitaltsunami.tmeter.level;

import static org.junit.Assert.*;
import net.digitaltsunami.tmeter.test.EnumUtils;

import org.junit.Test;

public class TimerThresholdTest {

    @Test
    public void testIsEnabledTimerLevel() {
        assertTrue("Medium equal to medium. Should have been enabled",
                TimerThreshold.MEDIUM.isEnabled(TimerThreshold.MEDIUM));
        assertTrue("Course more verbose than medium. Should have been enabled",
                TimerThreshold.MEDIUM.isEnabled(TimerThreshold.COURSE));
        assertFalse("Fine less verbose than medium. Should have been disabled",
                TimerThreshold.MEDIUM.isEnabled(TimerThreshold.FINE));
    }

    @Test
    public void testIsEnabledTimerLevelArray() {
        assertTrue("Medium equal to medium. Should have been enabled",
                TimerThreshold.MEDIUM.isEnabled(TimerThreshold.MEDIUM, TimerThreshold.FINE));
        assertTrue("Course more verbose than medium. Should have been enabled",
                TimerThreshold.MEDIUM.isEnabled(TimerThreshold.COURSE, TimerThreshold.FINE));
        assertFalse("Fine less verbose than medium. Should have been disabled",
                TimerThreshold.MEDIUM.isEnabled(TimerThreshold.FINE, TimerThreshold.FINE));
    }

    @Test
    public void testGetLevelType() {
        assertEquals("Should return threshold type", TimerLevelType.THRESHOLD, TimerThreshold.FINE.getLevelType());
        assertEquals("Should return threshold type", TimerLevelType.THRESHOLD, TimerThreshold.MEDIUM.getLevelType());
        assertEquals("Should return threshold type", TimerLevelType.THRESHOLD, TimerThreshold.COURSE.getLevelType());
        
    }

    @Test
    public void testGetGroup() {
        assertEquals("Group should be equal to TimerLevel class", TimerThreshold.class, TimerThreshold.FINE.getGroup());
        assertEquals("Group should be equal to TimerLevel class", TimerThreshold.class, TimerThreshold.MEDIUM.getGroup());
        assertEquals("Group should be equal to TimerLevel class", TimerThreshold.class, TimerThreshold.COURSE.getGroup());
    }
    
    @Test
    public void completeCoverage() {
        EnumUtils.superficialEnumCodeCoverage(TimerThreshold.class);
    }

}
