package io.github.pewphewnub.plotting.data.curve;

import io.github.pewphewnub.plotting.plots.AbstractPlot;
import javafx.geometry.Point2D;

public class Intersection {
    Point2D point;
    AbstractPlot plotA;
    AbstractPlot plotB;

    public Intersection(Point2D point, AbstractPlot plotA, AbstractPlot plotB) {
        this.point = point;
        this.plotA = plotA;
        this.plotB = plotB;
    }
    public Point2D getPoint() {
        return point;
    }
    public boolean isOn(AbstractPlot plot){
        return plotA == plot || plotB == plot;
    }
}
