package math.RootFindingAlgorithms;

import java.util.function.Function;

import math.Core.Calculus;
import math.Core.Interval;
import math.Core.RootSolution;
import math.Core.SolverState;
import math.Core.SolverStatus;

public class HybridSolvers{

    public static RootSolution findRootHybrid1(Function<Double, Double> f, Interval interval, double tolerance, int maxIter){
        Interval resultInterval = interval;
        int iterations = 0;
        while(resultInterval.width()/resultInterval.midpoint() > 0.01d){
            resultInterval = processBisectionIteration(f, resultInterval, tolerance);
            iterations++;
        }
        if (resultInterval.isNaN()) return new RootSolution("Bisection", SolverStatus.NAN_EXPLOSION, iterations, tolerance);
        double result = resultInterval.midpoint();
        double prev = resultInterval.midpoint();
        Function<Double, Double> df = Calculus.derivative(f, tolerance);
        for(int i = iterations; i < maxIter; i++){
            prev = result;
            result = NewtonSecant.newtonRaphsonStep(f, df, result, tolerance);

            if(RootFinderUtils.isConverged(f, result, prev, tolerance)){
                return new RootSolution(result, resultInterval, f.apply(result), i + 1, tolerance, SolverStatus.SUCCESS, "NewtonRaphson");
            }
            if(Double.isNaN(result)){
                return new RootSolution("NewtonSecant", SolverStatus.NAN_EXPLOSION, i + 1, tolerance);
            }
        }
        return new RootSolution("NewtonSecant", SolverStatus.MAX_ITERATIONS_EXCEEDED, maxIter, tolerance);
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
            if(RootFinderUtils.isConverged(f, x, prev, tolerance)) return new RootSolution(x, result, fx, i, tolerance, SolverStatus.SUCCESS, "NewtonCheck");

            prev = x;
            double safetyX = NewtonSecant.newtonRaphsonStep(f, df, x, tolerance);
            if(!Double.isNaN(safetyX)){
                if(RootFinderUtils.isConverged(f, safetyX, prev, tolerance)){
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

            result = Bisection.bisectionStep(f, result, tolerance);
            if(result.width() < tolerance && (Math.abs(x - prev) < tolerance && Math.abs(f.apply(x)) > 1)) return new RootSolution("Hybrid2 - Bisection", SolverStatus.POSSIBLE_ASYMPTOTE_REACHED, i + 1, tolerance);
            if(result.isNaN()) return new RootSolution("Bisection", SolverStatus.NAN_EXPLOSION, i + 1, tolerance);
            x = result.midpoint();
        }
        return new RootSolution("Hybrid2 - Bisection", SolverStatus.MAX_ITERATIONS_EXCEEDED, maxIter, tolerance);
    }

    public static RootSolution findRootHybrid2NoInterval(Function<Double, Double> function, double x0, double tolerance, int maxiter){
        Interval validInterval = RootFinderUtils.explodingIntervalSolver(function, x0, 0.1, 1e8, maxiter);
        if(!RootFinderUtils.validIntervalCheck(function, validInterval)) return new RootSolution("Interval Solver", SolverStatus.FAILED_TO_GENERATE_POSSIBLE_INTERVAL, 0, tolerance);
        return findRootHybrid2(function, validInterval, x0, tolerance, maxiter);
    }

    public static RootSolution findRootHybrid3(Function<Double, Double> f, Interval interval, SolverState currentState, double tolerance, int maxIter){
        Function<Double, Double> df = Calculus.derivative(f, tolerance);
        double prev2 = currentState.previous2;
        double prev = currentState.previous;
        double x = currentState.current;

        if(Double.isNaN(x) || Double.isNaN(prev) || Double.isNaN(prev2)) return new RootSolution("TriHybrid", SolverStatus.NAN_EXPLOSION, 0, tolerance);
        if(!interval.contains(x) || !interval.contains(prev) || !interval.contains(prev2)) return new RootSolution("TriHybrid", SolverStatus.FAILED_TO_GENERATE_HISTORICAL_POINTS, 0, tolerance);

        if(Math.abs(f.apply(prev2)) < tolerance) return new RootSolution(prev2, interval, f.apply(prev2), 0, tolerance, SolverStatus.SUCCESS, "PreIQICheck");
        if(Math.abs(f.apply(prev)) < tolerance && Math.abs(prev2 - prev) < tolerance) return new RootSolution(prev, interval, f.apply(prev), 0, tolerance, SolverStatus.SUCCESS, "PreIQICheck");
        if(Math.abs(f.apply(x)) < tolerance && Math.abs(prev - x) < tolerance) return new RootSolution(x, interval, f.apply(x), 0, tolerance, SolverStatus.SUCCESS, "PreIQICheck");

        Interval result = interval;
        for(int i = 2; i < maxIter; i++){
            if(RootFinderUtils.isConverged(f, x, prev, tolerance)) return new RootSolution(x, interval, f.apply(x), i + 1, tolerance, SolverStatus.SUCCESS, "PreNewtonCheck");;
            //First block handles the use of IQI
            double safetyXIQI = InverseQuadraticInterpolation.IQIStep(f, prev2, prev, x, tolerance);
            if(!Double.isNaN(safetyXIQI) && result.contains(safetyXIQI)){
                if(RootFinderUtils.isConverged(f, safetyXIQI, prev, tolerance)){
                    return new RootSolution(safetyXIQI, interval, f.apply(safetyXIQI), i + 1, tolerance, SolverStatus.SUCCESS, "IQI");
                }
                Interval newInterval = RootFinderUtils.reducedInterval(f, result, safetyXIQI);
                if(!newInterval.isNaN()){
                    result = newInterval;
                    prev2 = prev; prev = x; x = safetyXIQI;
                    continue;
                }
            }

            //Second block handles using the Secant method to estimate quickly
            
            double safetyX = NewtonSecant.newtonRaphsonStep(f, df, x, tolerance);
            if(!Double.isNaN(safetyX)){
                if(Math.abs(safetyX - x) < tolerance && Math.abs(f.apply(safetyX)) < tolerance){
                    return new RootSolution(safetyX, interval, f.apply(safetyX), i + 1, tolerance, SolverStatus.SUCCESS, "NewtonSecant");
                }

                Interval newInterval = RootFinderUtils.reducedInterval(f, result, safetyX);
                if(!newInterval.isNaN()){
                    result = newInterval;
                    x = safetyX;
                    continue;
                }
            }
            //This second block will use bisection if the Newton Raphson method returns an illegal value

            result = Bisection.bisectionStep(f, result, tolerance);
            if(result.width() < tolerance && (Math.abs(x - prev) < tolerance && Math.abs(f.apply(x)) > 1)) return new RootSolution("Hybrid2 - Bisection", SolverStatus.POSSIBLE_ASYMPTOTE_REACHED, i + 1, tolerance);
            if(result.isNaN()) return new RootSolution("Hybrid2 - Bisection", SolverStatus.NAN_EXPLOSION, 1 + i, tolerance);
            prev2 = prev; prev = x; x = result.midpoint();
        }
        return new RootSolution("Hybrid2", SolverStatus.MAX_ITERATIONS_EXCEEDED, maxIter, tolerance);
    }

    public static Interval processBisectionIteration(Function<Double, Double> f, Interval interval, double tolerance){
        Interval result = Bisection.bisectionStep(f, interval, tolerance);
        return result;
    }
    public static SolverState processNewtonStep(Function<Double, Double> f, Function <Double, Double> df, SolverState currentState, double tolerance){
        double current = currentState.current;
        double prev = currentState.previous;
        double safetyX = NewtonSecant.newtonRaphsonStep(f, df, current, tolerance);
        SolverState newState = currentState.copy();
        if(Double.isNaN(current)){
            return new SolverState(Double.NaN, Double.NaN, Double.NaN);
        }
        newState.previous2 = prev;
        newState.previous = current;
        newState.current = safetyX;
        return newState;
    }
}
