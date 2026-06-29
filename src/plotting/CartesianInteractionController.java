package plotting;

import java.util.ArrayList;

import interaction.InteractionResult;
import javafx.geometry.Point2D;
import plotting.data.curve.CurveData;
import plotting.data.curve.FunctionCurveData;
import plotting.data.curve.ImplicitCurveData;
import plotting.data.curve.Intersection;
import plotting.data.curve.ParametricCurveData;
import plotting.data.curve.PolarCurveData;
import plotting.plots.AbstractPlot;
import rendering.camera.Viewport;

public class CartesianInteractionController extends PlotInteractionController{

    @Override
    public void update(Viewport viewport, double mouseX, double mouseY) {
        if(Double.isNaN(mouseY) || Double.isNaN(mouseX)) return;
        updateHover(curveData, mouseX, mouseY, viewport);

        updateSelection(mouseX, mouseY, intersections, viewport);
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

    public void selectHovered(ArrayList<Intersection> intersections, Viewport viewport){
        plotManager.setSelectedPlot(hoveredPlot);
        
        if(hoveredPlot == null) return;

        selectedPlot = hoveredPlot;
        selectedCurve = hoveredCurve;

        selectedPoint = applySnapping(hoveredPoint, hoveredCurve, intersections, viewport);
    }

    public void updateSelection(double mouseX, double mouseY, ArrayList<Intersection> intersections, Viewport viewport){
        if(selectedPlot == null){
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
                f.targettedPoint(mouseX, mouseY),
                selectedCurve,
                intersections,
                viewport
            );
        }
        if(selectedCurve instanceof ParametricCurveData f){
            selectedPoint = applySnapping(
                f.targettedPoint(mouseX, mouseY),
                selectedCurve,
                intersections,
                viewport
            );
        }if(selectedCurve instanceof PolarCurveData f){
            selectedPoint = applySnapping(
                f.targettedPoint(mouseX, mouseY),
                selectedCurve,
                intersections,
                viewport
            );
        }if(selectedCurve instanceof ImplicitCurveData f){
            selectedPoint = applySnapping(
                f.targettedPoint(mouseX, mouseY),
                selectedCurve,
                intersections,
                viewport
            );
        }
    }

    private CurveData currentSelectedCurve(){
        if(selectedPlot == null) return null;

        for(CurveData curve : curveData){
            if(curve.plot() == selectedPlot){
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

    public void clearSelection(){
        selectedPlot = null;
        plotManager.setSelectedPlot(null);
        selectedCurve = null;
        selectedPoint = null;
    }

    
    public void clearHover(){
        hoveredPlot = null;
        hoveredCurve = null;
        hoveredPoint = null;
    }

    @Override
    public void plotAdded(AbstractPlot plot) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'plotAdded'");
    }

    @Override
    public void plotRemoved(AbstractPlot plot) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'plotRemoved'");
    }

    @Override
    public void plotsChanged() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'plotsChanged'");
    }

    @Override
    public void plotChanged(AbstractPlot plot) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'plotChanged'");
    }

    @Override
    public void selectedPlotChanged(AbstractPlot plot) {
        selectedPlot = plot;
    }

    @Override
    public void plotReordered(AbstractPlot plot1, AbstractPlot plot2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'plotReordered'");
    }

    @Override
    public void plotReordered(int index1, int index2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'plotReordered'");
    }

    @Override
    public void plotMovedTo(AbstractPlot plot, int index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'plotMovedTo'");
    }
    
}
