TMeter
======
A framework for recording elapsed time to perform a given task and to drive
processing of the completed timers.  The framework consists of two main classes:  Timer and TimeTracker.  Timer handles the recording of elapsed time and can be used in standalone mode or as part of TimeTracker framework.  

Timer
-----
 A record of elapsed time. Timers can be used independently or as part of the
 framework driven using {@link TimeTracker}.
 <p>
 Provides:
 <ul>
 <li>Immediate or delayed start to allow preparation tasks to be excluded in
 the timing.
 <li>Nanosecond precision recording of tasks.
 <li>Basic completion logging in text or CSV format. Default is to not log.
 <li>Configurable log location with stdout being the default.
 <li>Status
 <li>Domain specific notes. A list of notes can be added to the timer that may
 provide information useful to analyzing the results at a later time. These
 notes will be appended to both the Text and CSV outputs if used.
 </ul>
 

### <strong>Thread Safety</strong>
 <p>
 To avoid synchronization, which could cause context swaps, instances of Timer
 are <strong>not</strong> thread safe. This is not to say that they should not 
 be used in a multi-threaded environment, only that they should not be updated 
 by mulitple threads.  
 <p>
 Some aspects are immutable and some will not change after they are set.
 <p>
 Immutable:
 <ul>
 <li>Task name</li>
 <li>Thread name</li>
 </ul>
 <p>
 Will not change once set:
 <ul>
 <li>Start Time</li>
 <li>Stop Time</li>
 <li>Elapsed Time - Can get a running time for elapsed time, but once the
 timer is stopped, this will not change.</li>
 <li>Status - Once stopped, cannot be set to any previous state.</li>
 </ul>
 
### <strong>Usage:</strong>
 
 <p>
 Simple - create and return a running Timer with no logging and task name of
 "BuildResults".
 
 <pre>
 <code>
     Timer brTimer = new Timer("BuildResults");
     .
     taskProcessing
     .
     brTimer.stop();
     System.out.println("BuildResults: " + brTimer.getElapsedMillis());
 </code>
 Output: BuildResults: 1000
 
 </pre>
 
 More complex - create and return a non-running Timer with CSV logging and a
 set of domain specific notes. The CSV line along with the notes will be
 written to stdout.
 
 <pre>
 <code>
     Timer serviceTimer = new Timer("BuildResults", true, new ConsoleTimeRecorder(TimerLogType.CSV));
     serviceTimer.setNotes(siteName, 4, user);
     serviceTimer.start();
     .
     taskProcessing
     .
     serviceTimer.stop();
 </code>
 Output: 1303060069651,BuildResults,main,1000,1000131000,0,TravelSite,4,member
 
 </pre>
 


TimeTracker
-----------
Provides options to allow the recording process to be configured for low to
high volume recording.

List - Can be configured to keep a list of all timers started via TimeTracker.
This can be used when logging would cause too much overhead such as high
volume recordings.

+   A snapshot of the results can be returned during the run.
+   The results may be cleared during the run. If the testing is done in
    batches, then the results can be processed and the list cleared between 
    batches. This will reduce the memory requirements of the timer framework

Logging - Logging can be enabled to direct the timers to log on completion.
Logging can be directed to the console or a file. Current logging styles are
text and csv. See Timer.toString() and Timer.toCsv() for formats.

Action chain - Post processing of completed timers can be taken out of the
processing thread by use of an ActionChain. The Action Chain provides
a queue for processing a user provided chain of TimerActions that
enable more complex processing of the timer data. See ActionChain for
more information.

Disabling - Allows the creation of timers to be enabled/disabled during
processing so that the timer logic can be left in place with very little overhead
when disabled. When disabled, a single instance of TimerShell will be
returned for all timer recording requests.

Concurrent counts - Can maintain concurrent task counts if enabled. This
provides a rudimentary concurrent count for all timers recording the same
task - as determined by task name. The count is based on the number of
currently running tasks for a task name at the time the Timer was created.



