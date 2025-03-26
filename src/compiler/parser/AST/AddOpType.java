package compiler.parser.AST;

/**
 * Enumeration for additive operators
 */
public enum AddOpType {
    PLUS("+"),
    MINUS("-");

    private final String text;

    AddOpType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}