package net.digitaltsunami.tmeter.level;

import static org.junit.Assert.*;
import static net.digitaltsunami.tmeter.level.TimerLevelTestData.*;

import org.junit.Test;

public class TimerLevelSetTest {

    @Test
    public void testIsEnabledTimerLevel() {
        TimerLevelCollection tls = new TimerLevelSet(normal, cat1y);
        assertTrue("terse higher than normal and normal is in the set. Should have been true",
                tls.isEnabled(terse));
        assertTrue("normal is in the set. Should have been true", tls.isEnabled(normal));
        assertFalse("verbose lower than normal, should have been false.", tls.isEnabled(verbose));
        assertFalse("X is not in the set.  Should have been false", tls.isEnabled(cat1x));
        assertTrue("Y is in the set.  Should have been true", tls.isEnabled(cat1y));
        assertFalse("Z is not in the set.  Should have been false", tls.isEnabled(cat1z));
    }

    @Test
    public void testIsEnabledTimerLevelArray() {
        TimerLevelCollection tls = new TimerLevelSet(normal, cat1y);
        assertTrue("terse is higher than normal and is in the set. Should have been true.",
                tls.isEnabled(cat1z, terse));
        assertTrue("verbose is lower than normal, but Y is in the set.   Should have been true.",
                tls.isEnabled(cat1y, verbose));
        assertFalse(
                "verbose is lower than normal and Z is not in the set.   Should have been false.",
                tls.isEnabled(cat1z, verbose));
    }

    @Test
    public void testAddLevelSet() {
        TimerLevelSet tlset = new TimerLevelSet(normal, cat1y);
        TimerLevelCollection tls = new TimerLevelSet(cat1z);
        tls.addLevel(tlset);
        assertTrue("terse is higher than normal and normal is in the set. Should have been true",
                tls.isEnabled(terse));
        assertTrue("normal is in the set. Should have been true", tls.isEnabled(normal));
        assertFalse("verbose lower than normal, should have been false.", tls.isEnabled(verbose));
        assertFalse("X is not in the set.  Should have been false", tls.isEnabled(cat1x));
        assertTrue("Y is in the set.  Should have been true", tls.isEnabled(cat1y));
        assertTrue("Z is in the set.  Should have been true", tls.isEnabled(cat1z));
    }

    @Test
    public void testAddLevelArray() {
        TimerLevelSet tls = new TimerLevelSet(normal, cat1y);
        tls.addLevels(cat1x, cat1z);
        assertTrue("terse is higher than normal and normal is in the set. Should have been true",
                tls.isEnabled(terse));
        assertTrue("normal is in the set. Should have been true", tls.isEnabled(normal));
        assertFalse("verbose lower than normal, should have been false.", tls.isEnabled(verbose));
        assertTrue("X is in the set.  Should have been true", tls.isEnabled(cat1x));
        assertTrue("Y is in the set.  Should have been true", tls.isEnabled(cat1y));
        assertTrue("Z is in the set.  Should have been true", tls.isEnabled(cat1z));
    }

    @Test
    public void testAddLevelArrayCheckForExists() {
        TimerLevelSet tls = new TimerLevelSet(cat1y);
        assertTrue("Set did not already include X, should have been true", tls.addLevels(cat1x));
        assertFalse("Set did already include Y, should have been false", tls.addLevels(cat1y));
    }

    @Test
    public void testAddThresholdLevelSameGroup() {
        // Adding multiple to threshold levels to recreate ConcurrentModificationException
        TimerLevelCollection tls = new TimerLevelSet(warn, terse);
        assertTrue("warn is in the set.  Should have been true", tls.isEnabled(warn));
        assertFalse("info is below warn.  Should have been false", tls.isEnabled(info));
        assertTrue("terse is in the set.  Should have been true", tls.isEnabled(terse));
        assertFalse("verbose is below verbose.  Should have been false", tls.isEnabled(verbose));
        tls.addLevel(info);
        tls.addLevel(verbose);
        assertTrue("warn above info.  Should have been true", tls.isEnabled(warn));
        assertTrue("info is in the set.  Should have been true", tls.isEnabled(info));
        assertTrue("terse above verbose.  Should have been true", tls.isEnabled(terse));
        assertTrue("verbose is in the set.  Should have been true", tls.isEnabled(verbose));
    }

    @Test
    public void testAddThresholdLevelDifferentGroup() {
        TimerLevelCollection tls = new TimerLevelSet(warn);
        assertTrue("warn is in the set.  Should have been true", tls.isEnabled(warn));
        assertFalse("normal is not in the set.  Should have been false", tls.isEnabled(normal));
        tls.addLevel(normal);
        assertTrue("warn is in the set.  Should have been true", tls.isEnabled(warn));
        assertTrue("normal is in the set.  Should have been true", tls.isEnabled(normal));
    }

