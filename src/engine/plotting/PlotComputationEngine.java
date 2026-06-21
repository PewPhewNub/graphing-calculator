package engine.plotting;

import java.util.ArrayList;
import java.util.function.Function;

import core.math.Core.Calculus;
import core.math.Core.Interval;
import core.math.Core.Point;
import core.math.Core.RootSolution;
import core.math.Core.SolverStatus;
import core.math.RootFindingAlgorithms.HybridSolvers;
import core.model.ImplicitChunk;
import core.model.ODECurveChunk;
import core.model.ParametricCurveChunk;
import core.model.Segment2D;
import core.model.ViewportState;
import core.model.curve.CurveData;
import core.model.curve.FunctionCurveData;
import core.model.curve.Intersection;
import core.model.curve.ParametricCurveData;
import core.model.curve.PolarCurveData;
import engine.plotting.plots.FunctionPlot;
import engine.plotting.plots.ImplicitPlot;
import engine.plotting.plots.ODEPlot;
import engine.plotting.plots.ParametricPlot;
import engine.plotting.plots.Plot;
import engine.plotting.plots.PolarPlot;
import engine.plotting.plots.VectorFieldPlot;
import engine.rendering.camera.Viewport;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;

public class PlotComputationEngine { 

    public static ArrayList<Point2D> computeIntercepts(FunctionPlot plot1, ViewportState state){
        Function<Double, Double> function = plot1.getFunction();
        double prev = function.apply(state.left);
        ArrayList<Point2D> list = new ArrayList<>();
        double stepSize = state.worldWidth/state.viewportWidth;
        for(int i = 1; i < state.viewportWidth; i++){
            double i1 = (i - 1) * stepSize + state.left;
            double i2 = i * stepSize + state.left;
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

    public static ArrayList<Intersection> computeIntersections(FunctionPlot plot1, FunctionPlot plot2, ViewportState state){
        Function<Double, Double> function = x -> plot1.getFunction().apply(x) - plot2.getFunction().apply(x);
        double prev = function.apply(state.left);
        ArrayList<Intersection> list = new ArrayList<>();
        double stepSize = state.worldWidth/state.viewportWidth;
        for(int i = 1; i < state.viewportWidth; i++){
            double i1 = (i - 1) * stepSize + state.left;
            double i2 = i * stepSize + state.left;
            double current = function.apply(i2);
            if(prev * current < 0){
                RootSolution solution = HybridSolvers.findRootHybrid2(function, new Interval(i1, i2), i1 + (i2 - i1)/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(
                        new Intersection(
                            new Point2D(
                                solution.root(), 
                                plot1.getFunction().apply(solution.root())
                            ),
                        plot1,
                        plot2)
                    );
                } 
            }
            prev = current;
        }
        return list;
    }

    public static ArrayList<Intersection> computeIntersections(FunctionPlot plot1, ParametricPlot plot2, ViewportState state){
        Function<Double, Double> function1 = plot1.getFunction();
        Function<Double, Double> x = plot2.x;
        Function<Double, Double> y = plot2.y;
        Function<Double, Double> function = t -> function1.apply(x.apply(t)) - y.apply(t);
        double prev = function.apply(plot2.tMin);
        ArrayList<Intersection> list = new ArrayList<>();
        double stepSize = (plot2.tMax - plot2.tMin) / plot2.maxSamples;
        for (double t = plot2.tMin + stepSize; t <= plot2.tMax; t += stepSize) {
            double current = function.apply(t);
            if(prev * current < 0){
                RootSolution solution = HybridSolvers.findRootHybrid2(function, new Interval(t - stepSize, t), t - stepSize/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(
                        new Intersection(
                            new Point2D(
                                solution.root(), 
                                plot1.getFunction().apply(solution.root())
                            ),
                        plot1,
                        plot2)
                    );
                } 
            }
            prev = current;
        }
        return list;
    }
    
    public static ArrayList<Intersection> computeIntersections(FunctionPlot plot1, PolarPlot plot2, ViewportState state){
        Function<Double, Double> function1 = plot1.getFunction();
        Function<Double, Double> x = plot2.x;
        Function<Double, Double> y = plot2.y;
        Function<Double, Double> function = t -> function1.apply(x.apply(t)) - y.apply(t);
        double prev = function.apply(plot2.tMin);
        ArrayList<Intersection> list = new ArrayList<>();
        double stepSize = (plot2.tMax - plot2.tMin) / plot2.maxSamples;
        for (double t = plot2.tMin + stepSize; t <= plot2.tMax; t += stepSize) {
            double current = function.apply(t);
            if(prev * current < 0){
                RootSolution solution = HybridSolvers.findRootHybrid2(function, new Interval(t - stepSize, t), t - stepSize/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(
                        new Intersection(
                            new Point2D(
                                solution.root(), 
                                plot1.getFunction().apply(solution.root())
                            ),
                        plot1,
                        plot2)
                    );
                } 
            }
            prev = current;
        }
        return list;
    }

    public static ArrayList<Point2D> computeCriticalPoints(FunctionPlot plot1, ViewportState state){
        Function<Double, Double> function = plot1.getFunction();
        Function<Double, Double> derivative = Calculus.derivative(function, 1e-7);
        double prev = derivative.apply(state.left);
        ArrayList<Point2D> list = new ArrayList<>();
        double stepSize = state.worldWidth/state.viewportWidth;
        for(int i = 1; i < state.viewportWidth; i++){
            double i1 = (i - 1) * stepSize + state.left;
            double i2 = i * stepSize + state.left;
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

    public static ArrayList<Point2D> computeCriticalPoints(PolarPlot plot1){
        Function<Double, Double> x = plot1.x;
        Function<Double, Double> y = plot1.y;
        Function<Double, Double> dx = Calculus.derivative(x, 1e-7);
        Function<Double, Double> dy = Calculus.derivative(y, 1e-7);
        double tMin = plot1.tMin;
        double tMax = plot1.tMax;
        double prevX = dx.apply(tMin);
        double prevY = dy.apply(tMin);
        double stepSize = (plot1.tMax - plot1.tMin) / plot1.maxSamples;
        ArrayList<Point2D> list = new ArrayList<>();
        for(int i = 1; i < plot1.maxSamples; i++){
            double i1 = (i - 1) * stepSize + tMin;
            double i2 = i * stepSize + tMin;
            double currentX = dx.apply(i2);
            double currentY = dy.apply(i2);
            if(prevX * currentX < 0){
                RootSolution solution = HybridSolvers.findRootHybrid2(dx, new Interval(i1, i2), i1 + (i2 - i1)/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(x.apply(solution.root()), y.apply(solution.root())));
                } 
            }
            if(prevY * currentY < 0){
                RootSolution solution = HybridSolvers.findRootHybrid2(dy, new Interval(i1, i2), i1 + (i2 - i1)/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(x.apply(solution.root()), y.apply(solution.root())));
                } 
            }
            prevX = currentX;
            prevY = currentY;
        }
        return list;
    }

    public static ArrayList<Point2D> computeCriticalPoints(ParametricPlot plot1){
        Function<Double, Double> x = plot1.x;
        Function<Double, Double> y = plot1.y;
        Function<Double, Double> dx = Calculus.derivative(x, 1e-7);
        Function<Double, Double> dy = Calculus.derivative(y, 1e-7);
        double tMin = plot1.tMin;
        double tMax = plot1.tMax;
        double prevX = dx.apply(tMin);
        double prevY = dy.apply(tMin);
        double stepSize = (plot1.tMax - plot1.tMin) / plot1.maxSamples;
        ArrayList<Point2D> list = new ArrayList<>();
        for(int i = 1; i < plot1.maxSamples; i++){
            double i1 = (i - 1) * stepSize + tMin;
            double i2 = i * stepSize + tMin;
            double currentX = dx.apply(i2);
            double currentY = dy.apply(i2);
            if(prevX * currentX < 0){
                RootSolution solution = HybridSolvers.findRootHybrid2(dx, new Interval(i1, i2), i1 + (i2 - i1)/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(x.apply(solution.root()), y.apply(solution.root())));
                } 
            }
            if(prevY * currentY < 0){
                RootSolution solution = HybridSolvers.findRootHybrid2(dy, new Interval(i1, i2), i1 + (i2 - i1)/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(x.apply(solution.root()), y.apply(solution.root())));
                } 
            }
            prevX = currentX;
            prevY = currentY;
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
        ArrayList<Point2D> featurePoints = computeCriticalPoints(plot, state);
        featurePoints.addAll(computeIntercepts(plot, state));

        return new FunctionCurveData(plot, segments, featurePoints);
    }

    public static CurveData computeCurveData(PolarPlot plot, Viewport viewport){
        ViewportState state = new ViewportState(viewport);
        BoundingBox viewportBox = new BoundingBox(state.left, state.bottom, state.worldWidth, state.worldHeight);
        Function<Double, Double> x = plot.x;
        Function<Double, Double> y = plot.y;    
        double toleranceY = Math.abs(
                viewport.screenToWorldY(1)
            - viewport.screenToWorldY(0)
        );
        double toleranceX = Math.abs(
                viewport.screenToWorldX(1)
            - viewport.screenToWorldX(0)
        );
        ArrayList<Segment2D> segments = new ArrayList<>();
        for(ParametricCurveChunk chunk : plot.chunks){
            if(viewportBox.intersects(chunk.bounds)){
                segments.addAll(generateChunkSegments(x, y, chunk, state, toleranceX, toleranceY));
            }
        }
        ArrayList<Point2D> featurePoints = computeCriticalPoints(plot, state);
        featurePoints.addAll(computeIntercepts(plot, state));

        return new PolarCurveData(plot, segments, featurePoints);
    }

    private static ArrayList<Segment2D> generateChunkSegments(Function<Double, Double> x, Function<Double, Double> y, ParametricCurveChunk chunk, ViewportState state, double toleranceX, double toleranceY){
        ArrayList<Point2D> points = new ArrayList<>();
        ArrayList<Segment2D> segments = new ArrayList<>();
        adaptiveSampleParametric(x, y, state, chunk.parameterRange.a, chunk.parameterRange.b, points, toleranceX, toleranceY, 0);
        for (int j = 1; j < points.size(); j++) {
            Point2D p1 = points.get(j - 1);
            Point2D p2 = points.get(j);
            segments.add(new Segment2D(p1, p2));
        }
        return segments;
    }

    public static CurveData computeCurveData(ParametricPlot plot, Viewport viewport){
        ViewportState state = new ViewportState(viewport);
        BoundingBox viewportBox = new BoundingBox(state.left, state.bottom, state.worldWidth, state.worldHeight);
        Function<Double, Double> x = plot.x;
        Function<Double, Double> y = plot.y;    
        double toleranceY = Math.abs(
                viewport.screenToWorldY(1)
            - viewport.screenToWorldY(0)
        );
        double toleranceX = Math.abs(
                viewport.screenToWorldX(1)
            - viewport.screenToWorldX(0)
        );
        ArrayList<Segment2D> segments = new ArrayList<>();
        for(ParametricCurveChunk chunk : plot.chunks){
            if(viewportBox.intersects(chunk.bounds)){
                segments.addAll(generateChunkSegments(x, y, chunk, state, toleranceX, toleranceY));
            }
        }

        ArrayList<Point2D> featurePoints = computeCriticalPoints(plot, state);
        featurePoints.addAll(computeIntercepts(plot, state));

        return new ParametricCurveData(plot, segments, featurePoints);
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

        BoundingBox viewportBox = new BoundingBox(
            state.left,
            state.bottom,
            state.worldWidth,
            state.worldHeight
        );

        plot.ensureCovers(state.left, state.right);

        ArrayList<Segment2D> segments = new ArrayList<>();

        final double minPixelDistance2 = 1.0; // 1 px squared

        for (ODECurveChunk chunk : plot.leftBranch) {

            if (!viewportBox.intersects(chunk.bounds)) continue;

            Point anchor = plot.leftPoints.get(chunk.initial);

            for (int j = chunk.initial + 1; j <= chunk.end; j++) {

                Point current = plot.leftPoints.get(j);

                if (!Double.isFinite(anchor.x) || !Double.isFinite(anchor.y) ||
                    !Double.isFinite(current.x) || !Double.isFinite(current.y))
                    continue;

                double dx =
                    viewport.worldToScreenX(current.x)
                - viewport.worldToScreenX(anchor.x);

                double dy =
                    viewport.worldToScreenY(current.y)
                - viewport.worldToScreenY(anchor.y);

                double dist2 = dx * dx + dy * dy;

                if (dist2 < minPixelDistance2)
                    continue;

                segments.add(
                    new Segment2D(
                        new Point2D(anchor.x, anchor.y),
                        new Point2D(current.x, current.y)
                    )
                );

                anchor = current;
            }
        }

        for (ODECurveChunk chunk : plot.rightBranch) {

            if (!viewportBox.intersects(chunk.bounds)) continue;

            Point anchor = plot.rightPoints.get(chunk.initial);

            for (int j = chunk.initial + 1; j <= chunk.end; j++) {

                Point current = plot.rightPoints.get(j);

                if (!Double.isFinite(anchor.x) || !Double.isFinite(anchor.y) ||
                    !Double.isFinite(current.x) || !Double.isFinite(current.y))
                    continue;

                double dx =
                    viewport.worldToScreenX(current.x)
                - viewport.worldToScreenX(anchor.x);

                double dy =
                    viewport.worldToScreenY(current.y)
                - viewport.worldToScreenY(anchor.y);

                double dist2 = dx * dx + dy * dy;

                if (dist2 < minPixelDistance2)
                    continue;

                double[] clipped = liangBarsky(anchor.x, anchor.y, current.x, current.y, state.left, state.right, state.bottom, state.top);
                if(clipped != null)
                    segments.add(
                        new Segment2D(
                            new Point2D(clipped[0], clipped[1]),
                            new Point2D(clipped[2], clipped[3])
                        )
                    );
                else{
                    segments.add(
                        new Segment2D(
                            new Point2D(anchor.x, anchor.y),
                            new Point2D(current.x, current.y)
                        )
                    );
                }

                anchor = current;
            }
        }

        return new CurveData(plot, segments);
    }
    public static ArrayList<Point2D> computeIntercepts(Plot plot, ViewportState state) {
        if (plot instanceof FunctionPlot fp)
            return computeIntercepts(fp, state);

        if (plot instanceof ParametricPlot pp)
            return computeIntercepts(pp);

        if (plot instanceof PolarPlot pp)
            return computeIntercepts(pp);

        return new ArrayList<>();
    }
    public static ArrayList<Intersection> computeIntersections(Plot plot1, Plot plot2, Viewport viewport){
        if(plot1 instanceof ODEPlot) return new ArrayList<>();
        if(plot2 instanceof ODEPlot) return new ArrayList<>();
        ViewportState state = new ViewportState(viewport);
        if(plot1 instanceof FunctionPlot p1){
            if(plot2 instanceof FunctionPlot p2) return computeIntersections(p1, p2, state);   
            if(plot2 instanceof PolarPlot p2){
                ArrayList<Intersection> list = computeIntersections(p1, p2, state);
                return list;
            }
            if(plot2 instanceof ParametricPlot p2){
                ArrayList<Intersection> list = computeIntersections(p1, p2, state);
                return list;
            }
        }else if(plot1 instanceof ParametricPlot p1){
            if(plot2 instanceof FunctionPlot p2){
                ArrayList<Intersection> list = computeIntersections(p2, p1, state);
                return list;
            }else return computeIntersectionsCurves(plot1, plot2, viewport);
        }else if(plot1 instanceof PolarPlot p1){
            if(plot2 instanceof FunctionPlot p2){
                ArrayList<Intersection> list = computeIntersections(p2, p1, state);
                return list;
            }else return computeIntersectionsCurves(plot1, plot2, viewport);
        }else{
            return computeIntersectionsCurves(plot1, plot2, viewport);
        }
        return new ArrayList<>();
    }
    public static ArrayList<Point2D> computeCriticalPoints(Plot plot, ViewportState state){
         if (plot instanceof FunctionPlot fp)
            return computeCriticalPoints(fp, state);

        if (plot instanceof ParametricPlot pp)
            return computeCriticalPoints(pp);

        if (plot instanceof PolarPlot pp)
            return computeCriticalPoints(pp);

        return new ArrayList<>();
    }

    public static ArrayList<Intersection> computeIntersectionsCurves(Plot plot1, Plot plot2, Viewport viewport){
        if(plot1 instanceof ODEPlot || plot1 instanceof FunctionPlot) return new ArrayList<>();
        if(plot2 instanceof ODEPlot || plot2 instanceof FunctionPlot) return new ArrayList<>();
        if(plot1 == plot2) return new ArrayList<>();
        ArrayList<Intersection> list = new ArrayList<>();
        ArrayList<ParametricCurveChunk> chunks1 = null;
        ArrayList<ParametricCurveChunk> chunks2 = null;
        ViewportState state = new ViewportState(viewport);
        Function<Double, Double> x1 = null;
        Function<Double, Double> y1 = null;
        Function<Double, Double> x2 = null;
        Function<Double, Double> y2 = null;
        if(plot1 instanceof PolarPlot p1){
            chunks1 = p1.chunks;
            x1 = p1.x;
            y1 = p1.y;
        }else if(plot1 instanceof ParametricPlot p1){
            chunks1 = p1.chunks;
            x1 = p1.x;
            y1 = p1.y;
        }
        if(plot2 instanceof PolarPlot p2){
            chunks2 = p2.chunks;
            x2 = p2.x;
            y2 = p2.y;
        }else if(plot2 instanceof ParametricPlot p2){
            chunks2 = p2.chunks;
            x2 = p2.x;
            y2 = p2.y;
        }
        if(chunks1 == null || chunks2 == null)
            return new ArrayList<>();
        if(x1== x2 && y1 == y2) return new ArrayList<>();
        double toleranceY = Math.abs(
                viewport.screenToWorldY(.25)
            - viewport.screenToWorldY(0)
        );
        double toleranceX = Math.abs(
                viewport.screenToWorldX(.25)
            - viewport.screenToWorldX(0)
        );

        for(int i = 0; i < chunks1.size(); i++){
            ParametricCurveChunk chunk1 = chunks1.get(i);
            ArrayList<Segment2D> segments1 = generateChunkSegments(x1, y1, chunk1, state, toleranceX, toleranceY);
            for(int j = 0; j < chunks2.size(); j++){
                ParametricCurveChunk chunk2 = chunks2.get(j);
                if(!chunk1.bounds.intersects(chunk2.bounds)) continue;
                ArrayList<Segment2D> segments2 = generateChunkSegments(x2, y2, chunk2, state, toleranceX, toleranceY);

                for(Segment2D k : segments1){
                    for(Segment2D l : segments2){
                        if(k.equals(l)) continue;
                        Point2D a = k.point1;
                        Point2D b = k.point2;

                        Point2D c = l.point1;
                        Point2D d = l.point2;

                        double dx1 = b.getX() - a.getX();
                        double dy1 = b.getY() - a.getY();

                        double dx2 = d.getX() - c.getX();
                        double dy2 = d.getY() - c.getY();

                        double det = dx1 * dy2 - dy1 * dx2;

                        if (Math.abs(det) < 0.1)
                            continue;
                        double cx = c.getX() - a.getX();
                        double cy = c.getY() - a.getY();

                        double t = (cx * dy2 - cy * dx2) / det;
                        double u = (cx * dy1 - cy * dx1) / det;

                        if (t < 0 || t > 1 || u < 0 || u > 1)
                            continue;

                        double X = a.getX() + t * dx1;
                        double Y = a.getY() + t * dy1;

                        
                        list.add(
                            new Intersection(
                                new Point2D(X,Y),
                                plot1,
                                plot2)
                            );
                    }
                }
            }
        }
        return list;
    }

    public static CurveData computeCurveData(ImplicitPlot plot, Viewport viewport){
        plot.ensureCoverage(viewport);
        ViewportState state = new ViewportState(viewport);
        double CHUNK_SIZE = plot.CHUNK_SIZE;

        int minChunkX = (int)(Math.floor(state.left/CHUNK_SIZE));
        int maxChunkX = (int)(Math.floor(state.right/CHUNK_SIZE));
        int minChunkY = (int)(Math.floor(state.bottom/CHUNK_SIZE));
        int maxChunkY = (int)(Math.floor(state.top/CHUNK_SIZE));

        ArrayList<Segment2D> segments = new ArrayList<>();
        for(int cx = minChunkX; cx <= maxChunkX; cx++){
            for(int cy = minChunkY; cy <= maxChunkY; cy++){
                ImplicitChunk chunk = plot.chunks.get(new Point2D(cx, cy));

                if(chunk == null) continue;
                if(!chunk.generated) continue;

                segments.addAll(chunk.segments);
            }
        }
        System.out.println("render segments = " + segments.size());
        return new CurveData(plot, segments);
    }
    public static CurveData computeCurveData(VectorFieldPlot plot, Viewport viewport){
        return new CurveData(plot, plot.sample());
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


