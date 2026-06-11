package engine.plotting;

import engine.rendering.Viewport;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public interface Plot {
    String getName();
    Color getColor();
    void setColor(Color color);
    Point2D nearestPoint(double worldX, double worldY, Viewport viewport);
    double distanceSquaredFrom(double worldX, double worldY, Viewport viewport);
    public boolean contains(Point2D point);
}

