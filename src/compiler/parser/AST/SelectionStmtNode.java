package compiler.parser.AST;

/**
 * Represents an if (if-else) statement in the AST
 */
public class SelectionStmtNode extends StatementNode {
    private ExpressionNode condition;
    private StatementNode thenBranch;
    private StatementNode elseBranch;

    public SelectionStmtNode(int lineNum, ExpressionNode condition, StatementNode thenBranch) {
        super(lineNum);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = null;
    }

    public SelectionStmtNode(int lineNum, ExpressionNode condition,
            StatementNode thenBranch, StatementNode elseBranch) {
        super(lineNum);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public void printTree(StringBuilder sb, String indent) {
        sb.append(indent).append("If Statement [line: ").append(lineNum).append("]\n");

        sb.append(indent).append("  Condition:\n");
        condition.printTree(sb, indent + "    ");

        sb.append(indent).append("  Then Branch:\n");
        thenBranch.printTree(sb, indent + "    ");

        if (elseBranch != null) {
            sb.append(indent).append("  Else Branch:\n");
            elseBranch.printTree(sb, indent + "    ");
        }
    }
}