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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.digitaltsunami.tmeter.action.ActionChain;
import net.digitaltsunami.tmeter.action.TimerAction;
import net.digitaltsunami.tmeter.event.TimerStoppedEvent;
import net.digitaltsunami.tmeter.event.TimerStoppedListener;

/**
 * A framework for recording elapsed time to perform a given task and to drive
 * processing of the completed timers.
 * <p>
 * Provides options to allow the recording process to be configured for low to
 * high volume recording.
 * <p>
 * List - Can be configured to keep a list of all timers started via this class.
 * This can be used when logging would cause too much overhead such as high
 * volume recordings.
 * <ul>
 * <li>A snapshot of the results can be returned during the run.
 * <li>The results may be cleared during the run. If the testing is done in
 * batches, then the results can be processed and the list cleared between
 * batches. This will reduce the memory requirements of the timer framework
 * </ul>
 * <p>
 * Logging - Logging can be enabled to direct the timers to log on completion.
 * Logging can be directed to the console or a file. Current logging styles are
 * text and csv. See {@link Timer#toString()} and {@link Timer#toCsv()} for
 * formats.
 * <p>
 * Action chain - Post processing of completed timers can be taken out of the
 * processing thread by use of an {@link ActionChain}. The Action Chain provides
 * a queue for processing a user provided chain of {@link TimerAction}s that
 * enable more complex processing of the timer data. See {@link ActionChain} for
 * more information.
 * <p>
 * Disabling - Allows the creation of timers to be enabled/disabled during
 * processing so that the timer logic can be left in place with very little overhead
 * when disabled. When disabled, a single instance of {@link TimerShell} will be
 * returned for all timer recording requests.
 * <p>
 * Concurrent counts - Can maintain concurrent task counts if enabled. This
 * provides a rudimentary concurrent count for all timers recording the same
 * task - as determined by task name. The count is based on the number of
 * currently running tasks for a task name at the time the Timer was created.
 * 
 * 
 * @author dhagberg
 * 
 */
public class TimeTracker {
    /**
     * Indicates whether or not we are keeping a list of all timers.
     */
    private static boolean keepList;
    /**
     * Indicates whether or not we are keeping track of concurrent task count.
     */
    private static boolean trackConcurrent;

    /**
     * Indicates how to log the timer when it is stopped. This setting will be
     * used to set the corresponding entry on each timer as it is created.
     */
    private static TimerLogType logType = TimerLogType.NONE;

    /**
     * List of all timers created since the keepList value was set to true.
     */
    private static final List<Timer> timerList =
            Collections.synchronizedList(new ArrayList<Timer>());

    /**
     * Current count of timers by task name.
     */
    private static final ConcurrentHashMap<String, AtomicInteger> concurrentMap =
            new ConcurrentHashMap<String, AtomicInteger>();

    /**
     * Singleton {@link Timer} used when time tracking is turned off.
     */
    private static final Timer dummy = new TimerShell("TimerShellTask");

    /**
     * Indicates if tracking is disabled. If so, a {@link TimerShell} will be
     * returned by {@link #startRecording(String)}.
     */
    private static boolean trackingDisabled;

    /**
     * A set of actions configured to process completed timers.
     */
    private static ActionChain actionChain;

    /**
     * Listener for {@link TimerStoppedEvent}. Will drive any configured
     * completion processing.
     */
    private static final TimerStoppedEventHandler completionEventListener =
            new TimerStoppedEventHandler();

    /**
     * Indicates whether or not to register for the completion event when
     * creating new timers.
     */
    private static boolean listenForCompletion;

    /**
     * No instances of TimeTracker
     */
    private TimeTracker() {
        // No instances
    }

    /**
     * Create and configure a {@link Timer} instance as applicable. All
     * configuration settings are done prior to starting the timer to reduce the
     * effect of the overhead in the timing results.
     * <p>
     * If timing is disabled, an instance of {@link TimerShell} will be returned
     * so that the timing code can be left in place.
     * 
     * @param taskName
     *            Name used to represent a given task.
     * @return instance of {@link Timer} configured based on current settings.
     * @see #setTrackingDisabled(boolean)
     */
    public static Timer startRecording(String taskName) {
        // If not currently tracking time, return a shell so that invoking code
        // does not have to change.
        if (trackingDisabled) {
            return dummy;
        }

        Timer timer = new Timer(taskName, true, logType);
        // Do all time intensive settings prior to starting time
        // keeping list
        if (keepList) {
            timerList.add(timer);
        }
        // tracking concurrent
        if (trackConcurrent) {
            AtomicInteger concurrent = concurrentMap.get(taskName);
            if (concurrent == null) {
                // Task not found in list. Create a new counter
                concurrent = new AtomicInteger();
                // Place it in the map
                AtomicInteger entry = concurrentMap.putIfAbsent(taskName,
                        concurrent);
                // If the same entry was not returned, another thread created
                // during setup. Use the existing entry.
                if (entry != null && entry != concurrent) {
                    concurrent = entry;
                }
            }
            timer.setConcurrent(concurrent.incrementAndGet());
        }
        if (listenForCompletion) {
            timer.setCompletionListener(completionEventListener);
        }
        timer.start();
        return timer;
    }

