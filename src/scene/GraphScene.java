package scene;

import java.util.ArrayList;

import computation.ComputationCoordinator;
import interaction.FunctionInteractionController;
import interaction.InputController;
import interaction.PlotInteractionController;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
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
import plotting.GraphElementListener;
import plotting.GraphElementManager;
import plotting.data.GridData;
import rendering.camera.CameraSystem;
import rendering.camera.Viewport;
import rendering.camera.ViewportListener;
import rendering.core.RenderContext;
import rendering.core.Renderer;
import rendering.graph.Graph;
import settings.ApplicationSettings;

public abstract class GraphScene implements GraphElementListener, ViewportListener{
    protected StackPane root;
    protected Graph graph;
    protected GraphElementManager plotManager;
    protected CameraSystem cameraSystem;
    protected GridData gridData;
    protected Renderer renderer;
    protected PlotInteractionController interaction;
    protected RenderContext context;
    protected ApplicationSettings settings;
    protected ComputationCoordinator coordinator;
    protected InputController input;
    protected boolean unsaved;

    protected boolean viewportMoved = true;
    protected boolean plotsChanged = true;
    protected boolean variablesChanged = true;
    protected GraphMode mode;

    public abstract void render();
    public abstract void update();
    public abstract void fixedUpdate();
    public abstract void lateUpdate();

    public GraphScene(ApplicationSettings settings, GraphMode mode){
        graph = new Graph(0, 0);
        root = new StackPane(graph);
        this.settings = settings;
        this.mode = mode;
        plotManager = new GraphElementManager(mode);
        coordinator = new ComputationCoordinator(plotManager);
        interaction = new FunctionInteractionController(plotManager, coordinator);
        context = new RenderContext(graph.getGraphicsContext2D(), graph.viewport);
        renderer = new Renderer(context, this.settings.rendererSettings);
        cameraSystem = new CameraSystem(graph.viewport);
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
    public Renderer getRenderer() {
        return renderer;
    }
    public GridData gridData(){
        return gridData;
    }
    public ArrayList<GraphElement> elements(){
        return plotManager.elements;
    }
    public GraphElementManager getPlotManager(){
        return plotManager;
    }
    public PlotInteractionController getInteraction(){
        return interaction;
    }
    public Graph getGraph(){
        return graph;
    }
    public CameraSystem getCameraSystem() {
        return cameraSystem;
    }
    public RenderContext getContext() {
        return context;
    }
    public ApplicationSettings getSettings() {
        return settings;
    }
    public void setPlotManager(GraphElementManager plotManager) {
        this.plotManager = plotManager;
    }
    public ComputationCoordinator getCoordinator() {
        return coordinator;
    }
    public void setCoordinator(ComputationCoordinator coordinator) {
        this.coordinator = coordinator;
    }
    public StackPane getRoot() {
        return root;
    }
    public InputController getInput() {
        return input;
    }
    public void setUnsaved(boolean dirty) {
        this.unsaved = dirty;
    }
    public boolean isUnsaved() {
        return unsaved;
    }
    public GraphMode getMode() {
        return mode;
    }
}
