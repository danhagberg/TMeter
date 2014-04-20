package net.digitaltsunami.tmeter;

/**
 * A set of notes associated with the timer. May be key value pairs or a list of
 * values.
 * 
 * @author dhagberg
 * 
 */
public interface TimerNotes {

	/**
	 * Default value to delimit notes within single notes field
	 */
	public static final char NOTE_DELIMITER = 0x1E;
	/**
	 * Default value to delimit notes key from value within each note if keyed.
	 */
	public static final char KEY_VALUE_DELIMITER = 0x1F;

	/**
	 * Return the note at the provided index as a {@link String}.
	 * 
	 * @param index
	 * @return String representation of value stored at index. <code>Null</code>
	 *         values will be returned as "null"
	 */
	 String getStringValue(int index);

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
	 String getStringValue(String key);

	/**
	 * Return the note at the provided index.
	 * 
	 * @param index
	 * @return Note value at specified index.
	 */
	 Object getValue(int index);

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
	 Object getValue(String key);

	/**
	 * Return true if the note values are mapped by keys and either a key or
	 * index may be used to retrieve the note values. A value of false implies
	 * that an index is required to extract individual notes.
	 * 
	 * @return true if data is mapped to keys, false otherwise.
	 */
	 boolean isKeyed();

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
	 int getIndexForKey(String key);

	/**
	 * Return a copy of the keys array.
	 * 
	 * @return a copy of the keys.
	 */
	 String[] getKeys();

	/**
	 * Return a copy of the array of notes.
	 * <p>
	 * <strong>Note:</strong> This is a shallow copy and if storing mutable
	 * objects as note values, then changes to the internal attributes of those
	 * objects will be reflected in the backing store for the notes.
	 * 
	 * @return a copy of the note values.
	 */
	 Object[] getNotes();

	/**
	 * Return the number of notes.
	 * 
	 * @return the number of notes.
	 */
	 int getLength();

	/**
	 * Return the formatted note for the provided index. If keyed, note will be
	 * returned as key=val, otherwise just the value will be returned.
	 */
	 String getFormattedNote(int index);

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
	 String toSingleValue();

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
	 String toSingleValue(char noteDelimiter);

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
	 String toSingleValue(char noteDelimiter,
			char keyValueDelimiter);

}