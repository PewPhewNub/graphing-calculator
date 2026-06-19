package core.model;

import javafx.geometry.BoundingBox;
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

    public boolean intersects(Segment2D segment){
        double minX1 = Math.min(point1.getX(), point2.getX());
        double maxX1 = Math.max(point1.getX(), point2.getX());
        double minY1 = Math.min(point1.getY(), point2.getY());
        double maxY1 = Math.max(point1.getY(), point2.getY());
        double minX2 = Math.min(segment.point1.getX(), segment.point2.getX());
        double maxX2 = Math.max(segment.point1.getX(), segment.point2.getX());
        double minY2 = Math.min(segment.point1.getY(), segment.point2.getY());
        double maxY2 = Math.max(segment.point1.getY(), segment.point2.getY());

        return (new BoundingBox(minX2, maxX2, minY2, maxY2).intersects(new BoundingBox(minX1, maxX1, minY1, maxY1)));
    }

    public boolean intersects(BoundingBox bounds){
        double minX1 = Math.min(point1.getX(), point2.getX());
        double maxX1 = Math.max(point1.getX(), point2.getX());
        double minY1 = Math.min(point1.getY(), point2.getY());
        double maxY1 = Math.max(point1.getY(), point2.getY());

        return (bounds.intersects(new BoundingBox(minX1, maxX1, minY1, maxY1)));
    }

    public boolean equals(Segment2D segment){
        return point1.equals(segment.point1) && point2.equals(segment.point2) ;
    }
}
