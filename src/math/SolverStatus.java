package math;

/**
 * Enumerates the possible results of a numerical solver operation.
 */
public enum SolverStatus {
    /** The operation completed successfully. */
    SUCCESS,

    /** The initial bracket provided was invalid for the operation. */
    INVALID_INITIAL_BRACKET,
    /** The initial guess provided was invalid. */
    INVALID_INITIAL_GUESS,

    /** Numerical instability led to NaN results. */
    NAN_EXPLOSION,
    /** Historical data points required for the computation could not be generated. */
    FAILED_TO_GENERATE_HISTORICAL_POINTS,
    /** Duplicate y-values encountered, which prevent further progress. */
    DUPLICATE_Y_VALUES,
    /** The solver exceeded the maximum allowed number of iterations. */
    MAX_ITERATIONS_EXCEEDED,
    /** A possible asymptote was reached during computation. */
    POSSIBLE_ASYMPTOTE_REACHED,
    /** Could not generate a valid interval for the operation. */
    FAILED_TO_GENERATE_POSSIBLE_INTERVAL
}
