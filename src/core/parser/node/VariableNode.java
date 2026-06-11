package core.parser.node;

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
    public String toString(){
        return name;
    }
}
