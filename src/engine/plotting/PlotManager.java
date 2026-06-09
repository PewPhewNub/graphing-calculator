package engine.plotting;

import java.lang.reflect.Parameter;
import java.util.ArrayList;

import core.model.CurveData;
import core.model.ViewportState;
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
        ViewportState state = new ViewportState(viewport);
        for(Plot plot : plots){
            if(plot instanceof FunctionPlot)
                curveCache.add(
                    PlotComputationEngine.computeCurveData((FunctionPlot)plot, viewport)
                );
            if(plot instanceof ODEPlot){
                PlotComputationEngine.ensureCoverage((ODEPlot)plot, state.left, state.right, state.marginX);
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
        }
    }
    public void computeFeaturePoints(ViewportState state){
        featureCache = PlotComputationEngine.generatePoints(plots,state.left, state.right, state.worldWidth*0.2);
    }

    public void recompute(Viewport viewport){
        computeCurves(viewport);
        computeFeaturePoints(new ViewportState(viewport));
    }
}