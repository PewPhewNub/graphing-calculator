package rendering.graph;

import javafx.scene.canvas.Canvas;
import rendering.camera.Viewport;

public class Graph extends Canvas{
    public Viewport viewport;
    public GraphSettings settings;

    public Graph(double width, double height){
        setWidth(width);
        setHeight(height);
        super.minHeight(0);
        super.minWidth(0);
        viewport = new Viewport(width, height);
        settings = new GraphSettings();
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }
    @Override
    public double minWidth(double height) {
        return 0; // Allow shrinking to 0 width
    }

    @Override
    public double minHeight(double width) {
        return 0; // Allow shrinking to 0 height
    }
}
