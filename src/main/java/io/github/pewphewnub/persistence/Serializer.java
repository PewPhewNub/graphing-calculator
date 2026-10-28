package io.github.pewphewnub.persistence;

import java.util.ArrayList;

import io.github.pewphewnub.persistence.plotdata.FunctionPlotData;
import io.github.pewphewnub.persistence.plotdata.ImplicitPlotData;
import io.github.pewphewnub.persistence.plotdata.ODEPlotData;
import io.github.pewphewnub.persistence.plotdata.ParametricPlotData;
import io.github.pewphewnub.persistence.plotdata.PolarPlotData;
import io.github.pewphewnub.persistence.plotdata.VariableData;
import io.github.pewphewnub.plotting.GraphElement;
import io.github.pewphewnub.plotting.GraphElementManager;
import io.github.pewphewnub.plotting.Variable;
import io.github.pewphewnub.plotting.plots.FunctionPlot;
import io.github.pewphewnub.plotting.plots.ImplicitPlot;
import io.github.pewphewnub.plotting.plots.ODEPlot;
import io.github.pewphewnub.plotting.plots.ParametricPlot;
import io.github.pewphewnub.plotting.plots.PolarPlot;
import io.github.pewphewnub.rendering.camera.Viewport;
import io.github.pewphewnub.scene.FunctionGraphScene;
import io.github.pewphewnub.scene.GraphScene;
import io.github.pewphewnub.scene.ODEGraphScene;

public final class Serializer {
    Serializer(){}

    public static ProjectData serialize(GraphScene scene){
        ProjectData data = new ProjectData();
        if(scene instanceof FunctionGraphScene) data.graphType = GraphType.CARTESIAN;
        else if(scene instanceof ODEGraphScene) data.graphType = GraphType.ODE;
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
            if(element instanceof ODEPlot p){
                ODEPlotData plotData = new ODEPlotData();
                plotData.expression = p.expression;
                plotData.name = p.getName();
                plotData.color = p.getColor().toString();
                plotData.initialX = p.getInitial().x;
                plotData.initialY = p.getInitial().y;
                plotData.showSlopeField = p.showSlopeField();
                plotData.stepSize = p.getStepSize();

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
