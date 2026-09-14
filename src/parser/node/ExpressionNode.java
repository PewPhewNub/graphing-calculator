package parser.node;

import java.util.HashSet;

import parser.EvaluationContext;

/**
 * Base class for all nodes in the Abstract Syntax Tree (AST).
 * Provides the interface for evaluating expressions, string representation,
 * and dependency tracking (variable identification).
 */
public abstract class ExpressionNode {
    /**
     * Evaluates this expression node within the given context.
     *
     * @param context the {@link EvaluationContext} containing variable values
     * @return the numerical result of the evaluation
     */
    public abstract double evaluate(EvaluationContext context);
    
    /**
     * Returns a string representation of this node.
     *
     * @return a human-readable representation of this expression node
     */
    public abstract String toString();
    
    /**
     * Retrieves all variables referenced within this expression tree.
     *
     * @return a {@link HashSet} containing the names of all referenced variables
     */
    public abstract HashSet<String> getVariables();
}
