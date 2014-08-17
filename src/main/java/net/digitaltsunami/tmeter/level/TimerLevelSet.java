package net.digitaltsunami.tmeter.level;

import java.util.HashSet;
import java.util.Set;

/**
 * Set of {@link TimerLevel}s implementing the {@link TimerLevelCollection}.
 * 
 * @author dhagberg
 * 
 */
public class TimerLevelSet implements TimerLevelCollection {

    /**
     * Set of {@link TimerLevel} instances used as backing store for this collection.
     */
    private final Set<TimerLevel> levelSet = new HashSet<TimerLevel>();

    /**
     * Add one to many {@link TimerLevel} instances based on the rules specified
     * in {@link TimerLevelCollection}.
     * 
     * @param levels one to many {@link TimerLevel}s to add the set. 
     */
    public TimerLevelSet(TimerLevel... levels) {
        for (TimerLevel level : levels) {
            addLevel(level);
        }
    }

    /* (non-Javadoc)
     * @see net.digitaltsunami.tmeter.level.TimerLevelCollection#addLevel(net.digitaltsunami.tmeter.level.TimerLevel)
     */
    @Override
    public TimerLevel addLevel(TimerLevel newLevel) {
        if (newLevel == null) {
            return null;
        }
        switch (newLevel.getLevelType()) {

        case CATEGORY:
	        for (TimerLevel level : levelSet) {
	            if (level.isEnabled(newLevel)) {
	                return level;
	            }
	        }
            levelSet.add(newLevel);
            return null;
	
        case THRESHOLD:
            TimerLevel oldLevel = null;
            for (TimerLevel level : levelSet) {
                if (level.getLevelType() == TimerLevelType.THRESHOLD && level.getGroup() == newLevel.getGroup()) {
                    levelSet.add(newLevel);
                    levelSet.remove(level);
                    oldLevel = level;
                    // Existing level found and set modified.  Must leave iteration.
                    break;  
                }
            }
            levelSet.add(newLevel);
            return oldLevel;
        case SET:
            if (newLevel instanceof TimerLevelCollection) {
                for(TimerLevel levelElem : ((TimerLevelCollection)newLevel).getLevels()) {
	                this.addLevel(levelElem);
                }
            }
            else {
                throw new IllegalArgumentException(
                        "TimerLevels of type Set should implmement TimerLevelCollection");
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see net.digitaltsunami.tmeter.level.TimerLevelCollection#addLevels(net.digitaltsunami.tmeter.level.TimerLevel[])
     */
    @Override
    public boolean addLevels(TimerLevel... newLevels) {
        boolean levelAdded = false;
        for (TimerLevel newLevel : newLevels) {
            levelAdded |= addLevel(newLevel) == null;
        }
        return levelAdded;
    }

    /* (non-Javadoc)
     * @see net.digitaltsunami.tmeter.level.TimerLevelCollection#removeLevel(net.digitaltsunami.tmeter.level.TimerLevel)
     */
    @Override
    public boolean removeLevel(TimerLevel level) {
        return levelSet.remove(level);
    }

    /* (non-Javadoc)
     * @see net.digitaltsunami.tmeter.level.TimerLevelCollection#clear()
     */
    @Override
    public void clear() {
        levelSet.clear();
    }

    /* (non-Javadoc)
     * @see net.digitaltsunami.tmeter.level.TimerLevel#isEnabled(net.digitaltsunami.tmeter.level.TimerLevel)
     */
    public boolean isEnabled(TimerLevel oLevel) {
        for (TimerLevel level : levelSet) {
            if (level.isEnabled(oLevel)) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see net.digitaltsunami.tmeter.level.TimerLevel#isEnabled(net.digitaltsunami.tmeter.level.TimerLevel[])
     */
    public boolean isEnabled(TimerLevel... oLevels) {
        for (TimerLevel oLevel : oLevels) {
            if (isEnabled(oLevel)) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see net.digitaltsunami.tmeter.level.TimerLevel#getLevelType()
     */
    public TimerLevelType getLevelType() {
        return TimerLevelType.SET;
    }

    /* (non-Javadoc)
     * @see net.digitaltsunami.tmeter.level.TimerLevelCollection#getLevels()
     */
    public Set<TimerLevel> getLevels() {
        // Return a copy of the current level set.
        return new HashSet<TimerLevel>(levelSet);
    }

    /* (non-Javadoc)
     * @see net.digitaltsunami.tmeter.level.TimerLevel#getGroup()
     */
    @Override
    public Object getGroup() {
        return this;
    }
    
}
