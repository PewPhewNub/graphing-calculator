package plotting.data.curve;

import javafx.geometry.Point2D;
import plotting.plots.AbstractPlot;

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
