package core.parser.node;

import core.parser.TokenType;

public class BinaryNode extends ExpressionNode{
    private final ExpressionNode left;
    private final ExpressionNode right;
    private final BinaryOp operator;

    public BinaryNode(final ExpressionNode left, final BinaryOp operator, final ExpressionNode right){
        this.left = left; this.right = right; this.operator = operator;
    }

    public double evaluate(double x){
        final double leftValue = left.evaluate(x);
        final double rightValue = right.evaluate(x);
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
}
