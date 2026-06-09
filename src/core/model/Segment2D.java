package core.model;

import javafx.geometry.Point2D;

public class Segment2D {
    public Point2D point1;
    public Point2D point2;
    public Segment2D(Point2D point1, Point2D point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    @Override
    public String toString() {
        return "(" + point1.getX() + ", " + point1.getY() + "), (" + + point2.getX() + ", " + point2.getY() + ")";
    }
}
