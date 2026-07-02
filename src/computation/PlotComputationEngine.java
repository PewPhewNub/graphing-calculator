package computation;

import java.util.ArrayList;
import java.util.function.Function;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import math.Calculus;
import math.Interval;
import math.Point;
import math.RootFinding;
import math.RootSolution;
import math.SolverStatus;
import parser.EvaluationContext;
import plotting.data.ImplicitChunk;
import plotting.data.ODECurveChunk;
import plotting.data.ParametricCurveChunk;
import plotting.data.Segment2D;
import plotting.data.curve.CurveData;
import plotting.data.curve.FunctionCurveData;
import plotting.data.curve.ImplicitCurveData;
import plotting.data.curve.Intersection;
import plotting.data.curve.ParametricCurveData;
import plotting.data.curve.PolarCurveData;
import plotting.plots.AbstractPlot;
import plotting.plots.FunctionPlot;
import plotting.plots.ImplicitPlot;
import plotting.plots.ODEPlot;
import plotting.plots.ParametricPlot;
import plotting.plots.PolarPlot;
import plotting.plots.VectorFieldPlot;
import rendering.camera.Viewport;
import rendering.camera.ViewportState;

public class PlotComputationEngine { 

    
    
    private static ArrayList<Intersection> computeIntersections(FunctionPlot plot1, FunctionPlot plot2, EvaluationContext context, ViewportState state){
        Function<Double, Double> function = x -> plot1.getFunction(context).apply(x) - plot2.getFunction(context).apply(x);
        double prev = function.apply(state.left);
        ArrayList<Intersection> list = new ArrayList<>();
        double stepSize = state.worldWidth/state.viewportWidth;
        for(int i = 1; i < state.viewportWidth; i++){
            double i1 = (i - 1) * stepSize + state.left;
            double i2 = i * stepSize + state.left;
            double current = function.apply(i2);
            if(prev * current < 0){
                RootSolution solution = RootFinding.findRootHybrid2(function, new Interval(i1, i2), i1 + (i2 - i1)/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(
                        new Intersection(
                            new Point2D(
                                solution.root(), 
                                plot1.getFunction(context).apply(solution.root())
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

    private static ArrayList<Intersection> computeIntersections(FunctionPlot plot1, ParametricPlot plot2, EvaluationContext context, ViewportState state){
        Function<Double, Double> function1 = plot1.getFunction(context);
        Function<Double, Double> x = plot2.x;
        Function<Double, Double> y = plot2.y;
        Function<Double, Double> function = t -> function1.apply(x.apply(t)) - y.apply(t);
        double prev = function.apply(plot2.tMin);
        ArrayList<Intersection> list = new ArrayList<>();
        double stepSize = (plot2.tMax - plot2.tMin) / 2000;
        for (double t = plot2.tMin + stepSize; t <= plot2.tMax; t += stepSize) {
            double current = function.apply(t);
            if(prev * current < 0){
                RootSolution solution = RootFinding.findRootHybrid2(function, new Interval(t - stepSize, t), t - stepSize/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    double tRoot = solution.root();
                    double xRoot = x.apply(tRoot);
                    double yRoot = y.apply(tRoot);
                    list.add(
                        new Intersection(
                            new Point2D(
                                xRoot, 
                                yRoot
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
    
    private static ArrayList<Intersection> computeIntersections(FunctionPlot plot1, PolarPlot plot2, EvaluationContext context, ViewportState state){
        Function<Double, Double> function1 = plot1.getFunction(context);
        Function<Double, Double> x = plot2.x;
        Function<Double, Double> y = plot2.y;
        Function<Double, Double> function = t -> function1.apply(x.apply(t)) - y.apply(t);
        double prev = function.apply(plot2.tMin);
        ArrayList<Intersection> list = new ArrayList<>();
        double stepSize = (plot2.tMax - plot2.tMin) / 2000;
        for (double t = plot2.tMin + stepSize; t <= plot2.tMax; t += stepSize) {
            double current = function.apply(t);
            if(prev * current < 0){
                RootSolution solution = RootFinding.findRootHybrid2(function, new Interval(t - stepSize, t), t - stepSize/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    double tRoot = solution.root();
                    double xRoot = x.apply(tRoot);
                    double yRoot = y.apply(tRoot);
                    list.add(
                        new Intersection(
                            new Point2D(
                                xRoot, 
                                yRoot
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

    public static ArrayList<Point2D> computeIntercepts(AbstractPlot plot, EvaluationContext context, ViewportState state) {
        if (plot instanceof FunctionPlot fp)
            return computeIntercepts(fp, context, state);

        if (plot instanceof ParametricPlot pp)
            return computeIntercepts(pp, context);

        if (plot instanceof PolarPlot pp)
            return computeIntercepts(pp, context);

        if (plot instanceof ImplicitPlot ip)
            return computeIntercepts(ip, state);

        return new ArrayList<>();
    }
    public static ArrayList<Intersection> computeIntersections(AbstractPlot plot1, AbstractPlot plot2, EvaluationContext context, Viewport viewport){
        if(plot1 instanceof ODEPlot) return new ArrayList<>();
        if(plot2 instanceof ODEPlot) return new ArrayList<>();
        ViewportState state = new ViewportState(viewport);
        if(plot1 instanceof FunctionPlot p1){
            if(plot2 instanceof FunctionPlot p2) return computeIntersections(p1, p2, context, state);   
            if(plot2 instanceof PolarPlot p2){
                ArrayList<Intersection> list = computeIntersections(p1, p2, context, state);
                return list;
            }
            if(plot2 instanceof ParametricPlot p2){
                ArrayList<Intersection> list = computeIntersections(p1, p2, context, state);
                return list;
            }
        }else if(plot1 instanceof ParametricPlot p1){
            if(plot2 instanceof FunctionPlot p2){
                ArrayList<Intersection> list = computeIntersections(p2, p1, context, state);
                return list;
            }else return computeIntersectionsCurves(plot1, plot2, viewport);
        }else if(plot1 instanceof PolarPlot p1){
            if(plot2 instanceof FunctionPlot p2){
                ArrayList<Intersection> list = computeIntersections(p2, p1, context, state);
                return list;
            }else return computeIntersectionsCurves(plot1, plot2, viewport);
        }else{
            return computeIntersectionsCurves(plot1, plot2, viewport);
        }
        return new ArrayList<>();
    }
    private static ArrayList<Point2D> computeCriticalPoints(AbstractPlot plot, EvaluationContext context, ViewportState state){
         if (plot instanceof FunctionPlot fp)
            return computeCriticalPoints(fp, context, state);

        if (plot instanceof ParametricPlot pp)
            return computeCriticalPoints(pp);

        if (plot instanceof PolarPlot pp)
            return computeCriticalPoints(pp);

        return new ArrayList<>();
    }

    private static ArrayList<Intersection> computeIntersectionsCurves(AbstractPlot plot1, AbstractPlot plot2, Viewport viewport){
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

    private static CurveData computeCurveData(VectorFieldPlot plot, Viewport viewport){
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

    public static CurveData computeCurveData(AbstractPlot plot, EvaluationContext context, Viewport viewport){
        if(plot == null) return null;
        if(plot instanceof FunctionPlot p) return PlotComputationEngine.computeCurveData(p, context, viewport);
        if(plot instanceof ODEPlot p) return PlotComputationEngine.computeCurveData(p, viewport);
        if(plot instanceof ParametricPlot p) return PlotComputationEngine.computeCurveData(p, context, viewport);
        if(plot instanceof PolarPlot p) return PlotComputationEngine.computeCurveData(p, context, viewport);
        if(plot instanceof ImplicitPlot p) return PlotComputationEngine.computeCurveData(p, viewport);
        if(plot instanceof VectorFieldPlot p) return PlotComputationEngine.computeCurveData(p, viewport);
        else return null;
    }

}


