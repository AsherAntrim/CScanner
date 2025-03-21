package compiler.parser.AST;

/**
 * Represents a term (factor or factor * factor) in the AST
 */
public class TermNode extends ExpressionNode {
    private ExpressionNode leftFactor;
    private MulOpType operator;
    private ExpressionNode rightFactor;

    // For a single factor
    public TermNode(int lineNum, ExpressionNode factor) {
        super(lineNum);
        this.leftFactor = factor;
        this.operator = null;
        this.rightFactor = null;
    }

    // For multiplication/division
    public TermNode(int lineNum, ExpressionNode leftFactor,
            MulOpType operator, ExpressionNode rightFactor) {
        super(lineNum);
        this.leftFactor = leftFactor;
        this.operator = operator;
        this.rightFactor = rightFactor;
    }

    @Override
    public void printTree(StringBuilder sb, String indent) {
        if (operator == null) {
            // Just the factor
            leftFactor.printTree(sb, indent);
        } else {
            // Multiplication/division
            sb.append(indent).append("Operator: ").append(operator);
            sb.append(" [line: ").append(lineNum).append("]\n");

            sb.append(indent).append("  Left:\n");
            leftFactor.printTree(sb, indent + "    ");

            sb.append(indent).append("  Right:\n");
            rightFactor.printTree(sb, indent + "    ");
        }
    }
}