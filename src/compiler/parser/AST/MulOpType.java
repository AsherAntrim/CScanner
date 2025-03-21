package compiler.parser.AST;

/**
 * Enumeration for multiplicative operators in C-
 */
public enum MulOpType {
    TIMES("*"),
    DIVIDE("/");

    private final String text;

    MulOpType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}