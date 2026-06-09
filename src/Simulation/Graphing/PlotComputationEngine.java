package Simulation.Graphing;

import java.util.ArrayList;
import java.util.function.Function;

import Simulation.Plot.FunctionPlot;
import Simulation.Plot.ODEPlot;
import Simulation.Plot.ParametricPlot;
import Simulation.Plot.Plot;
import Simulation.Plot.RootFindable;
import javafx.geometry.Point2D;
import javafx.scene.effect.Light.Point;
import math.Core.Calculus;
import math.Core.Interval;
import math.Core.RootSolution;
import math.Core.SolverStatus;
import math.RootFindingAlgorithms.HybridSolvers;

public class PlotComputationEngine {

    public static ArrayList<Point2D> computeRoots(FunctionPlot plot1, double minWorldX, double maxWorldX, double stepSize){
        Function<Double, Double> function = plot1.getFunction();
        double prev = function.apply(minWorldX);
        ArrayList<Point2D> list = new ArrayList<>();
        for(int i = 1; i < (maxWorldX - minWorldX)/stepSize; i++){
            double i1 = (i - 1) * stepSize + minWorldX;
            double i2 = i * stepSize + minWorldX;
            double current = function.apply(i2);
            if(prev * current < 0){
                RootSolution solution = HybridSolvers.findRootHybrid2(function, new Interval(i1, i2), i1 + (i2 - i1)/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(solution.root(), 0));
                } 
            }
            prev = current;
        }
        return list;
    }

    public static ArrayList<Point2D> computeRoots(ParametricPlot plot1){
        Function<Double, Double> x = plot1.x;
        Function<Double, Double> y = plot1.y;
        double prevX = x.apply(plot1.tMin);
        double prevY = y.apply(plot1.tMin);
        ArrayList<Point2D> list = new ArrayList<>();
        double stepSize = (plot1.tMax - plot1.tMin) / plot1.maxSamples;
        for (double t = plot1.tMin + stepSize; t <= plot1.tMax; t += stepSize) {
            double currentX = x.apply(t);
            double currentY = y.apply(t);
            if(prevX * currentX < 0){
                RootSolution solution = HybridSolvers.findRootHybrid2(x, new Interval(t - stepSize, t), t - stepSize/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(x.apply(solution.root()), y.apply(solution.root())));
                } 
            }
            if(prevY * currentY < 0){
                RootSolution solution = HybridSolvers.findRootHybrid2(y, new Interval(t - stepSize, t), t - stepSize/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(x.apply(solution.root()), y.apply(solution.root())));
                } 
            }
            prevX = currentX;
            prevY = currentY;
        }
        return list;
    }

    public static ArrayList<Point2D> computeIntersections(FunctionPlot plot1, FunctionPlot plot2, double minWorldX, double maxWorldX, double stepSize){
        Function<Double, Double> function = x -> plot1.getFunction().apply(x) - plot2.getFunction().apply(x);
        double prev = function.apply(minWorldX);
        ArrayList<Point2D> list = new ArrayList<>();
        for(int i = 1; i < (maxWorldX - minWorldX)/stepSize; i++){
            double i1 = (i - 1) * stepSize + minWorldX;
            double i2 = i * stepSize + minWorldX;
            double current = function.apply(i2);
            if(prev * current < 0){
                RootSolution solution = HybridSolvers.findRootHybrid2(function, new Interval(i1, i2), i1 + (i2 - i1)/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(solution.root(), plot1.getFunction().apply(solution.root())));
                } 
            }
            prev = current;
        }
        return list;
    }

    public static ArrayList<Point2D> computeIntersections(FunctionPlot plot1, ParametricPlot plot2, double minWorldX, double maxWorldX){
        Function<Double, Double> function1 = plot1.getFunction();
        Function<Double, Double> x = plot2.x;
        Function<Double, Double> y = plot2.y;
        Function<Double, Double> function = t -> function1.apply(x.apply(t)) - y.apply(t);
        double prev = function.apply(plot2.tMin);
        ArrayList<Point2D> list = new ArrayList<>();
        double stepSize = (plot2.tMax - plot2.tMin) / plot2.maxSamples;
        for (double t = plot2.tMin + stepSize; t <= plot2.tMax; t += stepSize) {
            double current = function.apply(t);
            if(prev * current < 0){
                RootSolution solution = HybridSolvers.findRootHybrid2(function, new Interval(t - stepSize, t), t - stepSize/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(x.apply(solution.root()), y.apply(solution.root())));
                } 
            }
            prev = current;
        }
        return list;
    }

    public static ArrayList<Point2D> computeCriticalPoints(FunctionPlot plot1, double minWorldX, double maxWorldX, double stepSize){
        Function<Double, Double> function = plot1.getFunction();
        Function<Double, Double> derivative = Calculus.derivative(function, 1e-7);
        double prev = derivative.apply(minWorldX);
        ArrayList<Point2D> list = new ArrayList<>();
        for(int i = 1; i < (maxWorldX - minWorldX)/stepSize; i++){
            double i1 = (i - 1) * stepSize + minWorldX;
            double i2 = i * stepSize + minWorldX;
            double current = derivative.apply(i2);
            if(prev * current < 0){
                RootSolution solution = HybridSolvers.findRootHybrid2(derivative, new Interval(i1, i2), i1 + (i2 - i1)/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(solution.root(), function.apply(solution.root())));
                } 
            }
            prev = current;
        }
        return list;
    }

    public static ArrayList<Point2D> computeODETrajectory(ODEPlot plot, Point2D initial, double stepSize, double maxSteps){
        return plot.solve(initial, stepSize, maxSteps);
    }

    public static CurveData computeCurveData(ODEPlot plot, Viewport viewport){
        ArrayList<Point2D> points = plot.list();
        ArrayList<Segment2D> list = new ArrayList<>();

        if(points.isEmpty()) return new CurveData(plot, list);

        ViewportState state = new ViewportState(viewport);

        Point2D prev = points.getFirst();

        for(int i = 1; i < points.size(); i++){
            Point2D point = points.get(i);

            boolean prevVisible =
                prev.getX() >= state.left - state.marginX &&
                prev.getX() <= state.right + state.marginX &&
                prev.getY() >= state.bottom - state.marginY &&
                prev.getY() <= state.top + state.marginY;

            boolean currVisible =
                point.getX() >= state.left - state.marginX &&
                point.getX() <= state.right + state.marginX &&
                point.getY() >= state.bottom - state.marginY &&
                point.getY() <= state.top + state.marginY;

             boolean overlapsViewport =
                Math.max(prev.getX(), point.getX()) >= state.left &&
                Math.min(prev.getX(), point.getX()) <= state.right &&
                Math.max(prev.getY(), point.getY()) >= state.bottom &&
                Math.min(prev.getY(), point.getY()) <= state.top;

            if(prevVisible || currVisible || overlapsViewport){

                double x1 = viewport.worldToScreenX(prev.getX());
                double y1 = viewport.worldToScreenY(prev.getY());

                double x2 = viewport.worldToScreenX(point.getX());
                double y2 = viewport.worldToScreenY(point.getY());

                double dx = x2 - x1;
                double dy = y2 - y1;

                if(dx * dx + dy * dy >= 1){
                    list.add(new Segment2D(prev, point));
                }
            }

            prev = point;
        }

        System.out.println(list.size());
        return new CurveData(plot, list);
    }

    public static CurveData computeCurveData(FunctionPlot plot, Viewport viewport){
        ArrayList<Segment2D> list = new ArrayList<>();
        ViewportState state = new ViewportState(viewport);
        Function<Double, Double> function = plot.getFunction();
        double stepSize = (state.right - state.left)/state.viewportWidth;
        Point2D prev = new Point2D((state.left - stepSize), function.apply((state.left - stepSize)));
        for(double i = 1; i <= state.viewportWidth; i++){
            double x = (i * stepSize + state.left);
            Point2D point = new Point2D(x, function.apply(x));
            boolean prevVisible =
                prev.getX() >= state.left - state.marginX &&
                prev.getX() <= state.right + state.marginX &&
                prev.getY() >= state.bottom - state.marginY &&
                prev.getY() <= state.top + state.marginY;

            boolean currVisible =
                point.getX() >= state.left - state.marginX &&
                point.getX() <= state.right + state.marginX &&
                point.getY() >= state.bottom - state.marginY &&
                point.getY() <= state.top + state.marginY;

            boolean overlapsViewport =
                Math.max(prev.getX(), point.getX()) >= state.left &&
                Math.min(prev.getX(), point.getX()) <= state.right &&
                Math.max(prev.getY(), point.getY()) >= state.bottom &&
                Math.min(prev.getY(), point.getY()) <= state.top;

            if(prevVisible || currVisible){

                double x1 = viewport.worldToScreenX(prev.getX());
                double y1 = viewport.worldToScreenY(prev.getY());

                double x2 = viewport.worldToScreenX(point.getX());
                double y2 = viewport.worldToScreenY(point.getY());

                double dx = x2 - x1;
                double dy = y2 - y1;

                if(dx * dx + dy * dy >= 1 && dy <= viewport.height*0.95){
                    list.add(new Segment2D(prev, point));
                }
            }
            prev = point;
        }
        System.out.println(list.size());
        return new CurveData(plot, list);
    }
    
    public static CurveData computeCurveData(ParametricPlot plot, Viewport viewport){
        ViewportState state = new ViewportState(viewport);
        ArrayList<Point2D> points = plot.sample(viewport.width);
        ArrayList<Segment2D> list = new ArrayList<>();
        if(points.isEmpty()) return new CurveData(plot, list);
        Point2D prev = points.getFirst();
        for(int i = 1; i < points.size(); i+=1){
            Point2D point = points.get(i);
            boolean prevVisible =
                prev.getX() >= state.left - state.marginX &&
                prev.getX() <= state.right + state.marginX &&
                prev.getY() >= state.bottom - state.marginY &&
                prev.getY() <= state.top + state.marginY; 

            boolean currVisible =
                point.getX() >= state.left - state.marginX &&
                point.getX() <= state.right + state.marginX&&
                point.getY() >= state.bottom - state.marginY &&
                point.getY() <= state.top + state.marginY;

             boolean overlapsViewport =
                Math.max(prev.getX(), point.getX()) >= state.left &&
                Math.min(prev.getX(), point.getX()) <= state.right &&
                Math.max(prev.getY(), point.getY()) >= state.bottom &&
                Math.min(prev.getY(), point.getY()) <= state.top;

            if(prevVisible || currVisible || overlapsViewport){

                double x1 = viewport.worldToScreenX(prev.getX());
                double y1 = viewport.worldToScreenY(prev.getY());

                double x2 = viewport.worldToScreenX(point.getX());
                double y2 = viewport.worldToScreenY(point.getY());

                double dx = x2 - x1;
                double dy = y2 - y1;

                if(dx * dx + dy * dy >= 1){
                    list.add(new Segment2D(prev, point));
                }
            }
            prev = point;
        }
        System.out.println(list.size());
        return new CurveData(plot, list);
    }
    
    public static void ensureCoverage(ODEPlot plot, double worldMinX, double worldMaxX, double marginX){
        plot.extendCoverage(worldMinX, worldMaxX, marginX);
    }
    
    public static ArrayList<Point2D> generatePoints(ArrayList<Plot> plots, double minWorldX, double maxWorldX, double stepSize){//TODO: deduplicate points
        ArrayList<Point2D> list = new ArrayList<>();
        for(int i = 0; i < plots.size(); i++){
            for(int j = i; j < plots.size(); j++){
                if(i == j){
                    if(plots.get(i) instanceof ParametricPlot){
                        ParametricPlot plot = (ParametricPlot)(plots.get(i));
                        ArrayList<Point2D> accuratePoints = computeRoots(plot);
                        list.addAll(accuratePoints);
                        plot.accurateComputedPoints.addAll(accuratePoints);
                    }
                    else if(plots.get(i) instanceof FunctionPlot){
                        list.addAll(computeRoots((FunctionPlot)(plots.get(i)), minWorldX, maxWorldX, stepSize));
                        list.addAll(computeCriticalPoints((FunctionPlot)(plots.get(i)), minWorldX, maxWorldX, stepSize));
                    }
                }
                else 
                    if((plots.get(i) instanceof FunctionPlot) && (plots.get(j) instanceof FunctionPlot))
                        list.addAll(computeIntersections((FunctionPlot)(plots.get(i)), (FunctionPlot)(plots.get(j)), minWorldX, maxWorldX, stepSize));
                    if((plots.get(i) instanceof ParametricPlot) && (plots.get(j) instanceof FunctionPlot)){
                        ArrayList<Point2D> accuratePoints = computeIntersections((FunctionPlot)(plots.get(j)), (ParametricPlot)(plots.get(i)), minWorldX, maxWorldX);
                        list.addAll(accuratePoints);
                        ParametricPlot plotI = (ParametricPlot)(plots.get(i));
                        plotI.accurateComputedPoints.addAll(accuratePoints);
                    }
                    if((plots.get(j) instanceof ParametricPlot) && (plots.get(i) instanceof FunctionPlot)){
                        ArrayList<Point2D> accuratePoints = computeIntersections((FunctionPlot)(plots.get(i)), (ParametricPlot)(plots.get(j)), minWorldX, maxWorldX);
                        list.addAll(accuratePoints);
                        ParametricPlot plotI = (ParametricPlot)(plots.get(j));
                        plotI.accurateComputedPoints.addAll(accuratePoints);
                    }
            }
        }
        return list;
    }
}
