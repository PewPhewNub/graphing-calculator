package core.parser.node;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    public double evaluate(Map<String, Double> map) {
        return expression.evaluate(map);
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
