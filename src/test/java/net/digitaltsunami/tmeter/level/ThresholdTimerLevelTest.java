package net.digitaltsunami.tmeter.level;

import static org.junit.Assert.*;

import org.junit.Test;

public class ThresholdTimerLevelTest {

    enum Levels {
        TERSE, NORMAL, VERBOSE;
    }

    enum Cats {
        X, Y, Z;
    }

    TimerLevel terse = new ThresholdTimerLevel<Levels>(Levels.TERSE);
    TimerLevel tlb = new ThresholdTimerLevel<Levels>(Levels.NORMAL);
    TimerLevel verbose = new ThresholdTimerLevel<Levels>(Levels.VERBOSE);
    TimerLevel tlx = new ThresholdTimerLevel<Cats>(Cats.X);
    TimerLevel tly = new ThresholdTimerLevel<Cats>(Cats.Y);
    TimerLevel tlz = new ThresholdTimerLevel<Cats>(Cats.Z);

    @Test
    public void testIsEnabledTimerLevel() {
        assertTrue("TERSE higher than NORMAL, should have been true", tlb.isEnabled(terse));
        assertTrue("NORMAL equal to NORMAL, should have been true", tlb.isEnabled(tlb));
        assertFalse("VERBOSE lower than NORMAL, should have been false.", tlb.isEnabled(verbose));
        assertFalse("X is different level type.  Should have been false", tlb.isEnabled(tlx));
        assertFalse("Y is different level type.  Should have been false", tlb.isEnabled(tly));
        assertFalse("Z is different level type.  Should have been false", tlb.isEnabled(tlz));
    }

    @Test
    public void testIsEnabledTimerLevelArray() {
        assertTrue("TERSE is higher than NORMAL and is in the list. Should have been true.",
                tlb.isEnabled(tlz, terse));
        assertFalse("VERBOSE is lower than NORMAL and Z is a different type.  Should have been false.",
                tlb.isEnabled(tlz, verbose));
    }

    @Test
    public void testGetLevelType() {
        assertEquals("Should return threshold type", TimerLevelType.THRESHOLD, tlb.getLevelType());
    }

}
