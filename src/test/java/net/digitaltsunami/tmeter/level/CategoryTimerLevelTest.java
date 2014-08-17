package net.digitaltsunami.tmeter.level;

import static org.junit.Assert.*;
import static net.digitaltsunami.tmeter.level.TimerLevelTestData.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class CategoryTimerLevelTest {

    @Test
    public void testIsEnabledTimerLevel() {
        assertFalse("Normal is different type than cat1y, should have been true",
                cat1y.isEnabled(normal));
        assertFalse("Cat1x is not equal to cat1y.  Should have been false", cat1y.isEnabled(cat1x));
        assertTrue("Cat1y is equal to cat1y .  Should have been true", cat1y.isEnabled(cat1y));
        assertFalse("Cat1z is not equal to cat1z.  Should have been false", cat1y.isEnabled(cat1z));
    }

    @Test
    public void testIsEnabledDifferentGroup() {
        assertFalse("CTL.A is different level type.  Should have been false",
                normal.isEnabled(CTL.A));
    }

    @Test
    public void testIsEnabledTimerLevelArray() {
        assertTrue("cat1y is equal to cat1y and is in the list. Should have been true.",
                cat1y.isEnabled(cat1y, normal));
        assertFalse("cat1z is not cat1y and and terse is a different type",
                cat1y.isEnabled(cat1z, terse));
    }
    
    @Test
    public void testHash() {
        Set<TimerLevel> levels = new HashSet<TimerLevel>();
        CategoryTimerLevel<Cat1> cat1X1  = new CategoryTimerLevel<Cat1>(Cat1.X);
        CategoryTimerLevel<Cat1> cat1X2  = new CategoryTimerLevel<Cat1>(Cat1.X);
        levels.add(cat1X1);
        assertTrue("Hash code was different", levels.contains(cat1X2));
    }

    @Test
    public void testEquals() {
        CategoryTimerLevel<Cat1> cat1X1  = new CategoryTimerLevel<Cat1>(Cat1.X);
        CategoryTimerLevel<Cat1> cat1X2  = new CategoryTimerLevel<Cat1>(Cat1.X);
        assertEquals(cat1X1, cat1X2);
        assertFalse(cat1X1.equals(null));
        assertFalse(cat1X1.equals(CTL.A));
        assertFalse(cat1X1.equals(warn));
    }

    @Test
    public void testGetLevelType() {
        assertEquals("Should return Category type", TimerLevelType.CATEGORY, cat1y.getLevelType());
    }

    @Test
    public void testGetGroup() {
        assertEquals("Should return enum backing level", CTL.class, CTL.A.getGroup());
    }

    public enum CTL implements TimerLevel {
        A(),
        B();

        CTL() {
            timerLevel = new CategoryTimerLevel<CTL>(this);
        }

        CategoryTimerLevel<CTL> timerLevel;

        public boolean isEnabled(TimerLevel level) {
            return timerLevel.isEnabled(level);
        }

        public boolean isEnabled(TimerLevel... levels) {
            return timerLevel.isEnabled(levels);
        }

        public TimerLevelType getLevelType() {
            return timerLevel.getLevelType();
        }

        public Object getGroup() {
            return timerLevel.getGroup();
        }
    }

}
