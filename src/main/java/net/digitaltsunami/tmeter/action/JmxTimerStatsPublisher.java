package net.digitaltsunami.tmeter.action;

import net.digitaltsunami.tmeter.TimerBasicStatistics;
import net.digitaltsunami.tmeter.action.jmx.JmxTimerStats;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Timer stats publisher that publishes {@link TimerBasicStatistics} to a JMX bean.
 * A bean will be created for each task name using the group ID and the task name as the
 * identifier. The group ID will default to net.digitaltsunami.tmeter, but can be overridden in
 * the constructor.
 */
public class JmxTimerStatsPublisher implements TimerStatsPublisher {
    private static final String DEFAULT_GROUP_ID = "net.digitaltunami.tmeter";
    private final String beanGroupId;
    private final ConcurrentHashMap<String, JmxTimerStats> beansByTaskName = new ConcurrentHashMap<>();


    /**
     * Create an instance of the JMX statistic publisher using default group ID.
     */
    public JmxTimerStatsPublisher() {
        this(DEFAULT_GROUP_ID);
    }

    /**
     * Create an instance of the JMX statistic publisher using the provided group ID.
     */
    public JmxTimerStatsPublisher(String beanGroupId) {
        this.beanGroupId = beanGroupId;
    }

    /**
     * Publish the provided {@link TimerBasicStatistics} currently recorded for
     * a single task.
     *
     * @param stats Current statistics to publish.
     */
    @Override
    public void publish(TimerBasicStatistics stats) {
        JmxTimerStats mbean = getStatsBean(stats);
        mbean.setCount(stats.getCount());
        mbean.setMinElapsedMillis(stats.getMinElapsed(TimeUnit.MILLISECONDS));
        mbean.setMaxElapsedMillis(stats.getMaxElapsed(TimeUnit.MILLISECONDS));
        mbean.setMeanElapsedMillis(stats.getAverageElapsed(TimeUnit.MILLISECONDS));
        mbean.setStdDevElapsedMillis(stats.getStdDevElapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * Construct and return a bean name for the provided task name.
     *
     * @param taskName name of task for which bean name is being created.  Non-null
     * @return an identifier used to register the bean.
     */
    protected String getBeanName(String taskName) {
        return beanGroupId + ":type=" + taskName;
    }

    /**
     * Return the current JMX bean for the provided timer task.
     * If one does not currently exist, it will be created and cached.
     *
     * @param stats the statistics object for the task being tracked.
     * @return Instance of jmx bean for the provided stats.
     */
    protected JmxTimerStats getStatsBean(TimerBasicStatistics stats) {
        JmxTimerStats jmxTimerStats = beansByTaskName.get(stats.getTaskName());
        if (jmxTimerStats == null) {
            jmxTimerStats = new JmxTimerStats(stats);
            JmxTimerStats existing = beansByTaskName.put(stats.getTaskName(), jmxTimerStats);
            if (existing != null) {
                // Another thread already added it. Use that one instead.
                jmxTimerStats = existing;
            }
            try {
                MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                ObjectName statName = new ObjectName(getBeanName(stats.getTaskName()));
                if (server.isRegistered(statName)) {
                    server.unregisterMBean(statName);
                }
                server.registerMBean(jmxTimerStats, statName);

            } catch (MalformedObjectNameException
                    | NotCompliantMBeanException
                    | InstanceAlreadyExistsException
                    | MBeanException
                    | InstanceNotFoundException e) {
                // Ignore for now.  Result is that we cannot publish the state
            }
        }
        return jmxTimerStats;

    }

    /**
     * Reset the stats.  This will be deregister all beans and clear the cache.
     *
     * @param stats snapshot copy of all stats maintained by the
     *              {@link TimerStatsAction} prior to the reset.
     */
    @Override
    public void reset(Collection<TimerBasicStatistics> stats) {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            for (String name : beansByTaskName.keySet()) {
                ObjectName statName = new ObjectName(getBeanName(name));
                /* JUST FOR TESTING. UNDO THIS
                if (server.isRegistered(statName)) {
                    server.unregisterMBean(statName);
                }
                */
            }
            beansByTaskName.clear();
        } catch (MalformedObjectNameException
                //| MBeanException
               // | InstanceNotFoundException
                e) {
            // Ignore for now.  Result is that we cannot deregister the bean.
        }

    }
}
