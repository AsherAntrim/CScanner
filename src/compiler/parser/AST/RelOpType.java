package compiler.parser.AST;

/**
 * Enumeration for relational operators in C-
 */
public enum RelOpType {
    LTE("<="),
    LT("<"),
    GT(">"),
    GTE(">="),
    EQ("=="),
    NEQ("!=");

    private final String text;

    RelOpType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}