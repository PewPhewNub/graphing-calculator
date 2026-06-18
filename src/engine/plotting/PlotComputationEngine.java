package engine.plotting;

import java.util.ArrayList;
import java.util.function.Function;

import core.math.Core.Calculus;
import core.math.Core.Interval;
import core.math.Core.Point;
import core.math.Core.RootSolution;
import core.math.Core.SolverStatus;
import core.math.RootFindingAlgorithms.HybridSolvers;
import core.model.CurveChunk;
import core.model.CurveData;
import core.model.Segment2D;
import core.model.ViewportState;
import engine.plotting.plots.FunctionPlot;
import engine.plotting.plots.ODEPlot;
import engine.plotting.plots.ParametricPlot;
import engine.plotting.plots.Plot;
import engine.plotting.plots.PolarPlot;
import engine.rendering.Viewport;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;

public class PlotComputationEngine {

    public static ArrayList<Point2D> computeIntercepts(FunctionPlot plot1, double minWorldX, double maxWorldX, double stepSize){
        Function<Double, Double> function = plot1.getFunction();
        double prev = function.apply(minWorldX);
        ArrayList<Point2D> list = new ArrayList<>();
        for(int i = 1; i < (maxWorldX - minWorldX)/stepSize; i++){
            double i1 = (i - 1) * stepSize + minWorldX;
            double i2 = i * stepSize + minWorldX;
            double current = function.apply(i2);
            if(prev * current <= 0){
                RootSolution solution = HybridSolvers.findRootHybrid2(function, new Interval(i1, i2), i1 + (i2 - i1)/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(solution.root(), 0));
                } 
            }
            prev = current;
        }
        list.add(new Point2D(0, function.apply(0d)));
        return list;
    }

    public static ArrayList<Point2D> computeIntercepts(ParametricPlot plot1){
        Function<Double, Double> x = plot1.x;
        Function<Double, Double> y = plot1.y;
        double prevX = x.apply(plot1.tMin);
        double prevY = y.apply(plot1.tMin);
        ArrayList<Point2D> list = new ArrayList<>();
        double stepSize = (plot1.tMax - plot1.tMin) / plot1.maxSamples;
        for (double t = plot1.tMin + stepSize; t <= plot1.tMax; t += stepSize) {
            double currentX = x.apply(t);
            double currentY = y.apply(t);
            if(prevX * currentX <= 0){
                RootSolution solution = HybridSolvers.findRootHybrid2(x, new Interval(t - stepSize, t), t - stepSize/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(x.apply(solution.root()), y.apply(solution.root())));
                } 
            }
            if(prevY * currentY <= 0){
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

    public static ArrayList<Point2D> computeIntercepts(PolarPlot plot1){
        Function<Double, Double> x = plot1.x;
        Function<Double, Double> y = plot1.y;
        double prevX = x.apply(plot1.tMin);
        double prevY = y.apply(plot1.tMin);
        ArrayList<Point2D> list = new ArrayList<>();
        double stepSize = (plot1.tMax - plot1.tMin) / plot1.maxSamples;
        for (double t = plot1.tMin + stepSize; t <= plot1.tMax; t += stepSize) {
            double currentX = x.apply(t);
            double currentY = y.apply(t);
            if(prevX * currentX <= 0){
                RootSolution solution = HybridSolvers.findRootHybrid2(x, new Interval(t - stepSize, t), t - stepSize/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(x.apply(solution.root()), y.apply(solution.root())));
                } 
            }
            if(prevY * currentY <= 0){
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

    public static ArrayList<Point2D> generatePoints(ArrayList<Plot> plots, double minWorldX, double maxWorldX, double stepSize){//TODO: deduplicate points
        ArrayList<Point2D> list = new ArrayList<>();
        for(int i = 0; i < plots.size(); i++){
            for(int j = i; j < plots.size(); j++){
                if(i == j){
                    if(plots.get(i) instanceof ParametricPlot){
                        ParametricPlot plot = (ParametricPlot)(plots.get(i));
                        ArrayList<Point2D> accuratePoints = computeIntercepts(plot);
                        list.addAll(accuratePoints);
                        plot.accurateComputedPoints.addAll(accuratePoints);
                    }
                    else if(plots.get(i) instanceof FunctionPlot){
                        list.addAll(computeIntercepts((FunctionPlot)(plots.get(i)), minWorldX, maxWorldX, stepSize));
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

    private static void adaptiveSampleFunction(Function<Double, Double> f, ViewportState state, double x1, double x2,
        ArrayList<Point2D> points, double toleranceX, double toleranceY, int depth) {
        double y1 = f.apply(x1);
        double y2 = f.apply(x2);

        double midX = x1 + (x2 - x1) / 2;
        double midY = f.apply(midX);
        // Base case
        if (depth > 25) {

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
            adaptiveSampleFunction(f, state, x1, midX, points, toleranceX, toleranceY, depth + 1);
            adaptiveSampleFunction(f, state, midX, x2, points, toleranceX, toleranceY, depth + 1);
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
                adaptiveSampleFunction(f, state, x1, midX,
                    points, toleranceX, toleranceY, depth + 1);

                adaptiveSampleFunction(f, state, midX, x2,
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
        double offset = stepX * 0.123;

        ArrayList<Segment2D> segments = new ArrayList<>();
        Function<Double, Double> function = plot.getFunction();
        for(int i = 0; i < samples - 1; i+=2){
            double x = state.left + i*stepX + offset;
            ArrayList<Point2D> points = new ArrayList<>();
            adaptiveSampleFunction(function, state, x - stepX, x + stepX + offset, points, toleranceX, toleranceY, 0);
            //points.add(new Point2D(x + stepX, plot.getFunction().apply(x + stepX)));
            for (int j = 1; j < points.size(); j++) {
                Point2D p1 = points.get(j - 1);
                Point2D p2 = points.get(j);
                if (p1 == null || p2 == null) {
                    continue;
                }
                segments.add(new Segment2D(p1, p2));
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

    public static CurveData computeCurveData(PolarPlot plot, Viewport viewport){
        ViewportState state = new ViewportState(viewport);
        BoundingBox viewportBox = new BoundingBox(state.left, state.bottom, state.worldWidth, state.worldHeight);
        Function<Double, Double> x = plot.x;
        Function<Double, Double> y = plot.y;    
        double toleranceY = Math.abs(
                viewport.screenToWorldY(.25)
            - viewport.screenToWorldY(0)
        );
        double toleranceX = Math.abs(
                viewport.screenToWorldX(.25)
            - viewport.screenToWorldX(0)
        );ArrayList<Point2D> points = new ArrayList<>();
        ArrayList<Segment2D> segments = new ArrayList<>();
        for(CurveChunk chunk : plot.chunks){
            if(viewportBox.intersects(chunk.bounds)){
                points.clear();
                adaptiveSampleParametric(x, y, state, chunk.parameterRange.a, chunk.parameterRange.b, points, toleranceX, toleranceY, 0);
                for (int j = 1; j < points.size(); j++) {
                    Point2D p1 = points.get(j - 1);
                    Point2D p2 = points.get(j);
                    segments.add(new Segment2D(p1, p2));
                }
            }
        }

        return new CurveData(plot, segments);
    }

    public static CurveData computeCurveData(ParametricPlot plot, Viewport viewport){
        ViewportState state = new ViewportState(viewport);
        BoundingBox viewportBox = new BoundingBox(state.left, state.bottom, state.worldWidth, state.worldHeight);
        Function<Double, Double> x = plot.x;
        Function<Double, Double> y = plot.y;    
        double toleranceY = Math.abs(
                viewport.screenToWorldY(.25)
            - viewport.screenToWorldY(0)
        );
        double toleranceX = Math.abs(
                viewport.screenToWorldX(.25)
            - viewport.screenToWorldX(0)
        );ArrayList<Point2D> points = new ArrayList<>();
        ArrayList<Segment2D> segments = new ArrayList<>();
        for(CurveChunk chunk : plot.chunks){
            if(viewportBox.intersects(chunk.bounds)){
                points.clear();
                adaptiveSampleParametric(x, y, state, chunk.parameterRange.a, chunk.parameterRange.b, points, toleranceX, toleranceY, 0);
                for (int j = 1; j < points.size(); j++) {
                    Point2D p1 = points.get(j - 1);
                    Point2D p2 = points.get(j);
                    segments.add(new Segment2D(p1, p2));
                }
            }
        }

        return new CurveData(plot, segments);
    }
    
    public static void adaptiveSampleParametric(
        Function<Double, Double> x, Function<Double, Double> y, ViewportState state,
        double t0, double t1, ArrayList<Point2D> points,
        double toleranceX, double toleranceY, int depth) {

        if (depth > 14) {
            points.add(new Point2D(
                x.apply(t0),
                y.apply(t0)
            ));
            return;
        }

        double x0 = x.apply(t0);
        double y0 = y.apply(t0);

        double x1 = x.apply(t1);
        double y1 = y.apply(t1);

        if (Math.abs(t1 - t0) < 1e-8) {
            points.add(new Point2D(x0, y0));
            points.add(new Point2D(x1, y1));
            return;
        }

        double vx = x1 - x0;
        double vy = y1 - y0;

        if((vx*vx)/(toleranceX*toleranceX) + (vy*vy)/(toleranceY*toleranceY) < 1){
            points.add(new Point2D(x0, y0));
            points.add(new Point2D(x1, y1));
            return;
        }
        double len2 = vx * vx + vy * vy;

        double maxError2 = 0;
        
        double maxX = Math.max(x0, x1);
        double maxY = Math.max(y0, y1);
        double minX = Math.min(x0, x1);
        double minY = Math.min(y0, y1);

        double[] ts = {
            t0 + (t1 - t0) * 0.125,
            t0 + (t1 - t0) * 0.250,
            t0 + (t1 - t0) * 0.375,
            t0 + (t1 - t0) * 0.500,
            t0 + (t1 - t0) * 0.625,
            t0 + (t1 - t0) * 0.750,
            t0 + (t1 - t0) * 0.875
        };

        for (double t : ts) {

            double xt = x.apply(t);
            double yt = y.apply(t);
            double error2;
            maxX = Math.max(maxX, xt);
            maxY = Math.max(maxY, yt);

            minX = Math.min(minX, xt);
            minY = Math.min(minY, yt);

            if (len2 < 1e-20) {

                double dx = (xt - x0)/toleranceX;

                double dy = (yt - y0)/toleranceY;

                error2 = dx * dx + dy * dy;

            } else {

                double u =
                    ((xt - x0) * vx + (yt - y0) * vy) / len2;

                u = Math.max(0.0, Math.min(1.0, u));

                double closestX = x0 + u * vx;
                double closestY = y0 + u * vy;

                double dx = (xt - closestX)/toleranceX;

                double dy = (yt - closestY)/toleranceY;

                error2 = dx * dx + dy * dy;
            }

            maxError2 = Math.max(maxError2, error2);
        }
        boolean intersects =
            maxX >= state.left   - state.marginX &&
            minX <= state.right  + state.marginX &&
            maxY >= state.bottom - state.marginY &&
            minY <= state.top    + state.marginY;

        if(!intersects) return;

        if (maxError2 > 1) {

            double tm = (t0 + t1) * 0.5;

            adaptiveSampleParametric(
                x, y, state,
                t0, tm, points,
                toleranceX, toleranceY, depth + 1
            );
            adaptiveSampleParametric(
                x, y, state,
                tm, t1, points,
                toleranceX, toleranceY, depth + 1
            );

        } else {
            points.add(new Point2D(x0, y0));
            points.add(new Point2D(x1, y1));
        }
    }

    public static CurveData computeCurveData(ODEPlot plot, Viewport viewport) {
        ViewportState state = new ViewportState(viewport);
        BoundingBox viewportBox = new BoundingBox(state.left, state.bottom,
                                                state.worldWidth, state.worldHeight);

        // extend solution if viewport has moved beyond current range
        plot.ensureCovers(state.left, state.right);

        ArrayList<Segment2D> segments = new ArrayList<>();

        for (CurveChunk chunk : plot.leftBranch) {
            if (!viewportBox.intersects(chunk.bounds)) continue;
            for (int j = chunk.initial + 1; j <= chunk.end; j++) {
                Point p1 = plot.leftPoints.get(j - 1);
                Point p2 = plot.leftPoints.get(j);
                if (!Double.isFinite(p1.x) || !Double.isFinite(p1.y) ||
                    !Double.isFinite(p2.x) || !Double.isFinite(p2.y)) continue;
                segments.add(new Segment2D(new Point2D(p1.x, p1.y), new Point2D(p2.x, p2.y)));
            }
        }

        for (CurveChunk chunk : plot.rightBranch) {
            if (!viewportBox.intersects(chunk.bounds)) continue;
            for (int j = chunk.initial + 1; j <= chunk.end; j++) {
                Point p1 = plot.rightPoints.get(j - 1);
                Point p2 = plot.rightPoints.get(j);
                if (!Double.isFinite(p1.x) || !Double.isFinite(p1.y) ||
                    !Double.isFinite(p2.x) || !Double.isFinite(p2.y)) continue;
                segments.add(new Segment2D(new Point2D(p1.x, p1.y), new Point2D(p2.x, p2.y)));
            }
        }
        System.out.println("Left chunks: " + plot.leftBranch.size());
System.out.println("Right chunks: " + plot.rightBranch.size());
System.out.println("Segments: " + segments.size());

        return new CurveData(plot, segments);
    }
}
