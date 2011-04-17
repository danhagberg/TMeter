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
package net.digitaltsunami.tmeter.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.digitaltsunami.tmeter.Timer;

import org.junit.Test;

/**
 * @author dhagberg
 *
 */
public class TimerStoppedEventTest {

    /**
     * Test method for {@link net.digitaltsunami.tmeter.event.TimerStoppedEvent#TimerStoppedEvent(net.digitaltsunami.tmeter.Timer)}.
     */
    @Test
    public void testTimerStoppedEvent() {
        Timer t = new Timer("TEST_TIMER");
        TimerStoppedEvent event = new TimerStoppedEvent(t);
        assertEquals(t, event.getSource());
    }

    /**
     * Test method for {@link net.digitaltsunami.tmeter.event.TimerStoppedEvent#getTimer()}.
     */
    @Test
    public void testGetTimer() {
        Timer t = new Timer("TEST_TIMER");
        TimerStoppedEvent event = new TimerStoppedEvent(t);
        assertEquals(t, event.getTimer());
    }

}
