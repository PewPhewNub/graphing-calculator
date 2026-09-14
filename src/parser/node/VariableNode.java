package parser.node;

import java.util.HashSet;
import java.util.Map;

import parser.EvaluationContext;

/**
 * Represents a variable node in the AST. 
 * Can resolve to a variable value from the {@link EvaluationContext}
 * or a mathematical constant (e.g., pi, e).
 */
public class VariableNode extends ExpressionNode {
    /** The name of the variable. */
    public String name;

    /**
     * Constructs a new VariableNode.
     *
     * @param name the name of the variable
     */
    public VariableNode(String name){
        this.name = name;
    }

    /**
     * Resolves the variable's value from the context or returns known constant values.
     *
     * @param context the {@link EvaluationContext}
     * @return the resolved value of the variable
     */
    public double evaluate(EvaluationContext context){
        return switch (name) {
            case "pi" -> Math.PI;
            case "e"  -> Math.E;
            default   -> context.get(name);
        };
    }

    /** Returns the name of the variable. */
    public String toString(){
        return name;
    }
    
    /** Returns the name of the variable. */
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
