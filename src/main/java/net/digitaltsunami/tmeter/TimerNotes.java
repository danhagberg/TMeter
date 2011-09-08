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
public class TimerNotes implements Serializable {

    private static final long serialVersionUID = -2180211898163201478L;

    private final boolean keyed;
    private final String[] keys;
    private final Object[] notes;

    /**
     * Default value to delimit notes within single notes field
     */
    public static final char NOTE_DELIMITER = 0x1E;
    /**
     * Default value to delimit notes key from value within each note if keyed.
     */
    public static final char KEY_VALUE_DELIMITER = 0x1F;

    /**
     * Value used to delimit notes within single notes file.
     */
    private static char noteDelimiterOverride = NOTE_DELIMITER;
    /**
     * Value used to delimit notes key from value within each note if keyed.
     */
    private static char keyValueDelimiterOverride = KEY_VALUE_DELIMITER;

    /**
     * Create a new {@link TimerNotes} and store the provided notes for for
     * future retrieval. If keyed is true, the notes must be in key/value pairs
     * (.e.g., key1, value1, key2, value2 ...).
     * 
     * @param keyed
     *            true if notes are provided as key/value pairs, false if all
     *            notes are values.
     * @param notes
     *            list of either key/value pairs or values.
     */
    public TimerNotes(boolean keyed, Object... notes) {
        super();
        this.keyed = keyed;
        if (keyed) {
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
        } else {
            this.notes = notes;
            this.keys = null;
        }
    }

    /**
     * Create a new {@link TimerNotes} and store all provided values as notes.
     * 
     * @param notes
     *            list of values to be stored as notes.
     */
    public TimerNotes(Object... notes) {
        this(false, notes);
    }

    /**
     * Return the note at the provided index as a {@link String}.
     * 
     * @param index
     * @return String representation of value stored at index. <code>Null</code>
     *         values will be returned as "null"
     */
    public String getStringValue(int index) {
        return String.valueOf(notes[index]);
    }

    /**
     * Return the note for the provided key as a {@link String}.
     * <p>
     * Comparison of key values is not case sensitive. Case should not be used
     * to distinguish keys with timer notes.
     * <p>
     * If keyed access was not used to store the values by using
     * {@link #TimerNotes(boolean, Object[])}, an {@link IllegalStateException}
     * will be thrown.
     * 
     * @param key
     * @return String representation of value stored at index. <code>Null</code>
     *         values will be returned as "null"
     * @throws IllegalStateException
     *             if notes are not stored by key.
     */
    public String getStringValue(String key) {
        return String.valueOf(getValue(key));
    }

    /**
     * Return the note at the provided index.
     * 
     * @param index
     * @return Note value at specified index.
     */
    public Object getValue(int index) {
        return notes[index];
    }

    /**
     * Returns the note value as an object for the provided key. If not found,
     * <code>null</code> will be returned. Note that as nulls values are
     * allowed, a return of a null value can occur when either the value stored
     * for the key was null or the key was not found.
     * <p>
     * Comparison of key values is not case sensitive. Case should not be used
     * to distinguish keys with timer notes.
     * <p>
     * If keyed access was not used to store the values by using
     * {@link #TimerNotes(boolean, Object[])}, an {@link IllegalStateException}
     * will be thrown.
     * 
     * @param key
     *            case insensitive value used to locate note value.
     * @return Value for key or null if not found.
     * @throws IllegalStateException
     *             if notes are not stored by key.
     */
    public Object getValue(String key) {
        if (keyed == false) {
            throw new IllegalStateException("Cannot use keyed access on non-keyed values");
        }
        for (int i = 0; i < keys.length; i++) {
            if (key.equalsIgnoreCase(keys[i])) {
                return notes[i];
            }
        }
        return null;
    }

    /**
     * Return true if the note values are mapped by keys and either a key or
     * index may be used to retrieve the note values. A value of false implies
     * that an index is required to extract individual notes.
     * 
     * @return true if data is mapped to keys, false otherwise.
     */
    public boolean isKeyed() {
        return keyed;
    }

