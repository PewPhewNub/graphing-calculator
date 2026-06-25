package rendering.core;

import javafx.scene.canvas.GraphicsContext;
import rendering.camera.Viewport;
import rendering.camera.ViewportState;

public class RenderContext {

    GraphicsContext gc;
    Viewport viewport;
    ViewportState state;
    public RenderContext(GraphicsContext gc, Viewport viewport){
        this.gc = gc;
        this.viewport = viewport;
        this.state = new ViewportState(viewport);
    }
    public GraphicsContext getGc() {
        return gc;
    }
    public void getGc(GraphicsContext gc) {
        this.gc = gc;
    }
    public Viewport getViewport() {
        return viewport;
    }
    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }
    public ViewportState getState() {
        return state;
    }
    public void setState(ViewportState state) {
        this.state = state;
    }
    public void reload(){
        this.state = new ViewportState(viewport);
    }
}
