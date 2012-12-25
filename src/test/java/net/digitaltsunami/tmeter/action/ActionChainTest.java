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
/**
 * 
 */
package net.digitaltsunami.tmeter.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.TimerShell;
import net.digitaltsunami.tmeter.record.NullTimeRecorder;

import org.junit.Before;
import org.junit.Test;

/**
 * @author dhagberg
 * 
 */
public class ActionChainTest {
    private static final String TEST_TIMER = "TEST_TIMER";
    protected ActionChain testActionChain;
    protected Timer testTimer;
    protected TimerActionCount actionCount;

    @Before
    public void setup() {
        testTimer = new Timer(TEST_TIMER, false, NullTimeRecorder.getInstance());
        actionCount = new TimerActionCount();
        testActionChain = new ActionChain(actionCount);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.ActionChain#ActionChain(net.digitaltsunami.tmeter.action.TimerAction)}
     * .
     */
    @Test
    public void testActionChain() {
        assertTrue("Action list was not setup during construction.",
                testActionChain.hasActionList());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.ActionChain#submitCompletedTimer(net.digitaltsunami.tmeter.Timer)}
     * .
     * 
     * @throws InterruptedException
     */
    @Test
    public void testSubmitCompletedTimer() throws InterruptedException {
        testTimer.start();
        testTimer.stop();
        testActionChain.submitCompletedTimer(testTimer);
        Thread.sleep(1000); // Give the queue time to process the timer.
        assertEquals(1, actionCount.getCallCount());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.ActionChain#clearActions()}.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testClearActions() throws InterruptedException {
        assertTrue(testActionChain.hasActionList());
        testActionChain.clearActions();
        assertFalse(testActionChain.hasActionList());
        // Ensure that the actions are not being invoked.
        testTimer.start();
        testTimer.stop();
        testActionChain.submitCompletedTimer(testTimer);
        Thread.sleep(1000); // Give the queue time to process the timer.
        assertEquals(0, actionCount.getCallCount()); // Will be zero if not
                                                     // invoked.
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.ActionChain#addAction(net.digitaltsunami.tmeter.action.TimerAction)}
     * .
     * 
     * @throws InterruptedException
     */
    @Test
    public void testAddAction() throws InterruptedException {
        TimerActionLast actionLast = new TimerActionLast();
        testActionChain.addAction(actionLast);
        testTimer.start();
        testTimer.stop();
        testActionChain.submitCompletedTimer(testTimer);
        Thread.sleep(1000); // Give the queue time to process the timer.
        assertEquals(1, actionCount.getCallCount());
        assertEquals(testTimer, actionLast.getLastTimer());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.ActionChain#addAction(net.digitaltsunami.tmeter.action.TimerAction)}
     * .
     * 
     * @throws InterruptedException
     */
    @Test
    public void testAddActionAfterClear() throws InterruptedException {
        TimerActionLast actionLast = new TimerActionLast();
        testActionChain.addAction(actionLast);
        testTimer.start();
        testTimer.stop();
        testActionChain.submitCompletedTimer(testTimer);
        Thread.sleep(1000); // Give the queue time to process the timer.
        assertEquals(1, actionCount.getCallCount());
        assertEquals(testTimer, actionLast.getLastTimer());

        testActionChain.clearActions();

        testActionChain.addAction(actionLast);
        testActionChain.submitCompletedTimer(testTimer);
        Thread.sleep(1000); // Give the queue time to process the timer.
        assertEquals(1, actionCount.getCallCount());
        assertEquals(testTimer, actionLast.getLastTimer());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.ActionChain#hasActionList()}.
     */
    @Test
    public void testHasActionList() {
        assertTrue(testActionChain.hasActionList());
    }

    /**
     * Test method to ensure queue processor shuts down when a
     * {@link TimerShell} has been placed on the queue.
     */
    @Test
    public void testActionProcessorShutdown() throws InterruptedException {
        assertTrue(testActionChain.hasActionList());

        // Shutdown the queue processor by submitting TimerShell
        Timer shell = new TimerShell(TEST_TIMER);
        shell.stop();
        testActionChain.submitCompletedTimer(shell);
        Thread.sleep(1000); // Give the queue time to process the timer.

        // Ensure that the actions are not being invoked.
        testTimer.start();
        testTimer.stop();
        testActionChain.submitCompletedTimer(testTimer);
        Thread.sleep(1000); // Give the queue time to process the timer.
        assertEquals(0, actionCount.getCallCount()); // Will be zero if not
                                                     // invoked.

    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.ActionChain#reset()}.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testReset() throws InterruptedException {
        testTimer.start();
        testTimer.stop();
        testActionChain.submitCompletedTimer(testTimer);
        Thread.sleep(1000); // Give the queue time to process the timer.
        assertEquals(1, actionCount.getCallCount()); 
        testActionChain.reset();
        assertEquals(0, actionCount.getCallCount()); 
    }
}
