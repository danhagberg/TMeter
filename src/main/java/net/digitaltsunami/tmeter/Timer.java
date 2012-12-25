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
import java.util.Date;

import net.digitaltsunami.tmeter.action.ActionChain;
import net.digitaltsunami.tmeter.event.TimerStoppedEvent;
import net.digitaltsunami.tmeter.event.TimerStoppedListener;
import net.digitaltsunami.tmeter.record.TimeRecorder;

/**
 * A record of elapsed time. Timers can be used independently or as part of the
 * framework driven using {@link TimeTracker}.
 * <p>
 * Provides:
 * <ul>
 * <li>Immediate or delayed start to allow preparation tasks to be excluded in
 * the timing.
 * <li>Nanosecond precision recording of tasks.
 * <li>Basic completion logging in text or CSV format. Default is to not log.
 * <li>Configurable log location with stdout being the default.
 * <li>Status
 * <li>Domain specific notes. A list of notes can be added to the timer that may
 * provide information useful to analyzing the results at a later time. These
 * notes will be appended to both the Text and CSV outputs if used.
 * </ul>
 * 
 * <strong>Thread Safety</strong>
 * <p>
 * To avoid synchronization, which could cause context swaps, instances of Timer
 * are <strong>not</strong> thread safe. Some aspects are immutable and some
 * will not change after they are set.
 * <p>
 * Immutable:
 * <ul>
 * <li>Task name</li>
 * <li>Thread name</li>
 * </ul>
 * <p>
 * Will not change once set:
 * <ul>
 * <li>Start Time</li>
 * <li>Stop Time</li>
 * <li>Elapsed Time - Can get a running time for elapsed time, but once the
 * timer is stopped, this will not change.</li>
 * <li>Status - Once stopped, cannot be set to any previous state.</li>
 * </ul>
 * 
 * <strong>Usage:</strong>
 * 
 * <p>
 * Simple - create and return a running Timer with no logging and task name of
 * "BuildResults".
 * 
 * <pre>
 * <code>
 *     Timer brTimer = new Timer("BuildResults");
 *     .
 *     taskProcessing
 *     .
 *     brTimer.stop();
 *     System.out.println("BuildResults: " + brTimer.getElapsedMillis());
 * </code>
 * Output: BuildResults: 1000
 * 
 * </pre>
 * 
 * More complex - create and return a non-running Timer with CSV logging to the
 * console and a set of domain specific notes. The CSV line along with the notes
 * will be written to stdout.
 * 
 * <pre>
 * <code>
 *     Timer serviceTimer = new Timer("BuildResults", true, new ConsoleTimeRecorder(TimerLogType.CSV));
 *     serviceTimer.setNotes(siteName, 4, user);
 *     serviceTimer.start();
 *     .
 *     taskProcessing
 *     .
 *     serviceTimer.stop();
 * </code>
 * Output: 1303060069651,BuildResults,main,1000,1000131000,0,TravelSite,4,member
 * 
 * </pre>
 * 
 * 
 * @NotThreadSafe
 * 
 * @author dhagberg
 * 
 */
public class Timer implements Serializable {
    
    private static final long serialVersionUID = -7476041689241631462L;
    /**
     * The name of the task for which the time is being recorded.
     */
    private final String taskName;
    /**
     * Wall clock start time for task. Time is recorded in milliseconds
     * (1.0E-3).
     * 
     * @see {@link System#currentTimeMillis()}
     */
    private long startTimeMillis;
    /**
     * Start time for task in nanoseconds (1.0E-9). Does <strong>not</strong>
     * represent wall clock time, but is used to measure elapsed time. Provides
     * nanosecond precision, but not accuracy.
     * 
     * @see {@link System#nanoTime()}
     */
    private long startTimeNanos;
    /**
     * Stop time for task in nanoseconds (1.0E-9). Does <strong>not</strong>
     * represent wall clock time, but is used to measure elapsed time. Provides
     * nanosecond precision, but not accuracy.
     * 
     * @see {@link System#nanoTime()}
     */
    private long stopTimeNanos;
    /**
     * Number of concurrent timers performing this task. This value is set by an
     * external source; therefore, the value may not accurately reflect the true
     * number of concurrent tasks.
     */
    private int concurrent;

