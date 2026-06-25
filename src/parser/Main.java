package parser;

import parser.node.ExpressionNode;

public class Main {
    public static void main(String[] args){
        Lexer lexer = new Lexer("x");
        try{
            lexer.tokenize();
            System.out.println(lexer.tokenList.toString());
            Parser parser = new Parser(lexer.tokenList);
            ExpressionNode node = (parser.parseExpression());
            System.out.println(node.getVariables());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