    @Test
    public void testAddCategoryLevelDifferentGroup() {
        TimerLevelCollection tls = new TimerLevelSet(cat1y);
        assertTrue("cat1y is in the set.  Should have been true", tls.isEnabled(cat1y));
        assertFalse("cat2y is not in the set.  Should have been false", tls.isEnabled(cat2y));
        tls.addLevel(cat2y);
        assertTrue("cat1y is in the set.  Should have been true", tls.isEnabled(cat1y));
        assertTrue("cat2y is in the set.  Should have been true", tls.isEnabled(cat2y));
    }

    @Test
    public void testRemoveThresholdLevel() {
        TimerLevelCollection tls = new TimerLevelSet(warn);
        assertTrue("warn is in the set.  Should have been true", tls.isEnabled(warn));
        assertTrue("severe is greater than warn.  Should have been true", tls.isEnabled(severe));
        assertFalse("info is less than warn.  Should have been false", tls.isEnabled(info));
        assertFalse("normal is not in the set.  Should have been false", tls.isEnabled(normal));
        tls.addLevel(normal);
        assertTrue("warn is in the set.  Should have been true", tls.isEnabled(warn));
        assertTrue("normal is in the set.  Should have been true", tls.isEnabled(normal));
        tls.removeLevel(warn);
        assertFalse(
                "severe is greater than warn, but warn is no longer in set.  Should have been false",
                tls.isEnabled(severe));
        assertFalse("warn is not in the set.  Should have been false", tls.isEnabled(warn));
        assertTrue("normal is in the set.  Should have been true", tls.isEnabled(normal));
    }

    @Test
    public void testRemoveCategoryLevel() {
        TimerLevelSet tlset = new TimerLevelSet(cat1y, cat2y);
        assertTrue("cat1y is in the set and should have been true", tlset.isEnabled(cat1y));
        assertTrue("cat2y is in the set and should have been true", tlset.isEnabled(cat2y));
        tlset.removeLevel(cat1y);
        assertFalse("cat1y is removed from the set and should have been false",
                tlset.isEnabled(cat1y));
        assertTrue("cat2y is in the set and should have been true", tlset.isEnabled(cat2y));
    }

    @Test
    public void testRemoveCategoryLevelSameBackingEnum() {
        TimerLevelSet tlset = new TimerLevelSet(cat1z, cat3z_a, cat3z_b);
        assertTrue("cat1z is in the set and should have been true", tlset.isEnabled(cat1z));
        assertTrue("cat3z_a is in the set and should have been true", tlset.isEnabled(cat3z_a));
        assertTrue("cat3z_b is in the set and should have been true", tlset.isEnabled(cat3z_b));
        assertEquals("Size of set should be 2 as cat3z_a and _b are the same level", 2,
                tlset.getLevels().size());
        tlset.removeLevel(cat3z_a);
        assertEquals("Size of set should be 1 as cat3z_a was removed", 1, tlset.getLevels().size());
        assertTrue("cat1z is in the set and should have been true", tlset.isEnabled(cat1z));
        assertFalse("cat3z_a is not in the set and should have been false",
                tlset.isEnabled(cat3z_a));
        assertFalse("cat3z_b is not in the set and should have been false",
                tlset.isEnabled(cat3z_b));
    }
    
    @Test
    public void testAddNull() {
        TimerLevel nullLevel = null;
        TimerLevelSet tlset = new TimerLevelSet();
        assertNull("Should have returned null", tlset.addLevel(nullLevel));
    }

    @Test (expected=IllegalArgumentException.class)
    public void testAddInvalidSet() {
        // This SET is does not implement TimerLevelCollection
        TimerLevel invalidSet = new TimerLevel() {
            
            @Override
            public boolean isEnabled(TimerLevel... levels) {
                return false;
            }
            
            @Override
            public boolean isEnabled(TimerLevel level) {
                return false;
            }
            
            @Override
            public TimerLevelType getLevelType() {
                return TimerLevelType.SET;
            }
            
            @Override
            public Object getGroup() {
                return null;
            }
        };
        TimerLevelSet tlset = new TimerLevelSet();
        tlset.addLevel(invalidSet);
    }

    @Test
    public void testClearLevels() {
        TimerLevelSet tlset = new TimerLevelSet(cat1y, cat2y);
        assertTrue("cat1y is in the set and should have been true", tlset.isEnabled(cat1y));
        assertTrue("cat2y is in the set and should have been true", tlset.isEnabled(cat2y));
        tlset.clear();
        assertFalse("cat1y is removed from the set and should have been false",
                tlset.isEnabled(cat1y));
        assertFalse("cat2y is removed from the set and should have been false",
                tlset.isEnabled(cat2y));
    }

    @Test
    public void testGetLevelType() {
        TimerLevelCollection tls = new TimerLevelSet(normal, cat1y);
        assertEquals("Should return set type", TimerLevelType.SET, tls.getLevelType());
    }

    @Test
    public void testGetLevelGroup() {
        TimerLevelCollection tls = new TimerLevelSet(normal, cat1y);
        assertEquals("Group value for set is the set itself", tls, tls.getGroup());
    }

}
