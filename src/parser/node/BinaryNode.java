package parser.node;

import java.util.HashSet;

import parser.EvaluationContext;

/**
 * Represents a binary mathematical expression in the expression tree.
 *
 * <p>A binary expression consists of a left operand, a binary operator,
 * and a right operand. Examples include addition, subtraction,
 * multiplication, division, and exponentiation.</p>
 *
 * <p>For example, the expression {@code x + 2} is represented by a
 * {@code BinaryNode} whose left operand is a {@code VariableNode},
 * whose operator is {@code PLUS}, and whose right operand is a
 * {@code NumberNode}.</p>
 */
public class BinaryNode extends ExpressionNode {

    /** The left operand expression. */
    private final ExpressionNode left;
    
    /** The right operand expression. */
    private final ExpressionNode right;
    
    /** The binary operator applied to the operands. */
    private final BinaryOp operator;

    /**
     * Creates a binary expression node.
     *
     * @param left the left operand of the expression
     * @param operator the binary operator to apply
     * @param right the right operand of the expression
     */
    public BinaryNode(
            final ExpressionNode left,
            final BinaryOp operator,
            final ExpressionNode right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    /**
     * Evaluates both operands and applies the stored binary operator.
     *
     * @param context the evaluation context containing the values
     *                required to evaluate the expression
     * @return the result of applying the operator to the evaluated
     *         left and right operands, or {@code Double.NaN} if invalid
     */
    @Override
    public double evaluate(EvaluationContext context) {
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

    /**
     * Returns a string representation of this binary expression.
     *
     * @return a parenthesized representation containing both operands
     *         and the operator
     */
    @Override
    public String toString() {
        return "( " + left.toString() + operator + right.toString() + ") ";
    }

    /**
     * Returns all variables referenced by either operand.
     *
     * @return a set containing the variables used by the expression
     */
    @Override
    public HashSet<String> getVariables() {
        @SuppressWarnings("unchecked")
        HashSet<String> variables = (HashSet<String>) left.getVariables();
        variables.addAll(right.getVariables());
        return variables;
    }
}
