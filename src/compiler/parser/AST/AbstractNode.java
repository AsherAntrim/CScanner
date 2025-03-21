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
     * Print this node and its children to the provided StringBuilder.
     * 
     * @param sb     StringBuilder to append the tree representation
     * @param indent Current indentation level
     */
    public abstract void printTree(StringBuilder sb, String indent);
}
