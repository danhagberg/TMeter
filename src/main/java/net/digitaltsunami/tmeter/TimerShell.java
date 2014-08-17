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
package net.digitaltsunami.tmeter;

import net.digitaltsunami.tmeter.record.TimeRecorder;

/**
 * Timer shell used as return value when time recording is turned off.
 * <p>
 * Will return 0 or default values for all timing methods.  
 */
public class TimerShell extends Timer {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public TimerShell(String taskName) {
        super(taskName, false, null, null);
    }

    @Override
    public void start() {
        if (! isStopped() ) {
	        updateStatus(TimerStatus.RUNNING);
        }
    }

    @Override
    public long stop() {
        updateStatus(TimerStatus.STOPPED);
        return 0;
    }

    @Override
    public void setTimeRecorder(TimeRecorder timeRecorder) {
    }

    @Override
    public long getStartTimeMillis() {
        return 0L;
    }

    @Override
    public long getStartTimeNanos() {
        return 0L;
    }

    @Override
    public long getStopTimeNanos() {
        return 0L;
    }

    @Override
    public int getConcurrent() {
        return 0;
    }

    @Override
    public void setConcurrent(int concurrent) {
    }

    @Override
    public String toString() {
        return "TimerShell";
    }

    @Override
    public String toCsv() {
        return "TimerShell";
    }

    @Override
    public long getElapsedNanos(boolean snapshotTime) {
        if (getStatus() == TimerStatus.RUNNING) {
             return snapshotTime ? 0 : -1;
        }
        return 0L;
    }

    @Override
    public long getElapsedNanos() {
        return getElapsedNanos(false);
    }

    @Override
    public long getElapsedMillis() {
        return getStatus() == TimerStatus.RUNNING ? -1 : 0;
    }

    @Override
    public TimerStatus getStatus() {
        return super.getStatus();
    }
}
