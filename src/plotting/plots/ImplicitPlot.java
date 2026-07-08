package plotting.plots;

import java.util.Set;
import java.util.function.BiFunction;

import javafx.scene.paint.Color;
import parser.EvaluationContext;
import parser.ParseException;
import parser.node.DefinitionNode;
import plotting.GraphElement;

public class ImplicitPlot extends AbstractPlot implements FunctionCapable{
    public String expression1;
    public String expression2;
    public DefinitionNode definition;
    public String equivExpression;
    
    public ImplicitPlot(String name, String expression1, String expression2, Color color) throws ParseException{
        this.name = name;
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.equivExpression = "(" +  expression2 + ") - (" + expression1 + ")";
        this.color = color;

        definition = PlotGenerator.generateDefinition(equivExpression, "y", Set.of("x", "y"));
    }

    public ImplicitPlot(){
        this.name = "New Implicit Plot";
        this.expression1 = "y";
        this.expression2 = "x";
        this.equivExpression = "(y) - (x)";
        this.color = Color.RED;

        try {
            definition = PlotGenerator.generateDefinition(equivExpression, "y", Set.of("x", "y"));
        } catch (ParseException e) {
            return;
        }
    }

    

    public double sample(double x, double y, EvaluationContext context){ 
        context.set("x", x);
        context.set("y", y);
        return definition.evaluate(context);
    }

    public BiFunction<Double, Double, Double> getFunction(EvaluationContext context){
        return (x, y) -> sample(x, y, context);
    }

    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public String getName() {
        return name;
    }

    @Override
    public ImplicitPlot copy() {
        ImplicitPlot plot = new ImplicitPlot();
        plot.name = name;
        plot.expression1 = expression1;
        plot.expression2 = expression2;
        plot.equivExpression = equivExpression;
        plot.color = color;
        return plot;
    }

    @Override
    public boolean copyFrom(GraphElement other) {
        if(other instanceof ImplicitPlot p){
            name = p.name;
            expression1 = p.expression1;
            expression2 = p.expression2;
            equivExpression = p.equivExpression;
            color = p.color;
            definition = p.definition;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(AbstractPlot plot) {
        if(plot instanceof ImplicitPlot p){
            return expression1.trim().equals(p.expression1.trim())&&
                   expression2.trim().equals(p.expression2.trim())&&
                   color.equals(p.color)&&
                   name.equals(p.name);
        }else return false;
    }

    @Override
    public Set<String> getReferencedVariables() {
        return definition.getVariables();
    }
}
