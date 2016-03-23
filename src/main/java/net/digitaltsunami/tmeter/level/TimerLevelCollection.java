package net.digitaltsunami.tmeter.level;

import java.util.Set;

/**
 * A collection that consists of {@link TimerLevel}s and can be used as a
 * {@link TimerLevel}. All TimerLevel methods should operate on the entire
 * collection as a whole.
 * 
 * The contents of the collection follow a set of rules based on the
 * {@link TimerLevelType}.
 * <ul>
 * <li> {@link TimerLevelType#CATEGORY} The set may contain multiple distinct
 * entries from the same category.
 * <p>
 * Example, Given a Category timer level with three entries: CONNECT, POST, and
 * DISCONNECT. If the set contained CONNECT and POST was added, both CONNECT and
 * POST would remain in the set.
 * <li> {@link TimerLevelType#THRESHOLD} The set will contain only one entry for
 * a given threshold level.
 * <p>
 * Threshold levels are compared only against those of the same type. Another
 * Threshold type level will not be effected by this addition.
 * <p>
 * Example: Given a Threshold timer level with three entries: HIGH, MED, and
 * LOW. If the set contained MED and HIGH was added, then HIGH would be added
 * and MED removed. The same would occur for LOW.
 * <p>
 * Example: Given another Threshold timer level with: SEVERE, WARN, and INFO. If
 * the set contained WARN and HIGH was added, no change to WARN would occur.
 * Both HIGH and WARN would both exist in the set.
 * <li> {@link TimerLevelType#SET} All timer levels within the set will be
 * extracted and processed according to the rules applied to individual timer
 * levels.
 * </ul>
 * 
 * @author dhagberg
 * 
 */
public interface TimerLevelCollection extends TimerLevel {
    /**
     * Return a set of all {@link TimerLevel}s within this collection.
     * 
     * @return all timer levels.
     */
    public Set<TimerLevel> getLevels();

    /**
     * Add a new {@link TimerLevel} to the current collection. Addition of a
     * level is dependent upon type of the level and the current levels within
     * the set. See class comments for more information on contents and
     * exampled. Addition of timer level will be processed as follows:
     * <ul>
     * <li> {@link TimerLevelType#CATEGORY} The new timer level is added. If the
     * timer level was contained within the set, it will be returned.
     * <li> {@link TimerLevelType#THRESHOLD} The new timer level will be compared
     * against all others of the same type and will replace an existing entry if
     * present. If present, the existing entry will be removed from the set and
     * returned.
     * <p>
     * Threshold levels are compared only against those of the same type.
     * Another Threshold type level will not be effected by this addition.
     * <li> {@link TimerLevelType#SET} All timer levels within the set will
     * be extracted and processed according to the rules applied to individual
     * timer levels.
     * </ul>
     * 
     * @param newLevel
     *            Timer level to add.
     * @return Existing {@link TimerLevel} if this level is replacing another.
     */
    public TimerLevel addLevel(TimerLevel newLevel);

    /**
     * Add two or more {@link TimerLevel}s to the current collection. Addition
     * of a level is dependent upon type of the level and the current levels
     * within the set. See {@link #addLevel(TimerLevel)} for information on how
     * each level will be handled.
     * 
     * 
     * @param newLevels
     *            Two or more timer levels to add.
     * @return True if any levels were not already in the collection.
     */
    public boolean addLevels(TimerLevel... newLevels);

    /**
     * Remove the provided {@link TimerLevel} from the collection.
     * 
     * @param level
     *            Timer level to remove.
     * @return true if level was removed.
     */
    public boolean removeLevel(TimerLevel level);

    /**
     * Remove all levels from this collection.
     */
    public void clear();

}
