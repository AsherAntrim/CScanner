package compiler.parser;

import compiler.parser.AST.*;
import compiler.scanner.*;
import java.io.*;
import java.util.*;

/**
 * A recursive-descent parser for the C- language.
 */
public class CMinusParser implements Parser {
    private scanner scanner;
    private Token currentToken;
    private ProgramNode root;

    // For error handling
    private boolean hasError = false;
    private List<String> errors = new ArrayList<>();

    public CMinusParser(String inputFile) throws FileNotFoundException {
        this.scanner = new cminus(inputFile);
    }

    @Override
    public void parse() throws IOException {
        // Get the first token
        advance();

        // Parse the program
        root = program();

        // Check if we reached the end of file
        if (currentToken.getType() != TokenType.ENDFILE) {
            reportError("Expected end of file");
        }
    }

    @Override
    public void printTree(String outputFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            StringBuilder sb = new StringBuilder();

            // Print errors first if any
            if (hasError) {
                sb.append("SYNTAX ERRORS:\n");
                for (String error : errors) {
                    sb.append(error).append("\n");
                }
                sb.append("\n");
            }

            // Print the AST
            if (root != null) {
                root.printTree(sb, "");
            } else {
                sb.append("No valid AST was constructed due to syntax errors.");
            }

            writer.print(sb.toString());
        }
    }

    // Helper method to advance to the next token
    private void advance() throws IOException {
        currentToken = scanner.getToken();
        System.out.println("DEBUG: Token: " + currentToken.getType() + ", Value: " + currentToken.getValue());
    }

    // Helper method to match expected tokens
    private boolean match(TokenType expected) throws IOException {
        if (currentToken.getType() == expected) {
            advance();
            return true;
        }

        // Report error but don't advance - this is crucial
        reportError("Expected " + expected + ", found " + currentToken.getType());
        return false;
    }

    // Error reporting
    private void reportError(String message) {
        hasError = true;
        errors.add("Line " + currentToken.getLineNo() + ": " + message);
    }

    // program → declaration-list
    private ProgramNode program() throws IOException {
        ProgramNode node = new ProgramNode(currentToken.getLineNo());
        declarationList(node);
        return node;
    }

    // declaration-list → declaration {declaration}
    private void declarationList(ProgramNode programNode) throws IOException {
        while (currentToken.getType() == TokenType.INT ||
                currentToken.getType() == TokenType.VOID) {
            DeclarationNode decl = declaration();
            if (decl != null) {
                programNode.addDeclaration(decl);
            }
        }
    }

    // declaration → var-declaration | fun-declaration
    private DeclarationNode declaration() throws IOException {
        int lineNum = currentToken.getLineNo();
        TypeSpecifier type = typeSpecifier();

        if (type == null) {
            return null;
        }

        // Get the identifier
        String name = null;
        if (match(TokenType.ID)) {
            name = currentToken.getValue();
        } else {
            return null;
        }

        // Check if it's a function or variable declaration
        if (currentToken.getType() == TokenType.LPAREN) {
            return funDeclaration(lineNum, name, type);
        } else {
            return varDeclaration(lineNum, name, type);
        }
    }

    // type-specifier → int | void
    private TypeSpecifier typeSpecifier() throws IOException {
        if (currentToken.getType() == TokenType.INT) {
            advance();
            return TypeSpecifier.INT;
        } else if (currentToken.getType() == TokenType.VOID) {
            advance();
            return TypeSpecifier.VOID;
        } else {
            reportError("Expected type specifier (int or void)");
            return null;
        }
    }

    // var-declaration → type-specifier ID ([ NUM ] | ε) ;
    private VarDeclarationNode varDeclaration(int lineNum, String name, TypeSpecifier type) throws IOException {
        VarDeclarationNode node;

        // Check if it's an array declaration
        if (currentToken.getType() == TokenType.LBRACK) {
            advance();
            if (currentToken.getType() == TokenType.NUM) {
                int arraySize = Integer.parseInt(currentToken.getValue());
                advance();
                if (match(TokenType.RBRACK)) {
                    node = new VarDeclarationNode(lineNum, name, type, arraySize);
                } else {
                    node = new VarDeclarationNode(lineNum, name, type);
                }
            } else {
                reportError("Expected number for array size");
                node = new VarDeclarationNode(lineNum, name, type);
                if (currentToken.getType() == TokenType.RBRACK) {
                    advance();
                }
            }
        } else {
            node = new VarDeclarationNode(lineNum, name, type);
        }

        match(TokenType.SEMI);
        return node;
    }

    // fun-declaration → type-specifier ID ( params ) compound-stmt
    private FunDeclarationNode funDeclaration(int lineNum, String name, TypeSpecifier type) throws IOException {
        FunDeclarationNode node = new FunDeclarationNode(lineNum, name, type);

        match(TokenType.LPAREN);
        params(node);
        match(TokenType.RPAREN);

        CompoundStmtNode body = compoundStmt();
        node.setBody(body);

        return node;
    }

    // params → param-list | void
    private void params(FunDeclarationNode funNode) throws IOException {
        if (currentToken.getType() == TokenType.VOID) {
            advance();
            return; // No parameters
        }

        paramList(funNode);
    }

    // param-list → param {, param}
    private void paramList(FunDeclarationNode funNode) throws IOException {
        do {
            ParamNode param = param();
            if (param != null) {
                funNode.addParam(param);
            }

            if (currentToken.getType() != TokenType.COMMA) {
                break;
            }
            advance(); // Skip the comma
        } while (true);
    }

    // param → type-specifier ID | type-specifier ID [ ]
    private ParamNode param() throws IOException {
        int lineNum = currentToken.getLineNo();
        TypeSpecifier type = typeSpecifier();

        if (type == null) {
            return null;
        }

        String name = null;
        if (match(TokenType.ID)) {
            name = currentToken.getValue();
        } else {
            return null;
        }

        boolean isArray = false;
        if (currentToken.getType() == TokenType.LBRACK) {
            advance();
            match(TokenType.RBRACK);
            isArray = true;
        }

        return new ParamNode(lineNum, name, type, isArray);
    }

    // compound-stmt → { local-declarations statement-list }
    private CompoundStmtNode compoundStmt() throws IOException {
        int lineNum = currentToken.getLineNo();
        CompoundStmtNode node = new CompoundStmtNode(lineNum);

        match(TokenType.LBRACE);

        // Parse local declarations
        localDeclarations(node);

        // Parse statements
        statementList(node);

        match(TokenType.RBRACE);

        return node;
    }

    // local-declarations → {var-declaration}
    private void localDeclarations(CompoundStmtNode compoundNode) throws IOException {
        while (currentToken.getType() == TokenType.INT ||
                currentToken.getType() == TokenType.VOID) {
            int lineNum = currentToken.getLineNo();
            TypeSpecifier type = typeSpecifier();

            if (type == null) {
                break;
            }

            String name = null;
            if (match(TokenType.ID)) {
                name = currentToken.getValue();
            } else {
                break;
            }

            VarDeclarationNode varNode = varDeclaration(lineNum, name, type);
            if (varNode != null) {
                compoundNode.addLocalDeclaration(varNode);
            }
        }
    }

    // statement-list → {statement}
    private void statementList(CompoundStmtNode compoundNode) throws IOException {
        while (currentToken.getType() != TokenType.RBRACE &&
                currentToken.getType() != TokenType.ENDFILE) {
            StatementNode stmt = statement();
            if (stmt != null) {
                compoundNode.addStatement(stmt);
            }
        }
    }

    // statement → expression-stmt | compound-stmt | selection-stmt | iteration-stmt
    // | return-stmt
    private StatementNode statement() throws IOException {
        switch (currentToken.getType()) {
            case SEMI:
            case ID:
            case LPAREN:
            case NUM:
                return expressionStmt();
            case LBRACE:
                return compoundStmt();
            case IF:
                return selectionStmt();
            case WHILE:
                return iterationStmt();
            case RETURN:
                return returnStmt();
            default:
                reportError("Invalid statement");
                advance(); // Error recovery
                return null;
        }
    }

    // expression-stmt → expression ; | ;
    // expression-stmt → expression ; | ;
    // expression-stmt → expression ; | ;
    private ExpressionStmtNode expressionStmt() throws IOException {
        int lineNum = currentToken.getLineNo();

        if (currentToken.getType() == TokenType.SEMI) {
            advance();
            return new ExpressionStmtNode(lineNum);
        }

        ExpressionNode expr = expression();

        // Modified approach - check for semicolon but handle gracefully if missing
        if (currentToken.getType() == TokenType.SEMI) {
            advance();
        } else {
            reportError("Expected SEMI, found " + currentToken.getType());
            // Skip until semicolon or next statement
            while (currentToken.getType() != TokenType.SEMI &&
                    currentToken.getType() != TokenType.RBRACE &&
                    currentToken.getType() != TokenType.ENDFILE) {
                advance();
            }
            if (currentToken.getType() == TokenType.SEMI) {
                advance();
            }
        }

        return new ExpressionStmtNode(lineNum, expr);
    }

    // selection-stmt → if ( expression ) statement [else statement]
    private SelectionStmtNode selectionStmt() throws IOException {
        int lineNum = currentToken.getLineNo();

        match(TokenType.IF);
        match(TokenType.LPAREN);
        ExpressionNode condition = expression();
        match(TokenType.RPAREN);

        StatementNode thenBranch = statement();

        if (currentToken.getType() == TokenType.ELSE) {
            advance();
            StatementNode elseBranch = statement();
            return new SelectionStmtNode(lineNum, condition, thenBranch, elseBranch);
        } else {
            return new SelectionStmtNode(lineNum, condition, thenBranch);
        }
    }

    // iteration-stmt → while ( expression ) statement
    private IterationStmtNode iterationStmt() throws IOException {
        int lineNum = currentToken.getLineNo();

        match(TokenType.WHILE);
        match(TokenType.LPAREN);
        ExpressionNode condition = expression();
        match(TokenType.RPAREN);

        StatementNode body = statement();

        return new IterationStmtNode(lineNum, condition, body);
    }

    // return-stmt → return ; | return expression ;
    private ReturnStmtNode returnStmt() throws IOException {
        int lineNum = currentToken.getLineNo();

        match(TokenType.RETURN);

        if (currentToken.getType() == TokenType.SEMI) {
            advance();
            return new ReturnStmtNode(lineNum);
        } else {
            ExpressionNode expr = expression();
            match(TokenType.SEMI);
            return new ReturnStmtNode(lineNum, expr);
        }
    }

    // expression → var = expression | simple-expression
    // expression → var = expression | simple-expression
    private ExpressionNode expression() throws IOException {
        int lineNum = currentToken.getLineNo();

        // If the expression starts with an ID, it could be a var = expression or a
        // simple-expression
        if (currentToken.getType() == TokenType.ID) {
            String id = currentToken.getValue();
            advance();

            // Check if it's an array access
            ExpressionNode indexExpr = null;
            if (currentToken.getType() == TokenType.LBRACK) {
                advance();
                indexExpr = expression();
                match(TokenType.RBRACK);
            }

            VarExpressionNode var = new VarExpressionNode(lineNum, id, indexExpr);

            // If there's an assignment, it's a var = expression
            if (currentToken.getType() == TokenType.ASSIGN) {
                advance();
                ExpressionNode rightExpr = expression();
                return new AssignExpressionNode(lineNum, var, rightExpr);
            }

            // Otherwise, it's the start of a simple-expression
            // Pass the variable node we've already parsed
            return simpleExpression(lineNum, var);
        }

        // Not a variable or assignment, must be a simple expression
        return simpleExpression(lineNum, null);
    }

    // var → ID [[ expression ]]
    private VarExpressionNode var() throws IOException {
        int lineNum = currentToken.getLineNo();

        String id = currentToken.getValue();
        match(TokenType.ID);

        // Check if it's an array access
        if (currentToken.getType() == TokenType.LBRACK) {
            advance();
            ExpressionNode indexExpr = expression();
            match(TokenType.RBRACK);
            return new VarExpressionNode(lineNum, id, indexExpr);
        }

        return new VarExpressionNode(lineNum, id);
    }

    // simple-expression → additive-expression relop additive-expression |
    // additive-expression
    private ExpressionNode simpleExpression(int lineNum, ExpressionNode leftExpr) throws IOException {
        // If we have a leftExpr passed in, it's from an ID that we've already processed
        ExpressionNode left = (leftExpr != null) ? leftExpr : additiveExpression();

        // Check if there's a relational operator
        RelOpType operator = null;
        switch (currentToken.getType()) {
            case LT:
                operator = RelOpType.LT;
                break;
            case LTE:
                operator = RelOpType.LTE;
                break;
            case GT:
                operator = RelOpType.GT;
                break;
            case GTE:
                operator = RelOpType.GTE;
                break;
            case EQ:
                operator = RelOpType.EQ;
                break;
            case NEQ:
                operator = RelOpType.NEQ;
                break;
            default:
                return left; // No relational operator, just return the additive expression
        }

        advance(); // Skip the relational operator
        ExpressionNode right = additiveExpression();

        return new SimpleExpressionNode(lineNum, left, operator, right);
    }

    // additive-expression → term {addop term}
    private ExpressionNode additiveExpression() throws IOException {
        System.out.println("DEBUG: Entering additiveExpression");
        int lineNum = currentToken.getLineNo();
        ExpressionNode left = term();

        // Keep processing as long as we see + or - operators
        while (currentToken.getType() == TokenType.PLUS ||
                currentToken.getType() == TokenType.MINUS) {
            AddOpType operator = (currentToken.getType() == TokenType.PLUS) ? AddOpType.PLUS : AddOpType.MINUS;
            advance(); // Skip the operator

            // Get the right operand (another term)
            ExpressionNode right = term();

            // Create a new AddExpressionNode with the left and right operands
            left = new AddExpressionNode(lineNum, left, operator, right);
            System.out.println("DEBUG: additiveExpression processed term: " + left);
        }

        return left;
    }

    // term → factor {mulop factor}
    private ExpressionNode term() throws IOException {
        int lineNum = currentToken.getLineNo();
        ExpressionNode left = factor();

        // Keep processing as long as we see * or / operators
        while (currentToken.getType() == TokenType.TIMES ||
                currentToken.getType() == TokenType.OVER) {
            // Determine which operator we have
            MulOpType operator = (currentToken.getType() == TokenType.TIMES) ? MulOpType.TIMES : MulOpType.DIVIDE;
            advance(); // Skip the operator

            // Get the right operand (another factor)
            ExpressionNode right = factor();

            // Create a new TermNode with the left and right operands
            left = new TermNode(lineNum, left, operator, right);
        }

        return left;
    }

    // factor → ( expression ) | var | call | NUM
    // factor → ( expression ) | var | call | NUM
    private ExpressionNode factor() throws IOException {
        int lineNum = currentToken.getLineNo();

        switch (currentToken.getType()) {
            case LPAREN:
                advance();
                ExpressionNode expr = expression();
                match(TokenType.RPAREN);
                return expr;

            case ID:
                String id = currentToken.getValue();
                advance();

                // Check if it's a function call
                if (currentToken.getType() == TokenType.LPAREN) {
                    return call(lineNum, id);
                }

                // It's a variable
                ExpressionNode indexExpr = null;
                if (currentToken.getType() == TokenType.LBRACK) {
                    advance();
                    indexExpr = expression();
                    match(TokenType.RBRACK);
                }

                return new VarExpressionNode(lineNum, id, indexExpr);

            case NUM:
                int value = Integer.parseInt(currentToken.getValue());
                advance();
                return new NumberNode(lineNum, value);

            default:
                reportError("Invalid factor");
                // Error recovery - try to advance past the bad token
                advance();
                return new NumberNode(lineNum, 0); // Return a dummy node
        }
    }

    // call → ID ( args )
    private CallNode call(int lineNum, String functionName) throws IOException {
        CallNode node = new CallNode(lineNum, functionName);

        match(TokenType.LPAREN);
        args(node);
        match(TokenType.RPAREN);

        return node;
    }

    // args → arg-list | ε
    private void args(CallNode callNode) throws IOException {
        if (currentToken.getType() == TokenType.RPAREN) {
            return; // Empty argument list
        }

        argList(callNode);
    }

    // arg-list → expression {, expression}
    private void argList(CallNode callNode) throws IOException {
        do {
            ExpressionNode arg = expression();
            callNode.addArgument(arg);

            if (currentToken.getType() != TokenType.COMMA) {
                break;
            }
            advance(); // Skip the comma
        } while (true);
    }

    // Skip tokens until we reach a synchronization point
    private void synchronize() throws IOException {
        // Skip until we reach a statement terminator or block starter
        while (currentToken.getType() != TokenType.SEMI &&
                currentToken.getType() != TokenType.LBRACE &&
                currentToken.getType() != TokenType.RBRACE &&
                currentToken.getType() != TokenType.ENDFILE) {
            advance();
        }

        // Skip the synchronization token if it's a semicolon
        if (currentToken.getType() == TokenType.SEMI) {
            advance();
        }
    }
}