package plotting.data;

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
    public BoundingBox getBounds(){
        double minX1 = Math.min(point1.getX(), point2.getX());
        double maxX1 = Math.max(point1.getX(), point2.getX());
        double minY1 = Math.min(point1.getY(), point2.getY());
        double maxY1 = Math.max(point1.getY(), point2.getY());

        return new BoundingBox(minX1, minY1, maxX1 - minX1, maxY1 - minY1);
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

    public Point2D nearestPoint(double mouseX, double mouseY){
        double BAx = (point2.getX() - point1.getX());
        double BAy = (point2.getY() - point1.getY());

        double lenSq = BAx*BAx + BAy*BAy;

        if(lenSq < 1e-14) return midpoint();

        double QAx = (mouseX - point1.getX());
        double QAy = (mouseY - point1.getY());

        double t = (QAx*BAx + QAy*BAy)/(BAx*BAx + BAy*BAy);
        t = Math.max(0, Math.min(1, t));

        return new Point2D(
            point1.getX() + BAx*t,
            point1.getY() + BAy*t
        );
    }

    public double distanceSquared(double mouseX, double mouseY){
        Point2D closestPoint = nearestPoint(mouseX, mouseY);
        double dx = mouseX - closestPoint.getX();
        double dy = mouseY - closestPoint.getY();
        return dx*dx + dy*dy;
    }

    public Point2D midpoint(){
        return new Point2D((point1.getX() + point2.getX())/2, (point1.getY() + point2.getY())/2);
    }
}
