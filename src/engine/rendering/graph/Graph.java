package engine.rendering.graph;

import core.model.GridData;
import core.model.ViewportState;
import core.model.curve.CurveData;
import engine.UI.InputController;
import engine.plotting.PlotInteractionController;
import engine.plotting.PlotManager;
import engine.plotting.plots.ODEPlot;
import engine.plotting.plots.Plot;
import engine.rendering.camera.CameraIntent;
import engine.rendering.camera.Viewport;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

public class Graph extends Canvas{
    public Viewport viewport;
    public InputController input;
    public GraphSettings settings;

    
    public Graph(double width, double height){
        setWidth(width);
        setHeight(height);
        super.minHeight(0);
        super.minWidth(0);
        viewport = new Viewport(width, height);
        input = new InputController();
        settings = new GraphSettings();
        addHandlers();
    }

    private void addHandlers(){

        setFocusTraversable(true);

        addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            input.mouseX = e.getX();
            input.mouseY = e.getY();
            input.mouseMoved = true;
        });


        addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            input.mouseX = e.getX();
            input.mouseY = e.getY();
            input.mouseDown = true;
        });


        addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {

            requestFocus();

            input.pressedX = e.getX();
            input.pressedY = e.getY();

            input.mouseX = e.getX();
            input.mouseY = e.getY();

            input.mousePressed = true;
            input.mouseDown = true;
        });


        addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {

            input.mousePressed = false;
            input.mouseReleased = true;
            input.mouseDown = false;
        });


        addEventHandler(ScrollEvent.SCROLL, e -> {

            input.deltaScrollX = e.getDeltaX();
            input.deltaScrollY = e.getDeltaY();

        });


        addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            input.keysPressed.add(e.getCode());
        });


        addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            input.keysPressed.remove(e.getCode());
        });

        widthProperty().addListener((obs, oldV, newV) -> {
            viewport.width = newV.doubleValue();
        });

        heightProperty().addListener((obs, oldV, newV) -> {
            viewport.height = newV.doubleValue();
        });
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
    public InputController getInput() {
        return input;
    }
}
