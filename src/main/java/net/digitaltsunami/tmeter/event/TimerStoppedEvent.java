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
package net.digitaltsunami.tmeter.event;

import java.util.EventObject;

import net.digitaltsunami.tmeter.Timer;

public class TimerStoppedEvent extends EventObject {

    private static final long serialVersionUID = -4521533334911873218L;

    /**
     * Construct a completion event providing the timer that has completed.
     * 
     * @param timer subject of stopped event/jlkjlk
     */
    public TimerStoppedEvent(Timer timer) {
        super(timer);
    }

    public Timer getTimer() {
        return (Timer) getSource();
    }

}
