package scene;

import computation.ComputationCoordinator;
import interaction.CartesianInteractionController;
import interaction.InputController;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import plotting.GraphElement;
import plotting.GraphElementManager;
import plotting.data.GridData;
import plotting.plots.AbstractPlot;
import plotting.plots.CartesianPlot;
import rendering.camera.CameraIntent;
import rendering.camera.CameraSystem;
import rendering.camera.Viewport;
import rendering.core.RenderContext;
import rendering.core.Renderer;
import rendering.graph.Graph;
import settings.ApplicationSettings;

public class FunctionGraphScene extends GraphScene{
    
    public CurrentMode currentMode;
    private boolean unsaved;
    
    public FunctionGraphScene(double width, double height, ApplicationSettings settings){
        graph = new Graph(width, height);
        root = new StackPane(graph);
        this.settings = settings;
        plotManager = new GraphElementManager();
        coordinator = new ComputationCoordinator(plotManager);
        interaction = new CartesianInteractionController(plotManager, coordinator);
        context = new RenderContext(graph.getGraphicsContext2D(), graph.viewport);
        renderer = new Renderer(context, this.settings.rendererSettings);
        cameraSystem = new CameraSystem(graph.viewport);
        currentMode = CurrentMode.NONE;
        gridData = new GridData();
        input = new InputController();
        unsaved = false;

        graph.viewport.addListener(this);
        plotManager.addListener(this);
        plotManager.addListener(coordinator);
        
        graph.widthProperty().bind(root.widthProperty());
        graph.heightProperty().bind(root.heightProperty());
        graph.widthProperty().addListener((obs,o,n) -> render());
        graph.heightProperty().addListener((obs,o,n) -> render());

        
        root.setMinSize(0, 0);
        root.setMaxWidth(Double.MAX_VALUE);

        root.setBackground(
            new Background(
                new BackgroundFill(
                    Color.WHITE,
                    CornerRadii.EMPTY,
                    Insets.EMPTY
                )
            )
        );
        VBox.setVgrow(root, Priority.ALWAYS);
        HBox.setHgrow(root, Priority.ALWAYS);
        addHandlers();
    }

    private void addHandlers(){

        root.setFocusTraversable(true);
        root.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            root.requestFocus();
        });
        root.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            root.requestFocus();
        });

        root.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            input.mouseX = e.getX();
            input.mouseY = e.getY();
            input.worldX = graph.viewport.screenToWorldX(e.getX());
            input.worldY = graph.viewport.screenToWorldY(e.getY());
            input.mouseMoved = true;
        });


        root.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            input.mouseX = e.getX();
            input.mouseY = e.getY();
            input.worldX = graph.viewport.screenToWorldX(e.getX());
            input.worldY = graph.viewport.screenToWorldY(e.getY());
            input.mouseDown = true;
        });


        root.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            root.requestFocus();

            input.pressedX = e.getX();
            input.pressedY = e.getY();

            input.mouseX = e.getX();
            input.mouseY = e.getY();

            input.worldX = graph.viewport.screenToWorldX(e.getX());
            input.worldY = graph.viewport.screenToWorldY(e.getY());

            input.mousePressed = true;
            input.mouseDown = true;
        });


        root.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {    
            root.requestFocus();
            input.mousePressed = false;
            input.mouseReleased = true;
            input.mouseDown = false;
        });


        root.addEventHandler(ScrollEvent.SCROLL, e -> {
    System.out.println("SCROLL EVENT");
            input.deltaScrollX = e.getDeltaX();
            input.deltaScrollY = e.getDeltaY();
                        
            input.isShiftDown = e.isShiftDown();
            input.isCtrlDown = e.isControlDown();
        });

        root.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            input.keysPressed.add(e.getCode());
            System.out.println("KEY: " + e.getCode());
        });

        root.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            input.keysPressed.remove(e.getCode());
        });

        root.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            input.mouseX = Double.NaN;
            input.mouseY = Double.NaN;
            input.worldX = Double.NaN;
            input.worldY = Double.NaN;
        });
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
        double floorRight = viewport.screenToWorldX(viewport.getWidth());
        double floorBottom = viewport.screenToWorldY(viewport.getHeight());
        double floorTop = viewport.screenToWorldY(0);
        for (double x = Math.floor(floorLeft / gridData.stepX) * gridData.stepX; x < floorRight; x += gridData.stepX) {
            for (double y = Math.floor(floorBottom / gridData.stepY)* gridData.stepY; y < floorTop; y += gridData.stepY) {
                gridData.points.add(new Point2D(x, y));
            }
        }
    }

    public void handleCamera() {
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
                        input.isCtrlDown,
                        input.mouseX,
                        input.mouseY
                    ));
                }else{
                    cameraSystem.handle(new CameraIntent(
                        0, 0,
                        (input.deltaScrollY + input.deltaScrollX) / 400,
                        0, 0,
                        input.isCtrlDown,
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
            coordinator.compute(graph.viewport);
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
        if(plot instanceof CartesianPlot) return true;
        return false;
    }

    public void setUnsaved(boolean dirty) {
        this.unsaved = dirty;
    }
    public boolean isUnsaved() {
        return unsaved;
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
}
    

enum CurrentMode{
    NONE,

    PANNING,
    INSPECTING,
    RESCALE_X,
    RESCALE_Y,
    ZOOM
}
