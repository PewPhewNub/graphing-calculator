package persistence;

import java.util.ArrayList;

import engine.UI.GraphTab;
import engine.UI.UIPanel;
import engine.plotting.PlotManager;
import engine.plotting.plots.FunctionPlot;
import engine.plotting.plots.PlotGenerator;
import engine.rendering.camera.Viewport;
import javafx.scene.paint.Color;
import persistence.plotdata.FunctionPlotData;
import persistence.plotdata.PlotData;

public final class Deserializer {
    Deserializer(){}

    public static void apply(ProjectData data, GraphTab tab){
        setViewport(data.viewport,tab.getGraphScene().getGraph().viewport);
        setPlots(data.plots, tab.getGraphScene().getPlotManager());
        setUI(tab.getUiPanel());
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
                FunctionPlot plot = new FunctionPlot(d.name, d.expression,
                    PlotGenerator.generateFunction(d.expression, d.dependent, d.independent),
                    Color.web(d.color)
                );

                plotManager.addPlot(plot);
            }
        }
    }

    public static void setUI(UIPanel panel){
        panel.rebuildEditors();
    }
}
