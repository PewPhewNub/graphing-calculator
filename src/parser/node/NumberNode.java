package parser.node;

import java.util.HashSet;
import java.util.Map;

import parser.EvaluationContext;

public class NumberNode extends ExpressionNode {
    private final double number;

    public NumberNode(double number){
        this.number = number;
    }

    public double evaluate(EvaluationContext context){
        return number;
    }
    public String toString(){
        return number + "";
    }
    
    @Override
    public HashSet<String> getVariables() {
        return new HashSet<>();
    }
}
