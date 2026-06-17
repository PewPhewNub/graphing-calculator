package engine.plotting.plots;

import java.util.ArrayList;
import java.util.function.Function;

import core.model.CurveData;
import core.model.Segment2D;
import core.model.ViewportState;
import engine.plotting.settings.FunctionPlotSettings;
import engine.rendering.Viewport;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class FunctionPlot implements Plot{

    public Function<Double, Double> function;
    Color color;
    String name;
    public FunctionPlotSettings settings;

    public FunctionPlot(String name, Function<Double, Double> f, Color color){
        function = f;
        this.name = name;
        this.color = color;
        settings = new FunctionPlotSettings();
    }

    public String getName() {
        return name;
    }
    public Color getColor() {
        return color;
    }
    public Point2D nearestPoint(double worldX, double worldY, Viewport viewport) {
        return new Point2D(worldX, function.apply(worldX));
    }
    public double distanceSquaredFrom(double x0, double y0, Viewport viewport) {
        double minDist2 = Double.POSITIVE_INFINITY;
        double worldMinX = viewport.screenToWorldX(0);
        double worldMaxX = viewport.screenToWorldX(viewport.width);
        double mouseX = viewport.worldToScreenX(x0);
        double mouseY = viewport.worldToScreenY(y0);

        int samples = (int)viewport.width; // 1 sample per pixel (good UX baseline)
        
        for (int i = 0; i < samples; i++) {

            double x = worldMinX + (worldMaxX - worldMinX) * (i / (double) samples);

            double y = function.apply(x);
            if (!Double.isFinite(y)) continue;

            double dx = viewport.worldToScreenX(x) - mouseX;
            double dy = viewport.worldToScreenY(y) - mouseY;

            double dist2 = dx * dx + dy * dy;

            if (dist2 < minDist2) {
                minDist2 = dist2;
            }
        }
        return minDist2;
    }

    public Function<Double, Double> getFunction() {
        return function;
    }

    public boolean contains(Point2D point){
        double pointX = point.getX();
        double pointY = point.getY();
        return pointY - function.apply(pointX) < 1e-7;
    }
    
    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    public void setDependentVariable(String dependent){
        settings.dependentVariable = dependent;
    }
    
    public void setIndependentVariable(String independent){
        settings.independentVariable = independent;
    }

    public CurveData sample(Viewport viewport){
        ViewportState state = new ViewportState(viewport);
        ArrayList<Segment2D> list = new ArrayList<>();
        double minX = state.left;
        double maxX = state.right;
        double prevX = minX;
        double prevY = function.apply(minX);
        double screenX = viewport.worldToScreenX(prevX);
        double screenY = viewport.worldToScreenY(prevY);
        double step = (maxX - minX)/state.viewportWidth;

        boolean penDown = true;
        
        for(int i = 1; i < viewport.width; i++){
            double x = i*step + minX;
            double y = function.apply(x);

            if(isWithinBounds(new Point2D(prevX, prevY), new Point2D(x, y), viewport)){
                if(penDown){
                    double dx = screenX - viewport.worldToScreenX(x);
                    double dy = screenY - viewport.worldToScreenY(y);

                    if(dx*dx + dy*dy > 1){
                        list.add(new Segment2D(new Point2D(prevX, prevY), new Point2D(x, y)));
                    }
                }else{
                    penDown = true;
                }
            }else{
                penDown = false;
            }
        
            prevX = x;
            prevY = y;
        }

        return new CurveData(this, list);
    }

    private static boolean isWithinBounds(Point2D prev, Point2D point, Viewport viewport){
    // Reject any segment with non-finite coordinates before doing anything else
        if (!Double.isFinite(prev.getX()) || !Double.isFinite(prev.getY()) ||
            !Double.isFinite(point.getX()) || !Double.isFinite(point.getY())) {
            return false;
        }
    // ... rest of your existing code unchanged

        ViewportState state = new ViewportState(viewport);
        boolean prevVisible =
            prev.getX() >= state.left - state.marginX &&
            prev.getX() <= state.right + state.marginX &&
            prev.getY() >= state.bottom - state.marginY &&
            prev.getY() <= state.top + state.marginY; 

        boolean currVisible =
            point.getX() >= state.left - state.marginX &&
            point.getX() <= state.right + state.marginX&&
            point.getY() >= state.bottom - state.marginY &&
            point.getY() <= state.top + state.marginY;

        boolean overlapsViewport =
            Math.max(prev.getX(), point.getX()) >= state.left &&
            Math.min(prev.getX(), point.getX()) <= state.right &&
            Math.max(prev.getY(), point.getY()) >= state.bottom &&
            Math.min(prev.getY(), point.getY()) <= state.top;
        
        if(!(prevVisible || currVisible || overlapsViewport)) return false;

        return true;
    }

    public double sample(double x, double y){
        return function.apply(x);
    }
}
