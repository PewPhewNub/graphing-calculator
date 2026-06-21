package core.model.curve;

import engine.plotting.plots.Plot;
import javafx.geometry.Point2D;

public class Intersection {
    Point2D point;
    Plot plotA;
    Plot plotB;

    public Intersection(Point2D point, Plot plotA, Plot plotB) {
        this.point = point;
        this.plotA = plotA;
        this.plotB = plotB;
    }
    public Point2D getPoint() {
        return point;
    }
    public boolean isOn(Plot plot){
        return plotA == plot || plotB == plot;
    }
}
