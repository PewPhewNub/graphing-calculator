package core.parser;

public class Token {
    public final TokenType type;
    public final String value;
    public final int position;
    public Token(TokenType type, String value, int position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }
    public String toString(){
        return type + ", " + value;
    }
}