    /**
     * Indicates whether or not we are keeping a list of all timers.
     * 
     * @return the keepList
     */
    public static boolean isKeepList() {
        return keepList;
    }

    /**
     * Indicates whether or not we are keeping a list of all timers.
     * 
     * @param keepList
     *            True to keep a list of timers, otherwise false.
     */
    public static void setKeepList(boolean keepList) {
        TimeTracker.keepList = keepList;
    }

    /**
     * Indicates whether or not we are keeping track of concurrent task count.
     * 
     * @return the trackConcurrent
     */
    public static boolean isTrackConcurrent() {
        return trackConcurrent;
    }

    /**
     * Indicates whether or not we are keeping track of concurrent task count.
     * 
     * @param trackConcurrent
     *            True to keep track of concurrent task count, otherwise false.
     */
    public static void setTrackConcurrent(boolean trackConcurrent) {
        TimeTracker.trackConcurrent = trackConcurrent;
        // The decrement concurrent processing occurs in the listener, so it
        // must be enabled if tracking is enabled. Leave on if already on
        listenForCompletion = (listenForCompletion | trackConcurrent);
    }

    /**
     * Decrement the concurrent count for the provided task name.
     * 
     * @param taskName
     */
    private static void decrementConcurrent(String taskName) {
        AtomicInteger current = concurrentMap.get(taskName);
        if (current != null) {
            current.decrementAndGet();
        }
    }

    /**
     * @return the logType
     */
    public static TimerLogType getLogType() {
        return logType;
    }

    /**
     * Indicates how to log the timer when it is stopped. This setting will be
     * used to set the corresponding entry on each timer as it is created.
     * Changes to setting will affect only those timers created after this
     * invocation.
     * 
     * @param logType
     *            type of logging to occur on timer completion.
     */
    public static void setLogType(TimerLogType logType) {
        TimeTracker.logType = logType;
    }

    /**
     * Clears accumulated state. All configuration settings will remain intact.
     * The values cleared depend on the settings, but may include:
     * <ul>
     * <li>List of timers</li>
     * <li>Concurrent counts</li>
     * <li>Data within {@link TimerAction} instances within the
     * {@link ActionChain}. This will affect only the data and the action chain
     * will remain intact.</li>
     * </ul>
     * <p>
     */
    public static void clear() {
        timerList.clear();
        concurrentMap.clear();
        if (actionChain != null) {
            actionChain.reset();
        }
    }

    /**
     * Indicates if tracking is disabled. If so, a {@link TimerShell} will be
     * returned by {@link #startRecording(String)}.
     * 
     * @return
     */
    public static boolean isTrackingDisabled() {
        return trackingDisabled;
    }

    /**
     * Set tracking to enabled/disabled. If so, a {@link TimerShell} will be
     * returned by {@link #startRecording(String)}.
     * <p>
     * Default is enabled.
     */
    public static void setTrackingDisabled(boolean trackingDisabled) {
        TimeTracker.trackingDisabled = trackingDisabled;
    }

    /**
     * Return the current post completion action processor.
     * 
     * @return
     */
    public static ActionChain getActionChain() {
        return actionChain;
    }

    /**
     * Set the current post completion action processor.
     */
    public static void setActionChain(ActionChain newActionChain) {
        actionChain = newActionChain;
        // Activate the timer completion listener as this is where interaction
        // with the action processor occurs.
        listenForCompletion = true;
    }

    /**
     * Returns a copy of the current list of {@link Timer} entries. The copy is
     * a shallow copy; therefore, the instances may change after they are
     * returned. See {@link Timer} for a list of invariants.
     * 
     * @return a snapshot of the current list of timers.
     */
    public static Timer[] getCurrentTimers() {
        return timerList.toArray(new Timer[0]);
    }

    /**
     * Set the output for all Timer instances. Defaults to stdout. If the timer
     * should be written to an output stream other than stdout, it should be set
     * prior to the first invocation.
     * 
     * @param out
     *            PrintStream to which timer output will be written if logging is
     *            set to log upon stop.
     * @see #setLogType(TimerLogType)
     * @see Timer#setOut(PrintStream)
     */
    public static void setLogOut(PrintStream out) {
        Timer.setOut(out);
    }

    /**
     * Listener for timers being stopped.
     * 
     * @author dhagberg
     */
    static class TimerStoppedEventHandler implements TimerStoppedListener {
        @Override
        public void timerStopped(TimerStoppedEvent event) {
            Timer timer = event.getTimer();
            if (TimeTracker.isTrackConcurrent()) {
                TimeTracker.decrementConcurrent(timer.getTaskName());
            }
            if (actionChain != null) {
                actionChain.submitCompletedTimer(timer);
            }
        }
    }
}
