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

import java.util.concurrent.ConcurrentHashMap;

import net.digitaltsunami.tmeter.action.ActionChain;
import net.digitaltsunami.tmeter.action.ActionChainShutdownType;
import net.digitaltsunami.tmeter.action.TimerAction;
import net.digitaltsunami.tmeter.level.TimerLevel;
import net.digitaltsunami.tmeter.level.TimerLevelCollection;
import net.digitaltsunami.tmeter.record.NullTimeRecorder;
import net.digitaltsunami.tmeter.record.TimeRecorder;

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
 * Recording Compeletion - Recording of timer results is provided using
 * {@link TimeRecorder} specified via
 * {@link #setDefaultTimeRecorder(TimeRecorder)}. The default is set to
 * {@link NullTimeRecorder}, which will not record results. Logging can be
 * enabled to direct the timers to log on completion. Logging can be directed to
 * the console or a file. Current logging styles are text and csv. See
 * {@link Timer#toString()} and {@link Timer#toCsv()} for formats.
 * <p>
 * Action chain - Post processing of completed timers can be taken out of the
 * processing thread by use of an {@link ActionChain}. The Action Chain provides
 * a queue for processing a user provided chain of {@link TimerAction}s that
 * enable more complex processing of the timer data. See {@link ActionChain} for
 * more information.
 * <p>
 * Disabling - Allows the creation of timers to be enabled/disabled during
 * processing so that the timer logic can be left in place with very little
 * overhead when disabled. When disabled, a single instance of
 * {@link TimerShell} will be returned for all timer recording requests.
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
    private static final NamedTimeTracker common = new NamedTimeTracker("_TT_COMMON_");
    private static final ConcurrentHashMap<String, NamedTimeTracker> timeTrackers = new ConcurrentHashMap<String, NamedTimeTracker>();

    /**
     * No instances of TimeTracker
     */
    private TimeTracker() {
        // No instances
    }

    /**
     * Retrieve a {@link NamedTimeTracker} specified by the provided name. If no
     * entry is found for that name, then one will be created and stored under
     * that name for future retrieval.
     * 
     * @param name
     *            Name of time tracker instance to retrieve.
     * @return instance of {@link NamedTimeTracker} stored under the name
     *         provided.
     */
    public static NamedTimeTracker named(String name) {
        NamedTimeTracker tracker = timeTrackers.get(name);
        if (tracker == null) {
            // Task not found in list. Create a new counter
            tracker = new NamedTimeTracker(name);
            // Place it in the map
            NamedTimeTracker existing = timeTrackers.putIfAbsent(name,
                    tracker);
            // If the same entry was not returned, another thread created
            // during setup. Use the existing entry.
            if (existing != null && existing != tracker) {
                tracker = existing;
            }
        }
        return tracker;
    }

    /**
     * Create and configure a {@link Timer} instance as applicable. All
     * configuration settings are done prior to starting the timer to reduce the
     * effect of the overhead in the timing results.
     * <p>
     * If timing is disabled, an instance of {@link TimerShell} will be returned
     * so that the timing code can be left in place.
     * <p>
     * If a timer level is provided, it will be checked against the current
     * filter. If not enabled, then an instance of {@link TimerShell} will be
     * returned.
     * 
     * @param taskName
     *            Name used to represent a given task.
     * @param level
     *            {@link TimerLevel} of timer requested. Will be used to
     *            determine if the timer is enabled for the request.
     * @return instance of {@link Timer} configured based on current settings.
     * @see #setTrackingDisabled(boolean)
     */
    public static Timer startRecording(TimerLevel level, String taskName) {
        return common.startRecording(level, taskName);
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
        return common.startRecording(taskName);
    }

    /**
     * Indicates whether or not we are keeping a list of all timers.
     * 
     * @return the keepList
     */
    public static boolean isKeepList() {
        return common.isKeepList();
    }

    /**
     * Indicates whether or not we are keeping a list of all timers.
     * 
     * @param keepList
     *            True to keep a list of timers, otherwise false.
     */
    public static void setKeepList(boolean keepList) {
        common.setKeepList(keepList);
    }

    /**
     * Indicates whether or not we are keeping track of concurrent task count.
     * 
     * @return the trackConcurrent
     */
    public static boolean isTrackConcurrent() {
        return common.isTrackConcurrent();
    }

    /**
     * Indicates whether or not we are keeping track of concurrent task count.
     * 
     * @param trackConcurrent
     *            True to keep track of concurrent task count, otherwise false.
     */
    public static void setTrackConcurrent(boolean trackConcurrent) {
        common.setTrackConcurrent(trackConcurrent);
    }

    /**
     * @return the default {@link TimeRecorder} used to populate the
     *         corresponding field when creating {@link Timer}s
     */
    public static TimeRecorder getDefaultTimeRecorder() {
        return common.getDefaultTimeRecorder();
    }

    /**
     * Indicates the default method to record the timer when it is stopped. This
     * setting will be used to set the corresponding entry on each timer as it
     * is created. Changes to setting will affect only those timers created
     * after this invocation.
     * 
     * @param defaultTimeRecorder the default {@link TimeRecorder} used to populate
     *                            the corresponding field when creating {@link Timer}s
     */
    public static void setDefaultTimeRecorder(TimeRecorder defaultTimeRecorder) {
        common.setDefaultTimeRecorder(defaultTimeRecorder);
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
        common.clear();
    }

    /**
     * Indicates if tracking is disabled. If so, a {@link TimerShell} will be
     * returned by {@link #startRecording(String)}.
     * 
     * @return
     */
    public static boolean isTrackingDisabled() {
        return common.isTrackingDisabled();
    }

    /**
     * Set tracking to enabled/disabled. If disabled, a {@link TimerShell} will
     * be returned by {@link #startRecording(String)}.
     * <p>
     * Default is enabled.
     * <p>
     * If tracking is disabled, then all {@link TimerLevel}s enabled for this
     * session are disabled as well. If tracking is re-enabled, then the current
     * set of enabled timer levels will be become active.
     * 
     * @param trackingDisabled
     *            true if time tracking should be disabled.
     */
    public static void setTrackingDisabled(boolean trackingDisabled) {
        common.setTrackingDisabled(trackingDisabled);
    }

    /**
     * Shutdown all time tracking related processing threads connected to the
     * common time tracker.
     */
    public static void shutdown() {
        common.shutdown();
    }

    /**
     * Shutdown all {@link NamedTimeTracker} instances, including the common one
     * maintained by TimeTracker.
     */
    public static void shutdownAllTimeTrackers() {
        for (NamedTimeTracker timeTracker : TimeTracker.timeTrackers.values()) {
            timeTracker.shutdown();
        }
        common.shutdown();
    }

    /**
     * Return the current post completion action processor.
     * 
     * @return
     */
    public static ActionChain getActionChain() {
        return common.getActionChain();
    }

    /**
     * Set the current post completion action processor.
     */
    public static void setActionChain(ActionChain newActionChain) {
        common.setActionChain(newActionChain);
    }

    /**
     * Add an action to the chain of actions that will be performed upon each
     * timer completion.
     * 
     * @param action
     */
    public static void addCompletionAction(TimerAction action) {
        common.addCompletionAction(action);
    }

    /**
     * Returns a copy of the current list of {@link Timer} entries. The copy is
     * a shallow copy; therefore, the instances may change after they are
     * returned. See {@link Timer} for a list of invariants.
     * 
     * @return a snapshot of the current list of timers.
     */
    public static Timer[] getCurrentTimers() {
        return common.getCurrentTimers();
    }

    /**
     * Enable a {@link TimerLevel} for recording. All subsequent timer requests
     * enabled for this level will start a timer recording.
     * 
     * @param level
     *            to enable for recording.
     * @return timer level currently in filter if matches new level or null if
     *         no matching timer. See {@link TimerLevelCollection} for
     *         information on possible return values.
     * @see TimerLevelCollection
     * @see #setTrackingDisabled(boolean)
     */
    public static TimerLevel enableTimerLevel(TimerLevel level) {
        return common.enableTimerLevel(level);
    }

    /**
     * Enable all provided {@link TimerLevel}s for recording. All subsequent
     * timer requests enabled for these levels will start a timer recording.
     * 
     * @param levels
     *            to enable for recording.
     * @see TimerLevelCollection
     * @see #setTrackingDisabled(boolean)
     */
    public static void enableTimerLevels(TimerLevel... levels) {
        common.enableTimerLevels(levels);
    }

    /**
     * Disable a {@link TimerLevel} for recording. Subsequent requests will not
     * be enabled for this level and recording will not be started.
     * 
     * @param level
     *            to disable timer recording.
     * @return true if level matched an existing entry and was removed.
     * @see TimerLevelCollection
     * @see #setTrackingDisabled(boolean)
     */
    public static boolean disableTimerLevel(TimerLevel level) {
        return common.disableTimerLevel(level);
    }

    /**
     * Disable provided {@link TimerLevel}s for recording. Subsequent requests
     * will not be enabled for these levels and recording will not be started.
     * 
     * @param levels
     *            to disable timer recording.
     * @return true if any level matched an existing entry and was removed.
     * @see TimerLevelCollection
     * @see #setTrackingDisabled(boolean)
     */
    public static boolean disableTimerLevels(TimerLevel... levels) {
        return common.disableTimerLevels(levels);
    }

    /**
     * Disable all {@link TimerLevel}s for recording. Subsequent requests will
     * not be enabled for any levels and recording will not be started.
     * <p>
     * Note: This does non include timers with out a timer level as those are
     * not affected by the filter.
     * 
     * @see TimerLevelCollection
     * @see #setTrackingDisabled(boolean)
     */
    public static void clearTimerLevels() {
        common.clearTimerLevels();
    }

    /**
     * Clear out the action chain. This will cause the action chain to complete
     * processing and then terminate.
     */
    public static void clearActionChain() {
        common.clearActionChain();
    }
}