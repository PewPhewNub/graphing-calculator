package rendering.camera;

import javafx.geometry.Point2D;

public class Viewport {
    public double cameraX;
    public double cameraY;
    private double zoom = 90;

    public double width;
    public double height;

    public double scaleX; public double scaleY;

    public Viewport(double width, double height){
        this.width = width; this.height = height;
        cameraX = 0;
        cameraY = 0;
        zoom = 90;
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
        this.zoom = zoom;
        if(zoom > 1e8) this.zoom = 0.99999e8;
        if(zoom < 1e-6) this.zoom = 1.00001e-6;
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
        this.scaleX = scaleX;
    }
    public void setScaleY(double scaleY) {
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
}
