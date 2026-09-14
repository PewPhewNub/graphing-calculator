package math;

/**
 * Represents the result of a root-finding operation.
 *
 * @param root       the calculated root value
 * @param interval   the interval containing the root
 * @param residual   the residual value of the function at the root
 * @param iterations the number of iterations performed
 * @param tolerance  the tolerance used during the computation
 * @param status     the status of the solver operation
 * @param solverName the name of the solver used
 */
public record RootSolution(
    double root,
    Interval interval,
    double residual,
    int iterations,
    double tolerance,
    SolverStatus status,
    String solverName
){
    /**
     * Constructs a RootSolution representing a failed attempt.
     *
     * @param solverName   the name of the solver used
     * @param failurePoint the status of the failure
     * @param iterations   the number of iterations performed
     * @param tolerance    the tolerance used
     */
    public RootSolution(String solverName, SolverStatus failurePoint, int iterations, double tolerance){
        this(Double.NaN, new Interval(), Double.NaN, iterations, tolerance, failurePoint, solverName);
    }
}
