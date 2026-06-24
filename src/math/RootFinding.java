package math;

import java.util.function.Function;

public class RootFinding {
    private static Interval bisectionStep(Function<Double, Double> f, Interval interval, double tolerance){
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

            if(isConverged(f, result, prev, tolerance)){
                return new RootSolution(result, new Interval(), f.apply(result), i + 1, tolerance, SolverStatus.SUCCESS, "NewtonRaphson");
            }
            if(Double.isNaN(result)){
                return new RootSolution("NewtonSecant", SolverStatus.NAN_EXPLOSION, i + 1, tolerance);
            }
        }
        return new RootSolution("NewtonSecant", SolverStatus.MAX_ITERATIONS_EXCEEDED, maxIter, tolerance);
    }

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

    public static RootSolution findRootHybrid2(Function<Double, Double> f, Interval interval, double x0, double tolerance, int maxIter){
        Function<Double, Double> df = Calculus.derivative(f, tolerance);
        double x = x0;
        double prev = x0;
        Interval result = interval;    
        if(Math.abs(f.apply(interval.a)) < tolerance && Math.abs(interval.a - prev) < tolerance) return new RootSolution(result.a, result, f.apply(result.a), 0, tolerance, SolverStatus.SUCCESS, "Hybrid2-preloopcheck");
        if(Math.abs(f.apply(interval.b)) < tolerance && Math.abs(interval.b - prev) < tolerance) return new RootSolution(result.b, result, f.apply(result.b), 0, tolerance, SolverStatus.SUCCESS, "Hybrid2-preloopcheck");
        for(int i = 0; i < maxIter; i++){
            //First block handles using the Newton Raphson method to estimate quickly
            double fx = f.apply(x);
            if(isConverged(f, x, prev, tolerance)) return new RootSolution(x, result, fx, i, tolerance, SolverStatus.SUCCESS, "NewtonCheck");

            prev = x;
            double safetyX = newtonRaphsonStep(f, df, x, tolerance);
            if(!Double.isNaN(safetyX)){
                if(isConverged(f, safetyX, prev, tolerance)){
                    return new RootSolution(safetyX, result, f.apply(safetyX), i + 1, tolerance, SolverStatus.SUCCESS, "Hybrid2 - NewtonRaphson");
                }

                if(result.contains(safetyX)){
                    Interval newInterval = result;
                    double fSafetyX = f.apply(safetyX);
                    if(f.apply(result.a)*fSafetyX < 0) newInterval = new Interval(result.a, safetyX);
                    else if(f.apply(result.b)*fSafetyX < 0) newInterval = new Interval(safetyX, result.b);
                    else newInterval = new Interval(result.a, result.b);

                    if(newInterval.width()/result.width() < 0.9){
                        result = newInterval;
                        x = safetyX;
                        continue;
                    }
                }
            }
            //This second block will use bisection if the Newton Raphson method returns an illegal value

            result = bisectionStep(f, result, tolerance);
            if(result.width() < tolerance && (Math.abs(x - prev) < tolerance && Math.abs(f.apply(x)) > 1)) return new RootSolution("Hybrid2 - Bisection", SolverStatus.POSSIBLE_ASYMPTOTE_REACHED, i + 1, tolerance);
            if(result.isNaN()) return new RootSolution("Bisection", SolverStatus.NAN_EXPLOSION, i + 1, tolerance);
            x = result.midpoint();
        }
        return new RootSolution("Hybrid2 - Bisection", SolverStatus.MAX_ITERATIONS_EXCEEDED, maxIter, tolerance);
    }
}
