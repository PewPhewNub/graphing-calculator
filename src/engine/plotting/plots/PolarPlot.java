package engine.plotting.plots;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Function;

import core.model.ViewportState;
import engine.rendering.Viewport;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class PolarPlot implements Plot {
    public Function<Double, Double> r;
    public String name;
    public Color color;
    public double tMin;
    public double tMax;
    public double maxSamples;
    public ArrayList<Point2D> initialList;
    public ArrayList<Point2D> accurateComputedPoints;
    public final Set<String> knownVariables = Set.of("\u03B8");

    public ArrayList<Point2D> currentList;
    public PolarPlot(String name, Function<Double, Double> r, double tMin, double tMax, double maxSamples, Color color){
        this.r = r;
        this.name = name;
        this.color = color;
        this.tMin = tMin;
        this.tMax = tMax;
        this.maxSamples = maxSamples;
        currentList = new ArrayList<>();
        initialList = sample(maxSamples);
        accurateComputedPoints = new ArrayList<>();
    }

    public Point2D samplePoint(double t){
        double rVal = r.apply(t);
        double xVal = rVal * Math.cos(t);
        double yVal = rVal * Math.sin(t);
        return new Point2D(xVal, yVal);
    }

    public ArrayList<Point2D> sample(double viewportWidth) {
        ArrayList<Point2D> list = new ArrayList<>();
        int samples = 4 * (int)viewportWidth;

        double stepSize = (tMax - tMin) / samples;

        for (double t = tMin; t <= tMax; t += stepSize) {
            double rVal = r.apply(t);
            double xVal = rVal * Math.cos(t);
            double yVal = rVal * Math.sin(t);

            if (!Double.isFinite(xVal) || !Double.isFinite(yVal)) continue;

            list.add(new Point2D(xVal, yVal));
        }
        initialList = list;
        return list;
    }

    public String getName() {
        return name;
    }
    public Color getColor() {
        return color;
    }
    public Point2D evaluate(double t){
        double rVal = r.apply(t);
        return new Point2D(rVal * Math.cos(t), rVal * Math.sin(t));
    } 

    @Override
    public Point2D nearestPoint(double worldX, double worldY, Viewport viewport) {
        if (currentList == null || currentList.isEmpty()) return null;

        Point2D nearest = null;
        double bestDist = Double.POSITIVE_INFINITY;

        for (Point2D p : currentList) {
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
        if (currentList == null || currentList.isEmpty()) return Double.POSITIVE_INFINITY;
        double distance = Double.POSITIVE_INFINITY;
        double mouseX = viewport.worldToScreenX(worldX);
        double mouseY = viewport.worldToScreenY(worldY);
        for(Point2D i : currentList){
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
        return currentList.contains(point) || accurateComputedPoints.contains(point);
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    public void adaptiveSample(
        double t0,
        double t1,
        double tolerance,
        Viewport viewport,
        int depth) {

        if (depth > 20) {
            currentList.add(new Point2D(
                r.apply(t0) * Math.cos(t0),
                r.apply(t0) * Math.sin(t0)
            ));
            return;
        }

        ViewportState state = new ViewportState(viewport);

        double r0 = r.apply(t0);
        double x0 = r0 * Math.cos(t0);
        double y0 = r0 * Math.sin(t0);

        double r1 = r.apply(t1);
        double x1 = r1 * Math.cos(t1);
        double y1 = r1 * Math.sin(t1);

        if (Math.abs(t1 - t0) < 1e-8) {
            currentList.add(new Point2D(x0, y0));
            return;
        }

        double vx = x1 - x0;
        double vy = y1 - y0;
        double len2 = vx * vx + vy * vy;

        double maxError2 = 0;
        double maxX = Double.NEGATIVE_INFINITY; double maxY = Double.NEGATIVE_INFINITY;
        double minX = Double.POSITIVE_INFINITY; double minY = Double.POSITIVE_INFINITY;

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

            double rt = r.apply(t);
            double xt = rt * Math.cos(t);
            double yt = rt * Math.sin(t);
            maxX = Math.max(maxX, xt);
            maxY = Math.max(maxY, yt);
            minX = Math.min(minX, xt);
            minY = Math.min(minY, yt);
            double error2;

            if (len2 < 1e-20) {

                double dx = viewport.worldToScreenX(xt)
                        - viewport.worldToScreenX(x0);

                double dy = viewport.worldToScreenY(yt)
                        - viewport.worldToScreenY(y0);

                error2 = dx * dx + dy * dy;

            } else {

                double u =
                    ((xt - x0) * vx + (yt - y0) * vy) / len2;

                u = Math.max(0.0, Math.min(1.0, u));

                double closestX = x0 + u * vx;
                double closestY = y0 + u * vy;

                double dx =
                    viewport.worldToScreenX(xt)
                - viewport.worldToScreenX(closestX);

                double dy =
                    viewport.worldToScreenY(yt)
                - viewport.worldToScreenY(closestY);

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

        if (maxError2 > tolerance * tolerance) {

            double tm = (t0 + t1) * 0.5;

            adaptiveSample(
                t0,
                tm,
                tolerance,
                viewport,
                depth + 1
            );

            adaptiveSample(
                tm,
                t1,
                tolerance,
                viewport,
                depth + 1
            );

        } else {
            currentList.add(new Point2D(x0, y0));
        }
    }

    public void recomputePoints(Viewport viewport, double tolerance){
        currentList.clear();
        int segments = Math.max(
            128,
            (int)(new ViewportState(viewport).worldWidth / 20)
        );

        double step = (tMax - tMin) / segments;

        for(double t = tMin; t < tMax; t += step){
            adaptiveSample(
                t,
                Math.min(t + step, tMax),
                tolerance,
                viewport,
                0
            );
        }
        double r1 = r.apply(tMax);
        currentList.add(
            new Point2D(
                r1 * Math.cos(tMax),
                r1 * Math.sin(tMax)
            )
        );
    }
}
