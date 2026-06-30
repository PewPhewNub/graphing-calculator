package interaction.commands;

import plotting.GraphElement;
import plotting.GraphElementManager;

public class AddElementCommand implements Command{
    GraphElement plot;
    int index;
    GraphElementManager plotManager;
    public AddElementCommand(GraphElement plot, GraphElementManager plotManager){
        this.plot = plot;
        this.plotManager = plotManager;
    }

     @Override
    public void execute() {
        plotManager.addElement(plot);
    }

    @Override
    public void undo() {
        index = plotManager.elements.indexOf(plot);
        plotManager.removeElement(plot);
    }
}

