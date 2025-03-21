package compiler.parser.AST;

/**
 * Represents a function parameter in the AST
 */
public class ParamNode extends AbstractNode {
    private String name;
    private TypeSpecifier type;
    private boolean isArray;

    public ParamNode(int lineNum, String name, TypeSpecifier type, boolean isArray) {
        super(lineNum);
        this.name = name;
        this.type = type;
        this.isArray = isArray;
    }

    public String getName() {
        return name;
    }

    public TypeSpecifier getType() {
        return type;
    }

    public boolean isArray() {
        return isArray;
    }

    @Override
    public void printTree(StringBuilder sb, String indent) {
        sb.append(indent).append("Parameter: ").append(name);

        if (isArray) {
            sb.append("[]");
        }

        sb.append(" of type ").append(type);
        sb.append(" [line: ").append(lineNum).append("]\n");
    }
}