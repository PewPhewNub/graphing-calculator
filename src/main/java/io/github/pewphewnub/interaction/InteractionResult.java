package io.github.pewphewnub.interaction;

import io.github.pewphewnub.plotting.data.curve.CurveData;
import io.github.pewphewnub.plotting.plots.AbstractPlot;
import javafx.geometry.Point2D;

public class InteractionResult {
    AbstractPlot plot;
    Point2D nearestPoint;
    double distanceSquared;
    CurveData curveData;
    public InteractionResult(AbstractPlot plot, CurveData curveData, Point2D nearestPoint, double distanceSquared) {
        this.plot = plot;
        this.curveData = curveData;
        this.nearestPoint = nearestPoint;
        this.distanceSquared = distanceSquared;
    }

    public double getDistanceSquared() {
        return distanceSquared;
    }
    public Point2D getNearestPoint() {
        return nearestPoint;
    }
    public AbstractPlot getPlot() {
        return plot;
    }
    public CurveData getCurveData() {
        return curveData;
    }
}
