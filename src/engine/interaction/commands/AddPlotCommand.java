package engine.interaction.commands;

import engine.UI.controls.PlotEditor;
import engine.plotting.PlotManager;
import engine.plotting.plots.Plot;

public class AddPlotCommand implements Command{
    Plot plot;
    int index;
    PlotManager plotManager;
    public AddPlotCommand(Plot plot, PlotManager plotManager){
        this.plot = plot;
        this.plotManager = plotManager;
    }

    @Override
    public void execute() {
        plotManager.addPlot(index, plot);
    }

    @Override
    public void undo() {
        plotManager.removePlot(plot);
    }
}