    /**
     * Current status of timer.
     */
    private TimerStatus status;

    /**
     * What to do when the timer stops. Default is to do nothing.
     */
    private transient TimeRecorder timeRecorder;

    /**
     * Name of thread under which the timer creation was running.
     */
    private final String threadName;

    /**
     * Optional list of domain specific objects provided by user. These will be
     * displayed if provided.
     */
    private TimerNotes notes;

    /**
	 * Listener to notify when this timer is stopped.
	 */
    private transient TimerStoppedListener completionListener;

    /**
     * Construct an instance of Timer for the given task and start the timer.
     * 
     * @param taskName
     */
    public Timer(String taskName) {
        this(taskName, false, null);
    }

    /**
     * Construct an instance of Timer for the given task and optionally delay
     * the start of the timer.
     * 
     * @param taskName
     * @param delayStart
     * @param logType
     */
    public Timer(String taskName, boolean delayStart, TimeRecorder timeRecorder) {
        this.status = TimerStatus.INITIALIZED;
        this.taskName = taskName;
        this.timeRecorder = timeRecorder;
        this.threadName = Thread.currentThread().getName();
        if (!delayStart) {
            start();
        }
    }

    /**
     * Used by internal method constructing object from CSV file.
     * 
     * @param taskName
     * @param threadName
     */
    private Timer(String taskName, String threadName) {
        this.taskName = taskName;
        this.threadName = threadName;
    }

    /**
     * Start the time recording if not already started.
     */
    public void start() {
        if (status == TimerStatus.INITIALIZED) {
            startTimeNanos = System.nanoTime();
            startTimeMillis = System.currentTimeMillis();
            status = TimerStatus.RUNNING;
        }
    }

    /**
     * Stop the current time recording. Timer must be running and can be stopped
     * one time only. If stopped multiple times,only the first will be recorded.
     * <p>
     * The timer entering the stopped state triggers the optional reporting
     * processing such as logging and firing
     * {@link TimerStoppedListener#timerStopped(TimerStoppedEvent)}
     * 
     * @return the elapsed time in nanoseconds.
     */
    public long stop() {
        if (status == TimerStatus.RUNNING) {
            stopTimeNanos = System.nanoTime();
            status = TimerStatus.STOPPED;
            if (timeRecorder != null) {
                timeRecorder.record(this);
            }

            if (completionListener != null) {
                completionListener.timerStopped(new TimerStoppedEvent(this));
            }
        }

        return stopTimeNanos - startTimeNanos;
    }

    /**
     * Stop the current time recording. Timer must be running and can be stopped
     * one time only. If stopped multiple times,only the first will be recorded.
     * <p>
     * The timer entering the stopped state triggers the optional reporting
     * processing such as logging and firing
     * {@link TimerStoppedListener#timerStopped(TimerStoppedEvent)}
     * <p>
     * This overload of the stop method allows the notes to be placed on the
     * timer for inclusion in post processing and/or logging fired from this
     * action.
     * <p>
     * The notes are provided via the array of domain specific objects. These
     * values, if provided, will be displayed with the other timer values. As
     * all objects are stored as {@link Object}, any primitive passed in will be
     * auto boxed.
     * <p>
     * <strong>Note:</strong> Adding of notes incurs a cost due to auto boxing
     * of primitives and array creation for the variable arguments parameter.
     * This cost should be small, but for recording of very small intervals,
     * this could affect the measurements. If the notes are not needed for post
     * processing/logging, adding of notes should be done using
     * {@link #setNotes(Object...)}.
     * <p>
     * <strong>Note:</strong> This method will overwrite any current notes
     * already present.
     * 
     * @param notes
     *            List of domain specific values to store with the timer.
     * @return the elapsed time in nanoseconds.
     */
    public long stop(Object... notes) {
        return stop(false, notes);
    }

