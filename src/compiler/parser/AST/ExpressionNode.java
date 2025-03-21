package compiler.parser.AST;

/**
 * Base class for all expression nodes in the AST
 */
public abstract class ExpressionNode extends AbstractNode {
    public ExpressionNode(int lineNum) {
        super(lineNum);
    }
}