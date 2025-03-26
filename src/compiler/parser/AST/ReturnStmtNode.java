package compiler.parser.AST;

/**
 * Represents a return statement in the AST
 */
public class ReturnStmtNode extends StatementNode {
    private ExpressionNode expression;

    public ReturnStmtNode(int lineNum) {
        super(lineNum);
        this.expression = null;
    }

    public ReturnStmtNode(int lineNum, ExpressionNode expression) {
        super(lineNum);
        this.expression = expression;
    }

    @Override
    public void printTree(StringBuilder sb, String indent) {
        sb.append(indent).append("Return Statement [line: ").append(lineNum).append("]\n");

        if (expression != null) {
            sb.append(indent).append("  Expression:\n");
            expression.printTree(sb, indent + "    ");
        }
    }
}