package net.digitaltsunami.tmeter.record;

import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.TimerShell;
import net.digitaltsunami.tmeter.action.ActionChain;
import net.digitaltsunami.tmeter.action.ActionChainShutdownType;
import net.digitaltsunami.tmeter.action.TimeRecorderAction;
import net.digitaltsunami.tmeter.action.TimerAction;

/**
 * Time recorder that persists timers in a separate thread. This recorder does
 * not perform the persistence of the timer, but wraps the recorder that will
 * persist the timer.
 * <p>
 * As this implementation persists the timers using a queue, it may have
 * unfinished work when the application is shutdown. This default behavior may
 * be overridden by providing an {@link ActionChainShutdownType} to the
 * constructor.
 * 
 * @author dhagberg
 * 
 */
public class QueuedTimeRecorder implements TimeRecorder {

    private ActionChain chain;

    /**
     * Create a wrapper for the
     * 
     * @param recorder
     */
    public QueuedTimeRecorder(TimeRecorder recorder) {
        TimeRecorderAction action = new TimeRecorderAction(recorder);
        chain = new ActionChain(action);
    }

    public QueuedTimeRecorder(TimeRecorder recorder, ActionChainShutdownType shutdownType) {
        TimeRecorderAction action = new TimeRecorderAction(recorder);
        chain = new ActionChain(action, shutdownType);
    }

    @Override
    public void record(Timer timer) {
        chain.submitCompletedTimer(timer);
    }

    /**
     * Complete the current queue of timers and stop processing. No timers
     * submitted after this action will be processed.
     */
    public void shutdown() {
        chain.shutdown();
    }

    @Override
    public void prepareForShutdown() {
        shutdown();
    }

}
