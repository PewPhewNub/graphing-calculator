package parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds variable and constant bindings for mathematical expression evaluation.
 * Acts as a lookup registry for variable names and their associated numeric values
 * during the evaluation of an AST.
 */
public class EvaluationContext {
    /**
     * Internal map storing bindings of variable names to their current numeric values.
     */
    private final Map<String, Double> values;

    /**
     * Constructs an EvaluationContext initialized with a specific map of values.
     *
     * @param values the map containing initial variable-to-value bindings
     */
    public EvaluationContext(Map<String, Double> values){
        this.values = values;
    }

    /**
     * Retrieves the numeric value bound to the specified variable name.
     * If the variable is not defined in this context, a default value of {@code 0.0} is returned.
     *
     * @param name the name of the variable to look up
     * @return the value associated with the variable name, or {@code 0.0} if undefined
     */
    public double get(String name){
        return values.getOrDefault(name, 0.0);
    }

    /**
     * Binds or updates a variable name to a specific numeric value in this context.
     *
     * @param name the name of the variable to bind
     * @param value the numeric value to associate with the variable
     */
    public void set(String name, double value){
        values.put(name, value);
    }
    
    /**
     * Creates and returns a copy of this evaluation context with cloned state.
     * Modifications to the copied context will not affect the original.
     *
     * @return a new {@code EvaluationContext} containing a shallow copy of the current bindings
     */
    public EvaluationContext copy(){
        return new EvaluationContext(new HashMap<>(values));
    }
}
