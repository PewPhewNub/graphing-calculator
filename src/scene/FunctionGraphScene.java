package scene;

import javafx.scene.Cursor;
import plotting.GraphElement;
import plotting.plots.AbstractPlot;
import plotting.plots.FunctionCapable;
import rendering.camera.CameraIntent;
import rendering.camera.Viewport;
import settings.ApplicationSettings;

public class FunctionGraphScene extends GraphScene{
    
    public CurrentMode currentMode;
    
    public FunctionGraphScene(ApplicationSettings settings){
        super(settings, GraphMode.FUNCTION);
        currentMode = CurrentMode.NONE;
    }

    @Override
    public void render() {
        renderer.render(this);
    }
    
    private void handleCamera() {
        Viewport viewport = graph.viewport;
        if (currentMode == CurrentMode.PANNING && input.mouseDown) {
            double worldNowX = viewport.screenToWorldX(input.mouseX, input.pressedX, viewport.getZoom());
            double worldNowY = viewport.screenToWorldY(input.mouseY, input.pressedY, viewport.getZoom());

            cameraSystem.goTo(
                input.pressedX - (worldNowX - input.pressedWorldX),
                input.pressedY - (worldNowY - input.pressedWorldY)
            );
        }

        if (input.deltaScrollX != 0 || input.deltaScrollY != 0) {
            if (currentMode == CurrentMode.RESCALE_X) {
                cameraSystem.handle(new CameraIntent(
                    0, 0,
                    0,
                    -input.deltaScrollX / 400,
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
                    -input.deltaScrollX / 400,
                    false,
                    input.mouseX,
                    input.mouseY
                ));
            } else if(currentMode == CurrentMode.ZOOM){
                if(input.isCtrlDown){
                    cameraSystem.handle(new CameraIntent(
                        0, 0,
                        (input.deltaScrollY + input.deltaScrollX) / 400,
                        0, 0,
                        true,
                        input.mouseX,
                        input.mouseY
                    ));
                }else{
                    cameraSystem.handle(new CameraIntent(
                        0, 0,
                        (input.deltaScrollY + input.deltaScrollX) / 400,
                        0, 0,
                        false,
                        graph.viewport.getViewportCenterX(),
                        graph.viewport.getViewportCenterY()
                    ));
                }
            }
            input.deltaScrollX = 0;
            input.deltaScrollY = 0;
        }
        
        cameraSystem.update();
    }

    public void update(){
        graph.viewport.setWidth(graph.getWidth());
        graph.viewport.setHeight(graph.getHeight());
        context.reload();
        input.update();
        updateMode();  
        handleCamera();     // apply camera changes
             // decide what user is doing
        handleInteraction();// apply plot interaction
        input.clearFrameEvents();
    }

    public void fixedUpdate(){
        return;
    }

    public void lateUpdate(){
        if(plotsChanged || viewportMoved || variablesChanged){
            coordinator.compute(graph.viewport, gridData);
        }
        
        generateGridData(80);

        plotsChanged = false;
        viewportMoved = false;
        variablesChanged = false;
    }
    
    private void handleInteraction(){
        double worldX = graph.viewport.screenToWorldX(input.mouseX);
        double worldY = graph.viewport.screenToWorldY(input.mouseY);
        interaction.update(worldX, worldY, graph.viewport, plotManager.buildEvaluationContext());
    }

    private void updateMode(){
        double xAxis = graph.viewport.worldToScreenY(0);
        double yAxis = graph.viewport.worldToScreenX(0);

        double dx = (input.mouseX - yAxis);
        double dy = (input.mouseY - xAxis);
        
        if(input.isShiftDown){
            if (Math.abs(dx) < 50 && Math.abs(dy) < 50) {
                if (Math.abs(Math.abs(dx) - Math.abs(dy)) < 10) {
                    if(dx < 0 != dy < 0) graph.setCursor(Cursor.NE_RESIZE);
                    else graph.setCursor(Cursor.SE_RESIZE);
                    currentMode = CurrentMode.ZOOM;
                    return;
                }
                if(Math.abs(dx) < Math.abs(dy)){
                    graph.setCursor(Cursor.V_RESIZE);
                    currentMode = CurrentMode.RESCALE_Y;
                    return;
                }else{
                    graph.setCursor(Cursor.H_RESIZE);
                    currentMode =  CurrentMode.RESCALE_X;
                    return;
                }
            }

            if (Math.abs(dy) < 50){
                graph.setCursor(Cursor.H_RESIZE);
                currentMode = CurrentMode.RESCALE_X;
                return;
            }
            if (Math.abs(dx) < 50){
                graph.setCursor(Cursor.V_RESIZE);
                currentMode = CurrentMode.RESCALE_Y;
                return;
            }
            if(dx < 0 != dy < 0) graph.setCursor(Cursor.NE_RESIZE);
            else graph.setCursor(Cursor.SE_RESIZE);
            currentMode = CurrentMode.ZOOM;
            return;
            }
        if(input.mousePressed){
            if(interaction.getHoveredCurve() != null){
                currentMode = CurrentMode.INSPECTING;
                graph.setCursor(Cursor.CROSSHAIR);
                interaction.selectHovered(graph.viewport);
            }
            else{
                currentMode = CurrentMode.PANNING;
                graph.setCursor(Cursor.CLOSED_HAND);
                interaction.clearSelection();
            }

            input.pressedWorldX =
                graph.viewport.screenToWorldX(input.pressedX);

            input.pressedWorldY =
                graph.viewport.screenToWorldY(input.pressedY);
            return;
            
        }
        if(input.mouseReleased){
            graph.setCursor(Cursor.DEFAULT);
            currentMode = CurrentMode.NONE;
        }
        if(input.deltaScrollY != 0){
            graph.setCursor(Cursor.DEFAULT);
            currentMode = CurrentMode.ZOOM;
        }
        if(!input.mouseDown && 
        input.deltaScrollX == 0 && 
        input.deltaScrollY == 0){
            graph.setCursor(Cursor.DEFAULT);
            currentMode = CurrentMode.NONE;
        }
    }

    public boolean canAdd(AbstractPlot plot){
        if(plot instanceof FunctionCapable) return true;
        return false;
    }
    @Override
    public void elementsChanged() {
        System.out.println("ELEMENTS CHANGED");
        plotsChanged = true;
    }

    @Override
    public void viewportMoved() {
        viewportMoved = true;
    }

    @Override
    public void elementAdded(GraphElement element) {
        return;
    }

    @Override
    public void elementRemoved(GraphElement element) {
        // TODO Auto-generated method stub
        return;
    }

    @Override
    public void elementChanged(GraphElement element) {
        // TODO Auto-generated method stub
        return;
    }

    @Override
    public void selectedElementChanged(GraphElement element) {
        // TODO Auto-generated method stub
        return;
    }

    @Override
    public void elementsSwapped(GraphElement element1, GraphElement element2) {
        // TODO Auto-generated method stub
        return;
    }

    @Override
    public void elementsSwapped(int index1, int index2) {
        // TODO Auto-generated method stub
        return;
    }

    @Override
    public void elementMovedTo(GraphElement element, int index) {
        // TODO Auto-generated method stub
        return;
    }
    enum CurrentMode{
        NONE,

        PANNING,
        INSPECTING,
        RESCALE_X,
        RESCALE_Y,
        ZOOM
    }
}
    

