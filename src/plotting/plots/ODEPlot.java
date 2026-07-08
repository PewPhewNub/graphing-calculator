package plotting.plots;

import java.util.Set;
import java.util.function.BiFunction;

import javafx.scene.paint.Color;
import math.Point;
import parser.EvaluationContext;
import parser.ParseException;
import parser.node.DefinitionNode;
import plotting.GraphElement;

public class ODEPlot extends AbstractPlot implements ODECapable{
    
    Point initial;
    DefinitionNode definition;
    public String expression;
    boolean autoGenerate = false;
    boolean showSlopeField = false;

    public ODEPlot(String name, String expression, Point initial, Color color, boolean showSlopeField) throws ParseException{
        this.initial = initial;
        this.name = name;
        this.expression = expression;
        definition = PlotGenerator.generateDefinition(expression, "y", Set.of("x"));
        this.color = color;
        this.showSlopeField = showSlopeField;
    }

    public ODEPlot(){
        this.initial = new Point(0, 1);
        this.name = "ODE Plot";
        this.expression = "y";
        showSlopeField = false;
        try {
            definition = PlotGenerator.generateDefinition(expression, "y", Set.of("x", "y"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.color = Color.RED;
    }

    public void setAutoGenerate(boolean autoGenerate) {
        this.autoGenerate = autoGenerate;
    }

    public boolean showSlopeField(){
        return showSlopeField;
    }
    public void setShowSlopeField(boolean showSlopeField) {
        this.showSlopeField = showSlopeField;
    }

    @Override
    public ODEPlot copy() {
        try {
            return new ODEPlot(name, expression, initial, color, showSlopeField);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean copyFrom(GraphElement other) {
        if(other instanceof ODEPlot p){
            if(p.definition == null) return false;
            name = p.name;
            initial = p.initial;
            color = p.color;
            expression = p.expression;
            definition = p.definition;
            showSlopeField = p.showSlopeField;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(AbstractPlot plot) {
        if(plot instanceof ODEPlot p){
            return name.trim().equals(p.name.trim())&&
                   initial.equals(p.initial)&&
                   color.equals(p.color)&&
                   expression.equals(p.expression)&&
                   showSlopeField == p.showSlopeField;
        }
        return false;
    }

    @Override
    public Set<String> getReferencedVariables() {
        return definition.getVariables();
    }
    public Point getInitial() {
        return initial;
    }
    public BiFunction<Double, Double, Double> getFunction(EvaluationContext context){
        return (x, y) -> sample(x, y, context);
    }
    public double sample(double x, double y, EvaluationContext context){
        context.set("x", x);
        context.set("y", y);
        return definition.evaluate(context);
    }
    @Override
    public void setInitialPoint(Point point) {
        initial = point.copy();
    }
    @Override
    public Point getInitialPoint() {
        return initial;
    }
}
