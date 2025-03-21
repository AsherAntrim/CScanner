package compiler.parser.AST;

/**
 * Base class for declaration nodes (variable or function)
 */
public abstract class DeclarationNode extends AbstractNode {
    protected String name;
    protected TypeSpecifier type;

    public DeclarationNode(int lineNum, String name, TypeSpecifier type) {
        super(lineNum);
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public TypeSpecifier getType() {
        return type;
    }
}