package interaction.commands;

import plotting.PlotManager;
import plotting.plots.AbstractPlot;

public class EditPlotCommand implements Command{
    private final AbstractPlot target;
    private final AbstractPlot before;
    private final AbstractPlot after;
    private final PlotManager plotManager;

    public EditPlotCommand(AbstractPlot target, AbstractPlot before, AbstractPlot after, PlotManager plotManager){
        this.target = target;
        this.before = before;
        this.after = after;
        this.plotManager = plotManager;
    }

    @Override
    public void execute() {
        target.copyFrom(after);
        plotManager.plotChanged(target);
    }

    @Override
    public void undo() {
        target.copyFrom(before);
        plotManager.plotChanged(target);
    }
}
