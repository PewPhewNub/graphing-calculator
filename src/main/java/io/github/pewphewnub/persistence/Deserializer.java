package io.github.pewphewnub.persistence;

import java.util.ArrayList;

import io.github.pewphewnub.math.Point;
import io.github.pewphewnub.parser.ParseException;
import io.github.pewphewnub.persistence.plotdata.FunctionPlotData;
import io.github.pewphewnub.persistence.plotdata.GraphElementData;
import io.github.pewphewnub.persistence.plotdata.ImplicitPlotData;
import io.github.pewphewnub.persistence.plotdata.ODEPlotData;
import io.github.pewphewnub.persistence.plotdata.ParametricPlotData;
import io.github.pewphewnub.persistence.plotdata.PolarPlotData;
import io.github.pewphewnub.persistence.plotdata.VariableData;
import io.github.pewphewnub.plotting.GraphElementManager;
import io.github.pewphewnub.plotting.Variable;
import io.github.pewphewnub.plotting.plots.FunctionPlot;
import io.github.pewphewnub.plotting.plots.ImplicitPlot;
import io.github.pewphewnub.plotting.plots.ODEPlot;
import io.github.pewphewnub.plotting.plots.ParametricPlot;
import io.github.pewphewnub.plotting.plots.PolarPlot;
import io.github.pewphewnub.rendering.camera.Viewport;
import io.github.pewphewnub.scene.GraphScene;
import javafx.scene.paint.Color;

public final class Deserializer {
    Deserializer(){}

    public static void apply(ProjectData data, GraphScene scene){
        setViewport(data.viewport,scene.getGraph().viewport);
        setElements(data.elements, scene.getPlotManager());

        scene.getCameraSystem().setToCurrentViewport();
    }

    public static void setViewport(ViewportData viewportData, Viewport viewport){
        viewport.setCameraX(viewportData.centerX);
        viewport.setCameraY(viewportData.centerY);
        viewport.setScaleX(viewportData.scaleX);
        viewport.setScaleY(viewportData.scaleY);
        viewport.setZoom(viewportData.zoom);
    }

    
    public static void setElements(ArrayList<GraphElementData> plotDatas, GraphElementManager graphElementManager){
        graphElementManager.removeAll();

        for(GraphElementData data : plotDatas){
            if(data instanceof FunctionPlotData d){
                FunctionPlot plot;
                try {
                    plot = new FunctionPlot(d.name, d.expression, Color.web(d.color));
                    graphElementManager.addElement(plot);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            }
            if(data instanceof ParametricPlotData d){
                ParametricPlot plot;
                try {
                    plot = new ParametricPlot(d.name, d.expression1, d.expression2, d.minParameter, d.maxParameter, Color.web(d.color));
                    graphElementManager.addElement(plot);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(data instanceof PolarPlotData d){
                PolarPlot plot;
                try {
                    plot = new PolarPlot(d.name, d.expression, d.minParameter, d.maxParameter, Color.web(d.color));
                    graphElementManager.addElement(plot);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(data instanceof ImplicitPlotData d){
                ImplicitPlot plot;
                try {
                    plot = new ImplicitPlot(d.name, d.expression1, d.expression2, Color.web(d.color));
                graphElementManager.addElement(plot);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(data instanceof ODEPlotData d){
                ODEPlot plot;
                try {
                    plot = new ODEPlot(d.name, d.expression, new Point(d.initialX, d.initialY), d.stepSize, Color.web(d.color), d.showSlopeField);
                graphElementManager.addElement(plot);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(data instanceof VariableData d){
                Variable variable = new Variable(d.name);
                variable.setValue(d.value);
                graphElementManager.addElement(variable);
            }
        }
    }
}
