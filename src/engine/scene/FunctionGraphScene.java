package engine.scene;

import core.model.GridData;
import engine.UI.InputController;
import engine.plotting.CartesianInteractionController;
import engine.plotting.PlotManager;
import engine.plotting.plots.CartesianPlot;
import engine.plotting.plots.Plot;
import engine.rendering.camera.CameraIntent;
import engine.rendering.camera.CameraSystem;
import engine.rendering.camera.Viewport;
import engine.rendering.core.RenderContext;
import engine.rendering.core.Renderer;
import engine.rendering.graph.Graph;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;

public class FunctionGraphScene extends GraphScene{
    
    public CurrentMode currentMode;
    private CartesianInteractionController interaction;
    private boolean dirty;
    
    public FunctionGraphScene(double width, double height){
        graph = new Graph(width, height);
        interaction = new CartesianInteractionController();
        plotManager = new PlotManager(interaction);
        context = new RenderContext(graph.getGraphicsContext2D(), graph.viewport);
        renderer = new Renderer(context);
        cameraSystem = new CameraSystem(graph.viewport);
        currentMode = CurrentMode.NONE;
        gridData = new GridData();
        dirty = false;
        this.settings = new SceneSettings();
    }

    @Override
    public void render() {
        renderer.render(this);
    }

    public void generateGridData(double roughPixels){
        Viewport viewport = graph.viewport;
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

    public void handleCamera() {
        InputController input = graph.getInput();
        Viewport viewport = graph.viewport;
        if (currentMode == CurrentMode.PANNING && input.mouseDown) {
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
                    input.deltaScrollX / 400,
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
                    input.deltaScrollY / 400,
                    false,
                    input.mouseX,
                    input.mouseY
                ));
            } else {
                cameraSystem.handle(new CameraIntent(
                    0, 0,
                    input.deltaScrollY / 400,
                    0, 0,
                    input.isAltDown,
                    input.mouseX,
                    input.mouseY
                ));
            }
            input.deltaScrollX = 0;
            input.deltaScrollY = 0;
        }
        
        cameraSystem.update();
    }
    public void updateCursor(){
        if(currentMode == CurrentMode.PANNING) graph.setCursor(Cursor.CLOSED_HAND);
        else if(currentMode == CurrentMode.RESCALE_X) graph.setCursor(Cursor.H_RESIZE);
        else if(currentMode == CurrentMode.RESCALE_Y) graph.setCursor(Cursor.V_RESIZE);
        else if(currentMode == CurrentMode.INSPECTING) graph.setCursor(Cursor.CROSSHAIR);
        else graph.setCursor(Cursor.DEFAULT);
    }

    public void update(){
        graph.viewport.width = graph.getWidth();
        graph.viewport.height = graph.getHeight();
        context.reload();
        InputController input = graph.getInput();
        input.update();

        handleCamera();     // apply camera changes
        plotManager.computeCurveData(graph.viewport);
        updateMode();       // decide what user is doing
        handleInteraction();// apply plot interaction
        generateGridData(75);
        updateCursor();
        input.clearFrameEvents();
    }

    private void handleInteraction(){
        InputController input = graph.getInput();
        double worldX = graph.viewport.screenToWorldX(input.mouseX);
        double worldY = graph.viewport.screenToWorldY(input.mouseY);
        interaction.update(graph.viewport, worldX, worldY);
    }

    private void updateMode(){
        InputController input = graph.getInput();
        if(input.mousePressed){
            if(interaction.getHoveredCurve() != null){
                currentMode = CurrentMode.INSPECTING;
                interaction.selectHovered(plotManager.intersectionCache, graph.viewport);
            }
            else{
                currentMode = CurrentMode.PANNING;
                interaction.clearSelection();
            }

            input.pressedWorldX =
                graph.viewport.screenToWorldX(input.pressedX);

            input.pressedWorldY =
                graph.viewport.screenToWorldY(input.pressedY);
            
        }
        if(input.mouseReleased){
            currentMode = CurrentMode.NONE;
        }
        if(input.deltaScrollX != 0 || input.deltaScrollY != 0){

            if(input.isCtrlDown){
                currentMode = CurrentMode.RESCALE_Y;
            }
            else if(input.isShiftDown){
                currentMode = CurrentMode.RESCALE_X;
            }
            else{
                currentMode = CurrentMode.ZOOM;
            }
        }
        if(!input.mouseDown && 
        input.deltaScrollX == 0 && 
        input.deltaScrollY == 0){
            currentMode = CurrentMode.NONE;
        }
        if(input.mousePressed){

            
        }
    }

    public boolean canAdd(Plot plot){
        if(plot instanceof CartesianPlot) return true;
        return false;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    public boolean isDirty() {
        return dirty;
    }
    public SceneSettings getSettings(){
        return settings;
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
