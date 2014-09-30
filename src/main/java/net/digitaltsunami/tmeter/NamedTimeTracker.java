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

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.digitaltsunami.tmeter.action.ActionChain;
import net.digitaltsunami.tmeter.action.TimerAction;
import net.digitaltsunami.tmeter.event.TimerStoppedEvent;
import net.digitaltsunami.tmeter.event.TimerStoppedListener;
import net.digitaltsunami.tmeter.level.TimerLevel;
import net.digitaltsunami.tmeter.level.TimerLevelCollection;
import net.digitaltsunami.tmeter.level.TimerLevelSet;
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
public class NamedTimeTracker {
    /**
     * Indicates whether or not we are keeping a list of all timers.
     */
    private boolean keepList;
    /**
     * Indicates whether or not we are keeping track of concurrent task count.
     */
    private boolean trackConcurrent;

    /**
     * Indicates how to record the timer when it is stopped. This setting will
     * be used as a default to set the corresponding entry on each timer as it
     * is created.
     */
    private TimeRecorder defaultTimeRecorder = NullTimeRecorder.getInstance();

    /**
     * List of all timers created since the keepList value was set to true.
     */
    private final List<Timer> timerList =
            Collections.synchronizedList(new ArrayList<Timer>());

    /**
     * Current count of timers by task name.
     */
    private final ConcurrentHashMap<String, AtomicInteger> concurrentMap =
            new ConcurrentHashMap<String, AtomicInteger>();

    /**
     * Singleton {@link Timer} used when time tracking is turned off.
     */
    private final Timer dummy = new TimerShell("TimerShellTask");

    /**
     * Indicates if tracking is disabled. If so, a {@link TimerShell} will be
     * returned by {@link #startRecording(String)}.
     */
    private boolean trackingDisabled;

    /**
     * Listener for {@link TimerStoppedEvent}. Will drive any configured
     * completion processing.
     */
    private final TimerStoppedEventHandler completionEventListener =
            new TimerStoppedEventHandler();

    /**
     * Indicates whether or not to register for the completion event when
     * creating new timers.
     */
    private boolean listenForCompletion;

    /**
     * Default {@link TimerLevel} used when creating {@link Timer} instances if
     * one is not provided.
     */
    private final TimerLevel DEFAULT_LEVEL = null;
    /**
     * Current set of timer levels that are active. If none provided, then all
     * timer levels are active by default.
     */
    private final TimerLevelCollection filter = new TimerLevelSet();
    
    /**
     * Manages post processing actions. 
     */
    private final ActionChain actionChain = new ActionChain();
    
    /**
     * Name used to refer to this time tracker instance.
     */
    private final String name;
    
