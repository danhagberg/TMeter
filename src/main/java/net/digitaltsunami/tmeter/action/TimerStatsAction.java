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
/**
 * 
 */
package net.digitaltsunami.tmeter.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.TimerBasicStatistics;

/**
 * An implementation of {@link TimerAction} that provides basic statistics for all
 * timers processed by this action.  Basic statistics may be retrieved for a specific
 * task or the entire set of tasks. 
 * @author dhagberg 
 * 
 */
public class TimerStatsAction extends TimerAction {

    private final ConcurrentHashMap<String, TimerBasicStatistics> statsByTask;

    public TimerStatsAction() {
        super();
        statsByTask = new ConcurrentHashMap<String, TimerBasicStatistics>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.digitaltsunami.tmeter.TimeAction#timerCompleted(net.digitaltsunami.tmeter.Timer)
     */
    @Override
    protected void processTimer(Timer timer) {
        TimerBasicStatistics stat = statsByTask.get(timer.getTaskName());
        if (stat == null) {
            // Task not found in list. Create a new entry
            stat = new TimerBasicStatistics(timer.getTaskName());
            // Place it in the map
            TimerBasicStatistics currentStat = statsByTask.putIfAbsent(timer.getTaskName(),
                        stat);
            // If the same entry was not returned, another thread created
            // during setup. Use the existing entry.
            if (currentStat != null && currentStat != stat) {
                stat = currentStat;
            }
        }
        stat.addTimer(timer);
    }
    
    /**
     * Clears all accumulated statistics.
     * 
     * @see net.digitaltsunami.tmeter.action.TimerAction#reset()
     */
    @Override
    protected void reset() {
        statsByTask.clear();
    }

    /**
     * Return a live instance of {@link TimerBasicStatistics} for the provided
     * task name. Internal values will be modified as this action processes
     * timers.
     * 
     * @param taskName
     * @return Live instance of {@link TimerBasicStatistics} or null if that
     *         task has not yet been processed by this action.
     */
    public TimerBasicStatistics getTimerStatistics(String taskName) {
        return statsByTask.get(taskName);
    }

    /**
     * Return a snapshot instance of {@link TimerBasicStatistics} for the
     * provided task name. Internal values will be not be modified as this
     * action processes timers.
     * 
     * @param taskName
     * @return Snapshot of {@link TimerBasicStatistics} for task name or null if
     *         that task has not yet been processed by this action.
     */
    public TimerBasicStatistics getTimerStatisticsSnapshot(String taskName) {
        TimerBasicStatistics stat = statsByTask.get(taskName);
        return stat == null ? null : stat.getSnapshot();
    }
    
    /**
     * Return a collection of live instances of {@link TimerBasicStatistics} for
     * all tasks processed up to the point of invoking this method. Internal
     * values will be modified as this action processes timers.
     * 
     * @return Live instance of {@link TimerBasicStatistics}
     */
    public Collection<TimerBasicStatistics> getAllTimerStatistics() {
        return new HashSet<TimerBasicStatistics>(statsByTask.values());

    }

    /**
     * Return a collection of snapshot instances of {@link TimerBasicStatistics}
     * for all tasks processed up to the point of invoking this method. Internal
     * values will be modified as this action processes timers.
     * 
     * @return Snapshot instance of {@link TimerBasicStatistics}
     */
    public Collection<TimerBasicStatistics> getAllTimerStatisticsSnapshot() {
        // Create a working copy to avoid concurrent modification exception while building copy.
        Set<TimerBasicStatistics> workingCopy = new HashSet<TimerBasicStatistics>(statsByTask.values());
        Set<TimerBasicStatistics> snapshotSet = new HashSet<TimerBasicStatistics>(workingCopy.size());
        for(TimerBasicStatistics stat : workingCopy) {
            snapshotSet.add(stat.getSnapshot());
        }
        return snapshotSet;
    }

}
