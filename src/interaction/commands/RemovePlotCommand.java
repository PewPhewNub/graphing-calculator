package interaction.commands;

import plotting.PlotManager;
import plotting.plots.Plot;

public class RemovePlotCommand implements Command{
    Plot plot;
    int index;
    PlotManager plotManager;
    public RemovePlotCommand(Plot plot, PlotManager plotManager){
        this.plot = plot;
        this.plotManager = plotManager;
        this.index = plotManager.plots.indexOf(plot);
    }

    @Override
    public void execute() {
        plotManager.removePlot(plot);
    }

    @Override
    public void undo() {
        plotManager.addPlot(index, plot);
    }
}
