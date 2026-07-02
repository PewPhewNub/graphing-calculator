package plotting.plots;

import java.util.Set;
import java.util.function.Function;

import javafx.scene.paint.Color;
import parser.EvaluationContext;
import parser.ParseException;
import parser.node.DefinitionNode;
import plotting.GraphElement;

public class FunctionPlot extends AbstractPlot implements FunctionCapable, ODECapable{

    public String dependent = "y";
    public String independent = "x";
    private DefinitionNode definition;
    public String expression;

    public FunctionPlot(){
        this.name = "New Explicit Plot";
        this.color = Color.RED;
        expression = "x";
        try {
            definition = PlotGenerator.generateDefinition(expression, dependent, Set.of(independent));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public FunctionPlot(String name, String expression, Color color) throws ParseException{
        this.name = name;
        this.color = color;
        this.expression = expression;
        definition = PlotGenerator.generateDefinition(expression, dependent, Set.of(independent));
    
    }
    public double sample(double x, EvaluationContext context){
        context.set(independent, x);
        return definition.evaluate(context);
    }

    public String getName() {
        return name;
    }
    public Color getColor() {
        return color;
    }
    public void setDependentVariable(String dependent){
        this.dependent = dependent;
    }
    
    public void setIndependentVariable(String independent){
        this.independent = independent;
    }

    public void setExpression(String expression) throws ParseException {
        DefinitionNode newDefinition =
        PlotGenerator.generateDefinition(
            expression,
            dependent,
            Set.of(independent)
        );

        this.expression = expression;
        this.definition = newDefinition;
    }


    @Override
    public FunctionPlot copy() {
        try{
            return new FunctionPlot(name, expression, color);
        }catch(ParseException p){
            p.printStackTrace();
        }
        return null;
    }
    @Override
    public boolean copyFrom(GraphElement plot){
        if(plot == null) return false;
        if(plot instanceof FunctionPlot p){
            if(p.definition == null) return false;
            name = p.name;
            expression = p.expression;
            dependent = p.dependent;
            independent = p.independent;
            color = p.color;
            definition = p.definition;
            return true;
        }else{
            return false;
        }
    }

    public boolean equals(AbstractPlot plot) {
        if(plot instanceof FunctionPlot p){
            return p.name.trim().equals(name.trim())&&
                   p.expression.trim().equals(expression.trim())&&
                   p.color.equals(color); 
        }
        return false;
    }

    public Function<Double, Double> getFunction(EvaluationContext context){
        if(definition == null) return null;
        return x -> sample(x, context);
    }

    public Set<String> getReferencedVariables(){
        return definition.getVariables();
    }
}
