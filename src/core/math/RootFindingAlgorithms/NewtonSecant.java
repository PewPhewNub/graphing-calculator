package core.math.RootFindingAlgorithms;

import java.util.function.Function;

import core.math.Core.Interval;
import core.math.Core.RootSolution;
import core.math.Core.SolverStatus;

public class NewtonSecant {
    public static double newtonRaphsonStep(Function<Double, Double> f, Function<Double, Double> df, double x0, double tolerance){
        double dfx = df.apply(x0);
        if(Math.abs(dfx) < tolerance) return Double.NaN;
        double x = x0 - f.apply(x0)/dfx;
        if(Double.isNaN(x) || Double.isInfinite(x)){
            return Double.NaN;
        }
        return x;
    }

    public static RootSolution findRootNewtonRaphson(Function<Double, Double> f, Function<Double, Double> df, double newtonValue, double tolerance, int maxIter){
        double result = newtonValue;
        double prev = newtonValue;
        for(int i = 0; i < maxIter; i++){
            prev = result;
            result = newtonRaphsonStep(f, df, result, tolerance);

            if(RootFinderUtils.isConverged(f, result, prev, tolerance)){
                return new RootSolution(result, new Interval(), f.apply(result), i + 1, tolerance, SolverStatus.SUCCESS, "NewtonRaphson");
            }
            if(Double.isNaN(result)){
                return new RootSolution("NewtonSecant", SolverStatus.NAN_EXPLOSION, i + 1, tolerance);
            }
        }
        return new RootSolution("NewtonSecant", SolverStatus.MAX_ITERATIONS_EXCEEDED, maxIter, tolerance);
    }
}
