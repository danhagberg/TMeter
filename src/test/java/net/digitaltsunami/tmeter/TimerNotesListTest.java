/* __copyright_begin__
   Copyright 2011 Dan Hagberg

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
__copyright_end__ */
package net.digitaltsunami.tmeter;

import static org.junit.Assert.*;

import java.lang.reflect.Array;

import org.junit.Before;
import org.junit.Test;

/**
 * @author dhagberg
 * 
 */
public class TimerNotesListTest extends TimerNotesTest {


    /**
     * Create and initialize keyed and non-keyed instances of {@link TimerNoteList}
     * .
     * <p>
     * Note values for both are the same and consist of an native int, char,
     * String, and double.
     */
    @Before
    public void setUp() {
        super.setUp();
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerNoteList#TimerNotes(boolean, java.lang.Object[])}
     * .
     */
    @Test
    public void testTimerNotesFullConstructor() {
        // Same list of values, but no longer keyed. Keys are now plain values.
        TimerNotes timerNotes = new TimerNoteList("Int", 1, "Char", 'a', "String", "Test",
                "Double", 3.4);
        assertEquals(8, timerNotes.getLength());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerNoteList#TimerNotes(java.lang.Object[])}
     * .
     */
    @Test
    public void testTimerNotesValuesOnlyConstructor() {
        // Default is not keyed, so all arguments are note values.
        TimerNotes timerNotes = new TimerNoteList("Int", 1, "Char", 'a', "String", "Test",
                "Double", 3.4);
        assertEquals(8, timerNotes.getLength());
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerNoteList#isKeyed()}.
     */
    @Test
    public void testIsKeyed() {
        assertFalse(testTimerNotes.isKeyed());
    }
    
    @Test
    public void testSerializeNonKeyedNotes() {
        String notesString = testTimerNotes.toSingleValue();
        String expected = "" + 1 + TimerNotes.NOTE_DELIMITER
                + 'a' + TimerNotes.NOTE_DELIMITER
                + "Test" + TimerNotes.NOTE_DELIMITER
                + 3.4;
        assertEquals(expected, notesString);
    }
    
    @Test
    public void testSerializeNonKeyedNotesOverrideNotesDelimiter() {
        char notesDelimiter = ',';
        String notesString = testTimerNotes.toSingleValue(notesDelimiter);
        String expected = "" + 1 + notesDelimiter
                + 'a' + notesDelimiter
                + "Test" + notesDelimiter
                + 3.4;
        assertEquals(expected, notesString);
    }

    @Test
    public void testSerializeNonKeyedNotesOverrideNotesDelimiterKeyedDelimiter() {
        char notesDelimiter = ',';
        // Show that second key value delimiter is ignored.
        String notesString = testTimerNotes.toSingleValue(notesDelimiter, 'X');
        String expected = "" + 1 + notesDelimiter
                + 'a' + notesDelimiter
                + "Test" + notesDelimiter
                + 3.4;
        assertEquals(expected, notesString);
    }

    @Test
    public void testParseNonKeyedNotes() {
        String nonKeyedString = "" + 1 + TimerNotes.NOTE_DELIMITER
                + 'a' + TimerNotes.NOTE_DELIMITER
                + "Test" + TimerNotes.NOTE_DELIMITER
                + 3.4;
        TimerNotes parsed = TimerNotesParser.parse(nonKeyedString);
        assertFalse(parsed.isKeyed());
        assertEquals(1, testTimerNotes.getValue(0));
        assertEquals('a', testTimerNotes.getValue(1));
        assertEquals("Test", testTimerNotes.getValue(2));
        assertEquals(3.4, testTimerNotes.getValue(3));
    }
    
    @Test
    public void testParseNonKeyedNotesOverrideNoteDelimiter() {
        char notesDelimiter = ',';
        String nonKeyedString = "" + 1 + notesDelimiter
                + 'a' + notesDelimiter
                + "Test" + notesDelimiter
                + 3.4;
        TimerNotes parsed = TimerNotesParser.parse(nonKeyedString);
        assertFalse(parsed.isKeyed());
        assertEquals(1, testTimerNotes.getValue(0));
        assertEquals('a', testTimerNotes.getValue(1));
        assertEquals("Test", testTimerNotes.getValue(2));
        assertEquals(3.4, testTimerNotes.getValue(3));
    }

    @Override
    protected TimerNotes createTimerNotes(String[] keys, Object[] vals) {
        return new TimerNoteList(vals);
    }

    @Override
    public void testGetKeys() {
        assertEquals(0, testTimerNotes.getKeys().length);
    }

    @Override
    @Test (expected=IllegalStateException.class)
    public void testGetStringValueUsingKey() {
        testTimerNotes.getStringValue("Char");
    }

    @Override
    @Test (expected=IllegalStateException.class)
    public void testGetIndexForKey() {
        testTimerNotes.getIndexForKey("Char");
    }
    
    @Override
    @Test (expected=IllegalStateException.class)
    public void testGetIndexForKeyNotFound() {
        testTimerNotes.getIndexForKey("NotFound");
    }

    @Override
    @Test (expected=IllegalStateException.class)
    public void testGetValueUsingKey() {
        testTimerNotes.getValue("Char");
    }

}
