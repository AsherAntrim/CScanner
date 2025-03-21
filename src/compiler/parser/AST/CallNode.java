package compiler.parser.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a function call in the AST
 */
public class CallNode extends ExpressionNode {
    private String functionName;
    private List<ExpressionNode> arguments;

    public CallNode(int lineNum, String functionName) {
        super(lineNum);
        this.functionName = functionName;
        this.arguments = new ArrayList<>();
    }

    public void addArgument(ExpressionNode arg) {
        arguments.add(arg);
    }

    @Override
    public void printTree(StringBuilder sb, String indent) {
        sb.append(indent).append("Call to function: ").append(functionName);
        sb.append(" [line: ").append(lineNum).append("]\n");

        if (!arguments.isEmpty()) {
            sb.append(indent).append("  Arguments:\n");
            for (ExpressionNode arg : arguments) {
                arg.printTree(sb, indent + "    ");
            }
        }
    }
}