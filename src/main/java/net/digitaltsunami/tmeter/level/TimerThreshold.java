package net.digitaltsunami.tmeter.level;

/**
 * Basic threshold timer level providing three levels of granularity:
 * <ul>
 * <li>COURSE: Record timers at a high level. For example, recording only major
 * tasks like Add Account.
 * <li>MEDIUM: Record timers at a more detailed level. For example, recording
 * sub-tasks with Add Account: Validate, Insert, Prepare Response.
 * <li>FINE: Record timers at a detail level. For example, recording all
 * sub-tasks within Validate Account: Validate User, Validate Address, etc.
 * </ul>
 * 
 * @author dhagberg
 * 
 */
public enum TimerThreshold implements TimerLevel {
    /**
     * Course grained timer level. If filter set at this level, only timers with
     * this level will be recorded.
     */
    COURSE,
    /**
     * Medium grained timer level. If filter set at this level, timers with this
     * level and above will be recorded.
     */
    MEDIUM,
    /**
     * Fine grained timer level. If filter set at this level, all timers with a
     * {@link TimerThreshold} timer level will be recorded.
     */
    FINE;

    @Override
    public boolean isEnabled(TimerLevel level) {
        return (level instanceof TimerThreshold && this.ordinal() >= ((TimerThreshold) level).ordinal());
    }

    @Override
    public boolean isEnabled(TimerLevel... levels) {
        for (TimerLevel level : levels) {
            if (isEnabled(level)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public TimerLevelType getLevelType() {
        return TimerLevelType.THRESHOLD;
    }

    @Override
    public Object getGroup() {
        return this.getClass();
    }

}