    /**
     * Create an instance of a named time tracker with the given name.
     * @param name Name to identify time tracker.
     */
    public NamedTimeTracker(String name) {
        this.name = name;
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
    public Timer startRecording(TimerLevel level, String taskName) {
        // If not currently tracking time, return a shell so that invoking code
        // does not have to change.
        if (trackingDisabled) {
            return dummy;
        }

        // If a level was not provided, then skip over filtering for this timer,
        // otherwise ensure that the level active.
        if (level != DEFAULT_LEVEL) {
            if (!filter.isEnabled(level)) {
                return dummy;
            }
        }

        Timer timer = new Timer(taskName, true, defaultTimeRecorder, level);
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
    public Timer startRecording(String taskName) {
        return startRecording(DEFAULT_LEVEL, taskName);
    }

    /**
     * Indicates whether or not we are keeping a list of all timers.
     * 
     * @return the keepList
     */
    public boolean isKeepList() {
        return keepList;
    }

    /**
     * Indicates whether or not we are keeping a list of all timers.
     * 
     * @param keepList
     *            True to keep a list of timers, otherwise false.
     */
    public void setKeepList(boolean keepList) {
        this.keepList = keepList;
    }

    /**
     * Indicates whether or not we are keeping track of concurrent task count.
     * 
     * @return the trackConcurrent
     */
    public boolean isTrackConcurrent() {
        return trackConcurrent;
    }

    /**
     * Indicates whether or not we are keeping track of concurrent task count.
     * 
     * @param trackConcurrent
     *            True to keep track of concurrent task count, otherwise false.
     */
    public void setTrackConcurrent(boolean trackConcurrent) {
        this.trackConcurrent = trackConcurrent;
        // The decrement concurrent processing occurs in the listener, so it
        // must be enabled if tracking is enabled. Leave on if already on
        listenForCompletion = (listenForCompletion | trackConcurrent);
    }

    /**
     * Decrement the concurrent count for the provided task name.
     * 
     * @param taskName
     */
    private void decrementConcurrent(String taskName) {
        AtomicInteger current = concurrentMap.get(taskName);
        if (current != null) {
            current.decrementAndGet();
        }
    }

    /**
     * @return the default {@link TimeRecorder} used to populate the
     *         corresponding field when creating {@link Timer}s
     */
    public TimeRecorder getDefaultTimeRecorder() {
        return defaultTimeRecorder;
    }

    /**
     * Indicates the default method to record the timer when it is stopped. This
     * setting will be used to set the corresponding entry on each timer as it
     * is created. Changes to setting will affect only those timers created
     * after this invocation.
     * 
     * @param logType
     *            type of logging to occur on timer completion.
     */
    public void setDefaultTimeRecorder(TimeRecorder defaultTimeRecorder) {
        this.defaultTimeRecorder = defaultTimeRecorder;
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
    public void clear() {
        timerList.clear();
        concurrentMap.clear();
        actionChain.reset();
    }

    /**
     * Indicates if tracking is disabled. If so, a {@link TimerShell} will be
     * returned by {@link #startRecording(String)}.
     * 
     * @return
     */
    public boolean isTrackingDisabled() {
        return trackingDisabled;
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
     */
    public void setTrackingDisabled(boolean trackingDisabled) {
        this.trackingDisabled = trackingDisabled;
    }

    /**
     * Shutdown all time tracking related processing threads.
     */
    public void shutdown() {
        if (actionChain != null) {
            actionChain.shutdown();
        }
        listenForCompletion = false;
        // TODO: This needs to be moved somewhere.  Where?
        defaultTimeRecorder.prepareForShutdown();
        
    }

    /**
     * Return the current post completion action processor.
     * 
     * @return
     */
    public ActionChain getActionChain() {
        // TODO: should this return: actionChain, a clone, nothing?
        return actionChain;
    }

    /**
     * Set the current post completion action processor.
     */
    public void setActionChain(ActionChain newActionChain) {
        if (newActionChain != null) {
	        for (TimerAction action : newActionChain.getActions()) {
	            actionChain.addAction(action);
	        }
        }

        // Activate the timer completion listener as this is where interaction
        // with the action processor occurs.
        listenForCompletion = true;
    }

    /**
     * Add an action to the chain of actions that will be performed upon each
     * timer completion.
     * 
     * @param action
     */
    public void addCompletionAction(TimerAction action) {
        actionChain.addAction(action);
        listenForCompletion = true;
    }

    /**
     * Returns a copy of the current list of {@link Timer} entries. The copy is
     * a shallow copy; therefore, the instances may change after they are
     * returned. See {@link Timer} for a list of invariants.
     * 
     * @return a snapshot of the current list of timers.
     */
    public Timer[] getCurrentTimers() {
        return timerList.toArray(new Timer[0]);
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
    public TimerLevel enableTimerLevel(TimerLevel level) {
        return filter.addLevel(level);
    }

    /**
     * Enable all provided {@link TimerLevel}s for recording. All subsequent
     * timer requests enabled for these levels will start a timer recording.
     * 
     * @param levels
     *            to enable for recording.
     * @return
     * @see TimerLevelCollection
     * @see #setTrackingDisabled(boolean)
     */
    public void enableTimerLevels(TimerLevel... levels) {
        filter.addLevels(levels);
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
    public boolean disableTimerLevel(TimerLevel level) {
        return filter.removeLevel(level);
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
    public boolean disableTimerLevels(TimerLevel... levels) {
        boolean disabled = false;
        for (TimerLevel level : levels) {
            disabled |= filter.removeLevel(level);
        }
        return disabled;
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
    public void clearTimerLevels() {
        filter.clear();
    }

    /**
     * Clear out the action chain. This will cause the action chain to complete
     * processing and then terminate.
     */
    public void clearActionChain() {
        actionChain.clearActions();
    }

    /**
     * Listener for timers being stopped.
     * 
     * @author dhagberg
     */
    class TimerStoppedEventHandler implements TimerStoppedListener {
        @Override
        public void timerStopped(TimerStoppedEvent event) {
            Timer timer = event.getTimer();
            if (isTrackConcurrent()) {
                decrementConcurrent(timer.getTaskName());
            }
            if (listenForCompletion) {
                actionChain.submitCompletedTimer(timer);
            }
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NamedTimeTracker)) {
            return false;
        }
        NamedTimeTracker other = (NamedTimeTracker) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
        
}
