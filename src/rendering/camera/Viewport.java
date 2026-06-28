package rendering.camera;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;

public class Viewport {
    private double cameraX;
    private double cameraY;
    private double zoom = 90;

    private double width;
    private double height;

    private double scaleX; private double scaleY;

    private final List<ViewportListener> listeners = new ArrayList<>();

    public Viewport(double width, double height){
        this.width = width; this.height = height;
        cameraX = 0;
        cameraY = 0;
        zoom = 100;
        scaleX = 1; scaleY = 1;
    }

    public double worldToScreenX(double x){
        return (x - cameraX)/scaleX * zoom + width/2;
    }
    public double worldToScreenY(double y){
        return -(y - cameraY)/scaleY * zoom + height/2;
    }

    public double screenToWorldX(double x){
        return (x - width/2)/zoom*scaleX + cameraX; 
    }
    public double screenToWorldY(double y){
        return -(y - height/2)/zoom*scaleY + cameraY;
    }

    public double getZoom() {
        return zoom;
    }
    public void setZoom(double zoom) {
        if (Math.abs(zoom - this.zoom) < 1e-6)
            return;
        this.zoom = zoom;
        if(zoom > 1e8) this.zoom = 0.99999e8;
        if(zoom < 1e-6) this.zoom = 1.00001e-6;
        notifyListeners();
    }
    
    public double getViewportCenterX(){
        return width/2;
    }
    public double getViewportCenterY(){
        return height/2;
    }

    public void pan(double dx, double dy){
        cameraX -= dx;
        cameraY -= dy;
    }
    public void zoomAt(double mouseX, double mouseY, double delta) {

        double worldBeforeX = screenToWorldX(mouseX);
        double worldBeforeY = screenToWorldY(mouseY);

        setZoom(getZoom() * (1 + delta));

        double worldAfterX = screenToWorldX(mouseX);
        double worldAfterY = screenToWorldY(mouseY);

        cameraX += (worldBeforeX - worldAfterX);
        cameraY += (worldBeforeY - worldAfterY);
    }

    public void computeZoomAt(double mouseX, double mouseY, double delta){

    }
    public void setScaleX(double scaleX) {
        if (Math.abs(scaleX - this.scaleX) < 1e-6)
            return;
        this.scaleX = scaleX;
        notifyListeners();
    }
    public void setScaleY(double scaleY) {
        if (Math.abs(scaleY - this.scaleY) < 1e-6)
            return;
        this.scaleY = scaleY;
    }

    public Point2D screenDeltaToWorld(double x1, double y1, double x2, double y2) {
        x1 = screenToWorldX(x1);
        y1 = screenToWorldY(y1);
        x2 = screenToWorldX(x2);
        y2 = screenToWorldY(y2);
        return new Point2D(x2 - x1, y2 - y1);
    }

    public double screenToWorldX(double screenX, double cameraX, double zoom){
        return (screenX - width/2)/zoom*scaleX + cameraX; 
    }
    public double screenToWorldY(double screenY, double cameraY, double zoom){
        return -(screenY - height/2)/zoom*scaleY + cameraY;
    }

    public ViewportState getState(){
        ViewportState state = new ViewportState(this);
        return state;
    }

    public Viewport copy(){
        Viewport viewport = new Viewport(width, height);
        viewport.cameraX = cameraX;
        viewport.cameraY = cameraY;
        viewport.zoom = zoom;
        viewport.scaleX = scaleX;
        viewport.scaleY = scaleY;
        return viewport;
    }

    public void addListener(ViewportListener listener){
        listeners.add(listener);
    }

    public void removeListener(ViewportListener listener){
        listeners.add(listener);
    }

    public void notifyListeners(){
        for(ViewportListener listener : listeners){
            listener.viewportMoved();   
        }
    }

    public void setCameraX(double cameraX) {
        if (Math.abs(cameraX - this.cameraX) < 1e-6)
            return;
        this.cameraX = cameraX;
        notifyListeners();
    }
    public void setCameraY(double cameraY) {
        if (Math.abs(cameraY - this.cameraY) < 1e-6)
            return;
        this.cameraY = cameraY;
        notifyListeners();
    }
    public void setHeight(double height) {
        if (Math.abs(height - this.height) < 1e-6)
            return;
        this.height = height;
        notifyListeners();
    }
    public void setWidth(double width) {
        if (Math.abs(width - this.width) < 1e-6)
            return;
        this.width = width;
        notifyListeners();
    }
    public double getCameraX() {
        return cameraX;
    }
    public double getCameraY() {
        return cameraY;
    }
    public double getHeight() {
        return height;
    }
    public List<ViewportListener> getListeners() {
        return listeners;
    }
    public double getScaleX() {
        return scaleX;
    }
    public double getScaleY() {
        return scaleY;
    }
    public double getWidth() {
        return width;
    }
}
