package parser.node;

import java.util.HashSet;
import java.util.Map;

public class VariableNode extends ExpressionNode {
    public String name;

    public VariableNode(String name){
        this.name = name;
    }

    public double evaluate(double x){
        if(name.equals("pi"))
            return Math.PI;
        if(name.equals("e"))
            return Math.E;
        return x;
    }

    public double evaluate(Map<String, Double> variables){
        return variables.get(name);
    }

    public String toString(){
        return name;
    }
    public String getName() {
        return name;
    }
    
    @Override
    public HashSet<String> getVariables() {
        return new HashSet<String>() {
            {
                add(name);
            }
        };
    }
}
