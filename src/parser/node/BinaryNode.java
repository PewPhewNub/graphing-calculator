package parser.node;

import java.util.HashSet;
import java.util.Map;

import parser.EvaluationContext;

public class BinaryNode extends ExpressionNode{
    private final ExpressionNode left;
    private final ExpressionNode right;
    private final BinaryOp operator;

    public BinaryNode(final ExpressionNode left, final BinaryOp operator, final ExpressionNode right){
        this.left = left; this.right = right; this.operator = operator;
    }

    public double evaluate(EvaluationContext context){
        final double leftValue = left.evaluate(context);
        final double rightValue = right.evaluate(context);
        switch (operator) {
            case PLUS:
                return leftValue + rightValue;
            case SUBTRACT:
                return leftValue - rightValue;
            case MULTIPLY:
                return leftValue * rightValue;
            case DIVIDE:
                return leftValue / rightValue;
            case POWER:
                return Math.pow(leftValue, rightValue);
            default:
                return Double.NaN;
        }
    }

    public String toString(){
        return "( " + left.toString() + operator + right.toString() + ") ";
    }

    @Override
    public HashSet<String> getVariables() {
        HashSet<String> variables = (HashSet<String>) left.getVariables();
        variables.addAll(right.getVariables());
        return variables;
    }
}
