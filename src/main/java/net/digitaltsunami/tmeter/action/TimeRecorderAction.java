package net.digitaltsunami.tmeter.action;

import net.digitaltsunami.tmeter.TimeTracker;
import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.record.NullTimeRecorder;
import net.digitaltsunami.tmeter.record.QueuedTimeRecorder;
import net.digitaltsunami.tmeter.record.TimeRecorder;

/**
 * {@link TimerAction} that records {@link Timer}s. This action can be part of
 * an {@link ActionChain} and can be used to record timers to multiple locations
 * or just to move the recording off of the timed process's thread. 
 * Not necessary for simple time recorders, but for
 * those that might cause a delay it is better to move the recording task to
 * another thread.
 * <p>
 * There are a couple of ways to use this action:
 * <ol>
 * <li>Using the {@link QueuedTimeRecorder}. This recorder uses
 * {@link TimeRecorderAction} and performs all recording on a separate thread.
 * <ol>
 * <li>Create an instance of {@link QueuedTimeRecorder}
 * <li>Set the {@link TimeTracker#setDefaultTimeRecorder(queuedTimeRecorder)}
 * </ol>
 * <li>As part of an action chain
 * <ol>
 * <li>Create an instance of this class with an implementation of
 * {@link TimeRecorder}
 * <li>Add the instance to the TimeTracker action chain using
 * {@link TimeTracker#addAction(TimerAction)}
 * <li>If the processing threads should not record the timers, then set the default time 
 * recorder for TimeTracker to {@link NullTimeRecorder}
 * using {@link TimeTracker#setDefaultTimeRecorder(TimeRecorder)}.
 * </ol>
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
