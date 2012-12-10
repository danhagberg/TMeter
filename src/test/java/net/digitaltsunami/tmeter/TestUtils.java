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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import net.digitaltsunami.tmeter.record.FileTimeRecorder;

public class TestUtils {

    public final static String OS_LINE_FEED = System.getProperty("line.separator");

    /**
     * Utility method to extract the contents of a single log entry from the
     * timer. The timer should be configured prior to invoking this method. A
     * start and stop will occur on the timer and the contents of the log will
     * be returned. If stripLineFeed is true, then the OS dependent line
     * separator, (Sysetem.getProperty("line.separator"), will be removed prior
     * to returning.
     * 
     * @param timer
     * @param type type of output to write.
     * @param stripLineFeed
     *            If true, then the OS dependent line separator,
     *            (Sysetem.getProperty("line.separator"), will be removed prior
     *            to returning.
     * @return String containing a single output entry from the timer log 
     *         including/excluding the OS dependent line separator based on
     *         stripLineFeed.
     */
    public static String getTimerLogOutput(Timer timer, TimerLogType type, boolean stripLineFeed) {
        // Set timer output to byte array so the value can be captured.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileTimeRecorder recorder = new FileTimeRecorder(new PrintStream(out), type);
        timer.setTimeRecorder(recorder);
        timer.start();
        timer.stop();
        String outputLine = out.toString();
        if (stripLineFeed && outputLine.length() > 0) {
            outputLine = outputLine.substring(0, outputLine.lastIndexOf(OS_LINE_FEED));
        }
        return outputLine;
    }

    /**
     * Utility method to extract the contents of a single log entry from the
     * timer. The timer should be configured prior to invoking this method. A
     * start and stop will occur on the timer and the contents of the log will
     * 
     * @param timer
     * @param type type of output to write.
     * @return String containing a single output entry from the timer log
     *         including OS dependent line separator.
     */
    public static String getTimerLogOutput(Timer timer, TimerLogType type) {
        return getTimerLogOutput(timer, type, false);
    }

}
