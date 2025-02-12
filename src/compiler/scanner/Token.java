package compiler.scanner;

/**
 * Represents a single token identified during lexical analysis of C- source
 * code. Each token contains its type classification, the value, and line
 * number.
 *
 * @author Asher Antrim & Ethan Emery
 * @version 1.0
 *          File: Token.java
 *          Created: 31-Jan-2025
 *          ©Copyright Cedarville University, its Computer Science faculty, and
 *          the authors. All rights reserved.
 *
 *          Description: This class describes individual tokens found during the
 *          scanning process. Each token stores its classification, the value
 *          found in the source code, and its line number. The class provides
 *          access to these properties through getter methods and includes a
 *          toString method for debugging and display purposes.
 */

public class Token {
    private TokenType type;
    private String value;
    private int lineNo;

    public Token(TokenType type, String value, int lineNo) {
        // Type of the token from the TokenType enum
        this.type = type;
        // String value of the token
        this.value = value;
        // Line number where the token was found
        this.lineNo = lineNo;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLineNo() {
        return lineNo;
    }

    // Puts the token into a string representation of the token
    @Override
    public String toString() {
        return String.format("Line %d: Type: %s, Value: \"%s\"",
                lineNo, type, value);
    }
}
