package math.RootFindingAlgorithms;

import java.util.function.Function;

import math.Core.Interval;
import math.Core.RootSolution;

public class Main {
    public static void main(String[] args){
        Function<Double, Double> function = x -> x*x - 2;
        Interval initialInterval = new Interval(-1,5);
        double initialGuess = 0;
        double tolerance = 1e-7;
        int maxIter = 1000;

        RootSolution solution = HybridSolvers.findRootHybrid2(function, initialInterval, initialGuess, tolerance, maxIter);
        System.out.println(solution);
    }
}
