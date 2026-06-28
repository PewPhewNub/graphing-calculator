package interaction.commands;

import plotting.PlotManager;
import plotting.plots.AbstractPlot;

public class ReorderPlotCommand implements Command{
    AbstractPlot plot1;
    AbstractPlot plot2;
    PlotManager plotManager;

    public ReorderPlotCommand(AbstractPlot plot1, AbstractPlot plot2, PlotManager plotManager){
        this.plot1 = plot1;
        this.plot2 = plot2;
        this.plotManager = plotManager;
    }
    @Override
    public void execute() {
        plotManager.reorderPlot(plot1, plot2);
    }
    @Override
    public void undo() {
        plotManager.reorderPlot(plot2, plot1);
    }
}
