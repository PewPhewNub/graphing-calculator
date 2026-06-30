package interaction.commands;

import plotting.GraphElement;
import plotting.GraphElementManager;
import plotting.plots.AbstractPlot;

public class DuplicateElementCommand implements Command{
    GraphElement plotCopy;
    GraphElementManager plotManager;
    int index;

    public DuplicateElementCommand(GraphElement plot, GraphElementManager plotManager){
        this.plotCopy = plot.copy();
        this.plotManager = plotManager;
        this.index = plotManager.elements.indexOf(plot) + 1;
    }

    @Override
    public void execute() {
        plotManager.addElement(index, plotCopy);
        plotManager.setSelectedElement(plotCopy);
    }

    @Override
    public void undo() {
        plotManager.removeElement(plotCopy);
    }
}
