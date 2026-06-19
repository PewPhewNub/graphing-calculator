package engine.plotting.plots;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Function;

import core.math.Core.Interval;
import core.model.ParametricCurveChunk;
import engine.rendering.Viewport;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class ParametricPlot extends Plot{
    public Function<Double, Double> x;
    public Function<Double, Double> y;
    public double tMin;
    public double tMax;
    public double maxSamples;
    public ArrayList<ParametricCurveChunk> chunks;
    public ArrayList<Point2D> accurateComputedPoints;
    public final Set<String> knownVariables = Set.of("t");
    ArrayList<Point2D> initialList;
    public ParametricPlot(String name, Function<Double, Double> x, Function<Double, Double> y, double tMin, double tMax, double maxSamples, Color color){
        this.x = x;
        this.y = y;
        this.name = name;
        this.color = color;
        this.tMin = tMin;
        this.tMax = tMax;
        this.maxSamples = maxSamples;

        chunks = new ArrayList<>();    
        initializeChunks();
        accurateComputedPoints = new ArrayList<>();
    }

    public void initializeChunks(){
        double chunkSize = 0.5; // radians

        for(double t = tMin; t < tMax; t += chunkSize){
            chunks.add(
                new ParametricCurveChunk(
                    new Interval(t, Math.min(t + chunkSize, tMax)),
                    sample(t),
                    sample(Math.min(t + chunkSize, tMax)),
                    computeBounds(t, Math.min(t + chunkSize, tMax))
                )
            );
        }
    }

    public BoundingBox computeBounds(double t0, double t1){
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        int samples = 128;

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
    public Point2D nearestPoint(double worldX, double worldY, Viewport viewport) {
        if (initialList == null || initialList.isEmpty()) return null;

        Point2D nearest = null;
        double bestDist = Double.POSITIVE_INFINITY;

        for (Point2D p : initialList) {
            double dx = p.getX() - worldX;
            double dy = p.getY() - worldY;

            double dist = dx * dx + dy * dy;

            if (dist < bestDist) {
                bestDist = dist;
                nearest = p;
            }
        }

        return nearest;
    }

    @Override
    public double distanceSquaredFrom(double worldX, double worldY, Viewport viewport) {
        if (initialList == null || initialList.isEmpty()) return Double.POSITIVE_INFINITY;
        double distance = Double.POSITIVE_INFINITY;
        double mouseX = viewport.worldToScreenX(worldX);
        double mouseY = viewport.worldToScreenY(worldY);
        for(Point2D i : initialList){
            double dx = mouseX - viewport.worldToScreenX(i.getX());
            double dy = mouseY - viewport.worldToScreenY(i.getY());
            if(distance > dx*dx + dy*dy){
                distance = dx*dx + dy*dy;
            }
        }
        return distance;
    }

    @Override
    public boolean contains(Point2D point) {
        return initialList.contains(point) || accurateComputedPoints.contains(point);
    }

    public Point2D sample(double t){
        return new Point2D(x.apply(t), y.apply(t));
    }
}
