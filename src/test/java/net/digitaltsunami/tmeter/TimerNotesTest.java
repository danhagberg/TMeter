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

import org.junit.Before;
import org.junit.Test;

/**
 * @author dhagberg
 * 
 */
public class TimerNotesTest {

    private TimerNotes basicTimerNotes;
    private TimerNotes keyedTimerNotes;

    /**
     * Create and initialize keyed and non-keyed instances of {@link TimerNotes}
     * .
     * <p>
     * Note values for both are the same and consist of an native int, char,
     * String, and double.
     */
    @Before
    public void setUp() {
        basicTimerNotes = new TimerNotes(1, 'a', "Test", 3.4);
        keyedTimerNotes = new TimerNotes(true, "Int", 1, "Char", 'a', "String", "Test",
                "Double", 3.4);
        TimerNotes.resetDelimiterValues();
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerNotes#TimerNotes(boolean, java.lang.Object[])}
     * .
     */
    @Test
    public void testTimerNotesFullConstructor() {
        // Test with keyed values
        TimerNotes timerNotes = new TimerNotes(true, "Int", 1, "Char", 'a', "String", "Test",
                "Double", 3.4);
        assertEquals(4, timerNotes.getLength());

        // Same list of values, but no longer keyed. Keys are now plain values.
        timerNotes = new TimerNotes(false, "Int", 1, "Char", 'a', "String", "Test",
                "Double", 3.4);
        assertEquals(8, timerNotes.getLength());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerNotes#TimerNotes(java.lang.Object[])}
     * .
     */
    @Test
    public void testTimerNotesValuesOnlyConstructor() {
        // Default is not keyed, so all arguments are note values.
        TimerNotes timerNotes = new TimerNotes(false, "Int", 1, "Char", 'a', "String", "Test",
                "Double", 3.4);
        assertEquals(8, timerNotes.getLength());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerNotes#getValue(int)}. Tests to
     * ensure all note values are returned as the original datatype.
     */
    @Test
    public void testGetValueUsingIndex() {
        assertEquals(1, basicTimerNotes.getValue(0));
        assertTrue(basicTimerNotes.getValue(0) instanceof Integer);
        assertEquals('a', basicTimerNotes.getValue(1));
        assertTrue(basicTimerNotes.getValue(1) instanceof Character);
        assertEquals("Test", basicTimerNotes.getValue(2));
        assertTrue(basicTimerNotes.getValue(2) instanceof String);
        assertEquals(3.4, basicTimerNotes.getValue(3));
        assertTrue(basicTimerNotes.getValue(3) instanceof Double);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerNotes#getValue(java.lang.String)}.
     * Tests to ensure all note values are returned as the original datatype.
     */
    @Test
    public void testGetValueUsingKey() {
        assertEquals(1, keyedTimerNotes.getValue("Int"));
        assertTrue(keyedTimerNotes.getValue("Int") instanceof Integer);
        assertEquals('a', keyedTimerNotes.getValue("Char"));
        assertTrue(keyedTimerNotes.getValue("Char") instanceof Character);
        assertEquals("Test", keyedTimerNotes.getValue("String"));
        assertTrue(keyedTimerNotes.getValue("String") instanceof String);
        assertEquals(3.4, keyedTimerNotes.getValue("Double"));
        assertTrue(keyedTimerNotes.getValue("Double") instanceof Double);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerNotes#getStringValue(java.lang.String)}
     * . Tests to ensure all note values are returned as {@link String}.
     */
    @Test
    public void testGetStringUsingIndex() {
        assertEquals("1", keyedTimerNotes.getStringValue(0));
        assertEquals("a", keyedTimerNotes.getStringValue(1));
        assertEquals("Test", keyedTimerNotes.getStringValue(2));
        assertEquals("3.4", keyedTimerNotes.getStringValue(3));
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerNotes#getStringValue(java.lang.String)}
     * . Tests to ensure all note values are returned as {@link String}.
     */
    @Test
    public void testGetStringUsingKey() {
        assertEquals("1", keyedTimerNotes.getStringValue("Int"));
        assertEquals("a", keyedTimerNotes.getStringValue("Char"));
        assertEquals("Test", keyedTimerNotes.getStringValue("String"));
        assertEquals("3.4", keyedTimerNotes.getStringValue("Double"));
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerNotes#isKeyed()}.
     */
    @Test
    public void testIsKeyed() {
        assertTrue(keyedTimerNotes.isKeyed());
        assertFalse(basicTimerNotes.isKeyed());
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerNotes#getKeys()}.
     */
    @Test
    public void testGetKeys() {
        String[] keys = keyedTimerNotes.getKeys();
        assertEquals("Int", keys[0]);
        assertEquals("String", keys[2]);
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerNotes#getNotes()}.
     */
    @Test
    public void testGetNotes() {
        Object[] notes = basicTimerNotes.getNotes();
        assertEquals(new Integer(1), notes[0]);
        assertEquals("Test", notes[2]);
        Object[] notesFromKeyed = basicTimerNotes.getNotes();
        assertArrayEquals(notes, notesFromKeyed);
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerNotes#getLength()}.
     */
    @Test
    public void testGetLength() {
        assertEquals(4, basicTimerNotes.getLength());
        assertEquals(4, keyedTimerNotes.getLength());
    }

    @Test
    public void testSerializeKeyedNotes() {
        String keyedString = keyedTimerNotes.toSingleValue();
        String expected = "Int" + TimerNotes.KEY_VALUE_DELIMITER + 1 + TimerNotes.NOTE_DELIMITER
                + "Char" + TimerNotes.KEY_VALUE_DELIMITER + 'a' + TimerNotes.NOTE_DELIMITER
                + "String" + TimerNotes.KEY_VALUE_DELIMITER + "Test" + TimerNotes.NOTE_DELIMITER
                + "Double" + TimerNotes.KEY_VALUE_DELIMITER + 3.4;
        assertEquals(expected, keyedString);
    }

    @Test
    public void testSerializeKeyedNotesOverrideNotesDelimiter() {
        char notesDelimiter = ',';
        String keyedString = keyedTimerNotes.toSingleValue(notesDelimiter);
        String expected = "Int" + TimerNotes.KEY_VALUE_DELIMITER + 1 + notesDelimiter
                + "Char" + TimerNotes.KEY_VALUE_DELIMITER + 'a' + notesDelimiter
                + "String" + TimerNotes.KEY_VALUE_DELIMITER + "Test" + notesDelimiter
                + "Double" + TimerNotes.KEY_VALUE_DELIMITER + 3.4;
        assertEquals(expected, keyedString);
    }

    @Test
    public void testSerializeKeyedNotesOverrideBothDelimiters() {
        char notesDelimiter = ',';
        char keyValueDelimiter = ':';
        String keyedString = keyedTimerNotes.toSingleValue(notesDelimiter, keyValueDelimiter);
        String expected = "Int" + keyValueDelimiter + 1 + notesDelimiter
                + "Char" + keyValueDelimiter + 'a' + notesDelimiter
                + "String" + keyValueDelimiter + "Test" + notesDelimiter
                + "Double" + keyValueDelimiter + 3.4;
        assertEquals(expected, keyedString);
    }
    @Test
    public void testSerializeKeyedNotesOverrideNotesDelimiterAllNotes() {
        char notesDelimiter = ',';
        TimerNotes.overrideDefaultNoteDelimiter(notesDelimiter);
        String keyedString = keyedTimerNotes.toSingleValue();
        String expected = "Int" + TimerNotes.KEY_VALUE_DELIMITER + 1 + notesDelimiter
                + "Char" + TimerNotes.KEY_VALUE_DELIMITER + 'a' + notesDelimiter
                + "String" + TimerNotes.KEY_VALUE_DELIMITER + "Test" + notesDelimiter
                + "Double" + TimerNotes.KEY_VALUE_DELIMITER + 3.4;
        assertEquals(expected, keyedString);
    }

    @Test
    public void testSerializeKeyedNotesOverrideBothDelimitersAllNotes() {
        char notesDelimiter = ',';
        char keyValueDelimiter = ':';
        TimerNotes.overrideDefaultNoteDelimiter(notesDelimiter);
        TimerNotes.overrideDefaultKeyValueDelimiter(keyValueDelimiter);
        String keyedString = keyedTimerNotes.toSingleValue();
        String expected = "Int" + keyValueDelimiter + 1 + notesDelimiter
                + "Char" + keyValueDelimiter + 'a' + notesDelimiter
                + "String" + keyValueDelimiter + "Test" + notesDelimiter
                + "Double" + keyValueDelimiter + 3.4;
        assertEquals(expected, keyedString);
    }

    @Test
    public void testSerializeNonKeyedNotes() {
        String notesString = basicTimerNotes.toSingleValue();
        String expected = "" + 1 + TimerNotes.NOTE_DELIMITER
                + 'a' + TimerNotes.NOTE_DELIMITER
                + "Test" + TimerNotes.NOTE_DELIMITER
                + 3.4;
        assertEquals(expected, notesString);
    }
    
    @Test
    public void testSerializeNonKeyedNotesOverrideNotesDelimiter() {
        char notesDelimiter = ',';
        String notesString = basicTimerNotes.toSingleValue(notesDelimiter);
        String expected = "" + 1 + notesDelimiter
                + 'a' + notesDelimiter
                + "Test" + notesDelimiter
                + 3.4;
        assertEquals(expected, notesString);
    }

    @Test
    public void testSerializeNonKeyedNotesOverrideNotesDelimiterAllNotes() {
        char notesDelimiter = ',';
        TimerNotes.overrideDefaultNoteDelimiter(notesDelimiter);
        String notesString = basicTimerNotes.toSingleValue();
        String expected = "" + 1 + notesDelimiter
                + 'a' + notesDelimiter
                + "Test" + notesDelimiter
                + 3.4;
        assertEquals(expected, notesString);
    }

    @Test
    public void testParseKeyedNotes() {
        String keyedString =
                "Int" + TimerNotes.KEY_VALUE_DELIMITER + 1 + TimerNotes.NOTE_DELIMITER
                        + "Char" + TimerNotes.KEY_VALUE_DELIMITER + 'a' + TimerNotes.NOTE_DELIMITER
                        + "String" + TimerNotes.KEY_VALUE_DELIMITER + "Test"
                        + TimerNotes.NOTE_DELIMITER
                        + "Double" + TimerNotes.KEY_VALUE_DELIMITER + 3.4;
        TimerNotes parsed = TimerNotes.parse(keyedString);
        // Test that keys and notes were correctly extracted.
        assertTrue(parsed.isKeyed());
        assertEquals("1", parsed.getStringValue("Int"));
        assertEquals("a", parsed.getStringValue("Char"));
        assertEquals("Test", parsed.getStringValue("String"));
        assertEquals("3.4", parsed.getStringValue("Double"));
        // Test that order was maintained during extraction
        assertEquals(1, basicTimerNotes.getValue(0));
        assertEquals('a', basicTimerNotes.getValue(1));
        assertEquals("Test", basicTimerNotes.getValue(2));
        assertEquals(3.4, basicTimerNotes.getValue(3));
    }
    
    @Test
    public void testParseKeyedNotesOverrideNotesDelimiter() {
        char notesDelimiter = ',';
        String keyedString =
                "Int" + TimerNotes.KEY_VALUE_DELIMITER + 1 + notesDelimiter
                        + "Char" + TimerNotes.KEY_VALUE_DELIMITER + 'a' + notesDelimiter
                        + "String" + TimerNotes.KEY_VALUE_DELIMITER + "Test"
                        + notesDelimiter
                        + "Double" + TimerNotes.KEY_VALUE_DELIMITER + 3.4;
        TimerNotes parsed = TimerNotes.parse(keyedString, notesDelimiter);
        // Test that keys and notes were correctly extracted.
        assertTrue(parsed.isKeyed());
        assertEquals("1", parsed.getStringValue("Int"));
        assertEquals("a", parsed.getStringValue("Char"));
        assertEquals("Test", parsed.getStringValue("String"));
        assertEquals("3.4", parsed.getStringValue("Double"));
        // Test that order was maintained during extraction
        assertEquals(1, basicTimerNotes.getValue(0));
        assertEquals('a', basicTimerNotes.getValue(1));
        assertEquals("Test", basicTimerNotes.getValue(2));
        assertEquals(3.4, basicTimerNotes.getValue(3));
    }
    
    @Test
    public void testParseKeyedNotesOverrideBothDelimiters() {
        char notesDelimiter = ',';
        char keyValueDelimiter = ':';
        String keyedString =
                "Int" + keyValueDelimiter + 1 + notesDelimiter
                        + "Char" + keyValueDelimiter + 'a' + notesDelimiter
                        + "String" + keyValueDelimiter + "Test"
                        + notesDelimiter
                        + "Double" + keyValueDelimiter + 3.4;
        TimerNotes parsed = TimerNotes.parse(keyedString, notesDelimiter, keyValueDelimiter);
        // Test that keys and notes were correctly extracted.
        assertTrue(parsed.isKeyed());
        assertEquals("1", parsed.getStringValue("Int"));
        assertEquals("a", parsed.getStringValue("Char"));
        assertEquals("Test", parsed.getStringValue("String"));
        assertEquals("3.4", parsed.getStringValue("Double"));
        // Test that order was maintained during extraction
        assertEquals(1, basicTimerNotes.getValue(0));
        assertEquals('a', basicTimerNotes.getValue(1));
        assertEquals("Test", basicTimerNotes.getValue(2));
        assertEquals(3.4, basicTimerNotes.getValue(3));
    }

    @Test
    public void testParseNonKeyedNotes() {
        String nonKeyedString = "" + 1 + TimerNotes.NOTE_DELIMITER
                + 'a' + TimerNotes.NOTE_DELIMITER
                + "Test" + TimerNotes.NOTE_DELIMITER
                + 3.4;
        TimerNotes parsed = TimerNotes.parse(nonKeyedString);
        assertFalse(parsed.isKeyed());
        assertEquals(1, basicTimerNotes.getValue(0));
        assertEquals('a', basicTimerNotes.getValue(1));
        assertEquals("Test", basicTimerNotes.getValue(2));
        assertEquals(3.4, basicTimerNotes.getValue(3));
    }
    
    @Test
    public void testParseNonKeyedNotesOverrideNoteDelimiter() {
        char notesDelimiter = ',';
        String nonKeyedString = "" + 1 + notesDelimiter
                + 'a' + notesDelimiter
                + "Test" + notesDelimiter
                + 3.4;
        TimerNotes parsed = TimerNotes.parse(nonKeyedString);
        assertFalse(parsed.isKeyed());
        assertEquals(1, basicTimerNotes.getValue(0));
        assertEquals('a', basicTimerNotes.getValue(1));
        assertEquals("Test", basicTimerNotes.getValue(2));
        assertEquals(3.4, basicTimerNotes.getValue(3));
    }
}
