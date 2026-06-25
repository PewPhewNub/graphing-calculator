package interaction.commands;

import plotting.PlotManager;
import plotting.plots.Plot;
import ui.controls.PlotEditor;

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
