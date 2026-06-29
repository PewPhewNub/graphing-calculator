package parser.node;

import java.util.HashSet;
import java.util.Map;

public class FunctionNode extends ExpressionNode{
    private final String functionName;
    private final ExpressionNode argument;
    public FunctionNode(String functionName, ExpressionNode argument) {
        this.functionName = functionName;
        this.argument = argument;
    }

    public double evaluate(Map<String, Double> map){
        double argumentValue = argument.evaluate(map);

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

    public String toString(){
        return " " + functionName + "(" + argument.toString() + ") ";
    }
    @Override
    public HashSet<String> getVariables() {
        return argument.getVariables();
    }
    public String getFunctionName() {
        return functionName;
    }
    public ExpressionNode getArgument() {
        return argument;
    }
}
