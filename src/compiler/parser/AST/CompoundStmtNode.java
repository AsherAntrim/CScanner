package compiler.parser.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a compound statement (block) in the AST
 */
public class CompoundStmtNode extends StatementNode {
    private List<VarDeclarationNode> localDeclarations;
    private List<StatementNode> statements;

    public CompoundStmtNode(int lineNum) {
        super(lineNum);
        this.localDeclarations = new ArrayList<>();
        this.statements = new ArrayList<>();
    }

    public void addLocalDeclaration(VarDeclarationNode declaration) {
        localDeclarations.add(declaration);
    }

    public void addStatement(StatementNode statement) {
        statements.add(statement);
    }

    @Override
    public void printTree(StringBuilder sb, String indent) {
        sb.append(indent).append("Compound Statement [line: ").append(lineNum).append("]\n");

        if (!localDeclarations.isEmpty()) {
            sb.append(indent).append("  Local Declarations:\n");
            for (VarDeclarationNode decl : localDeclarations) {
                decl.printTree(sb, indent + "    ");
            }
        }

        if (!statements.isEmpty()) {
            sb.append(indent).append("  Statements:\n");
            for (StatementNode stmt : statements) {
                stmt.printTree(sb, indent + "    ");
            }
        }
    }
}