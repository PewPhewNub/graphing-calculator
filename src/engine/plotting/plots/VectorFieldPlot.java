package engine.plotting.plots;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import core.math.Core.ODESolution;
import core.math.Core.ODEStatus;
import core.math.Core.Point;
import core.math.ODESolvers.SystemSolvers;
import core.model.ODECurveChunk;
import core.model.Segment2D;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class VectorFieldPlot extends Plot {
    public BiFunction<Double, Double, Double> dx;
    public BiFunction<Double, Double, Double> dy;
    public double t0;
    ODEStatus statusLeft;
    ODEStatus statusRight;
    public Point initial;

    public ArrayList<Point> rightPoints = new ArrayList<>();
    public ArrayList<Point> leftPoints  = new ArrayList<>();
    public ArrayList<ODECurveChunk> rightBranch = new ArrayList<>();
    public ArrayList<ODECurveChunk> leftBranch  = new ArrayList<>();
    
    double rightExtent;
    double leftExtent;
    
    static final double CHUNK_WIDTH   = 16.0;
    static final double INIT_EXTENT   = 128.0;
    static final double EXTEND_BY     = 128.0; // how far to extend each time
    boolean autoGenerate = false;
    public final Set<String> knownVariables = Set.of("x", "y");
    public boolean showSlopeField = false;
    public VectorFieldPlot(String name, BiFunction<Double, Double, Double> dx, BiFunction<Double, Double, Double> dy, double t0, Point initial, Color color){
        long t = System.currentTimeMillis();
        System.out.println("ODE constructor start");
        this.dx = dx;
        this.dy = dy;
        this.t0 = t0;
        this.initial = initial;
        this.name = name;
        leftBranch = new ArrayList<ODECurveChunk>();
        rightBranch = new ArrayList<ODECurveChunk>();
        initializeCurve();
        System.out.println(leftPoints.size());
        System.out.println(rightPoints.size());
        this.color = color;
        System.out.println("ODE constructor end " + (System.currentTimeMillis() - t));
    }

    public void initializeCurve(){
        statusLeft = ODEStatus.SUCCESS;
        statusRight = ODEStatus.SUCCESS;
        rightPoints.add(new Point(initial.x, initial.y));
        leftPoints.add(new Point(initial.x, initial.y));
        rightExtent = initial.x;
        leftExtent  = initial.x;

        extendRight(initial.x + 256);
        extendLeft (initial.x - 256);
    }

    public ArrayList<Segment2D> sample() {
        ArrayList<Segment2D> segments = new ArrayList<>();

        // left branch
        for (int i = 1; i < leftPoints.size(); i++) {
            Point p1 = leftPoints.get(i - 1);
            Point p2 = leftPoints.get(i);
            if(p1 == null || p2 == null) continue;
            if(!Double.isFinite(p1.x) || !Double.isFinite(p1.y)) continue;
            if(!Double.isFinite(p2.x) || !Double.isFinite(p2.y)) continue;
            segments.add(new Segment2D(
                new Point2D(p1.x, p1.y),
                new Point2D(p2.x, p2.y)
            ));
        }

        // right branch
        for (int i = 1; i < rightPoints.size(); i++) {
            Point p1 = rightPoints.get(i - 1);
            Point p2 = rightPoints.get(i);

            if(p1 == null || p2 == null) continue;
            if(!Double.isFinite(p1.x) || !Double.isFinite(p1.y)) continue;
            if(!Double.isFinite(p2.x) || !Double.isFinite(p2.y)) continue;
            segments.add(new Segment2D(
                new Point2D(p1.x, p1.y),
                new Point2D(p2.x, p2.y)
            ));
        }

        return segments;
    }

    public void initialize() {
        rightPoints.add(new Point(initial.x, initial.y));
        leftPoints.add(new Point(initial.x, initial.y));
        rightExtent = initial.x;
        leftExtent  = initial.x;
        extendRight(initial.x + INIT_EXTENT);
        extendLeft (initial.x - INIT_EXTENT);
    }

    // called from computeCurveData before rendering
    public void ensureCovers(double xMin, double xMax) {
        if (xMax > rightExtent) extendRight(xMax + EXTEND_BY);
        if (xMin < leftExtent)  extendLeft (xMin - EXTEND_BY);
    }

    private void extendRight(double targetT) {
        Point last = rightPoints.get(rightPoints.size() - 1);
        int prevEnd = rightPoints.size() - 1;

        ODESolution sol = SystemSolvers.RK4(
            dx, dy, t0, new Point(last.x, last.y), 0.01, targetT);
        if (sol.list() == null) return;
        List<Point> pts = sol.list();
        for (int i = 1; i < pts.size(); i++) rightPoints.add(pts.get(i)); // skip [0], already stored

        //buildChunks(rightPoints, rightBranch, prevEnd);
        rightExtent = targetT;
    }

    private void extendLeft(double targetT) {
        Point last = leftPoints.get(leftPoints.size() - 1);
        int prevEnd = leftPoints.size() - 1;

        ODESolution sol = SystemSolvers.RK4(
            dx, dy, t0, new Point(last.x, last.y), -0.01, targetT);
        if (sol.list() == null) return;
        List<Point> pts = sol.list();
        for (int i = 1; i < pts.size(); i++) leftPoints.add(pts.get(i));

        //buildChunks(leftPoints, leftBranch, prevEnd);
        leftExtent = targetT;
    }

    private void buildChunks(ArrayList<Point> points, ArrayList<ODECurveChunk> chunks, int fromIndex) {
        int i = fromIndex;
        while (i < points.size() - 1) {
            double chunkStartX = points.get(i).x;
            int j = i + 1;
            // group points until we've covered CHUNK_WIDTH in x
            while (j < points.size() &&
                   Math.abs(points.get(j).x - chunkStartX) < CHUNK_WIDTH) j++;
            
            int end = Math.min(j, points.size() - 1);

            double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
            double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
            for (int k = i; k <= end; k++) {
                Point p = points.get(k);
                if (!Double.isFinite(p.x) || !Double.isFinite(p.y)) continue;
                minX = Math.min(minX, p.x); maxX = Math.max(maxX, p.x);
                minY = Math.min(minY, p.y); maxY = Math.max(maxY, p.y);
            }
            if (minX < Double.MAX_VALUE)
                chunks.add(new ODECurveChunk(i, end,
                    new BoundingBox(minX, minY, maxX - minX, maxY - minY)));
            if(end == i) end++;
            i = end;
        }
    }
    public void setAutoGenerate(boolean autoGenerate) {
        this.autoGenerate = autoGenerate;
    }

    public void setShowSlopeField(boolean showSlopeField) {
        this.showSlopeField = showSlopeField;
    }
}
