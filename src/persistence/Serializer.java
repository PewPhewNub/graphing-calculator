package persistence;

import java.util.ArrayList;

import engine.UI.GraphTab;
import engine.plotting.PlotManager;
import engine.plotting.plots.FunctionPlot;
import engine.plotting.plots.Plot;
import engine.rendering.camera.Viewport;
import engine.scene.GraphScene;
import persistence.plotdata.FunctionPlotData;

public final class Serializer {
    Serializer(){}

    public static ProjectData serialize(GraphTab tab){
        GraphScene scene = tab.getGraphScene();
        ProjectData data = new ProjectData();
        serializeViewportData(data, scene.getGraph().viewport);
        serializePlotData(data, scene.getPlotManager());
        
        return data;
    }
    public static void serializeViewportData(ProjectData data, Viewport viewport){
        data.viewport.centerX = viewport.cameraX;
        data.viewport.centerY = viewport.cameraY;
        data.viewport.zoom = viewport.getZoom();
        data.viewport.scaleX = viewport.scaleX;
        data.viewport.scaleY = viewport.scaleY;
        data.viewport.height = viewport.height;
        data.viewport.width = viewport.width;
    }

    public static void serializePlotData(ProjectData data, PlotManager plotManager){
        ArrayList<Plot> plots = plotManager.plots;

        for(Plot plot : plots){
            if(plot instanceof FunctionPlot p){
                FunctionPlotData plotData = new FunctionPlotData();
                plotData.expression = p.expression;
                plotData.name = p.getName();
                plotData.color = p.getColor().toString();
                plotData.dependent = p.dependent;
                plotData.independent = p.independent;

                data.plots.add(plotData);
            }
        }
    }
}
