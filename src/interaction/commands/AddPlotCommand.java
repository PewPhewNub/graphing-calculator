package interaction.commands;

import plotting.PlotManager;
import plotting.plots.AbstractPlot;

public class AddPlotCommand implements Command{
    AbstractPlot plot;
    int index;
    PlotManager plotManager;
    public AddPlotCommand(AbstractPlot plot, PlotManager plotManager){
        this.plot = plot;
        this.plotManager = plotManager;
    }

     @Override
    public void execute() {
        plotManager.addPlot(plot);
    }

    @Override
    public void undo() {
        index = plotManager.plots.indexOf(plot);
        plotManager.removePlot(plot);
    }
}

