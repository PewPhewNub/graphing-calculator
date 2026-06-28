package rendering.camera;

public class ViewportState {
    public double viewportHeight, viewportWidth;
    public double worldHeight, worldWidth;

    public double left, right, top, bottom;

    public double axisX, axisY;

    public boolean xAxisOnTop, xAxisOnBottom, yAxisOnLeft, yAxisOnRight;

    public double stepSizeX, stepSizeY;

    public double marginX, marginY;

    public ViewportState(Viewport viewport){
        viewportHeight = viewport.getHeight();
        viewportWidth = viewport.getWidth();
        left = viewport.screenToWorldX(0); 
        right = viewport.screenToWorldX(viewportWidth);
        bottom = viewport.screenToWorldY(viewportHeight);
        top = viewport.screenToWorldY(0);
        axisX = viewport.worldToScreenX(0); 
        axisY  = viewport.worldToScreenY(0);
        xAxisOnBottom = viewport.worldToScreenY(0) < 0; 
        xAxisOnTop = viewport.worldToScreenY(0) > viewportHeight - 30;
        yAxisOnRight = viewport.worldToScreenX(0) < 45;
        yAxisOnLeft = viewport.worldToScreenX(0) > viewportWidth + 7;
        
        worldHeight = top - bottom;
        worldWidth = right - left;
        stepSizeX = worldWidth/viewportWidth;
        stepSizeY = worldHeight/viewportHeight;
        marginX = .2*worldWidth;
        marginY = .2*worldHeight;
    }
}