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

import net.digitaltsunami.tmeter.Timer;

/**
 * Defines an interface for possibly more complex handling of timer instances.
 * <p>
 * Provides for a chain of actions to be defined that will execute upon the
 * timer being stopped. Each action should be non-destructive in that it should
 * not change the attributes of the {@link Timer}.
 * 
 * @author dhagberg
 * 
 */
public abstract class TimerAction {
    protected TimerAction nextAction;

    /**
     * Add the provided action to the chain of actions to perform.
     * <p>
     * No guarantee as to the order in which the actions will be executed.
     * 
     * @param action
     * @return provided action so that they may be chained without creating a
     *         local instance.
     */
    public TimerAction addAction(TimerAction action) {
        if (nextAction != null) {
            // If there is already a next action, insert the new action between
            // the two.
            action.nextAction = nextAction;
        }
        nextAction = action;
        return action;
    }

    public final void timerComplete(Timer timer) {
        processTimer(timer);
        if (nextAction != null) {
            nextAction.timerComplete(timer);
        }
    }

    /**
     * Perform any applicable processing specific to the actions. This method
     * will be invoked when a completed timer is submitted for processing.
     * <p>
     * Concrete classes must override this method in order to view the submitted
     * timers.
     * 
     * @param timer
     *            an instance of a completed {@link Timer}.
     */
    protected abstract void processTimer(Timer timer);

    /**
     * Perform any processing specific to the action for reset. This method will
     * be invoked when to reset the state of the action and may indicate that a
     * new timing run is starting.
     * <p>
     * Concrete classes should override this method if they accumulate timer
     * data that is specific to given run. The default is to do nothing. timers.
     */
    protected void reset() {
        // No action
    }

    /**
     * Drives the reset processing for all {@link TimerAction} instances.
     */
    public void resetState() {
        reset();
        if (nextAction != null) {
            nextAction.reset();
        }
    }
}
