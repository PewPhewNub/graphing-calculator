package core.parser;

public class NumberNode extends ExpressionNode {
    private final double number;

    public NumberNode(double number){
        this.number = number;
    }

    public double evaluate(double x){
        return number;
    }
    public String toString(){
        return number + "";
    }
}
