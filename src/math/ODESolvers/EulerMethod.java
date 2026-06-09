package math.ODESolvers;

import java.util.ArrayList;
import java.util.function.BiFunction;

import math.Core.ODESolution;
import math.Core.ODEStatus;
import math.Core.Point;

public class EulerMethod {
    public static ODESolution solution(BiFunction<Double, Double, Double> f, Point initial, double stepSize, double max){
        if(f == null) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        if(initial == null) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        if(stepSize == 0 || !Double.isFinite(stepSize) || !Double.isFinite(max)) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        if((max - initial.x) * stepSize < 0) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        int maxIter = (int)((max - initial.x)/stepSize);
        if(maxIter > 1e7) return new ODESolution(ODEStatus.EXCEEDED_MAX_ITERATIONS);
        ArrayList<Point> list = new ArrayList<Point>((int)maxIter + 1);
        double currentX = initial.x; double currentY = initial.y;
        list.add(new Point(currentX, currentY));
        for(int i = 0; i < maxIter; i++){
            double slope = f.apply(currentX, currentY);
            if(!Double.isFinite(slope)) return new ODESolution(ODEStatus.ENCOUNTERED_NAN);
            currentX = currentX + stepSize;
            currentY = currentY + stepSize * slope;
            if(!Double.isFinite(currentX) || !Double.isFinite(currentY)) return new ODESolution(ODEStatus.ENCOUNTERED_NAN);
            list.add(new Point(currentX, currentY));
        }
        return new ODESolution(list, (int)maxIter, stepSize, ODEStatus.SUCCESS);
    }
}
