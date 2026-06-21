package engine.plotting;

import java.util.ArrayList;

import core.model.ViewportState;
import core.model.curve.CurveData;
import core.model.curve.Intersection;
import engine.plotting.plots.FunctionPlot;
import engine.plotting.plots.ImplicitPlot;
import engine.plotting.plots.ODEPlot;
import engine.plotting.plots.ParametricPlot;
import engine.plotting.plots.Plot;
import engine.plotting.plots.PolarPlot;
import engine.plotting.plots.VectorFieldPlot;
import engine.rendering.camera.Viewport;
import javafx.geometry.Point2D;

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
    }
    public void addPlot(Plot plot){
        plots.add(plot);
    }
    public void removePlot(Plot plot){
        plots.remove(plot);
    }
    public void computeCurveData(Viewport viewport){
        curveCache.clear();
        for(Plot plot : plots){
            if(plot == null) continue;
            if(plot instanceof FunctionPlot p)
                curveCache.add(
                    PlotComputationEngine.computeCurveData(p, viewport)
                );
            if(plot instanceof ODEPlot p){
                curveCache.add(    
                    PlotComputationEngine.computeCurveData(p, viewport)
                );
            }
            if(plot instanceof ParametricPlot p){
                curveCache.add(    
                    PlotComputationEngine.computeCurveData(p, viewport)
                );
            }
            if(plot instanceof PolarPlot p){
                curveCache.add(    
                    PlotComputationEngine.computeCurveData(p, viewport)
                );
            }
            
            if(plot instanceof ImplicitPlot p){
                curveCache.add(    
                    PlotComputationEngine.computeCurveData(p, viewport)
                );
            }

            if(plot instanceof VectorFieldPlot p){
                curveCache.add(    
                    PlotComputationEngine.computeCurveData(p, viewport)
                );
            }
        }
        
        intersectionCache.clear();
        for(int i = 0; i < plots.size(); i++){
            Plot plot1 = plots.get(i);
            for(int j = i + 1; j < plots.size(); j++){
                intersectionCache.addAll(PlotComputationEngine.computeIntersections(plot1, plots.get(j), viewport));
            }
        }
    }
}