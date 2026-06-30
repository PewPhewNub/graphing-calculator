package parser.node;

import java.util.HashSet;

import parser.EvaluationContext;

public abstract class ExpressionNode {
    public abstract double evaluate(EvaluationContext context);
    public abstract String toString();
    public abstract HashSet<String> getVariables();
}
