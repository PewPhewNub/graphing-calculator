package interaction.commands;

import plotting.PlotManager;
import plotting.plots.AbstractPlot;

public class RemovePlotCommand implements Command{
    AbstractPlot plot;
    int index;
    PlotManager plotManager;
    public RemovePlotCommand(AbstractPlot plot, PlotManager plotManager){
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
