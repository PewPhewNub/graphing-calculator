package main.java.io.github.pewphewnub.plotting.data.curve;

import javafx.geometry.Point2D;

public interface InteractiveCurveData{
    Point2D nearestPoint(double x, double y);
    double distanceSquared(double x, double y);
}
