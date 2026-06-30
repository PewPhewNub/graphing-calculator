package persistence;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import parser.ParseException;
import persistence.plotdata.FunctionPlotData;
import persistence.plotdata.GraphElementData;
import persistence.plotdata.ImplicitPlotData;
import persistence.plotdata.ParametricPlotData;
import persistence.plotdata.PlotData;
import persistence.plotdata.PolarPlotData;
import persistence.plotdata.VariableData;
import plotting.GraphElementManager;
import plotting.Variable;
import plotting.plots.FunctionPlot;
import plotting.plots.ImplicitPlot;
import plotting.plots.ParametricPlot;
import plotting.plots.PolarPlot;
import rendering.camera.Viewport;
import scene.GraphScene;

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
                ParametricPlot plot = new ParametricPlot(d.name, d.expression1, d.expression2, d.minParameter, d.maxParameter, Color.web(d.color));
                graphElementManager.addElement(plot);
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
                ImplicitPlot plot = new ImplicitPlot(d.name, d.expression1, d.expression2, Color.web(d.color));
                graphElementManager.addElement(plot);
            }
            if(data instanceof VariableData d){
                Variable variable = new Variable(d.name);
                variable.setValue(d.value);
                graphElementManager.addElement(variable);
            }
        }
    }
}
