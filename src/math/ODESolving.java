package math;

import java.util.ArrayList;
import java.util.function.BiFunction;

public class ODESolving {
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
                multiplier = Math.max(1, multiplier * 1.5);
            }else{
                double factor = 0.9*Math.pow(tolerance/error,0.2);
                factor = Math.max(0.2, Math.min(5.0,factor));
                multiplier *= factor;   
            }
            multiplier = Math.min(multiplier, 5);
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
