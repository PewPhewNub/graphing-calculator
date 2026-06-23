package persistence;

import java.util.ArrayList;

import engine.plotting.PlotManager;
import engine.plotting.plots.FunctionPlot;
import engine.plotting.plots.ParametricPlot;
import engine.rendering.camera.Viewport;
import engine.scene.GraphScene;
import javafx.scene.paint.Color;
import persistence.plotdata.FunctionPlotData;
import persistence.plotdata.ParametricPlotData;
import persistence.plotdata.PlotData;

public final class Deserializer {
    Deserializer(){}

    public static void apply(ProjectData data, GraphScene scene){
        setViewport(data.viewport,scene.getGraph().viewport);
        setPlots(data.plots, scene.getPlotManager());
    }

    public static void setViewport(ViewportData viewportData, Viewport viewport){
        viewport.cameraX = viewportData.centerX;
        viewport.cameraY = viewportData.centerY;
        viewport.setScaleX(viewportData.scaleX);
        viewport.setScaleY(viewportData.scaleY);
        viewport.setZoom(viewportData.zoom);
    }

    
    public static void setPlots(ArrayList<PlotData> plotDatas, PlotManager plotManager){
        plotManager.removeAll();

        for(PlotData data : plotDatas){
            if(data instanceof FunctionPlotData d){
                FunctionPlot plot = new FunctionPlot(d.name, d.expression, Color.web(d.color));
                plotManager.addPlot(plot);
            }
            if(data instanceof ParametricPlotData d){
                ParametricPlot plot = new ParametricPlot(d.name, d.expression1, d.expression2, d.minParameter, d.maxParameter, Color.web(d.color));
                plotManager.addPlot(plot);
            }
        }
    }
}
