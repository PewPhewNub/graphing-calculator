package plotting.data;

import javafx.geometry.Point2D;

public class Arrow {
    public Point2D initial;
    public double dx;
    public double dy;
    public Arrow(Point2D point, double dx, double dy) {
        this.initial = point;
        this.dx = dx;
        this.dy = dy;
    }
}
