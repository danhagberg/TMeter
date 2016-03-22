package net.digitaltsunami.tmeter.action.jmx;

import net.digitaltsunami.tmeter.TimerBasicStatistics;

import java.util.concurrent.TimeUnit;

/**
 * Created by dhagberg on 3/18/16.
 */
public class JmxTimerStats implements JmxTimerStatsMBean {
    private final String taskName;
    private int count;
    private long minElapsedMillis;
    private long maxElapsedMillis;
    private double meanElapsedMillis;
    private double stdDevElapsedMillis;

    public JmxTimerStats(TimerBasicStatistics stats) {
        taskName = stats.getTaskName();
        count = stats.getCount();
        minElapsedMillis = stats.getMinElapsed(TimeUnit.MILLISECONDS);
        maxElapsedMillis = stats.getMaxElapsed(TimeUnit.MILLISECONDS);
        meanElapsedMillis = stats.getAverageElapsed(TimeUnit.MILLISECONDS);
        stdDevElapsedMillis = stats.getStdDevElapsed(TimeUnit.MILLISECONDS);
    }

    @Override
    public String getTaskName() {
        return taskName;
    }

    @Override
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public long getMinElapsedMillis() {
        return minElapsedMillis;
    }

    public void setMinElapsedMillis(long minElapsedMillis) {
        this.minElapsedMillis = minElapsedMillis;
    }

    @Override
    public long getMaxElapsedMillis() {
        return maxElapsedMillis;
    }

    public void setMaxElapsedMillis(long maxElapsedMillis) {
        this.maxElapsedMillis = maxElapsedMillis;
    }

    @Override
    public double getMeanElapsedMillis() {
        return meanElapsedMillis;
    }

    public void setMeanElapsedMillis(double meanElapsedMillis) {
        this.meanElapsedMillis = meanElapsedMillis;
    }

    @Override
    public double getStdDevElapsedMillis() {
        return stdDevElapsedMillis;
    }

    public void setStdDevElapsedMillis(double stdDevElapsedMillis) {
        this.stdDevElapsedMillis = stdDevElapsedMillis;
    }
}
