package compiler.parser.AST;

/**
 * Represents an additive expression in the AST
 */
public class AddExpressionNode extends ExpressionNode {
    private ExpressionNode leftExpr;
    private AddOpType operator;
    private ExpressionNode rightExpr;

    // For a single term
    public AddExpressionNode(int lineNum, ExpressionNode term) {
        super(lineNum);
        this.leftExpr = term;
        this.operator = null;
        this.rightExpr = null;
    }

    // For an addition/subtraction
    public AddExpressionNode(int lineNum, ExpressionNode leftExpr,
            AddOpType operator, ExpressionNode rightExpr) {
        super(lineNum);
        this.leftExpr = leftExpr;
        this.operator = operator;
        this.rightExpr = rightExpr;
    }

    @Override
    public void printTree(StringBuilder sb, String indent) {
        if (operator == null) {
            // Just the term
            leftExpr.printTree(sb, indent);
        } else {
            // Addition/subtraction
            sb.append(indent).append("Operator: ").append(operator);
            sb.append(" [line: ").append(lineNum).append("]\n");

            sb.append(indent).append("  Left:\n");
            leftExpr.printTree(sb, indent + "    ");

            sb.append(indent).append("  Right:\n");
            rightExpr.printTree(sb, indent + "    ");
        }
    }
}