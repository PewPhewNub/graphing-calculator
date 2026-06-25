package parser;

import java.util.ArrayList;

public class Lexer {
    public String inputString;
    public int position;
    public ArrayList<Token> tokenList;

    public Lexer(String inputString){
        this.inputString = inputString;
        position = 0;
        this.tokenList = new ArrayList<>();
    }

    public void tokenize() throws Exception{
        while(!isAtEnd()){
            skipWhitespace();
            if (isAtEnd()) break;
            if(isDigit(peek()) || peek() == '.'){
                Token token = readNumber();
                if(token.type == TokenType.ERROR){
                    throw new Exception("ERROR");
                }
                tokenList.add(token);
            }else if(isLetter(peek())){
                Token token = readIdentifier();
                if(token.type == TokenType.ERROR){
                    throw new Exception("ERROR");
                }
                tokenList.add(token);
            }else{
                Token token = readOperator();
                if(token.type == TokenType.ERROR){
                    throw new Exception("ERROR");
                }
                tokenList.add(token);
            }
        }
        tokenList.add(new Token(TokenType.EOF, "EOF", position));
    }

    private char advance(){
        return inputString.charAt(position++);
    }
    private char peek(){
        if (isAtEnd()) return '\0';
        return inputString.charAt(position);
    }
    private boolean isAtEnd(){
        return position >= inputString.length();
    }
    private boolean isDigit(char c){
        return Character.isDigit(c);
    }
    private boolean isLetter(char c){
        return Character.isLetter(c);
    }
    private boolean isPoint(char c){
        return c == '.';
    }

    public Token readNumber(){
        int startingIndex = position;
        String string = "";
        boolean isDecimal = false;
        while (!isAtEnd() && (isDigit(peek()) || isPoint(peek()))) {
            char c = advance();
            if (isPoint(c)) {
                if (isDecimal) break;
                isDecimal = true;
            }
            string = string + c;
        }
        return new Token(TokenType.NUMBER,
                string,
                startingIndex);
    }

    public Token readIdentifier(){
        if(!isLetter(peek())) return new Token(TokenType.ERROR, "ERROR", position);
        int startingIndex = position;
        String string = "";
        while (!isAtEnd() && (isDigit(peek()) || isLetter(peek()))) {
            char c = advance();
            string = string + c;
        }
        return new Token(TokenType.IDENTIFIER,
                string,
                startingIndex);
    }

    public void skipWhitespace(){
        while(!isAtEnd() && (peek() == ' ' || peek() == '\t' || peek() == '\n'))
            advance();
    }

    public Token readOperator(){
        if(!isAtEnd() && (isLetter(peek()) || isDigit(peek()))) return new Token(TokenType.ERROR, "ERROR", position);
        int startingIndex = position;
        char c = advance();
        TokenType type = switch (c) {
        case '+' -> TokenType.PLUS;
        case '-' -> TokenType.MINUS;
        case '*' -> TokenType.STAR;
        case '/' -> TokenType.SLASH;
        case '^' -> TokenType.POW;
        case '(' -> TokenType.LPAREN;
        case ')' -> TokenType.RPAREN;
        case '=' -> TokenType.ASSIGN;
        case ',' -> TokenType.COMMA;
        default -> TokenType.ERROR;
        };
        return new Token(type,
                null,
                startingIndex);
    }
}
