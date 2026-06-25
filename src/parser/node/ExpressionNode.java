package parser.node;

import java.util.HashSet;
import java.util.Map;

public abstract class ExpressionNode {
    public abstract double evaluate(Map<String, Double> map);
    public abstract String toString();
    public abstract HashSet<String> getVariables();
}
