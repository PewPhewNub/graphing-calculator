package computation;

import java.util.ArrayList;
import java.util.function.Function;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import math.Calculus;
import math.Interval;
import math.RootFinding;
import math.RootSolution;
import math.SolverStatus;
import parser.EvaluationContext;
import plotting.data.ParametricCurveChunk;
import plotting.data.Segment2D;
import plotting.data.curve.ParametricCurveData;
import plotting.plots.ParametricPlot;
import rendering.camera.Viewport;
import rendering.camera.ViewportState;

public class ParametricComputer extends AbstractPlotComputer<ParametricPlot, ParametricCurveData>{
    public ParametricComputer(ParametricPlot plot, ParametricCurveData data){
        super(plot, data);
    }
    public void generateCurveData( Viewport viewport, EvaluationContext context){
        ViewportState state = new ViewportState(viewport);
        BoundingBox viewportBox = new BoundingBox(state.left, state.bottom, state.worldWidth, state.worldHeight);
        Function<Double, Double> x = plot.getX(context);
        Function<Double, Double> y = plot.getY(context);    
        double toleranceY = Math.abs(
                viewport.screenToWorldY(.5)
            - viewport.screenToWorldY(0)
        );
        double toleranceX = Math.abs(
                viewport.screenToWorldX(.5)
            - viewport.screenToWorldX(0)
        );
        ArrayList<Segment2D> segments = new ArrayList<>();
        for(ParametricCurveChunk chunk : data.chunks){
            if(viewportBox.intersects(chunk.bounds)){
                segments.addAll(generateChunkSegments(x, y, chunk, state, toleranceX, toleranceY));
            }
        }

        ArrayList<Point2D> featurePoints = computeCriticalPoints(context);
        featurePoints.addAll(computeIntercepts(context));

        data.setFeaturePoints(featurePoints);
        data.setVisibleSegments(segments);
    }
    
    private void adaptiveSampleParametric(
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

     private ArrayList<Segment2D> generateChunkSegments(Function<Double, Double> x, Function<Double, Double> y, ParametricCurveChunk chunk, ViewportState state, double toleranceX, double toleranceY){
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
    private ArrayList<Point2D> computeIntercepts(EvaluationContext context){
        Function<Double, Double> x = plot.getX(context);
        Function<Double, Double> y = plot.getY(context);
        double prevX = x.apply(plot.tMin);
        double prevY = y.apply(plot.tMin);
        ArrayList<Point2D> list = new ArrayList<>();
        double stepSize = (plot.tMax - plot.tMin) / 2000;
        for (double t = plot.tMin + stepSize; t <= plot.tMax; t += stepSize) {
            double currentX = x.apply(t);
            double currentY = y.apply(t);
            if(prevX * currentX <= 0){
                RootSolution solution = RootFinding.findRootHybrid2(x, new Interval(t - stepSize, t), t - stepSize/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(x.apply(solution.root()), y.apply(solution.root())));
                } 
            }
            if(prevY * currentY <= 0){
                RootSolution solution = RootFinding.findRootHybrid2(y, new Interval(t - stepSize, t), t - stepSize/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(x.apply(solution.root()), y.apply(solution.root())));
                } 
            }
            prevX = currentX;
            prevY = currentY;
        }
        return list;
    }
    private ArrayList<Point2D> computeCriticalPoints(EvaluationContext context){
        Function<Double, Double> x = plot.getX(context);
        Function<Double, Double> y = plot.getY(context);
        Function<Double, Double> dx = Calculus.derivative(x, 1e-7);
        Function<Double, Double> dy = Calculus.derivative(y, 1e-7);
        double tMin = plot.tMin;
        double prevX = dx.apply(tMin);
        double prevY = dy.apply(tMin);
        double stepSize = (plot.tMax - plot.tMin) / 2000;
        ArrayList<Point2D> list = new ArrayList<>();
        for(int i = 1; i < 2000; i++){
            double i1 = (i - 1) * stepSize + tMin;
            double i2 = i * stepSize + tMin;
            double currentX = dx.apply(i2);
            double currentY = dy.apply(i2);
            if(prevX * currentX < 0){
                RootSolution solution = RootFinding.findRootHybrid2(dx, new Interval(i1, i2), i1 + (i2 - i1)/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(x.apply(solution.root()), y.apply(solution.root())));
                } 
            }
            if(prevY * currentY < 0){
                RootSolution solution = RootFinding.findRootHybrid2(dy, new Interval(i1, i2), i1 + (i2 - i1)/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(x.apply(solution.root()), y.apply(solution.root())));
                } 
            }
            prevX = currentX;
            prevY = currentY;
        }
        return list;
    }
    public void reloadChunks(EvaluationContext context){
        double chunkSize = 5; // radians
        double tMin = plot.tMin;
        double tMax = plot.tMax;

        ArrayList<ParametricCurveChunk> curveChunks = new ArrayList<>();

        for(double t = tMin; t < tMax; t += chunkSize){
            curveChunks.add(
                new ParametricCurveChunk(
                    new Interval(t, Math.min(t + chunkSize, tMax)),
                    plot.sample(t, context),
                    plot.sample(Math.min(t + chunkSize, tMax), context),
                    computeBounds(t, Math.min(t + chunkSize, tMax), context)
                )
            );
        }
        
        data.chunks.clear();
        data.chunks.addAll(curveChunks);
    }

    public BoundingBox computeBounds(double t0, double t1, EvaluationContext context){
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        int samples = 128;

        Function<Double, Double> x = plot.getX(context);
        Function<Double, Double> y = plot.getY(context);

        for(int i=0;i<=samples;i++){
            double t = t0 + (t1-t0)*i/(double)samples;

            double xt = x.apply(t);
            double yt = y.apply(t);

            minX = Math.min(minX, xt);
            minY = Math.min(minY, yt);
            maxX = Math.max(maxX, xt);
            maxY = Math.max(maxY, yt);
        }

        return new BoundingBox(minX,minY,maxX - minX,maxY - minY);
    }
    @Override
    public void ensureCoverage(Viewport viewport, EvaluationContext context) {
        if(data.chunks.isEmpty())
        reloadChunks(context);
    }
    
    @Override
    protected void invalidate() {
        data.chunks.clear();
    }
}
