package engine.plotting.plots;

import java.util.Set;
import java.util.function.Function;

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
}
