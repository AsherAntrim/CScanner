package compiler.scanner;

// Represents a token in the source file
// Mainly used to help format the output output files so they looks nice lol
// Do not know if this is needed as he said in the main project requirements that is needs to be able to be read from easily for the next project

public class Token {
    private TokenType type;
    private String lexeme;
    private int lineNo;

    public Token(TokenType type, String lexeme, int lineNo) {
        // Type of the token from the TokenType enum
        this.type = type;
        // String value of the token
        this.lexeme = lexeme;
        // Line number where the token was found
        this.lineNo = lineNo;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLineNo() {
        return lineNo;
    }

    // Puts the token into a string representation of the token
    @Override
    public String toString() {
        return String.format("Line %d: Type: %s, Lexeme: \"%s\"",
                lineNo, type, lexeme);
    }
}
