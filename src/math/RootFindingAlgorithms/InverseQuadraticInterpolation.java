package math.RootFindingAlgorithms;

import java.util.function.Function;

import math.Core.Calculus;
import math.Core.Interval;
import math.Core.RootSolution;
import math.Core.SolverStatus;

public class InverseQuadraticInterpolation {
    public static double IQIStep(Function<Double, Double> f, double x0, double x1, double x2, double tolerance){
        double y0 = f.apply(x0);
        double y1 = f.apply(x1);
        double y2 = f.apply(x2);

        if(x0 == x1 || x1 == x2 || x2 == x0) return Double.NaN;
        if(Math.abs(y0 - y1) < tolerance || Math.abs(y1 - y2) < tolerance || Math.abs(y2 - y0) < tolerance) return Double.NaN;
        double R = y1*y2/((y0 - y1)*(y0 - y2));
        double S = y0*y2/((y1 - y0)*(y1 - y2));
        double T = y1*y0/((y2 - y1)*(y2 - y0));

        return R*x0 + S*x1 + T*x2;
    }

    public static RootSolution findRootIQI(Function<Double, Double> f, double x0, double x1, double x2, double tolerance, int maxIter){
        double a = x0; double b = x1; double c = x2; double result = x2;
        Function<Double, Double> df = Calculus.derivative(f, tolerance);
        if(Double.isNaN(x0)) return new RootSolution("IQI", SolverStatus.INVALID_INITIAL_GUESS, 0, tolerance);
        if(Math.abs(a - b) < tolerance || Double.isNaN(b)) b = NewtonSecant.newtonRaphsonStep(f, df, a, tolerance);
        if(Double.isNaN(b)) return new RootSolution("IQI", SolverStatus.FAILED_TO_GENERATE_HISTORICAL_POINTS, 0, tolerance);
        if(Math.abs(b - c) < tolerance || Double.isNaN(c)) c = NewtonSecant.newtonRaphsonStep(f, df, b, tolerance);
        if(Double.isNaN(c)) return new RootSolution("IQI", SolverStatus.FAILED_TO_GENERATE_HISTORICAL_POINTS, 0, tolerance);

        for(int i = 0; i < maxIter; i++){
            result = IQIStep(f, a, b, c, tolerance);
            if(RootFinderUtils.isConverged(f, result, c, tolerance)) return new RootSolution(result, new Interval(), f.apply(result), i + 1, tolerance, SolverStatus.SUCCESS, "IQI");
            if(Double.isNaN(result)){
                return new RootSolution("IQI", SolverStatus.NAN_EXPLOSION, 0, tolerance);
            }
            a = b; b = c; c = result;
        }
        return new RootSolution("IQI", SolverStatus.MAX_ITERATIONS_EXCEEDED, 0, tolerance);
    }
}
