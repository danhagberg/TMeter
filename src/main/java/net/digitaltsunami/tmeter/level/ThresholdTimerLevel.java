package net.digitaltsunami.tmeter.level;

/**
 * {@link TimerLevel} representing a {@link TimerLevelType#THRESHOLD} type
 * backed by an enumeration. Timer levels of this type are threshold and will be
 * active only on a match equal to or above the backing enumeration.
 * <p>
 * As an example, use of an enum of type TLevel with values: MAIN, and DETAIL.
 * If a {@link CategoryTimerLevel} is created by using
 * 
 * <pre>
 * <code>
 *     enum TLevel {MAIN,DETAIL;}
 *     public static final TimerLevel MAIN = new CategoryTimerLevel<TLevel>(TLevel.MAIN);
 *     public static final TimerLevel DETAIL = new CategoryTimerLevel<TLevel>(TLevel.DETAIL);
 * </code>
 * </pre>
 * 
 * Usage:
 * 
 * <pre>
 * <code>
 *  TimeTracker.enableTimerLevel(MAIN);
 *  Timer tLogin = TimeTracker.startRecording(MAIN, "Login");
 *  Timer tSession = TimeTracker.startRecording(DETAIL, "CheckExistingSession");  // Would not be recorded
 * </code>
 * </pre>
 * 
 * Another method is to create an enum that extends {@link TimerLevel} and uses
 * this CategoryTimerLevel to provide the implementation.
 * 
 * <pre>
 * <code>
 *  public enum TrackLevel implements TimerLevel {
 *      MAIN(),
 *      DETAIL();
 * 
 *      CategoryTimerLevel<TrackLevel> timerLevel;
 * 
 *      TrackLevel() { timerLevel = new CategoryTimerLevel<TrackLevel>(this); }
 *      public boolean isEnabled(TimerLevel level) { return timerLevel.isEnabled(level); }
 *      public boolean isEnabled(TimerLevel... levels) { return timerLevel.isEnabled(levels); }
 *      public TimerLevelType getLevelType() { return timerLevel.getLevelType(); }
 *      public Object getGroup() { return timerLevel.getGroup(); }
 *  }
 * </code>
 * </pre>
 * 
 * Usage:
 * 
 * <pre>
 * <code>
 *  TimeTracker.enableTimerLevel(TrackLevel.MAIN);
 *  Timer tLogin = TimeTracker.startRecording(TrackLevel.MAIN, "Login");
 *  Timer tSession = TimeTracker.startRecording(TrackLevel.DETAIL, "CheckExistingSession");  // Would not be recorded
 * </code>
 * </pre>
 * 
 * @author dhagberg
 * 
 */
public class ThresholdTimerLevel<E extends Enum<E>> implements TimerLevel {

    private E level;
    private Class<Enum<E>> enumClass;

    @SuppressWarnings("unchecked")
    public ThresholdTimerLevel(E level) {
        this.level = level;
        this.enumClass = (Class<Enum<E>>) level.getClass();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isEnabled(TimerLevel oLevel) {
        if (oLevel instanceof ThresholdTimerLevel<?>) {
            Enum<?> oLevelLevel = ((ThresholdTimerLevel<?>) oLevel).level;
            if (oLevelLevel.getClass() == this.enumClass) {
                if (this.level.compareTo((E) oLevelLevel) >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isEnabled(TimerLevel... oLevels) {
        for (TimerLevel oLevel : oLevels) {
            if (isEnabled(oLevel)) {
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
        return enumClass;
    }

}
