package compiler.parser.AST;

/**
 * Enumeration for type specifiers in C-
 */
public enum TypeSpecifier {
    INT("int"),
    VOID("void");

    private final String text;

    TypeSpecifier(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}