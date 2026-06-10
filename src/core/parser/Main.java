package core.parser;

import java.util.function.Function;

public class Main {
    public static void main(String[] args){
        Lexer lexer = new Lexer("2x x x x");
        try{
            lexer.tokenize();
            System.out.println(lexer.tokenList.toString());
            Parser parser = new Parser(lexer.tokenList, 0);
            ExpressionNode node = (parser.parseExpression());
            Function<Double, Double> function = x -> node.evaluate(x);

            System.out.println(function.apply(2d));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
