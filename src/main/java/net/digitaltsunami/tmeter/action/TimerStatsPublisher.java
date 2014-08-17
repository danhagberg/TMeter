package net.digitaltsunami.tmeter.action;

import java.util.Collection;

import net.digitaltsunami.tmeter.TimerBasicStatistics;

/**
 * Interface to allow publishing of timer statistics.
 * 
 * @author dhagberg
 * 
 */
public interface TimerStatsPublisher {

    /**
     * Publish the provided {@link TimerBasicStatistics} currently recorded for
     * a single task.
     * 
     * @param stats
     *            Current statistics to publish.
     */
    public void publish(TimerBasicStatistics stats);

    /**
     * Publish if applicable that the timer stats processor has just been reset.
     * The listener is provided a copy of the stats at the moment before reset.
     * 
     * @param stats
     *            snapshot copy of all stats maintained by the
     *            {@link TimerStatsAction} prior to the reset.
     */
    public void reset(Collection<TimerBasicStatistics> stats);
}
