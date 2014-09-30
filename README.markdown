TMeter
======
A framework for recording elapsed time to perform a given task and to drive
processing of the completed timers.  The framework consists of a set of concerns: Timing, Recording, Post Processing, and Configuration. The central point for all of these concerns is the TimeTracker class. 

## TimeTracker
Entry point for all timing operations.  Provides methods for configuration, creation and tracking of Timer instances. 

### Common
Common configuration for environment. All operations on non-named Time Trackers will use this environment.  For example, The following disables only the common time tracker.  All named instances will remain unaffected by this operation.

    TimeTracker.setTrackingDisabled(true);

### Named
Individual configurations specified by a name.  Name must be unique within the environment.  All operations on named Time Trackers are specific only to the name instance.  For example, the following disables only the time tracker named "DBLayer".  The common and all other named instances will remain unaffected by this operation. 

    TimeTracker.named("DBLayer").setTrackingDisabled(true);

## Timing 
Timings are done by Timer instances that are created by TimeTracker or manually.  While Timers can be created manually, creating them via TimeTracker allows for a central configuration and tracking. 

Timers provide for tracking the following data for downstream recording and/or processing.  

* Time in nanoseconds from start to stop of timer
* Thread name
* Task name
* Current status: Initialized, Running, Stopped. 
* Domain specific notes. A list of notes can be added to the timer that may provide information useful to analyzing the results at a later time. These notes will be appended to both the Text and CSV outputs if used.
* All Timer attributes above to text or CSV format 
* Firing of registered recorder and/or post processing action chain

## Recording
Recording the results of timers is handled via the TimeRecorder associated with the Timer.  These are passed to the created timers by TimeTracker or directly if creating the Timer manually. 
The library contains four TimeRecorder implementations:

* NullTimeRecorder (default): Does not record timer information. 
* FileTimeRecorder:  Records data to file specified at creation of recorder instance.  Format (text or CSV) is also specified at creation time. 
* ConsoleTimeRecorder: Records data to console
* QueuedTimeRecorder: Takes an instance of TimeRecorder.  Timers are placed into a queue and processed off of the timed thread. This is useful when the implementation of the TimeRecorder may affect performance of the timed process.  For example, writing to a database. 

## Lists
In addtion to or instead of recording timer results, they may be stored within the common or named TimeTracker instance.  

* A snapshot of the results can be returned during the run.
* The results may be cleared during the run. If the testing is done in batches, then the results can be processed and the list cleared between batches. This will reduce the memory requirements of the timer framework


## Post Processing
TimeTracker and individual Timers provide a hook for post processing of completed timers.  This may be used to track statistics, trigger alerts to long running processes, or provide alternative logging.  

### Action Chain
While post processor can be added to a Timer manually, it is better to add an ActionChain to the common or named TimeTracker so that they can be processed off of the timed thread.  In addition, post processors can be chained to allow multiple actions to react to the data. 

#### Shutdown Types
When creating the action chain, a shutdown type should be provided to indicate the desired action on shutdown of the application.  The option to choose depends on the application and the priority of processing the timer data.  

* Terminate After Completion: Create a shutdown hook that Will shutdown the action chain, but will cause the VM to remain running until all timers in the queue have been processed.  This does not include any timers that have not yet completed.
* Terminate Immediately.  The action chain will be shutdown and processing will terminate regardless of the number of timers in the queue. 
* Terminate Manually: No processing on shutdown, the action chain must be manually terminated.  

#### Statistics
Recording of basic statistical for each task can be done by adding an instance of TimerStatsAction to the action chain. 
The statistics can be queried or published using an instance of TimerStatsPublisherAction. See java docs for more information.


## Configuration
TimeTracker, both common and named, can be configured to provide data to Timers during creation.  In addition, the following is provided:  

* Enable/Disable: Allows the creation of timers to be enabled/disabled during processing so that the timer logic can be left in place with very little overhead when disabled. When disabled, a single instance of TimerShell will be returned for all timer recording requests. 

* Concurrent counts: Can maintain concurrent task counts if enabled. This provides a rudimentary concurrent count for all timers recording the same task - as determined by task name. The count is based on the number of currently running tasks for a task name at the time the Timer was created.  

### Timer Levels
Timer levels allow a finer grained control on enabling/disabling timing logic.  Timer levels are one of three types:

* Threshold: enabled for the same level and all levels above. Examples are Course, Medium, Fine: where a level of Medium would be enabled if compared against Fine as Medium is more restrictive than Fine.
* Category: toggles and match on the same level. Examples are TimeDB, TimeWebService, TraceMethod: where these can be individually toggled and do not affect one another.
* Set: set of TimerLevels against which a TimerLevel can be compared. Examples of this include a filter that would use the set of filters to control recording of timers. Another might be where a timer is being recorded with multiple types (e.g., Medium and DebugDB)


## Misc
### Thread Safety of Timers

To avoid synchronization, which could cause context swaps, instances of Timer
are **not** thread safe. This is not to say that they should not 
be used in a multi-threaded environment, only that they should not be updated 
by multiple threads.  

Some aspects are immutable and some will not change after they are set.

**Immutable:**

* Name
* Thread name

Will not change once set:

* Start Time
* Stop Time
* Elapsed Time - Can get a running time for elapsed time, but once the
timer is stopped, this will not change.
Status - Once stopped, cannot be set to any previous state.
 
## Usage

### TimeTracker

#### Send all recording to the console in text format
    TimeTracker.setDefaultTimeRecorder(new ConsoleTimeRecorder());
    
#### Create a timer, process and record timer.
    Timer test = TimeTracker.startRecording("TEST");
    ... some process
    test.stop();
   
## TODO: Fill in with more examples

 