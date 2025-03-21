package compiler.parser.AST;

/**
 * Represents a variable reference (ID or ID[expr]) in the AST
 */
public class VarExpressionNode extends ExpressionNode {
    private String name;
    private ExpressionNode indexExpr;

    public VarExpressionNode(int lineNum, String name) {
        super(lineNum);
        this.name = name;
        this.indexExpr = null;
    }

    public VarExpressionNode(int lineNum, String name, ExpressionNode indexExpr) {
        super(lineNum);
        this.name = name;
        this.indexExpr = indexExpr;
    }

    public boolean isArray() {
        return indexExpr != null;
    }

    @Override
    public void printTree(StringBuilder sb, String indent) {
        sb.append(indent).append("Variable: ").append(name);
        sb.append(" [line: ").append(lineNum).append("]\n");

        if (indexExpr != null) {
            sb.append(indent).append("  Index:\n");
            indexExpr.printTree(sb, indent + "    ");
        }
    }
}