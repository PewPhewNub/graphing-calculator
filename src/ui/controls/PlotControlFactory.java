package ui.controls;

import plotting.PlotManager;
import plotting.plots.FunctionPlot;

public class PlotControlFactory {
    public static FunctionPlotEditor create(PlotManager manager, FunctionPlot plot){
        return new FunctionPlotEditor(manager, plot);
    }
}
