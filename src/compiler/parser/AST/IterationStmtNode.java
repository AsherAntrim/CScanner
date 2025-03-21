package compiler.parser.AST;

/**
 * Represents a while statement in the AST
 */
public class IterationStmtNode extends StatementNode {
    private ExpressionNode condition;
    private StatementNode body;

    public IterationStmtNode(int lineNum, ExpressionNode condition, StatementNode body) {
        super(lineNum);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void printTree(StringBuilder sb, String indent) {
        sb.append(indent).append("While Statement [line: ").append(lineNum).append("]\n");

        sb.append(indent).append("  Condition:\n");
        condition.printTree(sb, indent + "    ");

        sb.append(indent).append("  Body:\n");
        body.printTree(sb, indent + "    ");
    }
}