    /**
     * Stop the current time recording. Timer must be running and can be stopped
     * one time only. If stopped multiple times,only the first will be recorded.
     * <p>
     * The timer entering the stopped state triggers the optional reporting
     * processing such as logging and firing
     * {@link TimerStoppedListener#timerStopped(TimerStoppedEvent)}
     * <p>
     * This overload of the stop method allows the notes to be placed on the
     * timer for inclusion in post processing and/or logging fired from this
     * action.
     * <p>
     * The notes are provided via the array of domain specific objects. These
     * values, if provided, will be displayed with the other timer values. As
     * all objects are stored as {@link Object}, any primitive passed in will be
     * auto boxed.
     * <p>
     * <strong>Note:</strong> Adding of notes incurs a cost due to auto boxing
     * of primitives and array creation for the variable arguments parameter.
     * This cost should be small, but for recording of very small intervals,
     * this could affect the measurements. If the notes are not needed for post
     * processing/logging, adding of notes should be done using
     * {@link #setNotes(Object...)}.
     * <p>
     * <strong>Note:</strong> This method will overwrite any current notes
     * already present.
     * 
     * @param keyed
     *            true if notes are provided as key/value pairs (e.g.
     *            key1,val1,key2,val2...), false if all notes are values (e.g.,
     *            val1,val2,val3...).
     * @param notes
     *            list of either key/value pairs or values.
     * 
     * @return the elapsed time in nanoseconds.
     */
    public long stop(boolean keyed, Object... notes) {
        if (status == TimerStatus.RUNNING) {
            stopTimeNanos = System.nanoTime();
            status = TimerStatus.STOPPED;
            this.notes = new TimerNotes(keyed, notes);
            if (timeRecorder != null) {
                timeRecorder.record(this);
            }

            if (completionListener != null) {
                completionListener.timerStopped(new TimerStoppedEvent(this));
            }
        }

        return stopTimeNanos - startTimeNanos;
    }

    /**
     * Indicate whether or not to log the results of the timer upon completion.
     * See {@link TimeRecorder} for more information. Has no effect after the
     * timer has been stopped.
     * 
     * @param logType
     */
    public void setTimeRecorder(TimeRecorder timeRecorder) {
        this.timeRecorder = timeRecorder;
    }

    /**
     * Return the task name associated with this timer.
     * 
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Return the name of the thread under which this timer was created.
     * 
     * @return the taskName
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * Return the start time in milliseconds. This time represents wall clock
     * time. Timer must be be running or stopped prior to invoking this method.
     * 
     * @return the startTimeMillis
     */
    public long getStartTimeMillis() {
        if (status == TimerStatus.INITIALIZED) {
            throw new IllegalStateException("Timer has not been started");
        }
        return startTimeMillis;
    }

    /**
     * Return the start time in nanoseconds. This time does not represent wall
     * clock time and is used only to determine elapsed time. Timer must be be
     * running or stopped prior to invoking this method.
     * 
     * @return the startTimeNanos
     */
    public long getStartTimeNanos() {
        if (status == TimerStatus.INITIALIZED) {
            throw new IllegalStateException("Timer has not been started");
        }
        return startTimeNanos;
    }

    /**
     * Return the stop time in nanoseconds. This time does not represent wall
     * clock time and is used only to determine elapsed time. Timer must be
     * stopped prior to invoking this method.
     * 
     * @return the stopTimeNanos
     */
    public long getStopTimeNanos() {
        if (status != TimerStatus.STOPPED) {
            throw new IllegalStateException("Timer has not been started");
        }
        return stopTimeNanos;
    }

