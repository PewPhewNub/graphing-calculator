package plotting.plots;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import math.Interval;
import parser.EvaluationContext;
import parser.ParseException;
import parser.node.DefinitionNode;
import plotting.GraphElement;
import plotting.data.ParametricCurveChunk;

public class ParametricPlot extends AbstractPlot implements CartesianPlot{
    public String dependent1 = "x";
    public String dependent2 = "y";
    public String independent = "t";
    public double tMin;
    public double tMax;
    public String expression1;
    public String expression2;
    public DefinitionNode definition1;
    public DefinitionNode definition2;
    public ParametricPlot(String name, String expression1, String expression2, double tMin, double tMax, Color color) throws ParseException{
        this.name = name;
        this.color = color;
        this.tMin = tMin;
        this.tMax = tMax;
        this.expression1 = expression1;
        this.expression2 = expression2;
        definition1 = PlotGenerator.generateDefinition(expression1, dependent1, Set.of(independent));
        definition2 = PlotGenerator.generateDefinition(expression2, dependent2, Set.of(independent));
    }
    
    public ParametricPlot(){
        this.name = "New Parametric Plot";
        this.color = Color.RED;
        this.tMin = 0;
        this.tMax = 50;
        this.expression1 = "2*t";
        this.expression2 = "t^2";

        try {
            definition1 = PlotGenerator.generateDefinition(expression1, dependent1, Set.of(independent));
        definition2 = PlotGenerator.generateDefinition(expression2, dependent2, Set.of(independent));
        } catch (ParseException e) {
            // TODO: handle exception
        }
    }

    public Point2D sample(double t, EvaluationContext context){
        return new Point2D(getX(context).apply(t), getY(context).apply(t));
    }
    public double sampleX(double t, EvaluationContext context){
        context.set(independent, t);
        return definition1.evaluate(context);
    }
    public double sampleY(double t, EvaluationContext context){
        context.set(independent, t);
        return definition2.evaluate(context);
    }
    public Function<Double, Double> getX(EvaluationContext context){
        return t -> sampleX(t, context);
    }
    public Function<Double, Double> getY(EvaluationContext context){
        return t -> sampleY(t, context);
    }

    @Override
    public ParametricPlot copy() {
        try {
            return new ParametricPlot(name, expression1, expression2, tMin, tMax, color);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } 
    }
    @Override
    public boolean copyFrom(GraphElement plot){
        if(plot == null) return false;
        if(plot instanceof ParametricPlot p){
            if(p.definition2 == null) return false;
            if(p.definition1 == null) return false;
            name = p.name;
            expression1 = p.expression1;
            expression2 = p.expression2;
            color = p.color;
            tMin = p.tMin;
            tMax = p.tMax;
            definition1 = p.definition1;
            definition2 = p.definition2;
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean equals(AbstractPlot plot) {
        if(plot instanceof ParametricPlot p){
            return p.name.trim().equals(name.trim())&&
                   p.expression1.trim().equals(expression1.trim())&&
                   p.expression2.trim().equals(expression2.trim())&&
                   p.color.equals(color)&&
                   p.tMin == tMin &&
                   p.tMax == tMax; 
        }
        return false;
    }

    
    @Override
    public Set<String> getReferencedVariables() {
        Set<String> set = new HashSet<>(definition1.getVariables());
        set.addAll(definition2.getVariables());
        return set;    
    }
}
