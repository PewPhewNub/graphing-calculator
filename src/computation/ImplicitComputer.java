package computation;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

import javafx.geometry.Point2D;
import math.Interval;
import math.RootFinding;
import math.RootSolution;
import math.SolverStatus;
import parser.EvaluationContext;
import plotting.data.ImplicitChunk;
import plotting.data.Segment2D;
import plotting.data.curve.ImplicitCurveData;
import plotting.plots.ImplicitPlot;
import rendering.camera.Viewport;
import rendering.camera.ViewportState;

public class ImplicitComputer extends AbstractPlotComputer<ImplicitPlot, ImplicitCurveData>{
    public ImplicitComputer(ImplicitPlot plot, ImplicitCurveData data){
        super(plot, data);
    }

    public void generateCurveData(Viewport viewport, EvaluationContext context){
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

        ArrayList<Point2D> intercepts = computeIntercepts(new ViewportState(viewport), context);

        data.setFeaturePoints(intercepts);
        data.setVisibleSegments(segments);
    }
    private ArrayList<Point2D> computeIntercepts(ViewportState state, EvaluationContext context){
        ArrayList<Point2D> list = new ArrayList<>();
        BiFunction<Double, Double, Double> function = plot.getFunction(context);
        Function<Double, Double> yConst = y -> function.apply(0d, y);
        double prevX = yConst.apply(state.left);
        double stepSizeX = state.worldWidth/state.viewportWidth;
        for(int i = 1; i < state.viewportWidth; i++){
            double i1 = (i - 1) * stepSizeX + state.left;
            double i2 = i * stepSizeX + state.left;
            double current = yConst.apply(i2);
            if(prevX * current <= 0){
                RootSolution solution = RootFinding.findRootHybrid2(yConst, new Interval(i1, i2), i1 + (i2 - i1)/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(0 , solution.root()));
                } 
            }
            prevX = current;
        }

        Function<Double, Double> xConst = x -> function.apply(x, 0d);
        double prevY = yConst.apply(state.bottom);
        double stepSizeY = state.worldHeight/state.viewportHeight;
        for(int i = 1; i < state.viewportHeight; i++){
            double i1 = (i - 1) * stepSizeY + state.bottom;
            double i2 = i * stepSizeY + state.bottom;
            double current = yConst.apply(i2);
            if(prevY * current <= 0){
                RootSolution solution = RootFinding.findRootHybrid2(xConst, new Interval(i1, i2), i1 + (i2 - i1)/2, 1e-10, 1000);
                if(solution.status() == SolverStatus.SUCCESS){
                    list.add(new Point2D(solution.root(), 0));
                } 
            }
            prevY = current;
        }

        return list;
    }

    @Override
    protected void ensureCoverage(Viewport viewport, EvaluationContext context) {
        plot.ensureCoverage(viewport, context);
    }

    @Override
    protected void invalidate() {
        System.out.println("Invalidating implicit plot");
        plot.chunks.clear();
    }
}
