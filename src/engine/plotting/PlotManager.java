package engine.plotting;

import java.util.ArrayList;

import core.model.CurveData;
import core.model.ViewportState;
import engine.plotting.plots.FunctionPlot;
import engine.plotting.plots.ImplicitPlot;
import engine.plotting.plots.ODEPlot;
import engine.plotting.plots.ParametricPlot;
import engine.plotting.plots.Plot;
import engine.plotting.plots.PolarPlot;
import engine.rendering.Viewport;
import javafx.geometry.Point2D;

public class PlotManager{
    public ArrayList<Plot> plots;
    public ArrayList<CurveData> curveCache;
    public ArrayList<Point2D> featureCache;
    boolean dirty;

    public PlotManager(){
        plots = new ArrayList<>();
        curveCache = new ArrayList<>();
        featureCache = new ArrayList<>();
    }
    public void addPlot(Plot plot){
        plots.add(plot);
    }
    public void removePlot(Plot plot){
        plots.remove(plot);
    }
    public void computeCurves(Viewport viewport){
        curveCache.clear();
        for(Plot plot : plots){
            if(plot == null) continue;
            if(plot instanceof FunctionPlot)
                curveCache.add(
                    PlotComputationEngine.computeCurveData((FunctionPlot)plot, viewport)
                );
            if(plot instanceof ODEPlot){
                curveCache.add(    
                    PlotComputationEngine.computeCurveData((ODEPlot)plot, viewport)
                );
            }
            if(plot instanceof ParametricPlot){
                ((ParametricPlot)plot).accurateComputedPoints.clear();
                curveCache.add(    
                    PlotComputationEngine.computeCurveData((ParametricPlot)plot, viewport)
                );
            }
            if(plot instanceof PolarPlot){
                ((PolarPlot)plot).accurateComputedPoints.clear();
                curveCache.add(    
                    PlotComputationEngine.computeCurveData((PolarPlot)plot, viewport)
                );
            }
            
            if(plot instanceof ImplicitPlot){
                curveCache.add(    
                    PlotComputationEngine.computeCurveData((ImplicitPlot)plot, viewport)
                );
            }
        }
    }
    public void computeFeaturePoints(Viewport viewport){
        featureCache.clear();
        ViewportState state = new ViewportState(viewport);
        for(Plot plot : plots){
            featureCache.addAll(PlotComputationEngine.computeIntercepts(plot, state));
            featureCache.addAll(PlotComputationEngine.computeCriticalPoints(plot, state));
        }

        for(int i = 0; i < plots.size(); i++){
            Plot plot1 = plots.get(i);
            for(int j = i + 1; j < plots.size(); j++){
                featureCache.addAll(PlotComputationEngine.computeIntersections(plot1, plots.get(j), viewport));
            }
        }
    }

    public void recompute(Viewport viewport){
        computeCurves(viewport);
        computeFeaturePoints(viewport);
    }
}