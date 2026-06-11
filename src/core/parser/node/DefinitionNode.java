package core.parser.node;

import java.util.function.Function;

public class DefinitionNode extends ExpressionNode{
    private final ExpressionNode function;
    private final String name;
    private final String param;

    public DefinitionNode(String name, String param, ExpressionNode function){
        this.function = function;
        this.name = name;
        this.param = param;
    }

    @Override
    public double evaluate(double x) {
        return function.evaluate(x);
    }

    @Override
    public String toString() {
        return "f(x) = " + function.toString();
    }
    
    public Function<Double, Double> getFunction(){
        return x -> evaluate(x);
    }

    public String getName() {
        return name;
    }
}
