package parser;

/**
 * Categorizes the lexical tokens produced by the {@link Lexer}.
 * These token types correspond to basic mathematical elements (numbers, variables, operators)
 * and structural components of expressions.
 */
public enum TokenType {
    /** Represents numeric constants and decimal literals (e.g., 3.14, .5). */
    NUMBER,
    
    /** Represents variables or function names (e.g., x, y, sin, cos). */
    IDENTIFIER,
    
    /** Represent general operations. */
    OPERATOR,
    
    /** Represents the addition operator (+). */
    PLUS, 
    
    /** Represents the subtraction/negation operator (-). */
    MINUS,
    
    /** Represents the multiplication operator (*). */
    STAR, 
    
    /** Represents the division operator (/). */
    SLASH,
    
    /** Represents the exponentiation operator (^). */
    POW,
    
    /** Represents an opening parenthesis ((). */
    LPAREN, 
    
    /** Represents a closing parenthesis ()). */
    RPAREN,
    
    /** Represents a comma separator (,). */
    COMMA, 
    
    /** Represents a decimal point separator (.). */
    POINT,
    
    /** Represents an assignment or equality operator (=). */
    ASSIGN,
    
    /** Represents the end of the input stream. */
    EOF,
    
    /** Represents a fallback/tokenization error token. */
    ERROR
}
