package engine.plotting;

import java.util.ArrayList;

import core.model.curve.CurveData;
import core.model.curve.FunctionCurveData;
import core.model.curve.ImplicitCurveData;
import core.model.curve.Intersection;
import core.model.curve.ParametricCurveData;
import core.model.curve.PolarCurveData;
import engine.interaction.InteractionResult;
import engine.rendering.camera.Viewport;
import javafx.geometry.Point2D;

public class CartesianInteractionController extends PlotInteractionController{

    @Override
    public void update(Viewport viewport, double mouseX, double mouseY) {
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
        selectedCurve = null;
        selectedPoint = null;
    }

    
    public void clearHover(){
        hoveredPlot = null;
        hoveredCurve = null;
        hoveredPoint = null;
    }
}
