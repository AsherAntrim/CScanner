package compiler.parser.AST;

/**
 * Represents a variable declaration in the AST
 */
public class VarDeclarationNode extends DeclarationNode {
    private boolean isArray;
    private Integer arraySize;

    public VarDeclarationNode(int lineNum, String name, TypeSpecifier type) {
        super(lineNum, name, type);
        this.isArray = false;
        this.arraySize = null;
    }

    public VarDeclarationNode(int lineNum, String name, TypeSpecifier type, int arraySize) {
        super(lineNum, name, type);
        this.isArray = true;
        this.arraySize = arraySize;
    }

    public boolean isArray() {
        return isArray;
    }

    public Integer getArraySize() {
        return arraySize;
    }

    @Override
    public void printTree(StringBuilder sb, String indent) {
        sb.append(indent).append("Variable: ").append(name);

        if (isArray) {
            sb.append("[").append(arraySize).append("]");
        }

        sb.append(" of type ").append(type);
        sb.append(" [line: ").append(lineNum).append("]\n");
    }
}