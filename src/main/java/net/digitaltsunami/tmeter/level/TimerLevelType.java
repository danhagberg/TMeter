package net.digitaltsunami.tmeter.level;

/**
 * Enumeration of all valid {@link TimerLevel} types. See {@link TimerLevel} for
 * more information on types.
 * 
 * @author dhagberg
 * 
 */
public enum TimerLevelType {
    /**
     * Toggle type level. Levels of this type will match when equal to this
     * type.
     */
    CATEGORY,
    /**
     * Threshold type level. Levels of this type will match when equal to or
     * above this type.
     */
    THRESHOLD,
    /**
     * Set of timer levels. Levels of this type may match on ony one level
     * within the set according to the rules for the member levels. above this
     * type.
     */
    SET;
}
