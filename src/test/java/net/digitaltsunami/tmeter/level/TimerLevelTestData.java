package net.digitaltsunami.tmeter.level;

public class TimerLevelTestData {

    // Group of category levels
    public static TimerLevel cat1x = new CategoryTimerLevel<Cat1>(Cat1.X);
    public static TimerLevel cat1y = new CategoryTimerLevel<Cat1>(Cat1.Y);
    public static TimerLevel cat1z = new CategoryTimerLevel<Cat1>(Cat1.Z);
    // Second Group of category levels
    public static TimerLevel cat2x = new CategoryTimerLevel<Cat2>(Cat2.X);
    public static TimerLevel cat2y = new CategoryTimerLevel<Cat2>(Cat2.Y);
    public static TimerLevel cat2z = new CategoryTimerLevel<Cat2>(Cat2.Z);
    // Two timer levels for same category
    public static TimerLevel cat3z_a = new CategoryTimerLevel<Cat3>(Cat3.Z);
    public static TimerLevel cat3z_b = new CategoryTimerLevel<Cat3>(Cat3.Z);
    // Group of threshold levels
    public static TimerLevel severe = new ThresholdTimerLevel<ErrLevels>(ErrLevels.SEVERE);
    public static TimerLevel warn = new ThresholdTimerLevel<ErrLevels>(ErrLevels.WARNING);
    public static TimerLevel info = new ThresholdTimerLevel<ErrLevels>(ErrLevels.INFO);
    // Second distinct group of threshold levels
    public static TimerLevel terse = new ThresholdTimerLevel<Levels>(Levels.TERSE);
    public static TimerLevel normal = new ThresholdTimerLevel<Levels>(Levels.NORMAL);
    public static TimerLevel verbose = new ThresholdTimerLevel<Levels>(Levels.VERBOSE);

}

enum Cat1 {
    X, Y, Z;
}

enum Cat2 {
    X, Y, Z;
}

enum Cat3 {
    X, Y, Z;
}

enum ErrLevels {
    SEVERE, WARNING, INFO;
}

enum Levels {
    TERSE, NORMAL, VERBOSE;
}
