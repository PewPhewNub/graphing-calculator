package interaction;

import javafx.geometry.Point2D;
import plotting.data.curve.CurveData;
import plotting.plots.AbstractPlot;

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
