package core.parser;

import java.util.function.Function;

import core.parser.node.DefinitionNode;
import core.parser.node.ExpressionNode;

public class Main {
    public static void main(String[] args){
        Lexer lexer = new Lexer("sqrt(x)");
        try{
            lexer.tokenize();
            System.out.println(lexer.tokenList.toString());
            Parser parser = new Parser(lexer.tokenList);
            DefinitionNode node = (parser.parseDefinitionFunction());

            System.out.println(node.getFunction().apply(2d));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
