package core.math.Core;

import java.util.ArrayList;

public record ODESolution(ArrayList<Point> list, double minimumStepUsed, double maximumStepUsed, int acceptedSteps, int rejectedSteps, ODEStatus status){
    
    public ODESolution(ArrayList<Point> list, int iterations, double stepSize, ODEStatus status) {
        this(list, stepSize, stepSize, iterations, 0, status);
    }
    public ODESolution(ODEStatus status) {
        this(null, Double.NaN, Double.NaN, 0, 0, status);
    }

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
