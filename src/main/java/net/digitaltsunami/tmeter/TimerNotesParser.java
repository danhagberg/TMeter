package net.digitaltsunami.tmeter;

/**
 * Parser that will convert a string representation of timer notes into an
 * instance of {@link TimerNotes}.
 * 
 * @author dhagberg
 * 
 */
public class TimerNotesParser {

	/**
	 * Parse a {@link String} representation of a {@link KeyedTimerNotes}
	 * instance and return a new instance with the values set as appropriate.
	 * <p>
	 * Each of the notes must be delimited by {@link #NOTE_DELIMITER}. Notes may
	 * be keyed or non-keyed. If keyed, each note must be delimited by
	 * {@link #KEY_VALUE_DELIMITER}.
	 * 
	 * @param notes
	 *            Single string value containing notes delimited by the
	 *            applicable character values.
	 * @return an instance of {@link KeyedTimerNotes} populated using the values
	 *         contained within the notes parameter.
	 */
	public static TimerNotes parse(String notes) {
		return parse(notes, TimerNotes.NOTE_DELIMITER, TimerNotes.KEY_VALUE_DELIMITER);
	}

	/**
	 * Parse a {@link String} representation of a {@link KeyedTimerNotes}
	 * instance and return a new instance with the values set as appropriate.
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
	 * @return an instance of {@link KeyedTimerNotes} populated using the values
	 *         contained within the notes parameter.
	 */
	public static TimerNotes parse(String notes, char noteDelimiter) {
		return parse(notes, noteDelimiter, TimerNotes.KEY_VALUE_DELIMITER);

	}

	/**
	 * Parse a {@link String} representation of a {@link KeyedTimerNotes}
	 * instance and return a new instance with the values set as appropriate.
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
	 * @return an instance of {@link KeyedTimerNotes} populated using the values
	 *         contained within the notes parameter.
	 */
	public static TimerNotes parse(String notes, char noteDelimiter,
			char keyValueDelimiter) {
		if (notes.length() == 0) {
			return new KeyedTimerNotes();
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
			return new TimerNoteList(noteVals);
		}
		return new KeyedTimerNotes(noteArgs);
	}

}
