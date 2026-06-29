package interaction.commands;

import plotting.PlotManager;
import plotting.plots.AbstractPlot;

public class PushPlotToTopCommand implements Command{
    AbstractPlot plot;
    PlotManager plotManager;
    int index;

    public PushPlotToTopCommand(AbstractPlot plot, PlotManager plotManager){
        this.plot = plot;
        this.plotManager = plotManager;
        index = plotManager.plots.indexOf(plot);
    }

    @Override
    public void execute() {
        plotManager.movePlotTo(plot, 0);
    }

    
    @Override
    public void undo() {
        plotManager.movePlotTo(plot, index);
    }
}
