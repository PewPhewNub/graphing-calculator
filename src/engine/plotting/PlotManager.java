package engine.plotting;

import java.util.ArrayList;

import core.model.curve.CurveData;
import core.model.curve.Intersection;
import engine.plotting.plots.Plot;
import engine.rendering.camera.Viewport;

public class PlotManager{
    public ArrayList<Plot> plots;
    public ArrayList<CurveData> curveCache;
    public ArrayList<Intersection> intersectionCache;
    public PlotInteractionController interactionController;
    boolean dirty;

    public PlotManager(PlotInteractionController plotInteractionController){
        plots = new ArrayList<>();
        curveCache = new ArrayList<>();
        interactionController = plotInteractionController;
        intersectionCache = new ArrayList<>();
        plotInteractionController.setCaches(curveCache, intersectionCache);
        dirty = false;
    }
    public void addPlot(Plot plot){
        plots.add(plot);
        dirty = true;
    }
    public void removePlot(Plot plot){
        plots.remove(plot);
        dirty = true;
    }
    public void computeCurveData(Viewport viewport){
        curveCache.clear();
        for(Plot plot : plots){
            curveCache.add(
                    PlotComputationEngine.computeCurveData(plot, viewport)
                );
        }
        
        intersectionCache.clear();
        for(int i = 0; i < plots.size(); i++){
            Plot plot1 = plots.get(i);
            for(int j = i + 1; j < plots.size(); j++){
                intersectionCache.addAll(PlotComputationEngine.computeIntersections(plot1, plots.get(j), viewport));
            }
        }
    }
    public boolean isDirty() {
        return dirty;
    }
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void removeAll(){
        plots.clear();
        dirty = true;
    }
}