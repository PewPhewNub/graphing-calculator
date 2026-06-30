package interaction.commands;

import plotting.GraphElement;
import plotting.GraphElementManager;

public class SwapElementsCommand implements Command{
    GraphElement plot1;
    GraphElement plot2;
    GraphElementManager plotManager;

    public SwapElementsCommand(GraphElement plot1, GraphElement plot2, GraphElementManager plotManager){
        this.plot1 = plot1;
        this.plot2 = plot2;
        this.plotManager = plotManager;
    }
    @Override
    public void execute() {
        plotManager.swapElements(plot1, plot2);
    }
    @Override
    public void undo() {
        plotManager.swapElements(plot2, plot1);
    }
}
