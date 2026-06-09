package math.RootFindingAlgorithms;

import java.util.function.Function;

import math.Core.Interval;

public class RootFinderUtils {
    public static boolean validIntervalCheck(Function<Double, Double> f, Interval interval){
        return f.apply(interval.a) * f.apply(interval.b) <= 0;
    } 

    public static Interval explodingIntervalSolver(Function<Double, Double> f, double x0, double stepSize, double maxWidth, double maxIter){
        Interval interval = new Interval(x0 - stepSize, x0 + stepSize);
        int iterations = 1;
        while(iterations < maxIter){
            if(interval.width() > maxWidth) return new Interval();
            if(interval.isNaN()) return new Interval();
            interval = new Interval(interval.a - stepSize*(iterations + 1), interval.b + stepSize*(iterations + 1));
            if(validIntervalCheck(f, interval)) return interval;
        }
        return interval;
    }

    public static boolean isConverged(Function<Double, Double> f, double x, double prev, double tolerance){
        return !Double.isNaN(x) && Math.abs(f.apply(x)) < tolerance && Math.abs(x - prev) < tolerance;
    }

    public static Interval reducedInterval(Function<Double, Double> f, Interval interval, double cutoff){
        Interval newInterval;
        if(!interval.contains(cutoff)) return new Interval();
        if(f.apply(interval.a)*f.apply(cutoff) < 0) newInterval = new Interval(interval.a, cutoff);
        else if(f.apply(interval.b)*f.apply(cutoff) < 0) newInterval = new Interval(cutoff, interval.b);
        else newInterval = interval;

        if(newInterval.width()/interval.width() < 0.9){
            return newInterval;
        }
        return new Interval();
    }
}
