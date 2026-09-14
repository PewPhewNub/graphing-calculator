package math;

import java.util.ArrayList;

/**
 * Represents the result of an Ordinary Differential Equation (ODE) solver.
 *
 * @param list              the list of points defining the solution curve
 * @param minimumStepUsed   the minimum step size utilized during integration
 * @param maximumStepUsed   the maximum step size utilized during integration
 * @param acceptedSteps     the number of steps accepted by the solver
 * @param rejectedSteps     the number of steps rejected by the solver
 * @param status            the final status of the ODE solver
 */
public record ODESolution(
    ArrayList<Point> list, 
    double minimumStepUsed, 
    double maximumStepUsed, 
    int acceptedSteps, 
    int rejectedSteps, 
    ODEStatus status
){
    
    /**
     * Constructs a simpler ODESolution with minimal step information.
     *
     * @param list       the list of points defining the solution curve
     * @param iterations the number of accepted steps (iterations)
     * @param stepSize   the step size used
     * @param status     the status of the ODE solver
     */
    public ODESolution(ArrayList<Point> list, int iterations, double stepSize, ODEStatus status) {
        this(list, stepSize, stepSize, iterations, 0, status);
    }

    /**
     * Constructs an ODESolution representing a failed attempt.
     *
     * @param status the final status of the ODE solver
     */
    public ODESolution(ODEStatus status) {
        this(null, Double.NaN, Double.NaN, 0, 0, status);
    }

    /**
     * Generates a formatted debug string representation of the ODESolution.
     *
     * @return a debug string
     */
    public String debug(){
        Point first = (list == null || list.isEmpty())
            ? null
            : list.get(0);

        Point last = (list == null || list.isEmpty())
            ? null
            : list.get(list.size() - 1);

        return String.format(
            """
            ODESolution[
                status=%s,
                points=%d,
                accepted=%d,
                rejected=%d,
                minStep=%g,
                maxStep=%g,
                first=%s,
                last=%s
            ]
            """,
            status,
            list == null ? 0 : list.size(),
            acceptedSteps,
            rejectedSteps,
            minimumStepUsed,
            maximumStepUsed,
            first,
            last
        );
    }
}
