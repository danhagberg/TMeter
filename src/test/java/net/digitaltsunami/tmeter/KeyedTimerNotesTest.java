package net.digitaltsunami.tmeter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class KeyedTimerNotesTest extends TimerNotesTest {

    @Override
    protected TimerNotes createTimerNotes(String[] keys, Object[] vals) {
        Object[] interleaved = new Object[keys.length * 2];
        for(int i=0;i<keys.length;i++) {
            interleaved[i*2] = keys[i];
            interleaved[i*2+1] = vals[i];
        }
        return new KeyedTimerNotes(interleaved);
    }
    
    @Before
    public void setUp() {
        super.setUp();
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.KeyedTimerNotes#KeyedTimerNotes(Object...)}
     * .
     */
    @Test
    public void testTimerNotesFullConstructor() {
        // Test with keyed values
        TimerNotes timerNotes = new KeyedTimerNotes("Int", 1, "Char", 'a', "String", "Test",
                "Double", 3.4);
        assertEquals(4, timerNotes.getLength());
    }
    
    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.KeyedTimerNotes#KeyedTimerNotes(Object...)}
     * .
     */
    @Test(expected=IllegalArgumentException.class)
    public void testTimerNotesFullConstructorInvalid() {
        // Test with keyed values
        // Missing value for last key.
        TimerNotes timerNotes = new KeyedTimerNotes("Int", 1, "Char", 'a', "String", "Test",
                "Double");
        assertEquals(4, timerNotes.getLength());
    }
    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerNoteList#getValue(java.lang.String)}.
     * Tests to ensure all note values are returned as the original datatype.
     */
    @Test
    public void testGetValueUsingKey() {
        assertEquals(1, testTimerNotes.getValue("Int"));
        assertTrue(testTimerNotes.getValue("Int") instanceof Integer);
        assertEquals('a', testTimerNotes.getValue("Char"));
        assertTrue(testTimerNotes.getValue("Char") instanceof Character);
        assertEquals("Test", testTimerNotes.getValue("String"));
        assertTrue(testTimerNotes.getValue("String") instanceof String);
        assertEquals(3.4, testTimerNotes.getValue("Double"));
        assertTrue(testTimerNotes.getValue("Double") instanceof Double);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerNoteList#getValue(java.lang.String)}.
     */
    @Test
    public void testGetValueUsingKeyNotFound() {
        assertNull("Should have returned null", testTimerNotes.getValue("NotFound"));
    }


    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerNoteList#getStringValue(java.lang.String)}
     * . Tests to ensure all note values are returned as {@link String}.
     */
    @Test
    public void testGetStringValueUsingKey() {
        assertEquals("1", testTimerNotes.getStringValue("Int"));
        assertEquals("a", testTimerNotes.getStringValue("Char"));
        assertEquals("Test", testTimerNotes.getStringValue("String"));
        assertEquals("3.4", testTimerNotes.getStringValue("Double"));
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerNoteList#isKeyed()}.
     */
    @Override
    @Test
    public void testIsKeyed() {
        assertTrue(testTimerNotes.isKeyed());
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerNoteList#getKeys()}.
     */
    @Test
    public void testGetKeys() {
        String[] keys = testTimerNotes.getKeys();
        assertEquals("Int", keys[0]);
        assertEquals("String", keys[2]);
    }

    @Test
    public void testSerializeKeyedNotes() {
        String keyedString = testTimerNotes.toSingleValue();
        String expected = "Int" + TimerNotes.KEY_VALUE_DELIMITER + 1 + TimerNotes.NOTE_DELIMITER
                + "Char" + TimerNotes.KEY_VALUE_DELIMITER + 'a' + TimerNotes.NOTE_DELIMITER
                + "String" + TimerNotes.KEY_VALUE_DELIMITER + "Test" + TimerNotes.NOTE_DELIMITER
                + "Double" + TimerNotes.KEY_VALUE_DELIMITER + 3.4;
        assertEquals(expected, keyedString);
    }

    @Test
    public void testSerializeKeyedNotesOverrideNotesDelimiter() {
        char notesDelimiter = ',';
        String keyedString = testTimerNotes.toSingleValue(notesDelimiter);
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
        String keyedString = testTimerNotes.toSingleValue(notesDelimiter, keyValueDelimiter);
        String expected = "Int" + keyValueDelimiter + 1 + notesDelimiter
                + "Char" + keyValueDelimiter + 'a' + notesDelimiter
                + "String" + keyValueDelimiter + "Test" + notesDelimiter
                + "Double" + keyValueDelimiter + 3.4;
        assertEquals(expected, keyedString);
    }
    @Test
    public void testSerializeKeyedNotesOverrideNotesDelimiterAllNotes() {
        char notesDelimiter = ',';
        String keyedString = testTimerNotes.toSingleValue(notesDelimiter);
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
        String keyedString = testTimerNotes.toSingleValue(notesDelimiter, keyValueDelimiter);
        String expected = "Int" + keyValueDelimiter + 1 + notesDelimiter
                + "Char" + keyValueDelimiter + 'a' + notesDelimiter
                + "String" + keyValueDelimiter + "Test" + notesDelimiter
                + "Double" + keyValueDelimiter + 3.4;
        assertEquals(expected, keyedString);
    }

    @Test
    public void testParseKeyedNotes() {
        String keyedString =
                "Int" + TimerNotes.KEY_VALUE_DELIMITER + 1 + TimerNotes.NOTE_DELIMITER
                        + "Char" + TimerNotes.KEY_VALUE_DELIMITER + 'a' + TimerNotes.NOTE_DELIMITER
                        + "String" + TimerNotes.KEY_VALUE_DELIMITER + "Test"
                        + TimerNotes.NOTE_DELIMITER
                        + "Double" + TimerNotes.KEY_VALUE_DELIMITER + 3.4;
        TimerNotes parsed = TimerNotesParser.parse(keyedString);
        // Test that keys and notes were correctly extracted.
        assertTrue(parsed.isKeyed());
        assertEquals("1", parsed.getStringValue("Int"));
        assertEquals("a", parsed.getStringValue("Char"));
        assertEquals("Test", parsed.getStringValue("String"));
        assertEquals("3.4", parsed.getStringValue("Double"));
        // Test that order was maintained during extraction
        assertEquals(1, testTimerNotes.getValue(0));
        assertEquals('a', testTimerNotes.getValue(1));
        assertEquals("Test", testTimerNotes.getValue(2));
        assertEquals(3.4, testTimerNotes.getValue(3));
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
        TimerNotes parsed = TimerNotesParser.parse(keyedString, notesDelimiter);
        // Test that keys and notes were correctly extracted.
        assertTrue(parsed.isKeyed());
        assertEquals("1", parsed.getStringValue("Int"));
        assertEquals("a", parsed.getStringValue("Char"));
        assertEquals("Test", parsed.getStringValue("String"));
        assertEquals("3.4", parsed.getStringValue("Double"));
        // Test that order was maintained during extraction
        assertEquals(1, testTimerNotes.getValue(0));
        assertEquals('a', testTimerNotes.getValue(1));
        assertEquals("Test", testTimerNotes.getValue(2));
        assertEquals(3.4, testTimerNotes.getValue(3));
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
        TimerNotes parsed = TimerNotesParser.parse(keyedString, notesDelimiter, keyValueDelimiter);
        // Test that keys and notes were correctly extracted.
        assertTrue(parsed.isKeyed());
        assertEquals("1", parsed.getStringValue("Int"));
        assertEquals("a", parsed.getStringValue("Char"));
        assertEquals("Test", parsed.getStringValue("String"));
        assertEquals("3.4", parsed.getStringValue("Double"));
        // Test that order was maintained during extraction
        assertEquals(1, testTimerNotes.getValue(0));
        assertEquals('a', testTimerNotes.getValue(1));
        assertEquals("Test", testTimerNotes.getValue(2));
        assertEquals(3.4, testTimerNotes.getValue(3));
    }


    @Override
    @Test
    public void testGetIndexForKey() {
        assertEquals(1,testTimerNotes.getIndexForKey("Char"));
    }
    @Override
    @Test
    public void testGetIndexForKeyNotFound() {
        assertEquals(-1,testTimerNotes.getIndexForKey("NotFound"));
    }
}
