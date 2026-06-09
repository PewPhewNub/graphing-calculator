package Simulation.Plot;

import java.util.function.Function;

public interface RootFindable {
    Function<Double, Double> getFunction();
}
