package persistence;

import java.util.ArrayList;

import persistence.plotdata.FunctionPlotData;
import persistence.plotdata.ParametricPlotData;
import persistence.plotdata.PolarPlotData;
import plotting.PlotManager;
import plotting.plots.FunctionPlot;
import plotting.plots.ParametricPlot;
import plotting.plots.AbstractPlot;
import plotting.plots.PolarPlot;
import rendering.camera.Viewport;
import scene.GraphScene;

public final class Serializer {
    Serializer(){}

    public static ProjectData serialize(GraphScene scene){
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
        ArrayList<AbstractPlot> plots = plotManager.plots;

        for(AbstractPlot plot : plots){
            if(plot instanceof FunctionPlot p){
                FunctionPlotData plotData = new FunctionPlotData();
                plotData.expression = p.expression;
                plotData.name = p.getName();
                plotData.color = p.getColor().toString();
                plotData.dependent = p.dependent;
                plotData.independent = p.independent;

                data.plots.add(plotData);
            }
            if(plot instanceof ParametricPlot p){
                ParametricPlotData plotData = new ParametricPlotData();
                plotData.expression1 = p.expression1;
                plotData.expression2 = p.expression2;
                plotData.name = p.getName();
                plotData.color = p.getColor().toString();
                plotData.minParameter = p.tMin;
                plotData.maxParameter = p.tMax;

                data.plots.add(plotData);
            }
            if(plot instanceof PolarPlot p){
                PolarPlotData plotData = new PolarPlotData();
                plotData.expression = p.expression;
                plotData.name = p.getName();
                plotData.color = p.getColor().toString();
                plotData.minParameter = p.tMin;
                plotData.maxParameter = p.tMax;

                data.plots.add(plotData);
            }
        }
    }
}
