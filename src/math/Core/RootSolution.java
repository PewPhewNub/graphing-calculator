package math.Core;

public record RootSolution(
    double root,
    Interval interval,
    double residual,
    int iterations,
    double tolerance,
    SolverStatus status,
    String solverName
    ){
        public RootSolution(String solverName, SolverStatus failurePoint, int iterations, double tolerance){
            this(Double.NaN, new Interval(), Double.NaN, iterations, tolerance, failurePoint, solverName);
        }
    }