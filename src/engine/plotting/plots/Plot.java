package engine.plotting.plots;

import engine.rendering.Viewport;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public abstract class Plot {
    String name;
    Color color;

    public String getName(){
        return name;
    }
    public Color getColor(){
        return color;
    }
    public void setColor(Color color){
        this.color = color;
    }
    public abstract Point2D nearestPoint(double worldX, double worldY, Viewport viewport);
    public abstract double distanceSquaredFrom(double worldX, double worldY, Viewport viewport);
    public abstract boolean contains(Point2D point);
}

