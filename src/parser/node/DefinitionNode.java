package parser.node;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import parser.EvaluationContext;

public class DefinitionNode extends ExpressionNode{
    private final ExpressionNode expression;
    private final Set<String> params;
    private final String dependentVariable;

    public DefinitionNode(ExpressionNode expression, String dependentVariable, Set<String> knownVariables){
        this.expression = expression;
        this.dependentVariable = dependentVariable;
        Set<String> newParams = getVariables();
        newParams.removeAll(knownVariables);
        newParams.remove(dependentVariable);
        params = newParams;
    }

    @Override
    public double evaluate(EvaluationContext context) {
        return expression.evaluate(context);
    }

    @Override
    public String toString() {
        return expression.toString();
    }

    public String getName() {
        return dependentVariable;
    }

    public HashSet<String> getVariables() {
        return expression.getVariables();
    }

    public Set<String> getParams() {
        return params;
    }
}
