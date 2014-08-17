package net.digitaltsunami.tmeter.level;

/**
 * A level used to filter Timer calls. Timer levels may be of my several types,
 * specified by {@link TimerLevelType}. The types are:
 * <ul>
 * <li>Threshold: these types are enabled for the same level and all levels
 * above. Examples are Course, Medium, Fine: where a level of Medium would be
 * enabled if compared against Fine as Medium is more restrictive than Fine.</li>
 * <li>Category: these types are toggles and match on the same level. Examples
 * are TimeDB, TimeWebService, TraceMethod: where these can be individually
 * toggled and do not affect one another.</li>
 * <li>Set: A set of TimerLevels against which a TimerLevel can be compared.
 * Examples of this include a filter that would use the set of filters to
 * control recording of timers. Another might be where a timer is being recorded
 * with multiple types (e.g., Medium and DebugDB)</li>
 * <li></li>
 * </ul>
 * 
 * @author dhagberg
 * 
 */
public interface TimerLevel {
    /**
     * Return true if the provided level is enabled for this level.
     * 
     * @param level
     *            to compare against this level.
     * @return true if the level is enabled, false otherwise.
     */
    public boolean isEnabled(TimerLevel level);

    /**
     * Compare all provided levels against this level and return true if any of
     * the levels is enabled for this level.
     * 
     * @param levels
     *            list of levels to compare against this level.
     * @return true if one or more of the levels is enabled or false if none of
     *         the provided label are enabled.
     */
    public boolean isEnabled(TimerLevel... levels);

    /**
     * Return the type of level that this level represents.
     * 
     * @return instance of {@link TimerLevelType}
     */
    public TimerLevelType getLevelType();

    /**
     * Return the group to which this timer level belongs. Used to group for Set
     * operations. Must be unique. Good examples are declaring class for enum.
     */
    public Object getGroup();
}
