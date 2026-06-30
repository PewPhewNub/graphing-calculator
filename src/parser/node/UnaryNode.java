package parser.node;

import java.util.HashSet;
import java.util.Map;

import parser.EvaluationContext;
import parser.TokenType;

public class UnaryNode extends ExpressionNode{
    private final ExpressionNode right;
    private final TokenType operator;

    public UnaryNode(final TokenType operator, final ExpressionNode right){
        this.right = right; this.operator = operator;
    }

    public double evaluate(EvaluationContext context){
        final double rightValue = right.evaluate(context);
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
    
    @Override
    public HashSet<String> getVariables() {
        return right.getVariables();
    }
}
