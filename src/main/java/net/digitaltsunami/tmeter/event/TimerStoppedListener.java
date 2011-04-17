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

import java.util.EventListener;

import net.digitaltsunami.tmeter.Timer;

/**
 * Interface to register for timer stopped events. The timer will fire
 * timerStopped when stopped and provide an event that contains the timer that
 * was just stopped.
 * <p>
 * Implementers of this interface should take care to return as quickly as
 * possible. The {@link Timer} class maintains only a single listener and not a
 * list as is normal. This is to keep the processing as light as possible so
 * that the timed process can return to the actual processing.
 * 
 * @author dhagberg
 */
public interface TimerStoppedListener extends EventListener {

    /**
     * Event fired when a timer is stopped.
     * 
     * @param event
     *            containing the timer that was stopped.
     */
    public void timerStopped(TimerStoppedEvent event);

}
