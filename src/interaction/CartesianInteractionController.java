package interaction;

import java.util.ArrayList;

import computation.ComputationCoordinator;
import javafx.geometry.Point2D;
import parser.EvaluationContext;
import plotting.GraphElement;
import plotting.GraphElementManager;
import plotting.data.curve.CurveData;
import plotting.data.curve.FunctionCurveData;
import plotting.data.curve.ImplicitCurveData;
import plotting.data.curve.Intersection;
import plotting.data.curve.ParametricCurveData;
import plotting.data.curve.PolarCurveData;
import rendering.camera.Viewport;

public class CartesianInteractionController extends PlotInteractionController{
    
    public CartesianInteractionController(GraphElementManager plotManager, ComputationCoordinator coordinator) {
        super(plotManager, coordinator);
    }

    @Override
    public void update(double mouseX, double mouseY, Viewport viewport, EvaluationContext context) {
        if(Double.isNaN(mouseY) || Double.isNaN(mouseX)) return;
        updateHover(coordinator.getCurveData(), mouseX, mouseY, viewport);
        updateSelection(mouseX, mouseY, context,  coordinator.getIntersections(), viewport);
    }

    public InteractionResult findClosestCurve(ArrayList<CurveData> curves, double mouseX, double mouseY){
        InteractionResult result = null;

        for(CurveData curve : curves){
            if(curve == null) continue;
            InteractionResult currentResult = curve.hitTest(mouseX, mouseY);

            if(currentResult.getNearestPoint() == null) continue;

            if(result == null || currentResult.getDistanceSquared() < result.getDistanceSquared()){
                result = currentResult;
            }
        }

        return result;
    }

    public void updateHover(ArrayList<CurveData> curves, double mouseX, double mouseY, Viewport viewport){
        InteractionResult hover = findClosestCurve(curves, mouseX, mouseY);
        if(hover == null){
            hoveredPlot = null;
            hoveredPoint = null;
            hoveredCurve = null;
            return;
        }

        double dx = viewport.worldToScreenX(mouseX) - viewport.worldToScreenX(hover.getNearestPoint().getX());
        double dy = viewport.worldToScreenY(mouseY) - viewport.worldToScreenY(hover.getNearestPoint().getY());

        if(dx*dx + dy*dy > INTERACTION_DISTANCE*INTERACTION_DISTANCE){
            hoveredPlot = null;
            hoveredPoint = null;
            hoveredCurve = null;
            return;
        }

        hoveredPlot = hover.getPlot();
        hoveredPoint = hover.getNearestPoint();
        hoveredCurve = hover.getCurveData();
    }

    public void selectHovered(Viewport viewport){
        if(hoveredPlot == null) return;

        super.setSelectedPlot(hoveredPlot);
        selectedCurve = hoveredCurve;

        selectedPoint = applySnapping(hoveredPoint, hoveredCurve, coordinator.getIntersections(), viewport);
    }

    public void updateSelection(double mouseX, double mouseY, EvaluationContext context, ArrayList<Intersection> intersections, Viewport viewport){
        if(super.getSelectedPlot() == null){
            return;
        }

        CurveData curve = currentSelectedCurve();

        if(curve == null){
            clearSelection();
            return;
        }

        selectedCurve = curve;
        if(selectedCurve instanceof FunctionCurveData f){
            selectedPoint = applySnapping(
                f.targettedPoint(mouseX, mouseY, context),
                selectedCurve,
                intersections,
                viewport
            );
        }
        if(selectedCurve instanceof ParametricCurveData f){
            selectedPoint = applySnapping(
                f.targettedPoint(mouseX, mouseY, context),
                selectedCurve,
                intersections,
                viewport
            );
        }if(selectedCurve instanceof PolarCurveData f){
            selectedPoint = applySnapping(
                f.targettedPoint(mouseX, mouseY, context),
                selectedCurve,
                intersections,
                viewport
            );
        }if(selectedCurve instanceof ImplicitCurveData f){
            selectedPoint = applySnapping(
                f.targettedPoint(mouseX, mouseY, context),
                selectedCurve,
                intersections,
                viewport
            );
        }
    }

    private CurveData currentSelectedCurve(){
        if(super.getSelectedPlot() == null) return null;

        for(CurveData curve : coordinator.getCurveData()){
            if(curve.plot() == super.getSelectedPlot()){
                return curve;
            }
        }

        return null;
    }

    public Point2D applySnapping(Point2D candidate, CurveData curve, ArrayList<Intersection> intersections, Viewport viewport){
        if(!snappingEnabled) return candidate;
        double screenX = viewport.worldToScreenX(candidate.getX());
        double screenY = viewport.worldToScreenY(candidate.getY());

        double dist2 = Double.POSITIVE_INFINITY;
        Point2D currentPoint = null;
        for(Point2D point : getSnapPoints(curve, intersections)){
            double dx = screenX - viewport.worldToScreenX(point.getX());
            double dy = screenY - viewport.worldToScreenY(point.getY());

            if(dx*dx + dy*dy < dist2){
                dist2 = dx*dx + dy*dy;
                currentPoint = point;
            }
        }

        if(dist2 < INTERACTION_DISTANCE*INTERACTION_DISTANCE){
            return currentPoint;
        }else return candidate;
    }

    public ArrayList<Point2D> getSnapPoints(CurveData curve, ArrayList<Intersection> intersections){
        ArrayList<Point2D> points = new ArrayList<>();
        points.addAll(curve.featurePoints());

        for(Intersection intersection : intersections){
            if(intersection.isOn(curve.plot())){
                points.add(intersection.getPoint());
            }
        }

        return points;
    }
    
    public void clearHover(){
        hoveredPlot = null;
        hoveredCurve = null;
        hoveredPoint = null;
    }

    @Override
    public void elementsChanged() {
        return;
    }

    @Override
    public void elementAdded(GraphElement element) {
        // TODO Auto-generated method stub
        return;
    }

    @Override
    public void elementRemoved(GraphElement element) {
        // TODO Auto-generated method stub
        return;
    }

    @Override
    public void elementChanged(GraphElement element) {
        // TODO Auto-generated method stub
        return;
    }

    @Override
    public void selectedElementChanged(GraphElement element) {
        
    }

    @Override
    public void elementsSwapped(GraphElement element1, GraphElement element2) {
        // TODO Auto-generated method stub
        return;
    }

    @Override
    public void elementsSwapped(int index1, int index2) {
        // TODO Auto-generated method stub
        return;
    }

    @Override
    public void elementMovedTo(GraphElement element, int index) {
        // TODO Auto-generated method stub
        return;
    }
}
