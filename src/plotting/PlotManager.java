package plotting;

import java.util.ArrayList;

import plotting.data.curve.CurveData;
import plotting.data.curve.Intersection;
import plotting.plots.AbstractPlot;
import rendering.camera.Viewport;

public class PlotManager{
    public ArrayList<AbstractPlot> plots;
    public ArrayList<CurveData> curveCache;
    public ArrayList<Intersection> intersectionCache;
    public PlotInteractionController interactionController;
    public ArrayList<PlotListener> listeners;
    private Runnable dirtyCallback;

    public PlotManager(PlotInteractionController plotInteractionController){
        plots = new ArrayList<>();
        curveCache = new ArrayList<>();
        interactionController = plotInteractionController;
        intersectionCache = new ArrayList<>();
        plotInteractionController.setCaches(curveCache, intersectionCache);
        this.listeners = new ArrayList<>();
    }
    public void addPlot(AbstractPlot plot){
        plots.add(plot);
        for(PlotListener listener : listeners){
            listener.plotsChanged();
            listener.plotAdded(plot);
        }
        markUnsaved();
    }
    public void removePlot(AbstractPlot plot){
        plots.remove(plot);
        for(PlotListener listener : listeners){
            listener.plotsChanged();
            listener.plotRemoved(plot);
        }
        markUnsaved();
    }
    public void computeCurveData(Viewport viewport){
        curveCache.clear();
        for(AbstractPlot plot : plots){
            curveCache.add(
                PlotComputationEngine.computeCurveData(plot, viewport)
            );
        }
        
        intersectionCache.clear();
        for(int i = 0; i < plots.size(); i++){
            AbstractPlot plot1 = plots.get(i);
            for(int j = i + 1; j < plots.size(); j++){
                intersectionCache.addAll(PlotComputationEngine.computeIntersections(plot1, plots.get(j), viewport));
            }
        }
    }

    public void removeAll(){
        plots.clear();
        markUnsaved();
    }
    public void addPlot(int index, AbstractPlot plot){
        plots.add(index, plot);
        for(PlotListener listener : listeners){
            listener.plotsChanged();
            listener.plotAdded(plot);
        }
        markUnsaved();
    }
    public void addListener(PlotListener listener){
        listeners.add(listener);
    }
    public void removeListener(PlotListener listener){
        listeners.remove(listener);
    }
    public void plotChanged(AbstractPlot plot){
        for(PlotListener listener : listeners){
            listener.plotChanged(plot);
        }
        markUnsaved();
    }

    public void setDirtyCallback(Runnable dirtyCallback) {
        this.dirtyCallback = dirtyCallback;
    }
    void markUnsaved(){
        if(dirtyCallback!= null){
            dirtyCallback.run();
        }
    }
}