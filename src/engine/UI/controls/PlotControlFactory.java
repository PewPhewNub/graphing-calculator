package engine.UI.controls;

import engine.plotting.PlotManager;
import engine.plotting.plots.FunctionPlot;

public class PlotControlFactory {
    public static FunctionPlotEditor create(PlotManager manager, FunctionPlot plot){
        return new FunctionPlotEditor(manager, plot);
    }
}
