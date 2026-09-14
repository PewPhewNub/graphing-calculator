package parser;

/**
 * Exception thrown when a mathematical expression cannot be successfully
 * analyzed or parsed due to lexical errors, syntax errors, or semantic violations.
 */
public class ParseException extends Exception {
    
    /**
     * Constructs a new ParseException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the failure
     */
    public ParseException(String message){
        super(message);
    }
}
