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
public class KeyedTimerNotes implements Serializable, TimerNotes {

    private static final long serialVersionUID = -2180211898163201478L;

    private final String[] keys;
    private final Object[] notes;

    /**
     * Create a new {@link KeyedTimerNotes} and store the provided notes for for
     * future retrieval. If keyed is true, the notes must be in key/value pairs
     * (.e.g., key1, value1, key2, value2 ...).
     * 
     * @param keyed
     *            true if notes are provided as key/value pairs, false if all
     *            notes are values.
     * @param notes
     *            list of either key/value pairs or values.
     */
    public KeyedTimerNotes(Object... notes) {
        super();
        if (notes.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "Notes must be in key value pairs if keyed state is set");
        }
        this.keys = new String[notes.length / 2];
        this.notes = new Object[notes.length / 2];
        for (int i = 0; i < keys.length; i++) {
            this.keys[i] = (String) notes[2 * i];
            this.notes[i] = notes[2 * i + 1];

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.digitaltsunami.tmeter.TimerNotes#getStringValue(int)
     */
    @Override
    public String getStringValue(int index) {
        return String.valueOf(notes[index]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.digitaltsunami.tmeter.TimerNotes#getStringValue(java.lang.String)
     */
    @Override
    public String getStringValue(String key) {
        return String.valueOf(getValue(key));
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.digitaltsunami.tmeter.TimerNotes#getValue(int)
     */
    @Override
    public Object getValue(int index) {
        return notes[index];
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.digitaltsunami.tmeter.TimerNotes#getValue(java.lang.String)
     */
    @Override
    public Object getValue(String key) {
        for (int i = 0; i < keys.length; i++) {
            if (key.equalsIgnoreCase(keys[i])) {
                return notes[i];
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.digitaltsunami.tmeter.TimerNotes#isKeyed()
     */
    @Override
    public boolean isKeyed() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.digitaltsunami.tmeter.TimerNotes#getIndexForKey(java.lang.String)
     */
    @Override
    public int getIndexForKey(String key) {
        for (int i = 0; i < keys.length; i++) {
            if (key.equalsIgnoreCase(keys[i])) {
                return i;
            }
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.digitaltsunami.tmeter.TimerNotes#getKeys()
     */
    @Override
    public String[] getKeys() {
        return keys.clone();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.digitaltsunami.tmeter.TimerNotes#getNotes()
     */
    @Override
    public Object[] getNotes() {
        return notes.clone();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.digitaltsunami.tmeter.TimerNotes#getLength()
     */
    @Override
    public int getLength() {
        return notes.length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.digitaltsunami.tmeter.TimerNotes#getFormattedNote(int)
     */
    @Override
    public String getFormattedNote(int index) {
        StringBuilder sb = new StringBuilder(100);
        return sb.append(keys[index]).append("=").append(notes[index]).toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.digitaltsunami.tmeter.TimerNotes#toSingleValue()
     */
    @Override
    public String toSingleValue() {
        return toSingleValue(TimerNotes.NOTE_DELIMITER, TimerNotes.KEY_VALUE_DELIMITER);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.digitaltsunami.tmeter.TimerNotes#toSingleValue(char)
     */
    @Override
    public String toSingleValue(char noteDelimiter) {
        return toSingleValue(noteDelimiter, TimerNotes.KEY_VALUE_DELIMITER);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.digitaltsunami.tmeter.TimerNotes#toSingleValue(char, char)
     */
    @Override
    public String toSingleValue(char noteDelimiter, char keyValueDelimiter) {
        StringBuilder sb = new StringBuilder(100);
        for (int i = 0; i < notes.length; i++) {
            if (i > 0) {
                sb.append(noteDelimiter);
            }
            sb.append(keys[i]).append(keyValueDelimiter).append(notes[i]);
        }
        return sb.toString();
    }
}