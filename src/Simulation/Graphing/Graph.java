package Simulation.Graphing;

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
    public Renderer renderer;
    public InputController input;
    public CameraSystem cameraSystem;
    public PlotManager plotManager;
    public PlotInteractionController PIController;
    public CurrentMode currentMode;
    public GridData gridData;

    public void updateGrid(double roughPixels){
        gridData.points.clear();
        double[] roughDiff = new double[]{
            Math.abs(viewport.screenToWorldX(roughPixels) - viewport.screenToWorldX(0)),
            Math.abs(viewport.screenToWorldY(roughPixels) - viewport.screenToWorldY(0))
        };
        double[] gridlinesSpacing = new double[2];
        for(int i = 0 ; i <=1 ; i++){
            int exponent = (int)Math.floor(Math.log10(roughDiff[i]));
            double mantissa = roughDiff[i]/Math.pow(10,exponent);
            int niceValue;
            if(mantissa > 7.5){
                niceValue = 1; exponent++;
            } else if(mantissa > 3.75){
                niceValue = 5;
            } else if(mantissa > 1.5){
                niceValue = 2;
            } else niceValue = 1;

            gridlinesSpacing[i] = (niceValue * (Math.pow(10,exponent)));
        }
        gridData.stepX = gridlinesSpacing[0];
        gridData.stepY = gridlinesSpacing[1];
        double floorLeft = viewport.screenToWorldX(0); 
        double floorRight = viewport.screenToWorldX(viewport.width);
        double floorBottom = viewport.screenToWorldY(viewport.height);
        double floorTop = viewport.screenToWorldY(0);
        for (double x = Math.floor(floorLeft / gridData.stepX) * gridData.stepX; x < floorRight; x += gridData.stepX) {
            for (double y = Math.floor(floorBottom / gridData.stepY)* gridData.stepY; y < floorTop; y += gridData.stepY) {
                gridData.points.add(new Point2D(x, y));
            }
        }
    }
    public Graph(double width, double height){
        setWidth(width);
        setHeight(height);
        super.minHeight(0);
        super.minWidth(0);
        viewport = new Viewport(width, height);
        plotManager = new PlotManager();
        renderer = new Renderer(viewport, this.getGraphicsContext2D());
        cameraSystem = new CameraSystem(viewport);
        input = new InputController(cameraSystem);
        gridData = new GridData();
        PIController = new PlotInteractionController();
        currentMode = CurrentMode.NONE;

        addHandlers();
    }

    public void render(){
        renderer.clearCanvas();
        updateGrid(75);
        renderer.setState(new ViewportState(viewport));
        renderer.drawGridlines(gridData);
        renderer.drawAxesTicks(gridData);
        renderer.drawLabels(gridData);
        renderer.drawAxes();
        plotManager.recompute(viewport);
        for(CurveData i : plotManager.curveCache) renderer.drawCurveSegmented(i.visiblePoints(), i.originalPlot().getColor());
        for(Point2D i : plotManager.featureCache) renderer.drawMarker(i, 6, Color.LIGHTGRAY);
        if(PIController.inspectionPoint != null){
            renderer.drawMarker(PIController.inspectionPoint, 7, PIController.currentColor);
            renderer.drawInspectionLabel(PIController.inspectionPoint, Color.LIGHTGRAY);
        }
    }

    private void addHandlers(){
        this.setFocusTraversable(true);
        this.requestFocus();
        this.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e){
                input.mouseX = e.getX();
                input.mouseY = e.getY();
                if(!input.isCtrlDown && !input.isShiftDown && !input.isAltDown)currentMode = CurrentMode.NONE;
            }
        });
        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e){
                input.isMouseDown = true;
                input.mouseX = e.getX();
                input.mouseY = e.getY();
            }
        });
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e){
                requestFocus();
                input.pressedX = e.getX();
                input.pressedY = e.getY();
                input.pressedWorldX = viewport.screenToWorldX(input.pressedX);
                input.pressedWorldY = viewport.screenToWorldY(input.pressedY);
                input.mouseX = e.getX();
                input.mouseY = e.getY();
                input.isMouseDown = true;
                if(PIController.canInteract(plotManager.plots,
                        viewport, 
                        input.mouseX,
                        input.mouseY, 
                        input.isMouseDown
                    )) currentMode = CurrentMode.INSPECTING;
                else currentMode = CurrentMode.PANNING; 
            }
        });
        
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e){
                input.isMouseDown = false;
                currentMode = CurrentMode.NONE;
                PIController.reset();
            }
        });
        this.addEventHandler(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
           @Override
           public void handle(ScrollEvent e){
                input.deltaScrollY = e.getDeltaY();
                input.deltaScrollX = e.getDeltaX();
                if(input.isCtrlDown) currentMode = CurrentMode.RESCALE_Y;
                else if(input.isShiftDown) currentMode = CurrentMode.RESCALE_X;
                else currentMode = CurrentMode.NONE;
           } 
        });
        this.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e){
                input.keysPressed.add(e.getCode());
            }
        });
        
        this.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e){
                input.keysPressed.remove(e.getCode());
            }
        });
    }

    public void update(){
        viewport.height = (getHeight());
        viewport.width = (getWidth());
        input.handle();
        handleInput();
        updateCursor();
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

    public void handleInput() {
        if (currentMode == CurrentMode.PANNING && input.isMouseDown) {
            double dx = viewport.screenToWorldX(input.mouseX) - input.pressedWorldX;

            double dy = viewport.screenToWorldY(input.mouseY) - input.pressedWorldY;

            cameraSystem.handle(new CameraIntent(
                dx, dy,
                0,
                0, 0,
                false,
                input.mouseX,
                input.mouseY
            ));
        }

        if (input.deltaScrollX != 0 || input.deltaScrollY != 0) {
            if (currentMode == CurrentMode.RESCALE_X) {
                cameraSystem.handle(new CameraIntent(
                    0, 0,
                    0,
                    input.deltaScrollX / 1000,
                    0,
                    false,
                    input.mouseX,
                    input.mouseY
                ));
            } else if (currentMode == CurrentMode.RESCALE_Y) {
                cameraSystem.handle(new CameraIntent(
                    0, 0,
                    0,
                    0,
                    input.deltaScrollY / 1000,
                    false,
                    input.mouseX,
                    input.mouseY
                ));
            } else {
                cameraSystem.handle(new CameraIntent(
                    0, 0,
                    input.deltaScrollY / 1000,
                    0, 0,
                    input.isAltDown,
                    input.mouseX,
                    input.mouseY
                ));
            }
            input.deltaScrollX = 0;
            input.deltaScrollY = 0;
        }else if(currentMode == CurrentMode.INSPECTING){
            PIController.update(plotManager.plots,
                plotManager.featureCache,
                viewport, 
                input.mouseX,
                input.mouseY,
                input.isMouseDown
            );
        }
    }

    public void updateCursor(){
        if(currentMode == CurrentMode.PANNING) setCursor(Cursor.CLOSED_HAND);
        else if(currentMode == CurrentMode.RESCALE_X) setCursor(Cursor.H_RESIZE);
        else if(currentMode == CurrentMode.RESCALE_Y) setCursor(Cursor.V_RESIZE);
        else if(currentMode == CurrentMode.INSPECTING) setCursor(Cursor.CROSSHAIR);
        else setCursor(Cursor.DEFAULT);
    }
}
enum CurrentMode{
    NONE,

    PANNING,
    INSPECTING,
    RESCALE_X,
    RESCALE_Y,
    ZOOM
}
