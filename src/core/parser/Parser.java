package core.parser;

import java.io.EOFException;
import java.util.ArrayList;

import core.parser.node.BinaryNode;
import core.parser.node.BinaryOp;
import core.parser.node.DefinitionNode;
import core.parser.node.ExpressionNode;
import core.parser.node.FunctionNode;
import core.parser.node.NumberNode;
import core.parser.node.UnaryNode;
import core.parser.node.VariableNode;

public class Parser{
    private ArrayList<Token> tokenList;
    int position;
    
    public Parser(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
        this.position = 0;
    }

    private Token advance(){
        return tokenList.get(position++);
    }
    private Token peek(){
        return tokenList.get(position);
    }
    private boolean isAtEnd(){
        return peek().type == TokenType.EOF;
    }
    private boolean match(TokenType type){
        if(peek().type != type) return false; 
        advance();
        return true;
    }
    private boolean consume(TokenType type) throws Exception{
        if(peek().type != type) throw new Exception("SYNTAX ERROR"); 
        advance();
        return true;
    }

    private boolean startExpression(Token token){
        return token.type == TokenType.LPAREN || token.type == TokenType.NUMBER || token.type == TokenType.IDENTIFIER || token.type == TokenType.STAR || token.type == TokenType.SLASH;
    }

    private ExpressionNode parsePrimary() throws Exception{
        if(peek().type == TokenType.NUMBER) return new NumberNode(Double.parseDouble(advance().value));
        else if(peek().type == TokenType.IDENTIFIER){
            Token current = advance();
            if(match(TokenType.LPAREN)){
                ExpressionNode node = new FunctionNode(current.value, parseExpression());
                if(!consume(TokenType.RPAREN)) throw new Exception("SYNTAX ERROR");
                return node;
            }
            return new VariableNode(current.value);
        }else if(peek().type == TokenType.LPAREN){
            advance();
            ExpressionNode node = parseExpression();
            if(!consume(TokenType.RPAREN)) throw new Exception("SYNTAX ERROR");
            return node;
        }
        
        throw new Exception("ILLEGAL ARGUMENT");
    }

    private ExpressionNode parseUnary() throws Exception{
        if(peek().type == TokenType.PLUS){
            advance();
            return new UnaryNode(TokenType.PLUS, parsePrimary());
        }else if(peek().type == TokenType.MINUS){
            advance();
            return new UnaryNode(TokenType.MINUS, parsePrimary());
        }else 
            return parsePrimary();
    }

    private ExpressionNode parsePower() throws Exception{
        ExpressionNode left = parseUnary();
        if(match(TokenType.POW)){
            ExpressionNode right = parsePower();
            return new BinaryNode(left, BinaryOp.POWER, right);
        }
        return left;
    }

    private ExpressionNode parseTerm() throws Exception{
        ExpressionNode left = parsePower();
        while(startExpression(peek())){
            TokenType type = peek().type;
            if(type == TokenType.SLASH){
                advance();
                ExpressionNode right = parsePower();
                left = new BinaryNode(left, BinaryOp.DIVIDE, right);
            }else if (type == TokenType.STAR){
                advance();
                ExpressionNode right = parsePower();
                left = new BinaryNode(left, BinaryOp.MULTIPLY, right);
            }else{
                ExpressionNode right = parsePower();
                left = new BinaryNode(left, BinaryOp.MULTIPLY, right);
            }
        }
        return left;
    }

    public ExpressionNode parseExpression() throws Exception{
        ExpressionNode left = parseTerm();
        while(peek().type == TokenType.PLUS || peek().type == TokenType.MINUS){
            TokenType type = advance().type;
            ExpressionNode right = parseTerm();
            left =  new BinaryNode(left, (type == TokenType.PLUS) ? BinaryOp.PLUS : BinaryOp.SUBTRACT, right);
        }
        return left;
    }

    public DefinitionNode parseDefinitionFunction() throws Exception {

        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("Expected identifier at start of definition");
        }

        String name = advance().value;

        if (peek().type != TokenType.ASSIGN) {
            throw new RuntimeException("Expected '=' after identifier");
        }

        advance(); // consume '='

        ExpressionNode expr = parseExpression();

        if (name.equals("y")) {
            return new DefinitionNode("y", "x", expr);
        }

        return new DefinitionNode(name, "x", expr);
    }

    public DefinitionNode parseDefinitionPolar() throws Exception {

        if (peek().type != TokenType.IDENTIFIER) {
            throw new RuntimeException("Expected identifier at start of definition");
        }

        String name = advance().value;

        if (peek().type != TokenType.ASSIGN) {
            throw new RuntimeException("Expected '=' after identifier");
        }

        advance(); // consume '='

        ExpressionNode expr = parseExpression();

        if (name.equals("r")) {
            return new DefinitionNode("r", "x", expr);
        }

        return new DefinitionNode(name, "x", expr);
    }
}