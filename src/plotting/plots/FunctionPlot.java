package plotting.plots;

import java.util.function.Function;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import parser.ParseException;
import rendering.camera.Viewport;

public class FunctionPlot extends AbstractPlot implements CartesianPlot{

    public Function<Double, Double> function;
    public String expression;
    public String dependent = "y";
    public String independent = "x";

    public FunctionPlot(){
        this.name = "New Explicit Plot";
        this.color = Color.RED;
        this.expression = "x";
        try {
            function = PlotGenerator.generateFunction(expression, dependent, independent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public FunctionPlot(String name, String expression, Color color){
        this.name = name;
        this.color = color;
        this.expression = expression;
        try {
            function = PlotGenerator.generateFunction(expression, dependent, independent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        if(function == null) return new Point2D(Double.NaN, Double.NaN);
        return new Point2D(worldX, function.apply(worldX));
    }
    public double distanceSquaredFrom(double x0, double y0, Viewport viewport) {
        if(function == null) return Double.POSITIVE_INFINITY;
        double minDist2 = Double.POSITIVE_INFINITY;
        double worldMinX = viewport.screenToWorldX(0);
        double worldMaxX = viewport.screenToWorldX(viewport.getWidth());
        double mouseX = viewport.worldToScreenX(x0);
        double mouseY = viewport.worldToScreenY(y0);

        int samples = (int)viewport.getWidth(); // 1 sample per pixel (good UX baseline)
        
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
        try {
            function = PlotGenerator.generateFunction(expression, dependent, independent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean update(
            String name,
            String expression,
            String dependent,
            String independent,
            Color color) {

        try {
            Function<Double, Double> newFunction = PlotGenerator.generateFunction(expression, dependent, independent);            
            if(newFunction == null)
                return false;

            this.expression = expression;
            this.dependent = dependent;
            this.independent = independent;
            this.color = color;
            this.function = newFunction;
            this.name = name;
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public FunctionPlot copy() {
        FunctionPlot p = new FunctionPlot(); 
        p.update(name, expression, dependent, independent, color);
        return p;
    }
    @Override
    public boolean copyFrom(AbstractPlot plot){
        if(plot == null) return false;
        if(plot instanceof FunctionPlot p){
            if(p.getFunction() == null) return false;
            name = p.name;
            expression = p.expression;
            dependent = p.dependent;
            independent = p.independent;
            color = p.color;
            function = p.function;
            update();
            return true;
        }else{
            return false;
        }
    }

    public void update(){
        return;
    }

    public boolean equals(AbstractPlot plot) {
        if(plot instanceof FunctionPlot p){
            return p.name.trim().equals(name.trim())&&
                   p.expression.trim().equals(expression.trim())&&
                   p.color.equals(color); 
        }
        return false;
    }
}
