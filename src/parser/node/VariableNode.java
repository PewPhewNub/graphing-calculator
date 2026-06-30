package parser.node;

import java.util.HashSet;
import java.util.Map;

import parser.EvaluationContext;

public class VariableNode extends ExpressionNode {
    public String name;

    public VariableNode(String name){
        this.name = name;
    }

    public double evaluate(EvaluationContext context){
        return switch (name) {
            case "pi" -> Math.PI;
            case "e"  -> Math.E;
            default   -> context.get(name);
        };
    }

    public String toString(){
        return name;
    }
    public String getName() {
        return name;
    }
    
    @Override
    public HashSet<String> getVariables() {
        return new HashSet<String>() {
            {
                add(name);
            }
        };
    }
}
