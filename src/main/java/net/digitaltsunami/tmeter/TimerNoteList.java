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

import java.io.Serializable;
import java.util.Arrays;

/**
 * Structure to append domain specific notes to {@link Timer} recordings.
 * Examples of domain specific notes include number of query terms, size of
 * results, size of document, service provider name, transaction type, etc.
 * These notes may be attached as key value pairs or a list of items.
 * <p>
 * TimerNotes is immutable and as such, all note values must be provided during
 * construction.
 * <p>
 * TimerNotes may be persisted for later retrieval using serialization or by
 * converting the notes to a single {@link String} value using
 * {@link #toSingleValue()}. This value can be used to recreate the TimerNotes
 * instance using {@link #parse(String)}.
 * 
 * @author dhagberg
 * 
 */
public class TimerNoteList implements Serializable, TimerNotes {

    private static final long serialVersionUID = -2180211898163201478L;
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private final Object[] notes;

    /**
     * Create a new {@link TimerNoteList} and store all provided values as notes.
     * 
     * @param notes
     *            list of values to be stored as notes.
     */
    public TimerNoteList(Object... notes) {
        super();
        this.notes = notes.clone();
    }

    /* (non-Javadoc)
	 * @see net.digitaltsunami.tmeter.TimerNotes#getStringValue(int)
	 */
    @Override
	public String getStringValue(int index) {
        return String.valueOf(notes[index]);
    }

    /* (non-Javadoc)
	 * @see net.digitaltsunami.tmeter.TimerNotes#getStringValue(java.lang.String)
	 */
    @Override
	public String getStringValue(String key) {
        throw new IllegalStateException("Cannot use keyed access on non-keyed values");
    }

    /* (non-Javadoc)
	 * @see net.digitaltsunami.tmeter.TimerNotes#getValue(int)
	 */
    @Override
	public Object getValue(int index) {
        return notes[index];
    }

    /* (non-Javadoc)
	 * @see net.digitaltsunami.tmeter.TimerNotes#getValue(java.lang.String)
	 */
    @Override
	public Object getValue(String key) {
        throw new IllegalStateException("Cannot use keyed access on non-keyed values");
    }

    /* (non-Javadoc)
	 * @see net.digitaltsunami.tmeter.TimerNotes#isKeyed()
	 */
    @Override
	public boolean isKeyed() {
        return false;
    }

    /* (non-Javadoc)
	 * @see net.digitaltsunami.tmeter.TimerNotes#getIndexForKey(java.lang.String)
	 */
    @Override
	public int getIndexForKey(String key) {
        throw new IllegalStateException("Cannot use keyed access on non-keyed values");
    }

    /* (non-Javadoc)
	 * @see net.digitaltsunami.tmeter.TimerNotes#getNotes()
	 */
    @Override
	public Object[] getNotes() {
        return notes.clone();
    }

    /* (non-Javadoc)
	 * @see net.digitaltsunami.tmeter.TimerNotes#getLength()
	 */
    @Override
	public int getLength() {
        return notes.length;
    }

    /* (non-Javadoc)
	 * @see net.digitaltsunami.tmeter.TimerNotes#getFormattedNote(int)
	 */
    @Override
	public String getFormattedNote(int index) {
        return String.valueOf(notes[index]);
    }

    /* (non-Javadoc)
	 * @see net.digitaltsunami.tmeter.TimerNotes#toSingleValue()
	 */
    @Override
	public String toSingleValue() {
        return toSingleValue(TimerNotes.NOTE_DELIMITER);
    }

    /* (non-Javadoc)
	 * @see net.digitaltsunami.tmeter.TimerNotes#toSingleValue(char)
	 */
    @Override
	public String toSingleValue(char noteDelimiter) {
        StringBuilder sb = new StringBuilder(100);
        for (int i = 0; i < notes.length; i++) {
            if (i > 0) {
                sb.append(noteDelimiter);
            }
            sb.append(notes[i]);
        }
        return sb.toString();
    }

    /* (non-Javadoc)
	 * @see net.digitaltsunami.tmeter.TimerNotes#toSingleValue(char, char)
	 */
    @Override
	public String toSingleValue(char noteDelimiter, char keyValueDelimiter) {
        return toSingleValue(noteDelimiter);
    }

	@Override
	public String[] getKeys() {
		return EMPTY_STRING_ARRAY;
	}
}