    /**
     * Return the index value for the provided key. Key comparison is
     * case-insensitive.
     * <p>
     * This may be used to retrieve the index of the values array to speed value
     * retrieval for subsequent timers providing that the values were added in
     * the same manner.
     * 
     * @param Key
     *            used to query for index value.
     * @return index for key if found, otherwise -1.
     */
    public int getIndexForKey(String key) {
        for (int i = 0; i < keys.length; i++) {
            if (key.equalsIgnoreCase(keys[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Return a copy of the keys array.
     * 
     * @return a copy of the keys.
     */
    public String[] getKeys() {
        return keys.clone();
    }

    /**
     * Return a copy of the array of notes.
     * <p>
     * <strong>Note:</strong> This is a shallow copy and if storing mutable
     * objects as note values, then changes to the internal attributes of those
     * objects will be reflected in the backing store for the notes.
     * 
     * @return a copy of the note values.
     */
    public Object[] getNotes() {
        return notes.clone();
    }

    /**
     * Return the number of notes.
     * 
     * @return the number of notes.
     */
    public int getLength() {
        return notes.length;
    }

    /**
     * Return the formatted note for the provided index. If keyed, note will be
     * returned as key=val, otherwise just the value will be returned.
     */
    public String getFormattedNote(int index) {
        if (keyed) {
            StringBuilder sb = new StringBuilder(100);
            return sb.append(keys[index]).append("=").append(notes[index]).toString();
        } else {
            return String.valueOf(notes[index]);
        }
    }

    /**
     * Creates a single {@link String} value containing all notes delimited by
     * {@link #NOTE_DELIMITER}. If the values are keyed, then each value will be
     * preceded by the key and a delimiter of {@link #KEY_VALUE_DELIMITER}.
     * 
     * @param notes
     *            Single string value containing notes delimited by the
     *            applicable character values.
     * @param noteDelimiter
     *            character value used to delimit note values within provided
     *            notes string.
     * @param keyValueDelimiter
     *            character value used to delimit key and value within each note
     *            extracted from the notes parameter. A value should be selected
     *            that is not present in either the keys or values for the given
     *            notes.
     * @return Single string representation of the notes contained within this
     *         instance.
     */
    public String toSingleValue() {
        return toSingleValue(noteDelimiterOverride, keyValueDelimiterOverride);
    }

    /**
     * Creates a single {@link String} value containing all notes delimited by
     * the provided noteDelimiter. If the values are keyed, then each value will
     * be preceded by the key and a delimiter of {@link #KEY_VALUE_DELIMITER}.
     * 
     * @param noteDelimiter
     *            character value used to delimit note values within provided
     *            notes string.
     * @return Single string representation of the notes contained within this
     *         instance.
     */
    public String toSingleValue(char noteDelimiter) {
        return toSingleValue(noteDelimiter, keyValueDelimiterOverride);
    }

    /**
     * Creates a single {@link String} value containing all notes delimited by
     * the provided noteDelimiter. If the values are keyed, then each value will
     * be preceded by the key and a delimiter of the provided keyValueDelimiter.
     * 
     * @param noteDelimiter
     *            character value used to delimit note values within provided
     *            notes string.
     * @param keyValueDelimiter
     *            character value used to delimit key and value within each note
     *            extracted from the notes parameter. A value should be selected
     *            that is not present in either the keys or values for the given
     *            notes.
     * @return Single string representation of the notes contained within this
     *         instance.
     */
    public String toSingleValue(char noteDelimiter, char keyValueDelimiter) {
        StringBuilder sb = new StringBuilder(100);
        for (int i = 0; i < notes.length; i++) {
            if (i > 0) {
                sb.append(noteDelimiter);
            }
            if (keyed) {
                sb.append(keys[i]).append(keyValueDelimiter);
            }
            sb.append(notes[i]);
        }
        return sb.toString();
    }

    /**
     * Parse a {@link String} representation of a {@link TimerNotes} instance
     * and return a new instance with the values set as appropriate.
     * <p>
     * Each of the notes must be delimited by {@link #NOTE_DELIMITER}. Notes may
     * be keyed or non-keyed. If keyed, each note must be delimited by
     * {@link #KEY_VALUE_DELIMITER}.
     * 
     * @param notes
     *            Single string value containing notes delimited by the
     *            applicable character values.
     * @return an instance of {@link TimerNotes} populated using the values
     *         contained within the notes parameter.
     */
    public static TimerNotes parse(String notes) {
        return parse(notes, noteDelimiterOverride, keyValueDelimiterOverride);
    }

    /**
     * Parse a {@link String} representation of a {@link TimerNotes} instance
     * and return a new instance with the values set as appropriate.
     * <p>
     * Each of the notes must be delimited by the provided noteDelimiter value.
     * Notes may be keyed or non-keyed. If keyed, each note must be delimited by
     * {@link #KEY_VALUE_DELIMITER}.
     * 
     * @param notes
     *            Single string value containing notes delimited by the
     *            applicable character values.
     * @param noteDelimiter
     *            character value used to delimit note values within provided
     *            notes string.
     * @return an instance of {@link TimerNotes} populated using the values
     *         contained within the notes parameter.
     */
    public static TimerNotes parse(String notes, char noteDelimiter) {
        return parse(notes, noteDelimiter, keyValueDelimiterOverride);

    }

    /**
     * Parse a {@link String} representation of a {@link TimerNotes} instance
     * and return a new instance with the values set as appropriate.
     * <p>
     * Each of the notes must be delimited by the provided noteDelimiter value.
     * Notes may be keyed or non-keyed. If keyed, each note must be delimited by
     * the provided keyValueDelimiter value.
     * 
     * @param notes
     *            Single string value containing notes delimited by the
     *            applicable character values.
     * @param noteDelimiter
     *            character value used to delimit note values within provided
     *            notes string.
     * @param keyValueDelimiter
     *            character value used to delimit key and value within each note
     *            extracted from the notes parameter.
     * @return an instance of {@link TimerNotes} populated using the values
     *         contained within the notes parameter.
     */
    public static TimerNotes parse(String notes, char noteDelimiter, char keyValueDelimiter) {
        if (notes.length() == 0) {
            return new TimerNotes();
        }
        String[] noteVals = notes.split(String.valueOf(noteDelimiter));
        // Test the first note to determine if notes are keyed. If so,
        // assumption is all are keyed.
        boolean valsKeyed = noteVals[0].indexOf(keyValueDelimiter) > 0;
        Object[] noteArgs;
        // Keyed notes are provided key1,val1,key2,val2; whereas non-keyed are
        // just val1,val2.
        if (valsKeyed) {
            noteArgs = new Object[noteVals.length * 2];
            int argIdx = 0;
            for (String noteVal : noteVals) {
                int delimIdx = noteVal.indexOf(keyValueDelimiter);
                if (delimIdx > -1) {
                    noteArgs[argIdx++] = noteVal.substring(0, delimIdx);
                    noteArgs[argIdx++] = noteVal.substring(delimIdx + 1);
                }
            }

        } else {
            // Notes already in correct format.
            noteArgs = noteVals;
        }
        return new TimerNotes(valsKeyed, noteArgs);
    }

    /**
     * Override the default delimiter used when creating a single value
     * representing the notes. This value will be used for all notes after being
     * set.
     * 
     * @param noteDelimiterOverrideVal
     *            character value used to delimit note values within provided
     *            notes string.
     * @see TimerNotes#toSingleValue()
     */
    public static void overrideDefaultNoteDelimiter(char noteDelimiterOverrideVal) {
        noteDelimiterOverride = noteDelimiterOverrideVal;
    }

    /**
     * Override the default value used to delimit key from value within each
     * note value. This value will be used for all notes after being set.
     * 
     * @param keyValueDelimiterOverrideVal
     *            character value used to delimit key and value within each note
     *            extracted from the notes parameter. A value should be selected
     *            that is not present in either the keys or values for the given
     *            notes.
     * @see TimerNotes#toSingleValue()
     */
    public static void overrideDefaultKeyValueDelimiter(char keyValueDelimiterOverrideVal) {
        keyValueDelimiterOverride = keyValueDelimiterOverrideVal;
    }
    
    /**
     * Reset note and key/value delimiters to default values.
     * @see #NOTE_DELIMITER
     * @see #KEY_VALUE_DELIMITER
     */
    public static void resetDelimiterValues() {
        noteDelimiterOverride = NOTE_DELIMITER;
        keyValueDelimiterOverride = KEY_VALUE_DELIMITER;
    }
}