    /**
     * Return the number of concurrent timers recorded for a task. This value is
     * set by an external source; therefore, the value may not accurately
     * reflect the true number of concurrent tasks.
     * 
     * @return the number of concurrent timers recorded for a task.
     */
    public int getConcurrent() {
        return concurrent;
    }

    /**
     * Sets the number of concurrent timers recorded for a task.
     * 
     * @param concurrent
     *            the number of other timers with for this task at the time of
     *            its creation.
     */
    public void setConcurrent(int concurrent) {
        this.concurrent = concurrent;
    }

    /**
     * Return a string format of the current timer including the task name,
     * start time, elapsed time, and any notes associated with the timer. If the
     * timer has not yet completed, a value of -1 will be returned for the
     * elapsed time.
     * 
     * @see #setNotes(Object...)
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("Task: ").append(taskName);
        sb.append(" Start: ").append(new Date(startTimeMillis));
        sb.append(" Elapsed (ms): ").append(getElapsedMillis());
        sb.append(" Elapsed (ns): ").append(getElapsedNanos());
        if (notes != null) {
            sb.append(" Notes: ");
            for (int i = 0; i < notes.getLength(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(notes.getFormattedNote(i));
            }
        }
        return sb.toString();
    }

    /**
     * Return a CSV format of the current timer in the format: start time in
     * milliseconds, task name, thread name, elapsed milliseconds, elapsed
     * nanoseconds, concurrent count, and any associated notes. If the timer has
     * not yet completed, a value of -1 will be returned for the elapsed time.
     * 
     * @see #setConcurrent(int)
     * @see #setNotes(Object...)
     */
    public String toCsv() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(startTimeMillis);
        sb.append(",").append(taskName);
        sb.append(",").append(threadName);
        sb.append(",").append(getElapsedMillis());
        sb.append(",").append(getElapsedNanos());
        sb.append(",").append(getConcurrent());
        sb.append(",");
        if (notes != null) {
            sb.append(notes.toSingleValue());
        }
        return sb.toString();
    }

    /**
     * Return a CSV formatted string providing a header for the entries that
     * will be written if CSV logging is enabled. This may be used at the top of
     * a CSV file and will always match the format of the records.
     * 
     * @return
     */
    public static String getCsvHeader() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("start_time_ms");
        sb.append(",").append("task");
        sb.append(",").append("thread");
        sb.append(",").append("elapsed_ms");
        sb.append(",").append("elapsed_ns");
        sb.append(",").append("concurrent");
        sb.append(",").append("notes");
        return sb.toString();
    }

    /**
     * Create a {@link Timer} and populate the member variables using values
     * extracted from the CSV record. This {@link Timer} will be in a
     * {@link TimerStatus#STOPPED} state. Also, no handlers or completion
     * listeners will be added.
     * 
     * @param timerAsCsv
     * @return Timer from which values may be extracted.
     */
    public static Timer fromCsv(String timerAsCsv) {
        String[] values = timerAsCsv.split(",");
        Timer timer = new Timer(values[1], values[2]);
        timer.status = TimerStatus.STOPPED;
        timer.startTimeMillis = Long.parseLong(values[0].trim());
        timer.startTimeNanos = 0L; // nanoseconds start and stop are relative to
                                   // each other, not the wall clock.
        timer.stopTimeNanos = Long.parseLong(values[4].trim());
        timer.concurrent = Integer.parseInt(values[5].trim());
        if (values.length > 6) {
	        timer.notes = TimerNotes.parse(values[6]);
        }

        return timer;
    }

    /**
     * Return the elapsed time for this task in nanoseconds. If the task is not
     * yet complete, the value returned depends on the snapshotTime argument. If
     * snapshotTime is true, then the value returned will be the current time -
     * start time, otherwise a value of -1 will be returned.
     * 
     * @return elapsed time in nanoseconds or -1 if task is not yet complete and
     *         the snapshotTime indicator is set to false.
     */
    public long getElapsedNanos(boolean snapshotTime) {
        switch (status) {
        case INITIALIZED:
            throw new IllegalStateException("Timer has not been started");

        case RUNNING:
            return snapshotTime ? System.nanoTime() - startTimeNanos : -1;

        default:
            return stopTimeNanos - startTimeNanos;
        }

    }

    /**
     * Return the elapsed time for this task in nanoseconds if the timer has
     * been stopped. For currently running time use
     * {@link #getElapsedNanos(boolean)} with a value of true.
     * 
     * @return elapsed time in nanoseconds or -1 if task is not yet complete.
     */
    public long getElapsedNanos() {
        return getElapsedNanos(false);
    }

    /**
     * Return the elapsed time for this task in milliseconds. The task must have
     * been started prior to invoking this method.
     * 
     * @return elapsed time in milliseconds or -1 if task is not yet complete.
     */
    public long getElapsedMillis() {
        if (status == TimerStatus.INITIALIZED) {
            throw new IllegalStateException("Timer has not been started");
        }
        if (status == TimerStatus.STOPPED) {
            return (stopTimeNanos - startTimeNanos) / 1000000;
        } else {
            return -1;
        }
    }

    /**
     * Return the current state of the timer. See {@link TimerStatus} for the
     * different states.
     * 
     * @return
     * @see TimerStatus
     */
    public TimerStatus getStatus() {
        return status;
    }

    /**
     * Return array of domain specific objects.
     */
    public TimerNotes getNotes() {
        return notes;
    }

    /**
     * Optional array of domain specific objects. These will be displayed if
     * provided. As all objects are stored as {@link Object}, any primitive
     * passed in will be auto boxed.
     * <p>
     * The setNotes method is destructive in that it will overwrite any current
     * notes already present. The notes should be set once all of the applicable
     * notes have been accumulated.
     */
    public void setNotes(Object... notes) {
        this.notes = new TimerNotes(notes);
    }

    /**
     * Optional array of domain specific objects. These will be displayed if
     * provided. As all objects are stored as {@link Object}, any primitive
     * passed in will be auto boxed.
     * <p>
     * The setNotes method is destructive in that it will overwrite any current
     * notes already present. The notes should be set once all of the applicable
     * notes have been accumulated.
     */
    public void setNotes(boolean keyed, Object... notes) {
        this.notes = new TimerNotes(keyed, notes);
    }

    public static enum TimerStatus {
        INITIALIZED, RUNNING, STOPPED;
    }

    /**
     * Returns true if the timer is currently in a running state.
     * 
     * @return
     */
    public boolean isRunning() {
        return status == TimerStatus.RUNNING;
    }

    /**
     * Returns true if the timer has been stopped.
     * 
     * @return
     */
    public boolean isStopped() {
        return status == TimerStatus.STOPPED;
    }

    /**
     * Set the single completion listener for this timer. If one already exists,
     * it will be overridden.
     * <p>
     * <strong>NOTE:</strong> As the completion of the timer is most likely in
     * the current processing, the listener should return as quickly as possible
     * and with preferably no synchronization. For longer processing, look at
     * the {@link ActionChain} which provides a queue for processing timers off
     * of the main processing thread.
     * 
     * @param completionListener
     *            Completion listener or null to remove current listener.
     */
    public void setCompletionListener(TimerStoppedListener completionListener) {
        this.completionListener = completionListener;
    }

    /**
     * Update the status subject to state constraints.
     * 
     * @param proposedStatus
     * @return the current status.
     */
    protected TimerStatus updateStatus(TimerStatus proposedStatus) {
        switch (proposedStatus) {
        case RUNNING:
            if (status != TimerStatus.STOPPED) {
                status = TimerStatus.RUNNING;
            }
            break;
        case STOPPED:
            // Just set the status as either it is already Stopped or it is
            // allowed.
            status = proposedStatus;
            break;
        case INITIALIZED:
            // Nothing to do for initialized. Either it is already there or not
            // permitted.
            break;
        }
        return status;
    }
}
