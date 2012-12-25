/* __copyright_begin__
   Copyright 2011 Dan Hagberg

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
__copyright_end__ */
package net.digitaltsunami.tmeter.action;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.TimerShell;

/**
 * Maintains and controls chain of {@link TimerAction} instances used to perform
 * post completion processing of {@link Timer} instances.
 * <p>
 * All processing is handled on a separate thread to minimize the impact to the
 * timed processing thread.
 * <p>
 * Each timer is processed by each {@link TimerAction} in a chain, but the order
 * is not guaranteed.
 * <p>
 * Processing of {@link TimerAction}s can be stopped by clearing the action list
 * using {@link #clearActions()}. This will remove the chain of
 * {@link ActionChain} instances, but elements currently being processed will
 * continue.
 * <p>
 * If any of the {@link TimerAction}s in the chain maintain state (e.g.,
 * counts), these can be reset by invoking {@link ActionChain#reset()}. This
 * will cause each action in the chain to invoke {@link TimerAction#reset()}
 * 
 * @author dhagberg
 * 
 */
public class ActionChain {

    /**
     * Queued timer instances to be processed by TimerAction list.
     */
    private LinkedBlockingQueue<Timer> actionQueue;

    /**
     * Executor to process actionQueue
     */
    private ExecutorService queueProcessor;

    /**
     * Root action in potential chain of actions.
     */
    private TimerAction rootAction;

    public ActionChain() {
    }

    public ActionChain(TimerAction action) {
        this.rootAction = action;
        createQueueProcessor();
    }

    /**
     * Submit a completed timer for post processing. If the timer has not yet
     * completed, it will not be submitted.
     */
    public void submitCompletedTimer(Timer completedTimer) {
        if (hasActionList() && completedTimer.isStopped()) {
            actionQueue.add(completedTimer);
        }
    }

    /**
     * Invokes {@link TimerAction#reset()} on each action within the chain.
     */
    public void reset() {
        TimerAction tempRoot = rootAction;
        if (tempRoot != null) {
            tempRoot.resetState();
        }
    }

    /**
     * Clear the timer action chain.
     */
    public void clearActions() {
        Timer t = new TimerShell("EndProcesing");
        t.stop();
        submitCompletedTimer(t);
        rootAction = null;
    }

    /**
     * Add the provided {@link TimerAction} to the current chain of
     * {@link TimerAction} instances.
     * <p>
     * This method is synchronized as it may create the root node, but other
     * methods that check the root are not synchronized to reduce context
     * switching; therefore, the if a timer completes before a timer action is
     * added, then it may be missed. This should not happen in the normal
     * execution as the setup should be completed prior to starting tasks.
     */
    public synchronized TimerAction addAction(TimerAction action) {
        if (rootAction == null) {
            rootAction = action;
            createQueueProcessor();
            return action;
        }
        return rootAction.addAction(action);
    }

    /**
     * Returns true if an action list is defined to process completed timers.
     * 
     * @return
     */
    public boolean hasActionList() {
        return rootAction != null;
    }

    /**
     * Create the queue and queue processor thread. The thread will continue
     * until an instance of {@link TimerShell} is found on the queue at which
     * point it will terminate.
     */
    private void createQueueProcessor() {
        actionQueue = new LinkedBlockingQueue<Timer>();
        queueProcessor = Executors.newSingleThreadExecutor();
        queueProcessor.execute(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    Timer timer;
                    try {
                        timer = actionQueue.take();
                        if (timer instanceof TimerShell) {
                            // Shut down queue processor if TimerShell is placed
                            // on queue.
                            queueProcessor.shutdown();
                            return;
                        }
                    } catch (InterruptedException e) {
                        Thread.interrupted();
                        return;
                    }
                    // Place the instance in another variable to prevent it
                    // being cleared in between checking and using.
                    TimerAction currentRoot = rootAction;
                    if (currentRoot != null) {
                        currentRoot.timerComplete(timer);
                    }
                }
            }
        });
    }

    /**
     * Return a set of all actions currently in the chain.
     * @return
     */
    public Set<TimerAction> getActions() {
        Set<TimerAction> actions = new HashSet<TimerAction>();
        TimerAction currentAction = rootAction;
        while (currentAction != null) {
            actions.add(currentAction);
            currentAction = currentAction.nextAction;
        }
        return actions;
    }

}
