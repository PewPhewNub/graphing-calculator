package io.github.pewphewnub.interaction;

import io.github.pewphewnub.computation.ComputationCoordinator;
import io.github.pewphewnub.parser.EvaluationContext;
import io.github.pewphewnub.plotting.GraphElement;
import io.github.pewphewnub.plotting.GraphElementListener;
import io.github.pewphewnub.plotting.GraphElementManager;
import io.github.pewphewnub.plotting.data.curve.CurveData;
import io.github.pewphewnub.plotting.plots.AbstractPlot;
import io.github.pewphewnub.rendering.camera.Viewport;
import javafx.geometry.Point2D;

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
    protected UndoManager undoManager;

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

    public void setUndoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
    }
}
