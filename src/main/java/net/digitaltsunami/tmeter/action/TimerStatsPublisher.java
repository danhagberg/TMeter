package net.digitaltsunami.tmeter.action;

import java.util.Collection;

import net.digitaltsunami.tmeter.TimerBasicStatistics;

public interface TimerStatsPublisher {
    
    /**
     * Publish the provided {@link TimerBasicStatistics}. 
     * @param stats Current statistics to publish. 
     */
    public void publish(TimerBasicStatistics stats);
    
    public void reset(Collection<TimerBasicStatistics> stats);
}
