package core.math.ODESolvers;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

import core.math.Core.ODESolution;
import core.math.Core.ODEStatus;
import core.math.Core.Point;

public class SystemSolvers {
    public static ODESolution solution(BiFunction<Double, Double, Double> dx, BiFunction<Double, Double, Double> dy , double t0, Point initial, double stepSize, double maxTime){
        if(dy == null) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);        
        if(dx == null) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        if(initial == null) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        if(stepSize == 0 || !Double.isFinite(stepSize) || !Double.isFinite(maxTime)) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        if((maxTime - t0) * stepSize < 0) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        
        int maxIter = (int)Math.round((maxTime - t0)/stepSize);
        if(maxIter > 1e7) return new ODESolution(ODEStatus.EXCEEDED_MAX_ITERATIONS);
        ArrayList<Point> list = new ArrayList<Point>((int)maxIter + 1);
        double currentX = initial.x; double currentY = initial.y;
        list.add(new Point(currentX, currentY));
        for(int i = 0; i < maxIter; i++){
            double t = t0 + stepSize*i;
            double vx = dx.apply(currentX, currentY);
            double vy = dy.apply(currentX, currentY);
            if(!Double.isFinite(vx) || !Double.isFinite(vy)) {
                return new ODESolution(list, i, stepSize, ODEStatus.ENCOUNTERED_NAN);
            }
            currentX = currentX + stepSize * vx;
            currentY = currentY + stepSize * vy;
            if(!Double.isFinite(currentX) || !Double.isFinite(currentY)) return new ODESolution(list, i, stepSize, ODEStatus.ENCOUNTERED_NAN);
            list.add(new Point(currentX, currentY));
        }
        return new ODESolution(list, (int)maxIter, stepSize, ODEStatus.SUCCESS);
    }

    public static ODESolution RK4(BiFunction<Double, Double, Double> dx, BiFunction<Double, Double, Double> dy, double t0, Point initial, double stepSize, double maxTime){
        if(dy == null) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);        
        if(dx == null) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        if(initial == null) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        if(stepSize == 0 || !Double.isFinite(stepSize) || !Double.isFinite(maxTime)) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        if((maxTime - t0) * stepSize < 0) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        
        int maxIter = (int)Math.floor((maxTime - t0)/stepSize);
        if(maxIter > 1e7) return new ODESolution(ODEStatus.EXCEEDED_MAX_ITERATIONS);
        ArrayList<Point> list = new ArrayList<Point>((int)maxIter + 1);
        Point current = initial;
        list.add(initial);
        for(int i = 0; i < maxIter; i++){
            current = RK4Step(dx, dy, current, stepSize);
            if(current.isNaN()) return new ODESolution(list, i, stepSize, ODEStatus.ENCOUNTERED_NAN);
            list.add(current);
        }
        return new ODESolution(list, (int)maxIter, stepSize, ODEStatus.SUCCESS);
    }

    public static Point RK4Step(BiFunction<Double, Double, Double> dx, BiFunction<Double, Double, Double> dy, Point initial, double stepSize){
        double currentX = initial.x; double currentY = initial.y; 
        double kx1 = dx.apply(currentX, currentY);
        double ky1 = dy.apply(currentX, currentY);
        if(!Double.isFinite(kx1)) return new Point();
        if(!Double.isFinite(ky1)) return new Point();

        double kx2 = dx.apply(
            currentX + stepSize*kx1/2,
            currentY + stepSize*ky1/2
        );
        double ky2 = dy.apply(
            currentX + stepSize*kx1/2,
            currentY + stepSize*ky1/2
        );
        if(!Double.isFinite(kx2)) return new Point();
        if(!Double.isFinite(ky2)) return new Point();

        double kx3 = dx.apply(
            currentX + stepSize*kx2/2,
            currentY + stepSize*ky2/2
        );
        double ky3 = dy.apply(
            currentX + stepSize*kx2/2,
            currentY + stepSize*ky2/2
        );
        if(!Double.isFinite(kx3)) return new Point();
        if(!Double.isFinite(ky3)) return new Point();

        double kx4 = dx.apply(
            currentX + stepSize*kx3,
            currentY + stepSize*ky3
        );
        double ky4 = dy.apply(
            currentX + stepSize*kx3,
            currentY + stepSize*ky3
        );
        if(!Double.isFinite(kx4)) return new Point();
        if(!Double.isFinite(ky4)) return new Point();

        return new Point(currentX + stepSize*(kx1 + 2*kx2 + 2*kx3 + kx4)/6, currentY + stepSize*(ky1 + 2*ky2 + 2*ky3 + ky4)/6);
    }
}
