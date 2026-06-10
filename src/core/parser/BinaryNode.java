package core.parser;

public class BinaryNode extends ExpressionNode{
    private final ExpressionNode left;
    private final ExpressionNode right;
    private final TokenType operator;

    public BinaryNode(final ExpressionNode left, final TokenType operator, final ExpressionNode right){
        this.left = left; this.right = right; this.operator = operator;
    }

    public double evaluate(double x){
        final double leftValue = left.evaluate(x);
        final double rightValue = right.evaluate(x);
        switch (operator) {
            case PLUS:
                return leftValue + rightValue;
            case MINUS:
                return leftValue - rightValue;
            case STAR:
                return leftValue * rightValue;
            case SLASH:
                return leftValue / rightValue;
            case POW:
                return Math.pow(leftValue, rightValue);
            default:
                return Double.NaN;
        }
    }

    public String toString(){
        return "( " + left.toString() + operator + right.toString() + ") ";
    }
}
