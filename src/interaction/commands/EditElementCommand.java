package interaction.commands;

import plotting.GraphElement;
import plotting.GraphElementManager;

public class EditElementCommand implements Command{
    private final GraphElement target;
    private final GraphElement before;
    private final GraphElement after;
    private final GraphElementManager plotManager;

    public EditElementCommand(GraphElement target, GraphElement before, GraphElement after, GraphElementManager plotManager){
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
