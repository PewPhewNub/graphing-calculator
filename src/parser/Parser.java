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

/**
 * Recursive descent parser that converts a list of {@link Token} objects
 * into an Abstract Syntax Tree (AST) composed of {@link ExpressionNode}s.
 * 
 * <p>The parser enforces operator precedence and handles syntax validation.</p>
 */
public class Parser {
    /** The list of tokens to parse. */
    private ArrayList<Token> tokenList;
    
    /** Current position in the token list. */
    int position;
    
    /**
     * Constructs a new Parser for the given list of tokens.
     *
     * @param tokenList the list of tokens from the {@link Lexer}
     */
    public Parser(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
        this.position = 0;
    }

    /** Returns the current token and advances the position. */
    private Token advance(){
        return tokenList.get(position++);
    }
    
    /** Peeks at the current token without advancing. */
    private Token peek(){
        return tokenList.get(position);
    }
    
    /** Checks if the current token matches the given type. */
    private boolean match(TokenType type){
        if(peek().type != type) return false; 
        advance();
        return true;
    }
    
    /** 
     * Consumes the current token if it matches the expected type.
     * 
     * @throws ParseException if the current token does not match the expected type
     */
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

    /** Returns true if the token could start an expression term. */
    private boolean startExpression(Token token){
        return token.type == TokenType.LPAREN || token.type == TokenType.NUMBER || token.type == TokenType.IDENTIFIER || token.type == TokenType.STAR || token.type == TokenType.SLASH;
    }

    /** Parses a primary expression (number, variable, function call, or parenthesized expression). */
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

    /** Parses a unary expression (e.g., +x, -x). */
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

    /** Parses an exponentiation expression. */
    private ExpressionNode parsePower() throws ParseException{
        ExpressionNode left = parseUnary();
        if(match(TokenType.POW)){
            ExpressionNode right = parsePower();
            return new BinaryNode(left, BinaryOp.POWER, right);
        }
        return left;
    }

    /** Parses multiplicative expressions (multiplication, division). */
    private ExpressionNode parseTerm() throws ParseException{
        ExpressionNode left = parsePower();
        while(startExpression(peek())){
            TokenType type = peek().type;
            if(type == TokenType.SLASH){
                advance();
                ExpressionNode right = parsePower();
                left = new BinaryNode(left, BinaryOp.DIVIDE, right);
                if(right instanceof NumberNode n){
                    if(n.evaluate(null) == 0) throw new ParseException("Cannot divide by 0");
                }
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

    /** Parses additive expressions (addition, subtraction). */
    public ExpressionNode parseExpression() throws ParseException{
        ExpressionNode left = parseTerm();
        while(peek().type == TokenType.PLUS || peek().type == TokenType.MINUS){
            TokenType type = advance().type;
            ExpressionNode right = parseTerm();
            left =  new BinaryNode(left, (type == TokenType.PLUS) ? BinaryOp.PLUS : BinaryOp.SUBTRACT, right);
        }
        return left;
    }

    /**
     * Parses a definition (e.g., f(x) = ...).
     *
     * @param dependentVariable the variable being defined
     * @param knownVariables set of variables treated as parameters/known
     * @return a {@link DefinitionNode} representing the defined expression
     * @throws ParseException if syntax is invalid
     */
    public DefinitionNode parseDefinition(String dependentVariable, Set<String> knownVariables) throws ParseException {
        ExpressionNode expr = parseExpression();
        if(peek().type == TokenType.ASSIGN){
            advance();
            ExpressionNode expr2 = parseExpression();
            return new DefinitionNode(new BinaryNode(expr, BinaryOp.SUBTRACT, expr2), dependentVariable, knownVariables);
        }
        
        if (peek().type != TokenType.EOF) {
            throw new ParseException("Unexpected token: " + peek().value);
        }
        return new DefinitionNode(expr, dependentVariable, knownVariables);
    } 
}
