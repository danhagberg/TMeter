package net.digitaltsunami.tmeter.action;

import net.digitaltsunami.tmeter.TimeTracker;
import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.record.NullTimeRecorder;
import net.digitaltsunami.tmeter.record.TimeRecorder;

/**
 * Timer action that records timers in a separate thread to reduce the impact on the working thread being timed.  Not necessary
 * for simple time recorders, but for those that might cause a delay it is better to move the recording task to another thread.
 * <p>
 * To use this action: 
 * <ol>
 * <li> Set the default time recorder for TimeTracker to {@link NullTimeRecorder} using {@link TimeTracker#setDefaultTimeRecorder(TimeRecorder)}
 * <li> Create an instance of this class with an implementation of {@link TimeRecorder}
 * <li> Add the instance to the TimeTracker action chain using {@link TimeTracker#addAction(TimerAction)}
 * </ol>
 * 
 * @author dhagberg
 *
 */
public class TimeRecorderAction extends TimerAction {
    private final TimeRecorder recorder;
    
    public TimeRecorderAction(TimeRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    protected void processTimer(Timer timer) {
        recorder.record(timer);
    }

}
