package net.digitaltsunami.tmeter.level;

import net.digitaltsunami.tmeter.TimeTracker;

/**
 * {@link TimerLevel} representing a {@link TimerLevelType#CATEGORY} type backed
 * by an enumeration. Timer levels of this type are toggles and will be active
 * only on a match of the backing enumeration entry.
 * <p>
 * As an example, use of an enum of type SessionOps with values: LOGIN, LOGOUT,
 * PROFILE. If a {@link CategoryTimerLevel} is created by using
 * 
 * <pre>
 * <code>
 *     enum SessionOps {LOGIN,LOGOUT,PROFILE;}
 *     public static final TimerLevel LOGIN = new CategoryTimerLevel<SessionOps>(SessionOps.LOGIN);
 *     public static final TimerLevel LOGOUT = new CategoryTimerLevel<SessionOps>(SessionOps.LOGOUT);
 *     public static final TimerLevel PROFILE = new CategoryTimerLevel<SessionOps>(SessionOps.PROFILE);
 * </code>
 * </pre>
 * 
 * Usage:
 * 
 * <pre>
 * <code>
 *  TimeTracker.enableTimerLevel(LOGIN);
 *  TimeTracker.startRecording(LOGIN, "InitialLogin");
 * </code>
 * </pre>
 * 
 * Another method is to create an enum that extends {@link TimerLevel} and uses
 * this CategoryTimerLevel to provide the implementation.
 * 
 * <pre>
 * <code>
 *  public enum UserOps implements TimerLevel {
 *      LOGIN(),
 *      LOGOUT(),
 *      PROFILE();
 * 
 *      CategoryTimerLevel<UserOps> timerLevel;
 * 
 *      UserOps() { timerLevel = new CategoryTimerLevel<UserOps>(this); }
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
 *      TimeTracker.enableTimerLevel(UserOps.LOGIN);
 *      TimeTracker.startRecording(UserOps.LOGIN, "InitialLogin");
 * </code>
 * </pre>
 * 
 * @author dhagberg
 * 
 */
public class CategoryTimerLevel<E extends Enum<E>> implements TimerLevel {

    private final E level;
    private Class<Enum<E>> enumClass;

    @SuppressWarnings("unchecked")
    public CategoryTimerLevel(E level) {
        this.level = level;
        this.enumClass = (Class<Enum<E>>) level.getClass();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isEnabled(TimerLevel oLevel) {
        if (oLevel instanceof CategoryTimerLevel<?>) {
            Enum<?> oLevelLevel = ((CategoryTimerLevel<?>) oLevel).level;
            if (oLevelLevel.getClass() == this.enumClass) {
                if (this.level == oLevelLevel) {
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
        return TimerLevelType.CATEGORY;
    }

    @Override
    public Object getGroup() {
        return enumClass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((enumClass == null) ? 0 : enumClass.hashCode());
        result = prime * result + ((level == null) ? 0 : level.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
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
        if (!(obj instanceof CategoryTimerLevel)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        CategoryTimerLevel<E> other = (CategoryTimerLevel<E>) obj;
        if (!enumClass.equals(other.enumClass)) {
            return false;
        }
        if (level != other.level) {
            return false;
        }
        return true;
    }

}
