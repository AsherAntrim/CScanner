package compiler.parser.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a function declaration in the AST
 */
public class FunDeclarationNode extends DeclarationNode {
    private List<ParamNode> params;
    private CompoundStmtNode body;

    public FunDeclarationNode(int lineNum, String name, TypeSpecifier type) {
        super(lineNum, name, type);
        this.params = new ArrayList<>();
    }

    public void addParam(ParamNode param) {
        params.add(param);
    }

    public List<ParamNode> getParams() {
        return params;
    }

    public void setBody(CompoundStmtNode body) {
        this.body = body;
    }

    public CompoundStmtNode getBody() {
        return body;
    }

    @Override
    public void printTree(StringBuilder sb, String indent) {
        sb.append(indent).append("Function: ").append(name);
        sb.append(" returns ").append(type);
        sb.append(" [line: ").append(lineNum).append("]\n");
        sb.append(indent).append("  Parameters:\n");
        if (params.isEmpty()) {
            sb.append(indent).append("    void\n");
        } else {
            for (ParamNode param : params) {
                param.printTree(sb, indent + "    ");
            }
        }

        sb.append(indent).append("  Body:\n");
        if (body != null) {
            body.printTree(sb, indent + "    ");
        }
    }
}