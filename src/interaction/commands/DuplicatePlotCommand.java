package interaction.commands;

import plotting.PlotManager;
import plotting.plots.AbstractPlot;

public class DuplicatePlotCommand implements Command{
    AbstractPlot plotCopy;
    PlotManager plotManager;
    int index;

    public DuplicatePlotCommand(AbstractPlot plot, PlotManager plotManager){
        this.plotCopy = plot.copy();
        this.plotManager = plotManager;
        this.index = plotManager.plots.indexOf(plot) + 1;
    }

    @Override
    public void execute() {
        plotManager.addPlot(index, plotCopy);
        plotManager.setSelectedPlot(plotCopy);
    }

    @Override
    public void undo() {
        plotManager.removePlot(plotCopy);
    }
}
