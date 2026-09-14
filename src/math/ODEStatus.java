package math;

/**
 * Enumerates the possible states of an Ordinary Differential Equation (ODE) solver.
 */
public enum ODEStatus {
    /** The ODE solver finished successfully. */
    SUCCESS,
    /** The ODE solver is currently in progress. */
    IN_PROGRESS,
    /** The ODE solver was interrupted. */
    INTERRUPTED,

    /** A possible asymptote was encountered during the ODE solution. */
    POSSIBLE_ASYMPTOTE,
    /** NaN value was encountered during computation. */
    ENCOUNTERED_NAN,
    /** The provided arguments to the ODE solver were invalid. */
    INVALID_ARGUMENTS,
    /** The ODE solver exceeded the maximum allowed number of iterations. */
    EXCEEDED_MAX_ITERATIONS,
    /** The step size used for integration underflowed. */
    STEPSIZE_UNDERFLOW,
    /** The provided step size is invalid. */
    INVALID_STEPSIZE
}
