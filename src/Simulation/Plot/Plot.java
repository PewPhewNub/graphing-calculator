package Simulation.Plot;

import Simulation.Graphing.Viewport;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public interface Plot {
    String getName();
    Color getColor();
    Point2D nearestPoint(double worldX, double worldY, Viewport viewport);
    double distanceSquaredFrom(double worldX, double worldY, Viewport viewport);
    public boolean contains(Point2D point);
}

