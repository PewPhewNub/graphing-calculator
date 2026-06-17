package engine.plotting;

import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Function;

import core.math.Core.Calculus;
import core.math.Core.Interval;
import core.math.Core.Point;
import core.math.Core.RootSolution;
import core.math.Core.SolverStatus;
import core.math.RootFindingAlgorithms.HybridSolvers;
import core.model.CurveData;
import core.model.Segment2D;
import core.model.ViewportState;
import engine.plotting.plots.FunctionPlot;
import engine.plotting.plots.ODEPlot;
import engine.plotting.plots.ParametricPlot;
import engine.plotting.plots.Plot;
import engine.plotting.plots.PolarPlot;
import engine.rendering.Viewport;
import javafx.geometry.Point2D;

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

        Point2D prev = points.getFirst();

        for(int i = 1; i < points.size(); i++){
            Point2D point = points.get(i);
            if(isWithinBounds(prev, point, viewport) && isSignificant(prev, point, viewport)){
                list.add(new Segment2D(prev, point));
            }
            prev = point;
        }
        return new CurveData(plot, list);
    }

    /*public static CurveData computeCurveData(FunctionPlot plot, Viewport viewport) {
        ArrayList<Segment2D> list = new ArrayList<>();
        Function<Double, Double> function = plot.getFunction();
        ViewportState state = new ViewportState(viewport);
        double stepSize = (state.right - state.left) / state.viewportWidth;
        double viewportHeight = Math.abs(state.top - state.bottom);

        Point2D prev = new Point2D(state.left - stepSize,
                                function.apply(state.left - stepSize));

        for (double i = 1; i <= state.viewportWidth; i++) {
            double x = i * stepSize + state.left;
            double rawY = function.apply(x);

            boolean prevFinite = Double.isFinite(prev.getY());
            boolean currFinite = Double.isFinite(rawY);

            if (prevFinite && currFinite) {
                Point2D point = new Point2D(x, rawY);

                // Discontinuity guard: sign-flip AND big jump == asymptote crossing
                boolean prevPos = prev.getY() > 0, prevNeg = prev.getY() < 0;
                boolean currPos = rawY > 0,        currNeg = rawY < 0;
                boolean signFlip = (prevPos && currNeg) || (prevNeg && currPos);
                boolean bigJump  = Math.abs(rawY - prev.getY()) > viewportHeight;

                if (!(signFlip && bigJump) && isWithinBounds(prev, point, viewport)) {
                    list.add(new Segment2D(prev, point));
                }
                prev = point;

            } else if (prevFinite && Double.isInfinite(rawY)) {
                // Branch heading to ±∞ — extend visually to one viewport-height past the edge
                double extY = (prev.getY() >= 0)
                        ? state.top    + viewportHeight
                        : state.bottom - viewportHeight;
                Point2D extension = new Point2D(x, extY);
                if (isWithinBounds(prev, extension, viewport)) {
                    list.add(new Segment2D(prev, extension));
                }
                prev = new Point2D(x, rawY);  // keep the ±Inf value so next iter detects it

            } else if (Double.isInfinite(prev.getY()) && currFinite) {
                // Branch arriving from ±∞ — start one viewport-height past the edge
                double extY = (rawY >= 0)
                        ? state.top    + viewportHeight
                        : state.bottom - viewportHeight;
                Point2D extension = new Point2D(prev.getX(), extY);
                Point2D point     = new Point2D(x, rawY);
                if (isWithinBounds(extension, point, viewport)) {
                    list.add(new Segment2D(extension, point));
                }
                prev = point;

            } else {
                // NaN (domain gap) or both non-finite — just reset the branch
                prev = new Point2D(x, rawY);
            }
        }
        return new CurveData(plot, list);
    }*/

    public static CurveData computeCurveData(ParametricPlot plot, Viewport viewport){
        ArrayList<Point2D> points = plot.sample(viewport.width);
        ArrayList<Segment2D> list = new ArrayList<>();
        if(points.isEmpty()) return new CurveData(plot, list);
        Point2D prev = points.getFirst();
        for(int i = 1; i < points.size(); i+=1){
            Point2D point = points.get(i);
            if(isWithinBounds(prev, point, viewport) && isSignificant(prev, point, viewport)){
                list.add(new Segment2D(prev, point));
            }
            prev = point;
        }
        return new CurveData(plot, list);
    }
    
    public static CurveData computeCurveData(PolarPlot plot, Viewport viewport){
        plot.recomputePoints(viewport, 0.5);
        ArrayList<Point2D> points = plot.currentList;
        System.out.println(points.size());
        ArrayList<Segment2D> list = new ArrayList<>();
        if(points.isEmpty()) return new CurveData(plot, list);
        Point2D prev = points.getFirst();
        for(int i = 1; i < points.size(); i+=1){
            Point2D point = points.get(i);
            if(isWithinBounds(prev, point, viewport) && isSignificant(prev, point, viewport)){
                list.add(new Segment2D(prev, point));
            }
            prev = point;
        }
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

    private static boolean isWithinBounds(Point2D prev, Point2D point, Viewport viewport){
    // Reject any segment with non-finite coordinates before doing anything else
    if (!Double.isFinite(prev.getX()) || !Double.isFinite(prev.getY()) ||
        !Double.isFinite(point.getX()) || !Double.isFinite(point.getY())) {
        return false;
    }
    // ... rest of your existing code unchanged

        ViewportState state = new ViewportState(viewport);
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
        
        if(!(prevVisible || currVisible || overlapsViewport)) return false;

        return true;
    }
    private static boolean isSignificant(Point2D prev, Point2D point, Viewport viewport){
        if (!Double.isFinite(prev.getX()) || !Double.isFinite(prev.getY()) ||
            !Double.isFinite(point.getX()) || !Double.isFinite(point.getY())) {
            return false;
        }
        double dx = viewport.worldToScreenX(point.getX()) - viewport.worldToScreenX(prev.getX());
        double dy = viewport.worldToScreenY(point.getY()) - viewport.worldToScreenY(prev.getY());
        if ((dx * dx + dy * dy) < 1.0) {
            return false;
        }
        return true;
    }

    private static void adaptiveSample(Function<Double, Double> f, Function<Double, Double> derivative, ViewportState state, double x1, double x2,
        ArrayList<Point2D> points, double toleranceX, double toleranceY, int depth) {
        double y1 = f.apply(x1);
        double y2 = f.apply(x2);

        double midX = x1 + (x2 - x1) / 2;
        double midY = f.apply(midX);
        // Base case
        if (depth > 50) {

            if (Math.abs(y2 - y1) > toleranceY * 20) {
                points.add(null);
                return;
            }

            points.add(new Point2D(x1,y1));
            points.add(new Point2D(x2,y2));
            return;
        }
        if (!Double.isFinite(y1) || !Double.isFinite(y2) || !Double.isFinite(midY)) {
            points.add(null);
            return;
        }

        boolean allAbove =
            y1 > state.top &&
            midY > state.top &&
            y2 > state.top;

        boolean allBelow =
            y1 < state.bottom &&
            midY < state.bottom &&
            y2 < state.bottom;

        if ((allAbove || allBelow)) {
            return;
        }
        double error = Math.abs(midY - (y1 + (y2 - y1) / 2));
        if (
            error > toleranceY ||
            Math.abs(y2 - y1) > toleranceY * 4
        ) {
            
            adaptiveSample(f, derivative, state, x1, midX, points, toleranceX, toleranceY, depth + 1);
            adaptiveSample(f, derivative, state, midX, x2, points, toleranceX, toleranceY, depth + 1);
        } else {
            double slope = Math.abs((y2 - y1) / (x2 - x1));

            if (Double.isFinite(slope) && slope > 1.0 / toleranceX) {
                if (Math.abs(y2 - y1) > toleranceY * 10) {
                    points.add(null);
                    return;
                }
            }

            boolean crossesTop =
                (y1 < state.top && y2 > state.top) ||
                (y1 > state.top && y2 < state.top);

            boolean crossesBottom =
                (y1 < state.bottom && y2 > state.bottom) ||
                (y1 > state.bottom && y2 < state.bottom);

            if (crossesTop && crossesBottom) {
    adaptiveSample(f, derivative, state, x1, midX,
        points, toleranceX, toleranceY, depth + 1);

    adaptiveSample(f, derivative, state, midX, x2,
        points, toleranceX, toleranceY, depth + 1);

    return;
}

            if (crossesBottom) {
                Function<Double, Double> boundary =
                    t -> f.apply(t) - state.bottom;

                Interval interval =
                    (y1 - state.bottom) * (midY - state.bottom) <= 0
                    ? new Interval(x1, midX)
                    : new Interval(midX, x2);

                RootSolution solution =
                    HybridSolvers.findRootHybrid2(
                        boundary,
                        interval,
                        midX,
                        1e-6,
                        100
                    );

                double root = solution.root();     
                if(y1 > state.bottom){           
                    points.add(new Point2D(root, state.bottom));
                    points.add(null);
                }else{
                    points.add(null);
                    points.add(new Point2D(root, state.bottom));
                }
                return;
            }
            if (crossesTop) {
                Function<Double, Double> boundary =
                    t -> f.apply(t) - state.top;

                Interval interval =
                    (y1 - state.top) * (midY - state.top) <= 0
                    ? new Interval(x1, midX)
                    : new Interval(midX, x2);

                RootSolution solution =
                    HybridSolvers.findRootHybrid2(
                        boundary,
                        interval,
                        midX,
                        1e-6,
                        100
                    );

                double root = solution.root();
                if(y1 < state.top){           
                    points.add(new Point2D(root, state.top));
                    points.add(null);
                }else{
                    points.add(null);
                    points.add(new Point2D(root, state.top));
                }
                return;
            }
            points.add(new Point2D(x1, y1));
            points.add(new Point2D(x2, y2));
        }
    }

    static double highest;
    public static CurveData computeCurveData(FunctionPlot plot, Viewport viewport){
        ViewportState state = new ViewportState(viewport);
        double samples = (int)(viewport.width);
        double stepX = (state.right - state.left)/samples;
        double toleranceY = Math.abs(
                viewport.screenToWorldY(1)
            - viewport.screenToWorldY(0)
        );
        double toleranceX = Math.abs(
                viewport.screenToWorldX(1)
            - viewport.screenToWorldX(0)
        );
        highest = Double.NEGATIVE_INFINITY;
        ArrayList<Segment2D> segments = new ArrayList<>();
        Function<Double, Double> function = plot.getFunction();
        Function<Double, Double> derivative = Calculus.derivative(function, 1e-7);
        for(int i = 0; i < samples - 1; i+=2){
            double x = state.left + i*stepX;
            ArrayList<Point2D> points = new ArrayList<>();
            adaptiveSample(function, derivative, state, x - stepX, x + stepX, points, toleranceX, toleranceY, 0);
            //points.add(new Point2D(x + stepX, plot.getFunction().apply(x + stepX)));
            for (int j = 1; j < points.size(); j++) {
                Point2D p1 = points.get(j - 1);
                Point2D p2 = points.get(j);
                 if (p1 == null || p2 == null) {
                    continue;
                }
                // Clip both endpoints to viewport using Liang-Barsky
                double[] clipped = liangBarsky(
                    p1.getX(), p1.getY(), p2.getX(), p2.getY(),
                    state.left, state.right, state.bottom, state.top
                );
                if (clipped != null) {
                    segments.add(new Segment2D(
                        new Point2D(clipped[0], clipped[1]),
                        new Point2D(clipped[2], clipped[3])
                        ));
                    }
                }
            }

        return new CurveData(plot, segments);
    }

    private static double[] liangBarsky(double x1, double y1, double x2, double y2,
        double left, double right, double bottom, double top) {
        double dx = x2 - x1, dy = y2 - y1;
        double t0 = 0, t1 = 1;
        double[] p = {-dx, dx, -dy, dy};
        double[] q = {x1 - left, right - x1, y1 - bottom, top - y1};
        for (int i = 0; i < 4; i++) {
            if (p[i] == 0) { if (q[i] < 0) return null; }
            else if (p[i] < 0) t0 = Math.max(t0, q[i] / p[i]);
            else               t1 = Math.min(t1, q[i] / p[i]);
            if (t0 > t1) return null;
        }
        return new double[]{x1+t0*dx, y1+t0*dy, x1+t1*dx, y1+t1*dy};
    }
}
