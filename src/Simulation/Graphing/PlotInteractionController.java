package Simulation.Graphing;

import java.util.ArrayList;

import Simulation.Plot.ODEPlot;
import Simulation.Plot.Plot;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class PlotInteractionController {
    Plot selectedPlot;
    Point2D inspectionPoint;
    Color currentColor;

    public PlotInteractionController(){
        reset();
    }

    public Plot nearestPlot(ArrayList<Plot> plots, Viewport viewport, double worldX, double worldY){
        if(plots == null) return null;
        if(plots.isEmpty()) return null;
        double distance = Double.POSITIVE_INFINITY;
        Plot nearest = null;
        for(int i = 0; i < plots.size(); i++){
            Plot currentPlot = plots.get(i);
            double distance2 = currentPlot.distanceSquaredFrom(worldX, worldY, viewport);
            if(distance > distance2){
                distance = distance2;
                nearest = currentPlot;
            }
        }
        return nearest;
    }

    public void update(
        ArrayList<Plot> plots,
        ArrayList<Point2D> featurePoints,
        Viewport viewport,
        double mouseX,
        double mouseY,
        boolean mousePressed
    ){
        if(!mousePressed){
            reset();
            return;
        }
        double worldX =  viewport.screenToWorldX(mouseX);
        double worldY = viewport.screenToWorldY(mouseY);    
        if(selectedPlot == null) selectedPlot = nearestPlot(plots, viewport, worldX, worldY);
        if(selectedPlot == null) return;
        inspectionPoint = selectedPlot.nearestPoint(worldX, worldY, viewport);
        
        for(Point2D i : featurePoints){
            if(selectedPlot instanceof ODEPlot) continue;
            if(!selectedPlot.contains(i)) continue;
            double dx = viewport.worldToScreenX(i.getX())
                    - viewport.worldToScreenX(inspectionPoint.getX());

            double dy = viewport.worldToScreenY(i.getY())
                    - viewport.worldToScreenY(inspectionPoint.getY());
            if(dx*dx + dy*dy < 100){
                inspectionPoint = i;
                break;
            }
        }
        currentColor = selectedPlot.getColor();
    }
    public boolean canInteract(
        ArrayList<Plot> plots,
        Viewport viewport,
        double mouseX,
        double mouseY,
        boolean mousePressed
    ){
        if(!mousePressed){
            reset();
            return false;
        }
        double worldX =  viewport.screenToWorldX(mouseX);
        double worldY = viewport.screenToWorldY(mouseY);    
        Plot nearest = nearestPlot(plots, viewport, worldX, worldY);
        if(nearest == null) return false;
        double distanceSq = nearest.distanceSquaredFrom(worldX, worldY, viewport);
        return distanceSq < 100;
    }

    public void reset(){
        selectedPlot = null;
        currentColor = null;
        inspectionPoint = null;
    }
}
