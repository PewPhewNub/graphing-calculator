package plotting;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import plotting.data.curve.CurveData;
import plotting.data.curve.Intersection;
import plotting.plots.AbstractPlot;
import rendering.camera.Viewport;

public abstract class PlotInteractionController {

    protected AbstractPlot hoveredPlot;
    protected AbstractPlot selectedPlot;

    protected CurveData hoveredCurve;
    protected CurveData selectedCurve;

    protected Point2D hoveredPoint;
    protected Point2D selectedPoint;
    protected Point2D snappedPoint;

    protected boolean snappingEnabled = true;
    protected double INTERACTION_DISTANCE = 10;

    protected ArrayList<CurveData> curveData;
    protected ArrayList<Intersection> intersections;

    public void setCaches(ArrayList<CurveData> curveData, ArrayList<Intersection> intersections){
        this.curveData = curveData;
        this.intersections = intersections;
    }

    public abstract void update(
        Viewport viewport,
        double mouseX,
        double mouseY
    );

    public CurveData getHoveredCurve() {
        return hoveredCurve;
    }
    public AbstractPlot getHoveredPlot() {
        return hoveredPlot;
    }
    public Point2D getHoveredPoint() {
        return hoveredPoint;
    }
    public CurveData getSelectedCurve() {
        return selectedCurve;
    }
    public AbstractPlot getSelectedPlot() {
        return selectedPlot;
    }
    public Point2D getSelectedPoint() {
        return selectedPoint;
    }
    public Point2D getSnappedPoint() {
        return snappedPoint;
    }
}
