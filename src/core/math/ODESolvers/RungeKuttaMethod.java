package core.math.ODESolvers;

import java.util.ArrayList;
import java.util.function.BiFunction;

import core.math.Core.ODESolution;
import core.math.Core.ODEStatus;
import core.math.Core.Point;

public class RungeKuttaMethod {
    public static ODESolution MidpointRK2(BiFunction<Double, Double, Double> f, Point initial, double stepSize, double max){
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
            double slope1 = f.apply(currentX, currentY);
            if(!Double.isFinite(slope1)) return new ODESolution(ODEStatus.ENCOUNTERED_NAN);
            
            double midX = currentX + stepSize/2;
            double midY = currentY + stepSize * slope1/2;

            double slope2 = f.apply(midX, midY);
            if(!Double.isFinite(slope2)) return new ODESolution(ODEStatus.ENCOUNTERED_NAN);

            currentX = currentX + stepSize;
            currentY = currentY + stepSize * slope2;
            if(!Double.isFinite(currentX) || !Double.isFinite(currentY)) return new ODESolution(ODEStatus.ENCOUNTERED_NAN);
            list.add(new Point(currentX, currentY));
        }
        return new ODESolution(list, (int)maxIter, stepSize, ODEStatus.SUCCESS);
    }
    public static ODESolution TrapezoidalRK2(BiFunction<Double, Double, Double> f, Point initial, double stepSize, double max){
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
            double slope1 = f.apply(currentX, currentY);
            if(!Double.isFinite(slope1)) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
            
            double endX = currentX + stepSize;
            double endY = currentY + stepSize * slope1;

            double slope2 = f.apply(endX, endY);
            if(!Double.isFinite(slope2)) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);

            currentX = endX;
            currentY = currentY + stepSize * (slope1 + slope2)/2;
            if(!Double.isFinite(currentX) || !Double.isFinite(currentY)) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
            list.add(new Point(currentX, currentY));
        }
        return new ODESolution(list, (int)maxIter, stepSize, ODEStatus.SUCCESS);
    }

    public static ODESolution RK4(BiFunction<Double, Double, Double> f, Point initial, double stepSize, double max){
        if(f == null) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        if(initial == null) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        if(stepSize == 0 || !Double.isFinite(stepSize) || !Double.isFinite(max)) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        //if((max - initial.x) * stepSize < 0) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        int maxIter = (int)Math.abs((max - initial.x)/stepSize);
        if(maxIter > 1e7) return new ODESolution(ODEStatus.EXCEEDED_MAX_ITERATIONS);
        
        ArrayList<Point> list = new ArrayList<Point>((int)maxIter + 1);
        
        Point current = initial;
        list.add(initial.copy());
        for(int i = 0; i < maxIter; i++){
            current = RK4Step(f, current, stepSize);
            if(current.isNaN()) return new ODESolution(ODEStatus.ENCOUNTERED_NAN);
            list.add(current);
        }
        return new ODESolution(list, (int)maxIter, stepSize, ODEStatus.SUCCESS);
    }

    public static Point RK4Step(BiFunction<Double, Double, Double> f, Point initial, double stepSize){
        double currentX = initial.x; double currentY = initial.y; 
        double k1 = f.apply(currentX, currentY);
        if(!Double.isFinite(k1)) return new Point();

        double k2 = f.apply(
            currentX + stepSize/2,
            currentY + stepSize*k1/2
        );
        if(!Double.isFinite(k2)) return new Point();

        double k3 = f.apply(
            currentX + stepSize/2,
            currentY + stepSize*k2/2
        );
        if(!Double.isFinite(k3)) return new Point();

        double k4 = f.apply(
            currentX + stepSize,
            currentY + stepSize*k3
        );
        if(!Double.isFinite(k4)) return new Point();

        return new Point(currentX + stepSize, currentY + stepSize*(k1 + 2*k2 + 2*k3 + k4)/6);
    }

    public static ODESolution adaptiveRK4(BiFunction<Double, Double, Double> f, Point initial, double stepSize, double max, double tolerance){
        double multiplier = 1;
        if(f == null) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        if(initial == null) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        if(stepSize == 0 || !Double.isFinite(stepSize) || !Double.isFinite(max)) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        //if((max - initial.x) * stepSize < 0) return new ODESolution(ODEStatus.INVALID_ARGUMENTS);
        ArrayList<Point> list = new ArrayList<>();
        int acceptedSteps = 1; int rejectedSteps = 0;
        double minimumStepUsed = stepSize; double maximumStepUsed = stepSize;
        Point current = initial;
        list.add(initial.copy());
        
        while(Math.abs(current.x - max) > tolerance){
            if(list.size() > 1e7) return new ODESolution(list, minimumStepUsed, maximumStepUsed, acceptedSteps, rejectedSteps, ODEStatus.EXCEEDED_MAX_ITERATIONS);
            double sign = Math.signum(stepSize*multiplier);
            double h = sign * Math.min(Math.abs(stepSize*multiplier), Math.abs(max-current.x));
            //if(h < tolerance/10) return new ODESolution(ODEStatus.INVALID_STEPSIZE);
            Point safetyCurrent =  RK4Step(f, current, h);
            Point currentHalf1 = RK4Step(f, current, h / 2);
            Point currentHalf2 = RK4Step(f, currentHalf1, h / 2);
            if (current.x == currentHalf2.x) {
                System.out.println("Infinite loop detected: Stagnation at " + current.x);
                break; // Break to prevent the hang
            }
            if(safetyCurrent.isNaN() || currentHalf1.isNaN() || currentHalf2.isNaN()) return new ODESolution(ODEStatus.ENCOUNTERED_NAN);
            double error = Math.abs(safetyCurrent.y - currentHalf2.y)/15;
            
            if(Math.abs(h) < 1e-14){
                System.out.println(current.x);
                System.out.println(h);
                System.out.println(error);
                System.out.println(safetyCurrent.y);
                System.out.println(currentHalf2.y);
                return new ODESolution(ODEStatus.STEPSIZE_UNDERFLOW);
            }
            if(error == 0){
                multiplier = Math.max(20, multiplier * 1.5);
            }else{
                double factor = 0.9*Math.pow(tolerance/error,0.2);
                factor = Math.max(0.2, Math.min(5.0,factor));
                multiplier *= factor;   
            }

            if(error >= tolerance){
                rejectedSteps++;
                continue;
            }
            current = currentHalf2;
            acceptedSteps++;
            minimumStepUsed = Math.signum(minimumStepUsed)*Math.min(Math.abs(minimumStepUsed), h);
            maximumStepUsed = Math.signum(maximumStepUsed)*Math.max(Math.abs(maximumStepUsed), h);
            list.add(current);
        }
        return new ODESolution(list, minimumStepUsed, maximumStepUsed, acceptedSteps, rejectedSteps, ODEStatus.SUCCESS);
    }
}
