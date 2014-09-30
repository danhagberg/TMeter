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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.digitaltsunami.tmeter.Timer;

import org.junit.Before;
import org.junit.Test;

/**
 * @author dhagberg
 * 
 */
public class TimerActionTest {
    private TimerAction action;
    private final List<Timer> listOfTimers = new ArrayList<Timer>();

    @Before
    public void setup() {
        // Create an action that will populate a container with processed
        // timer.
        action = new TimerAction() {

            @Override
            protected void processTimer(Timer timer) {
                listOfTimers.add(timer);
            }
            
            @Override 
            protected void reset() {
                listOfTimers.clear();
            }
        };

    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.TimerAction#addAction(net.digitaltsunami.tmeter.action.TimerAction)}
     * .
     */
    @Test
    public void testAddAction() {
        TimerAction action2 = new TimerActionCount();
        assertNull(action.nextAction);
        action.addAction(action2);
        assertEquals(action2, action.nextAction);
        assertNull(action2.nextAction);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.TimerAction#addAction(net.digitaltsunami.tmeter.action.TimerAction)}
     * .
     */
    @Test
    public void testAddDuplicateAction() {
        Timer timer = new Timer("TEST");
        TimerActionCount action2 = new TimerActionCount();
        TimerActionCount action3 = new TimerActionCount();
        action.addAction(action2);
        action2.addAction(action3);
        action.addAction(action3);
        action3.addAction(action);
        action2.addAction(action);
        action.addAction(action);
        int count = 0;
        for (TimerAction curr = action; curr != null && count < 4; curr = curr.nextAction) {
            count++;
        }
        assertEquals(3, count);
        action.timerComplete(timer);
        assertTrue("Timer was not processed for root action", listOfTimers.contains(timer));
        assertEquals(1, action2.getCallCount());
        assertEquals(1, action3.getCallCount());
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.TimerAction#addAction(net.digitaltsunami.tmeter.action.TimerAction)}
     * .
     */
    @Test
    public void testAddActionInsert() {
        TimerAction action2 = new TimerActionCount();
        TimerAction action3 = new TimerActionLast();
        assertNull(action.nextAction);
        action.addAction(action2);
        assertEquals(action2, action.nextAction);
        assertNull(action2.nextAction);

        // Add third action to first action. This will result in an insert
        // between 1 and 2. This is not a use case, but a test to ensure code
        // coverage.
        action.addAction(action3);
        assertEquals(action3, action.nextAction);
        assertEquals(action2, action3.nextAction);
        assertNull(action2.nextAction);
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.TimerAction#timerComplete(net.digitaltsunami.tmeter.Timer)}
     * .
     */
    @Test
    public void testTimerComplete() {
        Timer t = new Timer("TEST_TASK");
        action.timerComplete(t);
        assertTrue("Timer list does not contain the timer. ", listOfTimers.contains(t));
    }

    /**
     * Test method for
     * {@link net.digitaltsunami.tmeter.action.TimerAction#resetState()} .
     */
    @Test
    public void testResetState() {
        TimerActionCount action2 = new TimerActionCount();
        action.addAction(action2);
        Timer timer = new Timer("TEST_NAME");
        timer.start();
        timer.stop();
        action.timerComplete(timer);
        assertTrue(listOfTimers.contains(timer));
        assertEquals(1, action2.getCallCount());
        action.resetState();
        assertFalse(listOfTimers.contains(timer));
        assertEquals(0, action2.getCallCount());
    }

}
