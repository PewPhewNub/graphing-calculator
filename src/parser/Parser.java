package parser;

import java.util.ArrayList;
import java.util.Set;

import parser.node.BinaryNode;
import parser.node.BinaryOp;
import parser.node.DefinitionNode;
import parser.node.ExpressionNode;
import parser.node.FunctionNode;
import parser.node.NumberNode;
import parser.node.UnaryNode;
import parser.node.VariableNode;

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
    private boolean match(TokenType type){
        if(peek().type != type) return false; 
        advance();
        return true;
    }
    private void consume(TokenType type) throws ParseException{
        if(peek().type != type){
            String character = 
                switch(type){
                    case ASSIGN -> "=";
                    case LPAREN -> "(";
                    case RPAREN -> ")";
                    case PLUS -> "+";
                    case MINUS -> "-";
                    case STAR -> "*";
                    case SLASH -> "/";
                    case POW -> "^";
                    case IDENTIFIER -> "identifier";
                    case OPERATOR -> "operator";
                    case NUMBER -> "number";
                    case POINT -> ".";
                    case COMMA -> ",";
                    default -> throw new ParseException("Syntax Error");
                };
                throw new ParseException("Expected " + character);
        } 
        advance();
    }

    private boolean startExpression(Token token){
        return token.type == TokenType.LPAREN || token.type == TokenType.NUMBER || token.type == TokenType.IDENTIFIER || token.type == TokenType.STAR || token.type == TokenType.SLASH;
    }

    private ExpressionNode parsePrimary() throws ParseException{
        if(peek().type == TokenType.NUMBER){
            String text = advance().value;
            if(text.trim().equals(".") || text.trim().equals("0.")) throw new ParseException("Expected number after .");
            return new NumberNode(Double.parseDouble(text));
        }
        else if(peek().type == TokenType.IDENTIFIER){
            Token current = advance();
            if(match(TokenType.LPAREN)){
                ExpressionNode node = new FunctionNode(current.value, parseExpression());
                consume(TokenType.RPAREN);
                return node;
            }
            return new VariableNode(current.value);
        }else if(peek().type == TokenType.LPAREN){
            advance();
            ExpressionNode node = parseExpression();
            consume(TokenType.RPAREN);
            return node;
        }
        
        throw new ParseException("Expected Expression");
    }

    private ExpressionNode parseUnary() throws ParseException{
        if(peek().type == TokenType.PLUS){
            advance();
            return new UnaryNode(TokenType.PLUS, parsePrimary());
        }else if(peek().type == TokenType.MINUS){
            advance();
            return new UnaryNode(TokenType.MINUS, parsePrimary());
        }else 
            return parsePrimary();
    }

    private ExpressionNode parsePower() throws ParseException{
        ExpressionNode left = parseUnary();
        if(match(TokenType.POW)){
            ExpressionNode right = parsePower();
            return new BinaryNode(left, BinaryOp.POWER, right);
        }
        return left;
    }

    private ExpressionNode parseTerm() throws ParseException{
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

    public ExpressionNode parseExpression() throws ParseException{
        ExpressionNode left = parseTerm();
        while(peek().type == TokenType.PLUS || peek().type == TokenType.MINUS){
            TokenType type = advance().type;
            ExpressionNode right = parseTerm();
            left =  new BinaryNode(left, (type == TokenType.PLUS) ? BinaryOp.PLUS : BinaryOp.SUBTRACT, right);
        }
        return left;
    }

    public DefinitionNode parseDefinition(String dependentVariable, Set<String> knownVariables) throws ParseException {
        ExpressionNode expr = parseExpression();
        if(peek().type == TokenType.ASSIGN){
            advance();
            ExpressionNode expr2 = parseExpression();
            return new DefinitionNode(new BinaryNode(expr, BinaryOp.SUBTRACT, expr2), dependentVariable, knownVariables);
        }
        return new DefinitionNode(expr, dependentVariable, knownVariables);
    } 
}