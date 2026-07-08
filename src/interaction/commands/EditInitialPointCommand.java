package interaction.commands;

import math.Point;
import plotting.GraphElementManager;
import plotting.plots.AbstractPlot;
import plotting.plots.ODECapable;

public class EditInitialPointCommand implements Command{
    private ODECapable plot;
    private GraphElementManager manager;
    private Point point1;
    private Point point2;

    public EditInitialPointCommand(ODECapable plot, GraphElementManager manager, Point newPoint){
        this.point1 = plot.getInitialPoint();
        this.point2 = newPoint;
        this.manager = manager;
        this.plot = plot;
    }

    @Override
    public void execute() {
        plot.setInitialPoint(point2);
        manager.elementChanged((AbstractPlot)plot);
    }

    @Override
    public void undo() {
        plot.setInitialPoint(point1);
        manager.elementChanged((AbstractPlot)plot);
    }
}
