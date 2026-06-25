package plotting;

import java.util.ArrayList;

import plotting.data.curve.CurveData;
import plotting.data.curve.Intersection;
import plotting.plots.Plot;
import rendering.camera.Viewport;

public class PlotManager{
    public ArrayList<Plot> plots;
    public ArrayList<CurveData> curveCache;
    public ArrayList<Intersection> intersectionCache;
    public PlotInteractionController interactionController;
    public ArrayList<PlotListener> listeners;
    boolean dirty;

    public PlotManager(PlotInteractionController plotInteractionController){
        plots = new ArrayList<>();
        curveCache = new ArrayList<>();
        interactionController = plotInteractionController;
        intersectionCache = new ArrayList<>();
        plotInteractionController.setCaches(curveCache, intersectionCache);
        this.listeners = new ArrayList<>();
        dirty = false;
    }
    public void addPlot(Plot plot){
        plots.add(plot);
        for(PlotListener listener : listeners){
            listener.plotsChanged();
            listener.plotAdded(plot);
            System.out.println("yes");
        }
        dirty = true;
    }
    public void removePlot(Plot plot){
        System.out.println("removePlot called");
        plots.remove(plot);
        for(PlotListener listener : listeners){
            listener.plotsChanged();
            listener.plotRemoved(plot);
        }
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
    public void addPlot(int index, Plot plot){
        plots.add(index, plot);
        for(PlotListener listener : listeners){
            listener.plotsChanged();
            listener.plotAdded(plot);
            System.out.println("yes");
        }
    }
    public void addListener(PlotListener listener){
        listeners.add(listener);
    }
    public void removeListener(PlotListener listener){
        listeners.remove(listener);
    }
    public void plotChanged(Plot plot){
        for(PlotListener listener : listeners){
            listener.plotChanged(plot);
        }
    }
}