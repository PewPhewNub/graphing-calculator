package core.parser;

public class UnaryNode extends ExpressionNode{
    private final ExpressionNode right;
    private final TokenType operator;

    public UnaryNode(final TokenType operator, final ExpressionNode right){
        this.right = right; this.operator = operator;
    }

    public double evaluate(double x){
        final double rightValue = right.evaluate(x);
        switch (operator) {
            case PLUS:
                return rightValue;
            case MINUS:
                return -rightValue;
            default:
                return Double.NaN;
        }
    }

    public String toString(){
        return operator + " " + right.toString();
    }
}
