package compiler.parser.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * Root node of the AST representing a program.
 */
public class ProgramNode extends AbstractNode {
    private List<DeclarationNode> declarations;

    public ProgramNode(int lineNum) {
        super(lineNum);
        this.declarations = new ArrayList<>();
    }

    public void addDeclaration(DeclarationNode declaration) {
        declarations.add(declaration);
    }

    @Override
    public void printTree(StringBuilder sb, String indent) {
        sb.append(indent).append("Program [line: ").append(lineNum).append("]\n");
        for (DeclarationNode decl : declarations) {
            decl.printTree(sb, indent + "  ");
        }
    }
}