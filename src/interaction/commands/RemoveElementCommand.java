package interaction.commands;

import plotting.GraphElement;
import plotting.GraphElementManager;

public class RemoveElementCommand implements Command{
    GraphElement plot;
    int index;
    GraphElementManager plotManager;
    public RemoveElementCommand(GraphElement plot, GraphElementManager plotManager){
        this.plot = plot;
        this.plotManager = plotManager;
        this.index = plotManager.elements.indexOf(plot);
    }

    @Override
    public void execute() {
        plotManager.removeElement(plot);
    }

    @Override
    public void undo() {
        plotManager.addElement(index, plot);
    }
}
