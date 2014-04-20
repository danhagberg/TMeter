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
public abstract class TimerNotesTest {

    protected TimerNotes testTimerNotes;
    protected final static String[] KEYS = { "Int","Char", "String", "Double"};
    protected final static Object[] VALS = { 1,  'a',  "Test", 3.4};

    /**
     * Create and initialize keyed and non-keyed instances of {@link TimerNoteList}
     * .
     * <p>
     * Note values for both are the same and consist of an native int, char,
     * String, and double.
     */
    public void setUp() {
        testTimerNotes = createTimerNotes(KEYS, VALS);
    }

    protected abstract TimerNotes createTimerNotes(String[] keys2, Object[] vals2);

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.TimerNotes#TimerNotes(boolean, java.lang.Object[])}
     * .
     */
    @Test
    public void testTimerNotesFullConstructor() {
        // Test with keyed values
        TimerNotes timerNotes = new KeyedTimerNotes("Int", 1, "Char", 'a', "String", "Test",
                "Double", 3.4);
        assertEquals(4, timerNotes.getLength());

        // Same list of values, but no longer keyed. Keys are now plain values.
        timerNotes = new TimerNoteList("Int", 1, "Char", 'a', "String", "Test",
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
        TimerNotes timerNotes = new TimerNoteList("Int", 1, "Char", 'a', "String", "Test",
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
        assertEquals(1, testTimerNotes.getValue(0));
        assertTrue(testTimerNotes.getValue(0) instanceof Integer);
        assertEquals('a', testTimerNotes.getValue(1));
        assertTrue(testTimerNotes.getValue(1) instanceof Character);
        assertEquals("Test", testTimerNotes.getValue(2));
        assertTrue(testTimerNotes.getValue(2) instanceof String);
        assertEquals(3.4, testTimerNotes.getValue(3));
        assertTrue(testTimerNotes.getValue(3) instanceof Double);
    }
    

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerNotes#getNotes()}.
     */
    @Test
    public void testGetNotes() {
        Object[] notes = testTimerNotes.getNotes();
        assertEquals(new Integer(1), notes[0]);
        assertEquals("Test", notes[2]);
        Object[] notesFromKeyed = testTimerNotes.getNotes();
        assertArrayEquals(notes, notesFromKeyed);
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerNotes#getLength()}.
     */
    @Test
    public void testGetLength() {
        assertEquals(4, testTimerNotes.getLength());
    }
    
    @Test
    public void testGetStringValueForIndex() {
        assertEquals("1", testTimerNotes.getStringValue(0));
    }
    
    @Test
    abstract public void testGetStringValueUsingKey(); 
    
    @Test
    abstract public void testGetIndexForKey(); 
    
    @Test
    abstract public void testGetValueUsingKey(); 
    
    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerNotes#isKeyed()}.
     */
    @Test
    public abstract void testIsKeyed();

    /**
     * Test method for {@link net.digitaltsunami.tmeter.TimerNotes#getKeys()}.
     */
    @Test
    public abstract void testGetKeys();

    @Test
    public abstract void testGetIndexForKeyNotFound();
}
