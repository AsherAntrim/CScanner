package compiler.parser.AST;

/**
 * Represents a simple expression that can include both additive and relational operations
 */
public class SimpleExpressionNode extends ExpressionNode {
    private ExpressionNode leftExpr;
    private RelOpType relOperator;  // For relational operations (<, >, etc.)
    private AddOpType addOperator;  // For additive operations (+, -)
    private ExpressionNode rightExpr;
    private boolean isRelational;   // Flag to determine type of expression

    // For a basic expression with no operators
    public SimpleExpressionNode(int lineNum, ExpressionNode expr) {
        super(lineNum);
        this.leftExpr = expr;
        this.relOperator = null;
        this.addOperator = null;
        this.rightExpr = null;
        this.isRelational = false;
    }

    // For a relational expression
    public SimpleExpressionNode(int lineNum, ExpressionNode leftExpr,
            RelOpType operator, ExpressionNode rightExpr) {
        super(lineNum);
        this.leftExpr = leftExpr;
        this.relOperator = operator;
        this.addOperator = null;
        this.rightExpr = rightExpr;
        this.isRelational = true;
    }

    // For an additive expression
    public SimpleExpressionNode(int lineNum, ExpressionNode leftExpr,
            AddOpType operator, ExpressionNode rightExpr) {
        super(lineNum);
        this.leftExpr = leftExpr;
        this.relOperator = null;
        this.addOperator = operator;
        this.rightExpr = rightExpr;
        this.isRelational = false;
    }

    @Override
    public void printTree(StringBuilder sb, String indent) {
        if (relOperator == null && addOperator == null) {
            // Just a simple expression
            leftExpr.printTree(sb, indent);
        } else if (isRelational) {
            // Relational expression
            sb.append(indent).append("Comparison: ").append(relOperator);
            sb.append(" [line: ").append(lineNum).append("]\n");
            
            sb.append(indent).append("  Left:\n");
            leftExpr.printTree(sb, indent + "    ");
            
            sb.append(indent).append("  Right:\n");
            rightExpr.printTree(sb, indent + "    ");
        } else {
            // Additive expression
            sb.append(indent).append("Operator: ").append(addOperator);
            sb.append(" [line: ").append(lineNum).append("]\n");
            
            sb.append(indent).append("  Left:\n");
            leftExpr.printTree(sb, indent + "    ");
            
            sb.append(indent).append("  Right:\n");
            rightExpr.printTree(sb, indent + "    ");
        }
    }
}