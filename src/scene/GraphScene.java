package scene;

import java.util.ArrayList;

import plotting.PlotInteractionController;
import plotting.PlotManager;
import plotting.data.GridData;
import plotting.plots.AbstractPlot;
import rendering.camera.CameraSystem;
import rendering.core.RenderContext;
import rendering.core.Renderer;
import rendering.graph.Graph;
import settings.ApplicationSettings;

public abstract class GraphScene {
    protected Graph graph;
    protected PlotManager plotManager;
    protected CameraSystem cameraSystem;
    protected GridData gridData;
    protected Renderer renderer;
    protected PlotInteractionController interaction;
    protected RenderContext context;
    protected ApplicationSettings settings;

    public abstract void render();
    public abstract void update();
    public abstract void generateGridData(double roughPixels);
    public GridData gridData(){
        return gridData;
    }
    public ArrayList<AbstractPlot> plots(){
        return plotManager.plots;
    }
    public PlotManager getPlotManager(){
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
    public void setPlotManager(PlotManager plotManager) {
        this.plotManager = plotManager;
    }
}
