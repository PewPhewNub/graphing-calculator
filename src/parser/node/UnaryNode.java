package parser.node;

import java.util.HashSet;
import java.util.Map;

import parser.EvaluationContext;
import parser.TokenType;

/**
 * Represents a unary expression in the AST (e.g., +x, -x).
 */
public class UnaryNode extends ExpressionNode {
    /** The child expression node. */
    private final ExpressionNode right;
    
    /** The unary operator type. */
    private final TokenType operator;

    /**
     * Constructs a new UnaryNode.
     *
     * @param operator the unary operator type (e.g., PLUS, MINUS)
     * @param right the operand expression node
     */
    public UnaryNode(final TokenType operator, final ExpressionNode right){
        this.right = right; 
        this.operator = operator;
    }

    /**
     * Evaluates the operand and applies the unary operator.
     *
     * @param context the {@link EvaluationContext}
     * @return the result of the unary operation
     */
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

    /** Returns a string representation of the unary expression. */
    public String toString(){
        return operator + " " + right.toString();
    }
    
    @Override
    public HashSet<String> getVariables() {
        return right.getVariables();
    }
}
