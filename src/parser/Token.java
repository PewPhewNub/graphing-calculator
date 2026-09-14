package parser;

/**
 * Represents a discrete lexical unit (token) identified during lexical analysis of an expression.
 * Each token consists of a category, its string value in the original source, and its character offset.
 */
public class Token {
    /** The category or type of this token. */
    public final TokenType type;
    
    /** The actual character string of the token as it appears in the input expression. */
    public final String value;
    
    /** The starting character index of this token in the original input string. */
    public final int position;

    /**
     * Constructs a Token with the given properties.
     *
     * @param type the category of the token
     * @param value the raw text content of the token
     * @param position the zero-based character offset where this token starts in the input
     */
    public Token(TokenType type, String value, int position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }

    /**
     * Returns a string representation of the Token for debugging and diagnostic messages.
     *
     * @return a description containing the token type and value
     */
    @Override
    public String toString(){
        return type + ", " + value;
    }
}
