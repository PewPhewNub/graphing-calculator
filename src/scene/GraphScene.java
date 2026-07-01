package scene;

import java.util.ArrayList;

import computation.ComputationCoordinator;
import interaction.PlotInteractionController;
import plotting.GraphElement;
import plotting.GraphElementListener;
import plotting.GraphElementManager;
import plotting.data.GridData;
import rendering.camera.CameraSystem;
import rendering.camera.ViewportListener;
import rendering.core.RenderContext;
import rendering.core.Renderer;
import rendering.graph.Graph;
import settings.ApplicationSettings;

public abstract class GraphScene implements GraphElementListener, ViewportListener{
    protected Graph graph;
    protected GraphElementManager plotManager;
    protected CameraSystem cameraSystem;
    protected GridData gridData;
    protected Renderer renderer;
    protected PlotInteractionController interaction;
    protected RenderContext context;
    protected ApplicationSettings settings;
    protected ComputationCoordinator coordinator;

    protected boolean viewportMoved = true;
    protected boolean plotsChanged = true;
    protected boolean variablesChanged = true;

    public abstract void render();
    public abstract void update();
    public abstract void fixedUpdate();
    public abstract void lateUpdate();
    public abstract void generateGridData(double roughPixels);
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
}
