package parser.node;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import parser.EvaluationContext;

/**
 * Represents a formal definition in the AST (e.g., f(x) = ...).
 * Stores the expression, the dependent variable, and the known parameters.
 */
public class DefinitionNode extends ExpressionNode {
    /** The expression being defined. */
    private final ExpressionNode expression;
    
    /** The set of parameters (variables not considered 'known'). */
    private final Set<String> params;
    
    /** The name of the dependent variable. */
    private final String dependentVariable;

    /**
     * Constructs a new DefinitionNode.
     *
     * @param expression the expression to be defined
     * @param dependentVariable the variable representing the function/result name
     * @param knownVariables the set of variables already known (parameters)
     */
    public DefinitionNode(ExpressionNode expression, String dependentVariable, Set<String> knownVariables){
        this.expression = expression;
        this.dependentVariable = dependentVariable;
        Set<String> newParams = getVariables();
        newParams.removeAll(knownVariables);
        newParams.remove(dependentVariable);
        params = newParams;
    }

    /**
     * Evaluates the definition's expression.
     *
     * @param context the {@link EvaluationContext}
     * @return the result of the expression
     */
    @Override
    public double evaluate(EvaluationContext context) {
        return expression.evaluate(context);
    }

    /** Returns the string representation of the expression. */
    @Override
    public String toString() {
        return expression.toString();
    }

    /** Returns the name of the dependent variable. */
    public String getName() {
        return dependentVariable;
    }

    @Override
    public HashSet<String> getVariables() {
        return expression.getVariables();
    }

    /** Returns the set of parameters associated with this definition. */
    public Set<String> getParams() {
        return params;
    }
}
