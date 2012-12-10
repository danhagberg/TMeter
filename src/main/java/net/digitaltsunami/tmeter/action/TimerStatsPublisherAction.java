/**
 * 
 */
package net.digitaltsunami.tmeter.action;

import java.util.Collection;

import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.TimerBasicStatistics;

/**
 * @author dhagberg
 *
 */
public class TimerStatsPublisherAction extends TimerStatsAction {
    private final TimerStatsPublisher publisher;

    /**
     * 
     */
    public TimerStatsPublisherAction(TimerStatsPublisher publisher) {
        super();
        if (publisher == null) {
            throw new IllegalArgumentException("Publisher cannot be null");
        }
        this.publisher = publisher;
    }

    /* (non-Javadoc)
     * @see net.digitaltsunami.tmeter.action.TimerAction#processTimer(net.digitaltsunami.tmeter.Timer)
     */
    @Override
    protected void processTimer(Timer timer) {
        super.processTimer(timer);
        publisher.publish(getTimerStatisticsSnapshot(timer.getTaskName()));
    }

    /* (non-Javadoc)
     * @see net.digitaltsunami.tmeter.action.TimerStatsAction#reset()
     */
    @Override
    protected void reset() {
        Collection<TimerBasicStatistics> stats = getAllTimerStatisticsSnapshot();
        super.reset();
        publisher.reset(stats);
    }

}
