package net.digitaltsunami.tmeter.record;

import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.action.ActionChain;

/**
 * Records the {@link Timer} for later processing.
 */
public interface TimeRecorder {
    /**
     * Record the time spent on the task and additional information recorded with the task. 
     * <p>
     * Performance Note: This method may be invoked during high load timings and
     * processing should be kept to a minimum. For recording options that
     * require more processing or resources that may cause swapping or delay,
     * the timer should instead be processed using the {@link ActionChain}.
     * 
     * @param timer Completed timer to record.
     */
    void record(Timer timer);
}
