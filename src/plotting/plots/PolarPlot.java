package plotting.plots;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Function;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import parser.EvaluationContext;
import parser.ParseException;
import parser.node.DefinitionNode;
import plotting.GraphElement;

public class PolarPlot extends AbstractPlot implements FunctionCapable{
    public double tMin;
    public double tMax;
    public String expression;
    private DefinitionNode definition;
    public String dependent = "r";
    public String independent = "\u03B8";

    public ArrayList<Point2D> currentList;
    public PolarPlot(String name, String expression, double tMin, double tMax, Color color) throws ParseException{
        this.name = name;
        this.color = color;
        this.tMin = tMin;
        this.tMax = tMax;
        this.expression = expression;
        this.definition = PlotGenerator.generateDefinition(expression, dependent, Set.of(independent));
    }
    
    public PolarPlot(){
        this.name = "New Polar Plot";
        this.color = Color.RED;
        this.tMin = 0;
        this.tMax = 50;
        this.expression = "\u03B8";
        try {
            definition = PlotGenerator.generateDefinition(expression, dependent, Set.of(independent));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Point2D sample(double t, EvaluationContext context){
        return new Point2D(sampleX(t, context), sampleY(t, context));
    }
    public double sampleR(double t, EvaluationContext context){
        context.set(independent, t);
        return definition.evaluate(context);
    }
    public double sampleX(double t, EvaluationContext context){
        return sampleR(t, context)* Math.cos(t);
    }
    public double sampleY(double t, EvaluationContext context){
        return sampleR(t, context)* Math.sin(t);
    }

    @Override
    public PolarPlot copy() {
        try{
            return new PolarPlot(name, expression, tMin, tMax, color);
        }catch(ParseException p){
            p.printStackTrace();
        }
        return null;
    }
    @Override
    public boolean copyFrom(GraphElement plot){
        if(plot == null) return false;
        if(plot instanceof PolarPlot p){
            if(p.definition == null) return false;
            name = p.name;
            expression = p.expression;
            color = p.color;
            tMin = p.tMin;
            tMax = p.tMax;
            definition = p.definition;
            return true;
        }else{
            return false;
        }
    }
    @Override
    public boolean equals(AbstractPlot plot) {
        if(plot instanceof PolarPlot p){
            return p.name.trim().equals(name.trim())&&
                   p.expression.trim().equals(expression.trim())&&
                   p.color.equals(color)&&
                   p.tMin == tMin &&
                   p.tMax == tMax;
        }
        return false;
    }

    @Override
    public Set<String> getReferencedVariables() {
        return definition.getVariables();    
    }

    public Function<Double, Double> getX(EvaluationContext context){
        return t -> sampleX(t, context);
    }
    public Function<Double, Double> getY(EvaluationContext context){
        return t -> sampleY(t, context);
    }
}
