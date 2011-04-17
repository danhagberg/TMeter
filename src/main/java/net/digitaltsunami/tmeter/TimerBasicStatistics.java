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

import java.util.concurrent.TimeUnit;

/**
 * Maintains basic statistics for a given task. The maintained statistics are
 * basic so that it does not have to maintain a list of the elapsed values.
 * <p>
 * Currently tracked statistics are:
 * <ul>
 * <li>Count</li>
 * <li>Min Elapsed Time</li>
 * <li>Max Elapsed Time</li>
 * <li>Total Elapsed Time</li>
 * <li>Average Elapsed Time</li>
 * </ul>
 * 
 * @author dhagberg
 * 
 */
public class TimerBasicStatistics implements Comparable<TimerBasicStatistics> {

    private final String taskName;
    private volatile int count;
    private volatile long totalElapsedNanos;
    private volatile long minElapsedNanos = Long.MAX_VALUE;
    private volatile long maxElapsedNanos = Long.MIN_VALUE;

    /**
     * Create an empty statistics instance for the task.
     * 
     * @param taskName
     */
    public TimerBasicStatistics(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Create a statistics instance and initialize with the values from the
     * {@link Timer} provided.
     * 
     * @param timer
     */
    public TimerBasicStatistics(Timer timer) {
        this.taskName = timer.getTaskName();
        addTimer(timer);
    }

    /**
     * Add the results of a timer to the currently tracked statistics for this
     * timer.
     * 
     * @param timer
     * @throws IllegalArgumentException
     *             if the {@link Timer#getTaskName()} does not match
     *             {@link #taskName}
     */
    public void addTimer(Timer timer) {
        if (!taskName.equals(timer.getTaskName())) {
            throw new IllegalArgumentException(
                    "Task Statistics belong to a different task than provided timer");
        }
        synchronized (this) {
            count++;
            totalElapsedNanos += timer.getElapsedNanos();
            minElapsedNanos = Math.min(minElapsedNanos, timer.getElapsedNanos());
            maxElapsedNanos = Math.max(maxElapsedNanos, timer.getElapsedNanos());
        }
    }

    /**
     * Return the average elapsed time recorded for this task in nanoseconds.
     * <p>
     * Guarded by this as it requires the current setting of both count and
     * total elapsed time.
     * 
     * @return average elapsed time in nanoseconds.
     */
    public synchronized double getAverageElapsedNanos() {
        return (count == 0) ? 0.0 : totalElapsedNanos / count;
    }

    /**
     * Return the average elapsed time recorded for this task. The time will be
     * returned in the {@link TimeUnit} provided.
     * <p>
     * Guarded by this as it requires the current setting of both count and
     * total elapsed time.
     * 
     * @param timeUnit
     *            Desired time unit for returned value.
     * @return average elapsed time in {@link TimeUnit} provided.
     */
    public synchronized double getAverageElapsed(TimeUnit timeUnit) {
        if (count == 0) {
            return 0.0;
        } else {
            return timeUnit.convert(totalElapsedNanos, TimeUnit.NANOSECONDS) / count;
        }
    }

    /**
     * Return the task name for which the statistics are being tracked.
     * 
     * @return
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Return the number of times this instance has been updated with timer
     * values.
     * 
     * @return
     */
    public int getCount() {
        return count;
    }

    /**
     * Return the accumulated number of elapsed nanoseconds recorded for this
     * task.
     * 
     * @return
     */
    public long getTotalElapsedNanos() {
        return totalElapsedNanos;
    }

    /**
     * Return the accumulated time recorded for this task. The time will be
     * returned in the {@link TimeUnit} provided.
     * 
     * @param timeUnit
     *            Desired time unit for returned value.
     * @return
     */
    public long getTotalElapsed(TimeUnit timeUnit) {
        return timeUnit.convert(totalElapsedNanos, TimeUnit.NANOSECONDS);
    }

    /**
     * Return the minimum number of elapsed nanoseconds recorded for this task.
     * 
     * @return Minimum elapsed time in nanoseconds for this this task or
     *         {@link Long#MAX_VALUE} if no timers have yet been recorded.
     */
    public long getMinElapsedNanos() {
        return minElapsedNanos;
    }

    /**
     * Return the minimum time recorded for this task. The time will be returned
     * in the {@link TimeUnit} provided.
     * 
     * @param timeUnit
     *            Desired time unit for returned value.
     * @return
     */
    public long getMinElapsed(TimeUnit timeUnit) {
        return timeUnit.convert(minElapsedNanos, TimeUnit.NANOSECONDS);
    }

    /**
     * Return the maximum number of elapsed nanoseconds recorded for this task.
     * 
     * @return Maximum elapsed time in nanoseconds for this this task or
     *         {@link Long#MIN_VALUE} if no timers have yet been recorded.
     */
    public long getMaxElapsedNanos() {
        return maxElapsedNanos;
    }

    /**
     * Return the maximum time recorded for this task. The time will be returned
     * in the {@link TimeUnit} provided.
     * 
     * @param timeUnit
     *            Desired time unit for returned value.
     * @return
     */
    public long getMaxElapsed(TimeUnit timeUnit) {
        return timeUnit.convert(maxElapsedNanos, TimeUnit.NANOSECONDS);
    }

    /**
     * Creates a snapshot of the instance and returns as a new instance. These
     * values will not be updated as more timers are processed.
     * 
     * @return new instance of {@link TimerBasicStatistics} that is a snapshot
     *         at the time returned.
     */
    public TimerBasicStatistics getSnapshot() {
        TimerBasicStatistics copy = new TimerBasicStatistics(taskName);
        synchronized (this) {
            copy.count = count;
            copy.minElapsedNanos = minElapsedNanos;
            copy.maxElapsedNanos = maxElapsedNanos;
            copy.totalElapsedNanos = totalElapsedNanos;
        }
        return copy;
    }

    @Override
    public int compareTo(TimerBasicStatistics o) {
        return taskName.compareTo(o.taskName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode() Eclipse generated
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((taskName == null) ? 0 : taskName.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object) Eclipse generated
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TimerBasicStatistics)) {
            return false;
        }
        TimerBasicStatistics other = (TimerBasicStatistics) obj;
        if (taskName == null) {
            if (other.taskName != null) {
                return false;
            }
        } else if (!taskName.equals(other.taskName)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TimerBasicStatistics [taskName=" + taskName + ", count=" + count
                + ", totalElapsedNanos=" + totalElapsedNanos + ", minElapsedNanos="
                + minElapsedNanos + ", maxElapsedNanos=" + maxElapsedNanos + "]";
    }

}
