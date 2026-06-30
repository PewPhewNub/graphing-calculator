package interaction.commands;

import plotting.GraphElement;
import plotting.GraphElementManager;

public class ReplaceElementCommand implements Command{
    GraphElementManager plotManager;
    GraphElement plot1;
    GraphElement plot2;
    int index;

    public ReplaceElementCommand(GraphElementManager plotManager, GraphElement plot1, GraphElement plot2, int index){
        this.plot1 = plot1;
        this.plot2 = plot2;
        this.plotManager = plotManager;
        this.index = index;
    }

    @Override
    public void execute() {
        if(plot1 != null){
            plotManager.removeElement(plot1);
        }
        if(plot2 != null){
            plotManager.addElement(index, plot2);
        }
    }
    
    @Override
    public void undo() {
        if(plot2 != null){
            plotManager.removeElement(plot2);
        }
        if(plot1 != null){
            plotManager.addElement(index, plot1);
        }
    }
}
