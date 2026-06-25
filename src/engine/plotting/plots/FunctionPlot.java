package engine.plotting.plots;

import java.util.function.Function;

import engine.interaction.state.FunctionPlotState;
import engine.rendering.camera.Viewport;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class FunctionPlot extends Plot implements CartesianPlot{

    public Function<Double, Double> function;
    public String expression;
    public String dependent = "y";
    public String independent = "x";

    public FunctionPlot(){
        this.name = "New Function Plot";
        this.color = Color.RED;
        this.expression = "x";
        function = PlotGenerator.generateFunction(expression, dependent, independent);
    }

    public FunctionPlot(String name, String expression, Color color){
        function = PlotGenerator.generateFunction(expression, dependent, independent);
        this.name = name;
        this.color = color;
        this.expression = expression;
    }
    
    public FunctionPlot(String name, String expression, Function<Double, Double> f, Color color){
        function = f;
        this.name = name;
        this.color = color;
        this.expression = expression;
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
    
    public void setDependentVariable(String dependent){
        this.dependent = dependent;
    }
    
    public void setIndependentVariable(String independent){
        this.independent = independent;
    }

    public double sample(double x, double y){
        return function.apply(x);
    }
    public void setExpression(String expression) {
        this.expression = expression;
        this.function = PlotGenerator.generateFunction(expression, dependent, independent);
    }

    public boolean update(
            String expression,
            String dependent,
            String independent,
            Color color) {

        Function<Double, Double> newFunction =
                PlotGenerator.generateFunction(
                        expression,
                        dependent,
                        independent);

        if(newFunction == null)
            return false;

        this.expression = expression;
        this.dependent = dependent;
        this.independent = independent;
        this.color = color;
        this.function = newFunction;

        return true;
    }

    public FunctionPlotState getState(){
        return new FunctionPlotState(name, expression, independent, dependent, color);
    }
}
