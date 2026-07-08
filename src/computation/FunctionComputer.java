package computation;

import java.util.ArrayList;
import java.util.function.Function;

import javafx.geometry.Point2D;
import math.Calculus;
import math.Interval;
import math.RootFinding;
import math.RootSolution;
import math.SolverStatus;
import parser.EvaluationContext;
import plotting.data.GridData;
import plotting.data.Segment2D;
import plotting.data.curve.FunctionCurveData;
import plotting.plots.FunctionPlot;
import rendering.camera.Viewport;
import rendering.camera.ViewportState;

public class FunctionComputer extends AbstractPlotComputer<FunctionPlot, FunctionCurveData>{
    public FunctionComputer(FunctionPlot plot, FunctionCurveData data) {
        super(plot, data);
    }

    private void adaptiveSampleFunction(Function<Double, Double> f, ViewportState state, double x1, double x2,
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
                    RootFinding.findRootHybrid2(
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
                    RootFinding.findRootHybrid2(
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

    private ArrayList<Point2D> computeIntercepts(EvaluationContext context, ViewportState state){
        Function<Double, Double> function = plot.getFunction(context);
        double prev = function.apply(state.left);
        ArrayList<Point2D> list = new ArrayList<>();
        double stepSize = state.worldWidth/state.viewportWidth;
        for(int i = 1; i < state.viewportWidth; i++){
            double i1 = (i - 1) * stepSize + state.left;
            double i2 = i * stepSize + state.left;
            double current = function.apply(i2);
            if(prev * current <= 0){
                RootSolution solution = RootFinding.findRootHybrid2(function, new Interval(i1, i2), i1 + (i2 - i1)/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(solution.root(), 0));
                } 
            }
            prev = current;
        }
        list.add(new Point2D(0, function.apply(0d)));
        return list;
    }

    private ArrayList<Point2D> computeCriticalPoints(EvaluationContext context, ViewportState state){
        Function<Double, Double> function = plot.getFunction(context);
        Function<Double, Double> derivative = Calculus.derivative(function, 1e-7);
        double prev = derivative.apply(state.left);
        ArrayList<Point2D> list = new ArrayList<>();
        double stepSize = state.worldWidth/state.viewportWidth;
        for(int i = 1; i < state.viewportWidth; i++){
            double i1 = (i - 1) * stepSize + state.left;
            double i2 = i * stepSize + state.left;
            double current = derivative.apply(i2);
            if(prev * current < 0){
                RootSolution solution = RootFinding.findRootHybrid2(derivative, new Interval(i1, i2), i1 + (i2 - i1)/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(solution.root(), function.apply(solution.root())));
                } 
            }
            prev = current;
        }
        return list;
    }

    @Override
    public void ensureCoverage(Viewport viewport, EvaluationContext context) {
        return;
    }

    @Override
    public void generateCurveData(Viewport viewport, GridData gridData, EvaluationContext context) {
        ViewportState state = new ViewportState(viewport);
        double samples = (int)(viewport.getWidth());
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
        Function<Double, Double> function = plot.getFunction(context);
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
        ArrayList<Point2D> featurePoints = computeCriticalPoints(context, state);
        featurePoints.addAll(computeIntercepts(context, state));

        data.setFeaturePoints(featurePoints);
        data.setVisibleSegments(segments);
    }
    @Override
    public void invalidate() {
        return;
    }
}
