package parser;

/**
 * Functional interface for evaluating mathematical expressions.
 * Any component that can resolve a mathematical formula to a numerical
 * value in a given variable evaluation context implements this interface.
 */
@FunctionalInterface
public interface ExpressionEvaluator {
    
    /**
     * Evaluates the expression within the provided variable context.
     *
     * @param context the {@link EvaluationContext} containing values for variables
     *                referenced in the expression
     * @return the double precision floating-point result of the evaluation
     */
    double evaluate(EvaluationContext context);
}
