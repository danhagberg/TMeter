/**
 * The Time Recorder framework, specified by the {@link net.digitaltsunami.tmeter.record.TimeRecorder} interface provides a mechanism to 
 record the time spent on the task and additional information recorded with the task. 
 * <p>
 * Performance Note: This method may be invoked during high load timings and
 * processing should be kept to a minimum. 
 * <p>
 * Provided recorders:
 * <ul>
 * <li>NullTimeRecorder - Will drop all timers and not record. This, in effect, disables recording</li>
 * {@link net.digitaltsunami.tmeter.record.NullTimeRecorder}
 * <li>ConsoleTimeRecorder - Records to the console all completed timers.  See below for format of output.</li>
 * {@link net.digitaltsunami.tmeter.record.ConsoleTimeRecorder}
 * <li>FileTimeRecorder</li>
 * <li>FileTimeRecorder - Records to all completed timers to a file.  See below for format of output.</li>
 * {@link net.digitaltsunami.tmeter.record.FileTimeRecorder}
 * <li>QueuedTimeRecorder - Moves recording of timers off of a timed thread and onto a maintenance thread.
 * Delegates processing of completed timer to configured instance of TimeRecorder.</li>
 * {@link net.digitaltsunami.tmeter.record.QueuedTimeRecorder}
 * </ul>
 * Provided formats:
 * <p>
 * Currently, there are two provided recording formats.  Each of these can be passed to the console or file recorders.
 * <ul>
 * <li>{@link net.digitaltsunami.tmeter.TimerLogType#CSV}  See {@link net.digitaltsunami.tmeter.Timer#toCsv()} for more information. </li>
 * <li>{@link net.digitaltsunami.tmeter.TimerLogType#TEXT} See {@link net.digitaltsunami.tmeter.Timer#toString()} for more information. </li>
 * </ul>
 */
package net.digitaltsunami.tmeter.record;

