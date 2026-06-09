package core.math.RootFindingAlgorithms;

import java.util.function.Function;

import core.math.Core.Interval;
import core.math.Core.RootSolution;
import core.math.Core.SolverStatus;

public class Bisection {
    /**
     * 
     * @param f             The target function to evaluate
     * @param interval      The interval
     * @param tolerance     Maximum acceptable error threshold
     * @return RootResult object containing a verified interval of half the size
     */
    public static Interval bisectionStep(Function<Double, Double> f, Interval interval, double tolerance){
        double l = interval.a; double u = interval.b;
        if (l == u || Math.abs(u - l) < tolerance) {
            return interval; 
        }
        double fl = f.apply(l);
        double fu = f.apply(u);
        if(fl*fu > 0){
            return new Interval(Double.NaN, Double.NaN);
        }

        double m = interval.midpoint();
        double fm = f.apply(m);
        if(Math.abs(fm) <= tolerance || Math.abs(u - l) < tolerance) return new Interval(m, m);
        if(fm * fl < 0){
            return new Interval(l, m);
        }else{
            return new Interval(m, u);
        }
    }

    public static RootSolution findRootBisection(Function<Double, Double> f, Interval interval, double tolerance, int maxIter){
        Interval result = interval;
        for(int i = 0; i < maxIter; i++){
            if(result.isPoint(tolerance)) return new RootSolution(result.midpoint(), result, f.apply(result.midpoint()), i + 1, tolerance, SolverStatus.SUCCESS, "Bisection");
            result = bisectionStep(f, result, tolerance);
            if(result.isNaN()) return new RootSolution("Bisection", SolverStatus.NAN_EXPLOSION, i + 1, tolerance);
        }
        return new RootSolution("Bisection", SolverStatus.MAX_ITERATIONS_EXCEEDED, maxIter, tolerance);
    }
}
