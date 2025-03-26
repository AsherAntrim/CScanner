package compiler.parser.AST;

/**
 * Base class for all nodes in the Abstract Syntax Tree.
 */
public abstract class AbstractNode {
    protected int lineNum;

    public AbstractNode(int lineNum) {
        this.lineNum = lineNum;
    }

    public int getLineNum() {
        return lineNum;
    }

    /**
     * @param sb
     * @param indent
     */

    public abstract void printTree(StringBuilder sb, String indent);
}
