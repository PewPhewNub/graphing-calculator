package interaction.commands;

import plotting.PlotManager;
import plotting.plots.AbstractPlot;

public class ReplacePlotCommand implements Command{
    PlotManager plotManager;
    AbstractPlot plot1;
    AbstractPlot plot2;
    int index;

    public ReplacePlotCommand(PlotManager plotManager, AbstractPlot plot1, AbstractPlot plot2, int index){
        this.plot1 = plot1;
        this.plot2 = plot2;
        this.plotManager = plotManager;
        this.index = index;
    }

    @Override
    public void execute() {
        if(plot1 != null){
            plotManager.removePlot(plot1);
        }
        if(plot2 != null){
            plotManager.addPlot(index, plot2);
        }
    }
    
    @Override
    public void undo() {
        if(plot2 != null){
            plotManager.removePlot(plot2);
        }
        if(plot1 != null){
            plotManager.addPlot(index, plot1);
        }
    }
}
