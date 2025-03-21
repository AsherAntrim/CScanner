package compiler.parser.AST;

/**
 * Represents a numeric literal in the AST
 */
public class NumberNode extends ExpressionNode {
    private int value;

    public NumberNode(int lineNum, int value) {
        super(lineNum);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void printTree(StringBuilder sb, String indent) {
        sb.append(indent).append("Number: ").append(value);
        sb.append(" [line: ").append(lineNum).append("]\n");
    }
}