package net.digitaltsunami.tmeter.action.jmx;

/**
 * Created by dhagberg on 3/17/16.
 */
public interface JmxTimerStatsMBean {
    String getTaskName();

    int getCount();

//    void setCount(int count);

    long getMinElapsedMillis();

 //   void setMinElapsedMillis(long minElapsedMillis);

    long getMaxElapsedMillis();

  //  void setMaxElapsedMillis(long maxElapsedMillis);

    double getMeanElapsedMillis();

   // void setMeanElapsedMillis(double meanElapsedMillis);

    double getStdDevElapsedMillis();

    //void setStdDevElapsedMillis(double stdDevElapsedMillis);
}
