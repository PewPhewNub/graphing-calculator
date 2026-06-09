package core.math.App;
import java.util.function.BiFunction;

import core.math.Core.ODESolution;
import core.math.Core.ODEStatus;
import core.math.Core.Point;
import core.math.ODESolvers.RungeKuttaMethod;


public class Main {
    public static void main(String[] args) throws Exception {
        BiFunction<Double, Double, Double> diffEquation = (x,y) -> y;
        ODESolution solution = RungeKuttaMethod.adaptiveRK4(diffEquation, new Point(0,1), 1e-4, 5, 1e-7);
        if(solution.status() != ODEStatus.SUCCESS) System.out.println(solution.status());
        else for(Point i : solution.list()) System.out.println(i.toString());
        System.out.println(solution.debug());
    }
}
