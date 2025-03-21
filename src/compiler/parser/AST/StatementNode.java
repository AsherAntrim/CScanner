package compiler.parser.AST;

/**
 * Base class for all statement nodes in the AST
 */
public abstract class StatementNode extends AbstractNode {
    public StatementNode(int lineNum) {
        super(lineNum);
    }
}