package compiler.parser.AST;

/**
 * Represents a simple expression with optional relational operation in the AST
 */
public class SimpleExpressionNode extends ExpressionNode {
    private ExpressionNode leftExpr;
    private RelOpType operator;
    private ExpressionNode rightExpr;

    // For a simple expression without relational operator
    public SimpleExpressionNode(int lineNum, ExpressionNode expr) {
        super(lineNum);
        this.leftExpr = expr;
        this.operator = null;
        this.rightExpr = null;
    }

    // For a relational expression
    public SimpleExpressionNode(int lineNum, ExpressionNode leftExpr,
            RelOpType operator, ExpressionNode rightExpr) {
        super(lineNum);
        this.leftExpr = leftExpr;
        this.operator = operator;
        this.rightExpr = rightExpr;
    }

    @Override
    public void printTree(StringBuilder sb, String indent) {
        if (operator == null) {
            // Just the additive expression
            leftExpr.printTree(sb, indent);
        } else {
            // Relational expression
            sb.append(indent).append("Comparison: ").append(operator);
            sb.append(" [line: ").append(lineNum).append("]\n");

            sb.append(indent).append("  Left:\n");
            leftExpr.printTree(sb, indent + "    ");

            sb.append(indent).append("  Right:\n");
            rightExpr.printTree(sb, indent + "    ");
        }
    }
}