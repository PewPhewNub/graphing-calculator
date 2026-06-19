package engine.scene;

import java.util.ArrayList;

import core.model.GridData;
import engine.plotting.PlotInteractionController;
import engine.plotting.PlotManager;
import engine.plotting.plots.Plot;
import engine.rendering.camera.CameraSystem;
import engine.rendering.core.RenderContext;
import engine.rendering.core.Renderer;
import engine.rendering.graph.Graph;

public abstract class GraphScene {
    protected Graph graph;
    protected PlotManager plotManager;
    protected CameraSystem cameraSystem;
    protected GridData gridData;
    protected Renderer renderer;
    protected PlotInteractionController interaction;
    protected RenderContext context;

    public abstract void render();
    public abstract void update();
    public abstract void generateGridData(double roughPixels);
    public GridData gridData(){
        return gridData;
    }
    public ArrayList<Plot> plots(){
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
}
