package persistence;

import java.util.ArrayList;

import persistence.plotdata.FunctionPlotData;
import persistence.plotdata.ImplicitPlotData;
import persistence.plotdata.ParametricPlotData;
import persistence.plotdata.PolarPlotData;
import persistence.plotdata.VariableData;
import plotting.GraphElement;
import plotting.GraphElementManager;
import plotting.Variable;
import plotting.plots.FunctionPlot;
import plotting.plots.ImplicitPlot;
import plotting.plots.ParametricPlot;
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
        data.viewport.centerX = viewport.getCameraX();
        data.viewport.centerY = viewport.getCameraY();
        data.viewport.zoom = viewport.getZoom();
        data.viewport.scaleX = viewport.getScaleX();
        data.viewport.scaleY = viewport.getScaleY();
        data.viewport.height = viewport.getHeight();
        data.viewport.width = viewport.getWidth();
    }

    public static void serializePlotData(ProjectData data, GraphElementManager plotManager){
        ArrayList<GraphElement> elements = plotManager.elements;

        for(GraphElement element : elements){
            if(element instanceof FunctionPlot p){
                FunctionPlotData plotData = new FunctionPlotData();
                plotData.expression = p.expression;
                plotData.name = p.getName();
                plotData.color = p.getColor().toString();
                plotData.dependent = p.dependent;
                plotData.independent = p.independent;

                data.elements.add(plotData);
            }
            if(element instanceof ParametricPlot p){
                ParametricPlotData plotData = new ParametricPlotData();
                plotData.expression1 = p.expression1;
                plotData.expression2 = p.expression2;
                plotData.name = p.getName();
                plotData.color = p.getColor().toString();
                plotData.minParameter = p.tMin;
                plotData.maxParameter = p.tMax;

                data.elements.add(plotData);
            }
            if(element instanceof PolarPlot p){
                PolarPlotData plotData = new PolarPlotData();
                plotData.expression = p.expression;
                plotData.name = p.getName();
                plotData.color = p.getColor().toString();
                plotData.minParameter = p.tMin;
                plotData.maxParameter = p.tMax;

                data.elements.add(plotData);
            }
            if(element instanceof ImplicitPlot p){
                ImplicitPlotData plotData = new ImplicitPlotData();
                plotData.expression1 = p.expression1;
                plotData.expression2 = p.expression2;
                plotData.name = p.getName();
                plotData.color = p.getColor().toString();

                data.elements.add(plotData);
            }
            if(element instanceof Variable p){
                VariableData plotData = new VariableData();
                plotData.name = p.getName();
                plotData.value = p.getValue();

                data.elements.add(plotData);
            }
        }
    }
}
