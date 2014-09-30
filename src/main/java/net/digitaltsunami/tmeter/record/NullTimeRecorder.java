package net.digitaltsunami.tmeter.record;

import net.digitaltsunami.tmeter.Timer;

public class NullTimeRecorder implements TimeRecorder {
    
    private static final NullTimeRecorder INSTANCE = new NullTimeRecorder();

    private NullTimeRecorder() {
    }
    
    public static NullTimeRecorder getInstance() {
        return INSTANCE;
    }
    @Override
    public void record(Timer timer) {
        // No-op
    }

    @Override
    public void prepareForShutdown() {
        // No-op
    }

}
