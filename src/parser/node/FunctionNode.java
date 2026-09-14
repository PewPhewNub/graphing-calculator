package parser.node;

import java.util.HashSet;

import parser.EvaluationContext;

/**
 * Represents a mathematical function call (e.g., sin(x), sqrt(y)).
 */
public class FunctionNode extends ExpressionNode {
    /** The name of the function (e.g., "sin"). */
    private final String functionName;
    
    /** The argument expression node. */
    private final ExpressionNode argument;
    
    /**
     * Constructs a new FunctionNode.
     *
     * @param functionName the name of the function
     * @param argument the expression node passed as an argument
     */
    public FunctionNode(String functionName, ExpressionNode argument) {
        this.functionName = functionName;
        this.argument = argument;
    }

    /**
     * Evaluates the argument and applies the corresponding mathematical function.
     *
     * @param context the {@link EvaluationContext}
     * @return the result of applying the function, or {@code Double.NaN} if unrecognized
     */
    public double evaluate(EvaluationContext context){
        double argumentValue = argument.evaluate(context);

        switch (functionName) {
            case "sin":
                return Math.sin(argumentValue);
            case "cos":
                return Math.cos(argumentValue);
            case "tan":
                return Math.tan(argumentValue);
            case "asin":
                return Math.asin(argumentValue);
            case "acos":
                return Math.acos(argumentValue);
            case "atan":
                return Math.atan(argumentValue);
            case "sgn":
                return Math.signum(argumentValue);
            case "exp":
                return Math.exp(argumentValue);
            case "ln":
                return Math.log(argumentValue);
            case "sqrt":
                return Math.sqrt(argumentValue);
            case "abs":
                return Math.abs(argumentValue);
            case "sinh":
                return Math.sinh(argumentValue);
            case "cosh":
                return Math.cosh(argumentValue);
            case "tanh":
                return Math.tanh(argumentValue);
            default:
                break;
        }
        return Double.NaN;
    }    

    /** Returns a string representation of the function call. */
    public String toString(){
        return " " + functionName + "(" + argument.toString() + ") ";
    }
    
    @Override
    public HashSet<String> getVariables() {
        return argument.getVariables();
    }
    
    /** Returns the name of the function. */
    public String getFunctionName() {
        return functionName;
    }
    
    /** Returns the argument node. */
    public ExpressionNode getArgument() {
        return argument;
    }
}
