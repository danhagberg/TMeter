package net.digitaltsunami.tmeter.record;

import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.record.TimeRecorder;

public class CountTimeRecorder implements TimeRecorder {
    private int count;

    @Override
    public void record(Timer timer) {
        count++;
    }
    
    public int getCount() {
        return count;
    }

    @Override
    public void prepareForShutdown() {
        // No-op
    }
    
}