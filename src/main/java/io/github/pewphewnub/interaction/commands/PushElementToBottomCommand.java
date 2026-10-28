package io.github.pewphewnub.interaction.commands;

import io.github.pewphewnub.plotting.GraphElement;
import io.github.pewphewnub.plotting.GraphElementManager;

public class PushElementToBottomCommand implements Command{
    GraphElement plot;
    GraphElementManager plotManager;
    int index;

    public PushElementToBottomCommand(GraphElement plot, GraphElementManager plotManager){
        this.plot = plot;
        this.plotManager = plotManager;
        index = plotManager.elements.indexOf(plot);
    }

    @Override
    public void execute() {
        plotManager.movePlotTo(plot, plotManager.getCount() - 1);
    }

    
    @Override
    public void undo() {
        plotManager.movePlotTo(plot, index);
    }
}