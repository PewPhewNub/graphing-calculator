package engine.plotting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Function;

import engine.rendering.Viewport;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class ParametricPlot implements Plot{
    public Function<Double, Double> x;
    public Function<Double, Double> y;
    public String name;
    public Color color;
    public double tMin;
    public double tMax;
    public double maxSamples;
    public ArrayList<Point2D> initialList;
    public ArrayList<Point2D> accurateComputedPoints;

    public ParametricPlot(String name, Function<Double, Double> x, Function<Double, Double> y, double tMin, double tMax, double maxSamples, Color color){
        this.x = x;
        this.y = y;
        this.name = name;
        this.color = color;
        this.tMin = tMin;
        this.tMax = tMax;
        this.maxSamples = maxSamples;

        initialList = sample(Double.POSITIVE_INFINITY);
        accurateComputedPoints = new ArrayList<>();
    }

    public ArrayList<Point2D> sample(double viewportWidth) {
        ArrayList<Point2D> list = new ArrayList<>();
        int samples = (int)Math.min(maxSamples, 2 * viewportWidth);

        double stepSize = (tMax - tMin) / samples;

        for (double t = tMin; t <= tMax; t += stepSize) {
            double xVal = x.apply(t);
            double yVal = y.apply(t);

            if (!Double.isFinite(xVal) || !Double.isFinite(yVal)) continue;

            list.add(new Point2D(xVal, yVal));
        }

        return list;
    }

    public String getName() {
        return name;
    }
    public Color getColor() {
        return color;
    }
    public Point2D evaluate(double t){
        return new Point2D(x.apply(t), y.apply(t));
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

    @Override
    public void setColor(Color color) {
        this.color = color;
    }
}
