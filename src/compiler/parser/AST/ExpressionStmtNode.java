package compiler.parser.AST;

/**
 * Represents an expression statement in the AST
 */
public class ExpressionStmtNode extends StatementNode {
    private ExpressionNode expression;

    public ExpressionStmtNode(int lineNum) {
        super(lineNum);
        this.expression = null; // For empty statements (semicolon only)
    }

    public ExpressionStmtNode(int lineNum, ExpressionNode expression) {
        super(lineNum);
        this.expression = expression;
    }

    @Override
    public void printTree(StringBuilder sb, String indent) {
        if (expression == null) {
            sb.append(indent).append("Empty Statement [line: ").append(lineNum).append("]\n");
        } else {
            sb.append(indent).append("Expression Statement [line: ").append(lineNum).append("]\n");
            expression.printTree(sb, indent + "  ");
        }
    }
}