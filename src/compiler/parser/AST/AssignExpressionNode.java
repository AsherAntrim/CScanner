package compiler.parser.AST;

/**
 * Represents an assignment expression (var = expr) in the AST
 */
public class AssignExpressionNode extends ExpressionNode {
    private VarExpressionNode variable;
    private ExpressionNode expression;

    public AssignExpressionNode(int lineNum, VarExpressionNode variable, ExpressionNode expression) {
        super(lineNum);
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public void printTree(StringBuilder sb, String indent) {
        sb.append(indent).append("Assign [line: ").append(lineNum).append("]\n");

        sb.append(indent).append("  Left:\n");
        variable.printTree(sb, indent + "    ");

        sb.append(indent).append("  Right:\n");
        expression.printTree(sb, indent + "    ");
    }
}