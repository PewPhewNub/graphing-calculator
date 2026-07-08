package computation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import math.Interval;
import math.RootFinding;
import math.RootSolution;
import math.SolverStatus;
import parser.EvaluationContext;
import plotting.data.GridData;
import plotting.data.ImplicitChunk;
import plotting.data.Segment2D;
import plotting.data.curve.ImplicitCurveData;
import plotting.plots.ImplicitPlot;
import rendering.camera.Viewport;
import rendering.camera.ViewportState;

public class ImplicitComputer extends AbstractPlotComputer<ImplicitPlot, ImplicitCurveData>{  
    public final double CHUNK_SIZE = 16;
    private final double BASE_SIZE = 1f/16;
    private final int maxCellsPerSide = 512;
    private double minimumCellSize = 1e-4;
    public ImplicitComputer(ImplicitPlot plot, ImplicitCurveData data){
        super(plot, data);
    }

    public void generateCurveData(Viewport viewport, GridData gridData, EvaluationContext context){
        ViewportState state = new ViewportState(viewport);

        int minChunkX = (int)(Math.floor(state.left/CHUNK_SIZE));
        int maxChunkX = (int)(Math.floor(state.right/CHUNK_SIZE));
        int minChunkY = (int)(Math.floor(state.bottom/CHUNK_SIZE));
        int maxChunkY = (int)(Math.floor(state.top/CHUNK_SIZE));

        ArrayList<Segment2D> segments = new ArrayList<>();
        for(int cx = minChunkX; cx <= maxChunkX; cx++){
            for(int cy = minChunkY; cy <= maxChunkY; cy++){
                ImplicitChunk chunk = data.chunks.get(new Point2D(cx, cy));

                if(chunk == null) continue;
                if(!chunk.generated) continue;

                segments.addAll(chunk.segments);
            }
        }

        ArrayList<Point2D> intercepts = computeIntercepts(state, context);

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
    public void invalidate() {
        System.out.println("Invalidating implicit plot");
        data.chunks.clear();
    }

    public ArrayList<Segment2D> marchingSquares(ImplicitChunk chunk, int LOD, EvaluationContext context){
        ArrayList<Segment2D> segments = new ArrayList<>();

        double left = chunk.bounds.getMinX();
        double bottom = chunk.bounds.getMinY();

        this.minimumCellSize = BASE_SIZE / Math.pow(2, LOD);

        DoubleBinaryOperator sample = (x, y) -> plot.sample(x, y, context);

        double spacing = CHUNK_SIZE / (1 << 12);

        subdivide(
            chunk,
            0, 0,
            1 << 12, 1 << 12,
            left,
            bottom,
            chunk.bounds.getWidth(),
            chunk.bounds.getHeight(),
            sampleCached(chunk, 0,        1 << 12, sample, spacing), // A
            sampleCached(chunk, 1 << 12,  1 << 12, sample, spacing), // B
            sampleCached(chunk, 1 << 12,  0,       sample, spacing), // C
            sampleCached(chunk, 0,        0,       sample, spacing), // D
            segments,
            sample,
            spacing,
            0
        );

        return segments;
    }

    public void ensureCoverage(Viewport viewport, EvaluationContext context){
        ViewportState state = new ViewportState(viewport);

        int LOD = calculateLOD(viewport);

        int minChunkX = (int)(Math.floor(state.left/CHUNK_SIZE));
        int maxChunkX = (int)(Math.floor(state.right/CHUNK_SIZE));
        int minChunkY = (int)(Math.floor(state.bottom/CHUNK_SIZE));
        int maxChunkY = (int)(Math.floor(state.top/CHUNK_SIZE));

        for(int cx = minChunkX; cx <= maxChunkX; cx++){
            for(int cy = minChunkY; cy <= maxChunkY; cy++){
                Point2D key = new Point2D(cx, cy);

                if(!data.chunks.containsKey(key)) {
                    generateChunk(cx, cy, LOD, context);
                    continue;
                }

                ImplicitChunk chunk = data.chunks.get(key);
                if(!chunk.generated) continue;
                if(chunk.LOD != LOD){
                    generateChunk(cx, cy, LOD, context);
                }
            }
        }
    }

    public int calculateLOD(Viewport viewport) {
        ViewportState state = new ViewportState(viewport);

        double worldUnitsPerPixel =
            state.worldWidth / state.viewportWidth;

        double desiredStep =
            worldUnitsPerPixel * 4.0;

        int lod = (int)Math.round(
            Math.log(BASE_SIZE / desiredStep) / Math.log(2)
        );

        return Math.min(Math.max(0, lod), 4);
    }

    public void generateChunk(double cx, double cy, int LOD, EvaluationContext context){
        ImplicitChunk chunk = new ImplicitChunk();
        chunk.bounds = new BoundingBox(cx * CHUNK_SIZE, cy*CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE);
        ArrayList<Segment2D> segments = marchingSquares(chunk, LOD, context);
        if(segments== null){
            chunk.generated = false;
            return;
        }
        if(segments.isEmpty()){
            chunk.generated = true;
            chunk.hasCurve = false;
            return;
        }
        chunk.segments = segments;
        chunk.LOD = LOD;
        chunk.generated = true;

        data.chunks.put(new Point2D(cx, cy), chunk);
    }

    private void marchCell(double left, double bottom, double width, double height, double fA, double fB, double fC, double fD, double fM, ArrayList<Segment2D> segments){
        int a = (fA > 0) ? 1 : 0;
        int b = (fB > 0) ? 1 : 0;
        int c = (fC > 0) ? 1 : 0;
        int d = (fD > 0) ? 1 : 0;            
        int center = (fM > 0) ? 1 : 0;

        int code = (a << 3) | (b << 2) | (c << 1) | d;

        double tAB = Math.abs(fA)/(Math.abs(fA) + Math.abs(fB));
        double tCB = Math.abs(fC)/(Math.abs(fB) + Math.abs(fC));
        double tDC = Math.abs(fD)/(Math.abs(fC) + Math.abs(fD));
        double tDA = Math.abs(fD)/(Math.abs(fD) + Math.abs(fA));

        double stepX = width;
        double stepY = height;

        switch (code) {
            case 0: case 15: break;
            case 1:  case 14: segments.add(new Segment2D(new Point2D(left, tDA*stepY + bottom), new Point2D(tDC*stepX + left, bottom))); break; // 0001
            case 2:  case 13: segments.add(new Segment2D(new Point2D(tDC*stepX + left, bottom), new Point2D(left + width, tCB*stepY + bottom))); break; // 0010
            case 3:  case 12: segments.add(new Segment2D(new Point2D(left, tDA*stepY + bottom), new Point2D(left + width, tCB*stepY + bottom))); break;   // 0011
            case 4:  case 11: segments.add(new Segment2D(new Point2D(left + width, tCB*stepY + bottom), new Point2D(tAB*stepX + left, bottom + height))); break;    // 0100
            case 6:  case 9:  segments.add(new Segment2D(new Point2D(tAB*stepX + left, bottom + height), new Point2D(tDC*stepX + left, bottom))); break;   // 0110
            case 7:  case 8:  segments.add(new Segment2D(new Point2D(tAB*stepX + left, bottom + height), new Point2D(left, tDA*stepY + bottom))); break;     // 0111
            case 5: // 0101
            if (center > 0) {
                // Right -> Top, Bottom -> Left
                segments.add(new Segment2D(
                    new Point2D(left + width, tCB * stepY + bottom),
                    new Point2D(tAB * stepX + left, bottom + height)
                ));
                segments.add(new Segment2D(
                    new Point2D(tDC * stepX + left, bottom),
                    new Point2D(left, tDA * stepY + bottom)
                ));
            } else {
                // Right -> Bottom, Top -> Left
                segments.add(new Segment2D(
                    new Point2D(left + width, tCB * stepY + bottom),
                    new Point2D(tDC * stepX + left, bottom)
                ));
                segments.add(new Segment2D(
                    new Point2D(tAB * stepX + left, bottom + height),
                    new Point2D(left, tDA * stepY + bottom)
                ));
            }
            break;

        case 10: // 1010
            if (center > 0) {
                // Right -> Bottom, Top -> Left
                segments.add(new Segment2D(
                    new Point2D(left + width, tCB * stepY + bottom),
                    new Point2D(tDC * stepX + left, bottom)
                ));
                segments.add(new Segment2D(
                    new Point2D(tAB * stepX + left, bottom + height),
                    new Point2D(left, tDA * stepY + bottom)
                ));
            } else {
                // Right -> Top, Bottom -> Left
                segments.add(new Segment2D(
                    new Point2D(left + width, tCB * stepY + bottom),
                    new Point2D(tAB * stepX + left, bottom + height)
                ));
                segments.add(new Segment2D(
                    new Point2D(tDC * stepX + left, bottom),
                    new Point2D(left, tDA * stepY + bottom)
                ));
            }
            break;
            // Cases like 11, 12, 13, 14 are handled by their inverse counterparts
        }
    }

    private void subdivide(
        ImplicitChunk chunk,
        int ix0, int iy0,
        int ix1, int iy1,
        double left, double bottom,
        double width, double height,
        double fA, double fB, double fC, double fD,
        ArrayList<Segment2D> segments,
        DoubleBinaryOperator sample,
        double spacing,
        int depth){

        int mx = (ix0 + ix1) >> 1;
        int my = (iy0 + iy1) >> 1;

        double fM  = sampleCached(chunk, mx , my , sample, spacing);

        if (depth >= 12 || width < minimumCellSize || height < minimumCellSize) {
            marchCell(left, bottom, width, height, fA, fB, fC, fD, fM, segments);
            return;
        }

        double fAB = sampleCached(chunk, mx , iy1, sample, spacing);
        double fBC = sampleCached(chunk, ix1, my , sample, spacing);
        double fCD = sampleCached(chunk, mx , iy0, sample, spacing);
        double fDA = sampleCached(chunk, ix0, my , sample, spacing);

        double dfdx = (fBC - fDA) / width;
        double dfdy = (fAB - fCD) / height;

        double grad = Math.hypot(dfdx, dfdy);
        double radius = 0.5 * Math.hypot(width, height);

        if (Math.abs(fM) > grad * radius * 10) {
            if (depth >= 4) {
                return;
            }
        }

        int a = (fA > 0) ? 1 : 0;
        int b = (fB > 0) ? 1 : 0;
        int c = (fC > 0) ? 1 : 0;
        int d = (fD > 0) ? 1 : 0;            
        int center = (fM > 0) ? 1 : 0;
        int ab = (fAB > 0) ? 1 : 0;
        int bc = (fBC > 0) ? 1 : 0;
        int cd = (fCD > 0) ? 1 : 0;
        int da = (fDA > 0) ? 1 : 0;

        if(a==b && b==c && c==d && center == a && ab == bc && bc == cd && cd == da && da == center){
            if (depth >= 4) {
                return;
            }
        }

        // Bottom Left
        subdivide(
            chunk,
            ix0, iy0,
            mx,  my,
            left,
            bottom,
            width / 2,
            height / 2,
            fDA,
            fM,
            fCD,
            fD,
            segments,
            sample,
            spacing,
            depth + 1
        );

        // Bottom Right
        subdivide(
            chunk,
            mx,  iy0,
            ix1, my,
            left + width / 2,
            bottom,
            width / 2,
            height / 2,
            fM,
            fBC,
            fC,
            fCD,
            segments,
            sample,
            spacing,
            depth + 1
        );

        // Top Right
        subdivide(
            chunk,
            mx,  my,
            ix1, iy1,
            left + width / 2,
            bottom + height / 2,
            width / 2,
            height / 2,
            fAB,
            fB,
            fBC,
            fM,
            segments,
            sample,
            spacing,
            depth + 1
        );

        // Top Left
        subdivide(
            chunk,
            ix0, my,
            mx,  iy1,
            left,
            bottom + height / 2,
            width / 2,
            height / 2,
            fA,
            fAB,
            fM,
            fDA,
            segments,
            sample,
            spacing,
            depth + 1
        );
    }

    private double sampleCached(
        ImplicitChunk chunk,
        int ix,
        int iy,
        DoubleBinaryOperator sample,
        double spacing) {

        long key = (((long) ix) << 32) | (iy & 0xffffffffL);

        Double value = chunk.sampleCache.get(key);
        if (value != null) {
            return value;
        }

        double x = chunk.bounds.getMinX() + ix * spacing;
        double y = chunk.bounds.getMinY() + iy * spacing;

        value = sample.applyAsDouble(x, y);
        chunk.sampleCache.put(key, value);

        return value;
    }

    private static long key(int x, int y) {
        return (((long)x) << 32) | (y & 0xffffffffL);
    }
}
