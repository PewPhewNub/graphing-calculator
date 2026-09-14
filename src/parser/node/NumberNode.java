package parser.node;

import java.util.HashSet;
import java.util.Map;

import parser.EvaluationContext;

/**
 * Represents a numeric constant node in the AST.
 */
public class NumberNode extends ExpressionNode {
    /** The numeric value. */
    private final double number;

    /**
     * Constructs a new NumberNode.
     *
     * @param number the numeric value
     */
    public NumberNode(double number){
        this.number = number;
    }

    /**
     * Returns the stored numeric value.
     *
     * @param context unused for numeric nodes
     * @return the number
     */
    public double evaluate(EvaluationContext context){
        return number;
    }
    
    /** Returns the string representation of the number. */
    public String toString(){
        return number + "";
    }
    
    @Override
    public HashSet<String> getVariables() {
        return new HashSet<>();
    }
}
