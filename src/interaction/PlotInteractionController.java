package interaction;

import computation.ComputationCoordinator;
import javafx.geometry.Point2D;
import parser.EvaluationContext;
import plotting.GraphElement;
import plotting.GraphElementListener;
import plotting.GraphElementManager;
import plotting.data.curve.CurveData;
import plotting.plots.AbstractPlot;
import rendering.camera.Viewport;

public abstract class PlotInteractionController implements GraphElementListener{

    protected AbstractPlot hoveredPlot;

    protected CurveData hoveredCurve;
    protected CurveData selectedCurve;

    protected Point2D hoveredPoint;
    protected Point2D selectedPoint;
    protected Point2D snappedPoint;

    protected boolean snappingEnabled = true;
    protected double INTERACTION_DISTANCE = 10;

    protected GraphElementManager graphElementManager;
    protected ComputationCoordinator coordinator;

    public PlotInteractionController(GraphElementManager plotManager, ComputationCoordinator coordinator){
        this.graphElementManager = plotManager;
        this.coordinator = coordinator;
    }

    public abstract void update(
        double mouseX,
        double mouseY,
        Viewport viewport,
        EvaluationContext context
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
        GraphElement element = graphElementManager.getSelectedElement();
        if(element instanceof AbstractPlot p) return p;
        return null;
    }
    public Point2D getSelectedPoint() {
        return selectedPoint;
    }
    public Point2D getSnappedPoint() {
        return snappedPoint;
    }
    public void setSelectedPlot(AbstractPlot plot){
        graphElementManager.setSelectedElement(plot);
    }

    public abstract void selectHovered(Viewport viewport);
    public void clearSelection(){
        selectedCurve = null;
        selectedPoint = null;
        graphElementManager.setSelectedElement(null);
    }
}
