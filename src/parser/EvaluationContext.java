package parser;

import java.util.HashMap;
import java.util.Map;

public class EvaluationContext {
    private final Map<String, Double> values;

    public EvaluationContext(Map<String, Double> values){
        this.values = values;
    }

    public double get(String name){
        return values.getOrDefault(name, 0.0);
    }

    public void set(String name, double value){
        values.put(name, value);
    }
    
    public EvaluationContext copy(){
        return new EvaluationContext(new HashMap<>(values));
    }
}
