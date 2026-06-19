package engine.plotting.plots;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import core.math.Core.ODESolution;
import core.math.Core.ODEStatus;
import core.math.Core.Point;
import core.math.ODESolvers.RungeKuttaMethod;
import core.model.ODECurveChunk;
import engine.rendering.Viewport;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class ODEPlot extends Plot {
    public BiFunction<Double, Double, Double> equation;
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
    public ODEPlot(String name, BiFunction<Double, Double, Double> equation, Point initial, Color color){
        long t = System.currentTimeMillis();
        System.out.println("ODE constructor start");
        this.equation = equation;
        this.initial = initial;
        this.name = name;
        leftBranch = new ArrayList<ODECurveChunk>();
        rightBranch = new ArrayList<ODECurveChunk>();

        this.color = color;
        initialize();
        System.out.println("ODE constructor end " + (System.currentTimeMillis() - t));
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

    private void extendRight(double targetX) {
        Point last = rightPoints.get(rightPoints.size() - 1);
        int prevEnd = rightPoints.size() - 1;

        ODESolution sol = RungeKuttaMethod.adaptiveRK4(
            equation, new Point(last.x, last.y), 0.05, targetX, 1e-3);
        if (sol.status() != ODEStatus.SUCCESS) return;
        List<Point> pts = sol.list();
        for (int i = 1; i < pts.size(); i++) rightPoints.add(pts.get(i)); // skip [0], already stored

        buildChunks(rightPoints, rightBranch, prevEnd);
        rightExtent = targetX;
    }

    private void extendLeft(double targetX) {
        Point last = leftPoints.get(leftPoints.size() - 1);
        int prevEnd = leftPoints.size() - 1;

        ODESolution sol = RungeKuttaMethod.adaptiveRK4(
            equation, new Point(last.x, last.y), -0.05, targetX, 1e-3);
        if (sol.status() != ODEStatus.SUCCESS) return;
        List<Point> pts = sol.list();
        for (int i = 1; i < pts.size(); i++) leftPoints.add(pts.get(i));

        buildChunks(leftPoints, leftBranch, prevEnd);
        leftExtent = targetX;
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
      
    public Point2D nearestPoint(double worldX, double worldY, Viewport viewport) {
        return null;
    }

    public double distanceSquaredFrom(double worldX, double worldY, Viewport viewport) {
        return Double.POSITIVE_INFINITY;
    }

    public boolean contains(Point2D point){
        return false;
    }

    public void setAutoGenerate(boolean autoGenerate) {
        this.autoGenerate = autoGenerate;
    }
}
