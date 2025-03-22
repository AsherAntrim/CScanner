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
    private boolean hasError = false;
    private List<String> errors = new ArrayList<>();

    public CMinusParser(String inputFile) throws FileNotFoundException {
        this.scanner = new cminus(inputFile);
    }

    @Override
    public void parse() throws IOException {
        advance();
        root = program();
        if (currentToken.getType() != TokenType.ENDFILE) {
            reportError("Expected end of file");
        }
    }

    @Override
    public void printTree(String outputFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            StringBuilder sb = new StringBuilder();
            if (hasError) {
                sb.append("SYNTAX ERRORS:\n");
                for (String error : errors) {
                    sb.append(error).append("\n");
                }
                sb.append("\n");
            }
            if (root != null) {
                root.printTree(sb, "");
            } else {
                sb.append("No valid AST was constructed due to syntax errors.");
            }

            writer.print(sb.toString());
        }
    }

    // Method to get the next token from the scanner
    private void advance() throws IOException {
        currentToken = scanner.getToken();
        if (currentToken.getValue() != null) {
            currentToken = new Token(
                currentToken.getType(),
                currentToken.getValue().trim(),
                currentToken.getLineNo()
            );
        }
        
        System.out.println("DEBUG: Token: " + currentToken.getType() + 
                          ", Value: " + currentToken.getValue());
    }

    // Match expected tokens
    private boolean match(TokenType expected) throws IOException {
        if (currentToken.getType() == expected) {
            advance();
            return true;
        }
        reportError("Expected " + expected + ", found " + currentToken.getType());
        return false;
    }

    // Error reporting
    private void reportError(String message) {
        hasError = true;
        errors.add("Line " + currentToken.getLineNo() + ": " + message);
    }

    // program -> decl {decl}
    private ProgramNode program() throws IOException {
        ProgramNode node = new ProgramNode(currentToken.getLineNo());
        
        DeclarationNode decl = decl();
        if (decl != null) {
            node.addDeclaration(decl);
        }
        while (currentToken.getType() == TokenType.INT || 
               currentToken.getType() == TokenType.VOID) {
            decl = decl();
            if (decl != null) {
                node.addDeclaration(decl);
            }
        }
        
        return node;
    }

    // decl -> void ID fun-decl' | int ID decl'
    private DeclarationNode decl() throws IOException {
        int lineNum = currentToken.getLineNo();
        if (currentToken.getType() == TokenType.VOID) {
            advance();
            
            if (currentToken.getType() != TokenType.ID) {
                reportError("Expected identifier after 'void'");
                return null;
            }
            
            String name = currentToken.getValue();
            advance();
            
            return funDeclPrime(lineNum, name, TypeSpecifier.VOID);
        } 
        else if (currentToken.getType() == TokenType.INT) {
            advance();
            
            if (currentToken.getType() != TokenType.ID) {
                reportError("Expected identifier after 'int'");
                return null;
            }
            
            String name = currentToken.getValue();
            advance();
            
            return declPrime(lineNum, name, TypeSpecifier.INT);
        }
        reportError("Expected 'void' or 'int'");
        return null;
    }

    // decl' -> ; | [num]; | fun-decl'
    private DeclarationNode declPrime(int lineNum, String name, TypeSpecifier type) throws IOException {
        if (currentToken.getType() == TokenType.SEMI) {
            advance();
            return new VarDeclarationNode(lineNum, name, type);
        }
        else if (currentToken.getType() == TokenType.LBRACK) {
            advance();
            int arraySize = 0;
            if (currentToken.getType() == TokenType.NUM) {
                arraySize = Integer.parseInt(currentToken.getValue());
                advance();
            } else {
                reportError("Expected number for array size");
            }
            match(TokenType.RBRACK);
            match(TokenType.SEMI);
            return new VarDeclarationNode(lineNum, name, type, arraySize);
        }
        else if (currentToken.getType() == TokenType.LPAREN) {
            return funDeclPrime(lineNum, name, type);
        }
        reportError("Invalid declaration");
        return null;
    }

    // fun-decl' -> (params) compound-stmt
    private FunDeclarationNode funDeclPrime(int lineNum, String name, TypeSpecifier type) throws IOException {
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
            return;
        }
        paramList(funNode);
    }

    // param-list → param{, param}
    private void paramList(FunDeclarationNode funNode) throws IOException {
        do {
            ParamNode param = param();
            if (param != null) {
                funNode.addParam(param);
            }
            if (currentToken.getType() != TokenType.COMMA) {
                break;
            }
            advance();
        } while (true);
    }

    // param → int ID[\[\]]
    private ParamNode param() throws IOException {
        int lineNum = currentToken.getLineNo();
        if (currentToken.getType() != TokenType.INT) {
            reportError("Expected 'int' in parameter");
            return null;
        }
        advance();
        if (currentToken.getType() != TokenType.ID) {
            reportError("Expected identifier for parameter");
            return null;
        }
        String name = currentToken.getValue();
        advance();
        boolean isArray = false;
        if (currentToken.getType() == TokenType.LBRACK) {
            advance();
            match(TokenType.RBRACK);
            isArray = true;
        }
        return new ParamNode(lineNum, name, TypeSpecifier.INT, isArray);
    }

    // compound-stmt → \{ local-declarations statement-list \}
    private CompoundStmtNode compoundStmt() throws IOException {
        int lineNum = currentToken.getLineNo();
        CompoundStmtNode node = new CompoundStmtNode(lineNum);
        match(TokenType.LBRACE);
        localDeclarations(node);
        statementList(node);
        match(TokenType.RBRACE);
        return node;
    }

    // local-declarations → {var-declaration}
    private void localDeclarations(CompoundStmtNode compoundNode) throws IOException {
        while (currentToken.getType() == TokenType.INT) {
            int lineNum = currentToken.getLineNo();
            advance();
            if (currentToken.getType() != TokenType.ID) {
                reportError("Expected identifier after 'int'");
                break;
            }
            String name = currentToken.getValue();
            advance();
            if (currentToken.getType() == TokenType.LBRACK) {
                advance();
                int arraySize = 0;
                if (currentToken.getType() == TokenType.NUM) {
                    arraySize = Integer.parseInt(currentToken.getValue());
                    advance();
                } else {
                    reportError("Expected number for array size");
                }
                match(TokenType.RBRACK);
                match(TokenType.SEMI);
                compoundNode.addLocalDeclaration(new VarDeclarationNode(lineNum, name, TypeSpecifier.INT, arraySize));
            } else {
                match(TokenType.SEMI);
                compoundNode.addLocalDeclaration(new VarDeclarationNode(lineNum, name, TypeSpecifier.INT));
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

    // statement → expression-stmt | compound-stmt | selection-stmt | iteration-stmt | return-stmt
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
                advance();
                return null;
        }
    }

    // expression-stmt → [expression];
    private ExpressionStmtNode expressionStmt() throws IOException {
        int lineNum = currentToken.getLineNo();
        if (currentToken.getType() == TokenType.SEMI) {
            advance();
            return new ExpressionStmtNode(lineNum);
        }
        ExpressionNode expr = expression();
        match(TokenType.SEMI);
        return new ExpressionStmtNode(lineNum, expr);
    }

    // selection-stmt → if (expression) statement [else statement]
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

    // iteration-stmt → while (expression) statement
    private IterationStmtNode iterationStmt() throws IOException {
        int lineNum = currentToken.getLineNo();
        match(TokenType.WHILE);
        match(TokenType.LPAREN);
        ExpressionNode condition = expression();
        match(TokenType.RPAREN);
        StatementNode body = statement();
        return new IterationStmtNode(lineNum, condition, body);
    }

    // return-stmt → return [expression];
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

    // expression -> (expression) simple-expression' | NUM simple-expression' | ID expression'
    private ExpressionNode expression() throws IOException {
        int lineNum = currentToken.getLineNo();
        if (currentToken.getType() == TokenType.LPAREN) {
            advance();
            ExpressionNode expr = expression();
            match(TokenType.RPAREN);
            return simpleExpressionPrime(expr);
        } 
        else if (currentToken.getType() == TokenType.NUM) {
            int value = Integer.parseInt(currentToken.getValue());
            advance();
            return simpleExpressionPrime(new NumberNode(lineNum, value));
        }
        else if (currentToken.getType() == TokenType.ID) {
            String id = currentToken.getValue();
            advance();
            return expressionPrime(lineNum, id);
        }
        reportError("Invalid expression");
        return new NumberNode(lineNum, 0);
    }

    // expression' -> = expression | [expression] expression'' | [(args)] simple-expression'
    private ExpressionNode expressionPrime(int lineNum, String id) throws IOException {
        if (currentToken.getType() == TokenType.ASSIGN) {
            advance();
            ExpressionNode rightExpr = expression();
            return new AssignExpressionNode(lineNum, new VarExpressionNode(lineNum, id), rightExpr);
        }
        else if (currentToken.getType() == TokenType.LBRACK) {
            advance();
            ExpressionNode indexExpr = expression();
            match(TokenType.RBRACK);
            
            VarExpressionNode var = new VarExpressionNode(lineNum, id, indexExpr);
            return expressionDoublePrime(lineNum, var);
        }
        else if (currentToken.getType() == TokenType.LPAREN) {
            advance();
            CallNode callNode = new CallNode(lineNum, id);
            args(callNode);
            match(TokenType.RPAREN);
            return simpleExpressionPrime(callNode);
        }
        return simpleExpressionPrime(new VarExpressionNode(lineNum, id));
    }

    // expression'' -> = expression | simple-expression'
    private ExpressionNode expressionDoublePrime(int lineNum, VarExpressionNode var) throws IOException {
        if (currentToken.getType() == TokenType.ASSIGN) {
            advance();
            ExpressionNode rightExpr = expression();
            return new AssignExpressionNode(lineNum, var, rightExpr);
        }
        return simpleExpressionPrime(var);
    }

    // simple-expression' -> additive-expression' [relop additive-expression']
    private ExpressionNode simpleExpressionPrime(ExpressionNode leftExpr) throws IOException {
        int lineNum = leftExpr.getLineNum();
        ExpressionNode left = additiveExpressionPrime(leftExpr);
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
                return left;
        }
        advance();
        ExpressionNode right = additiveExpressionPrime(null);
        return new SimpleExpressionNode(lineNum, left, operator, right);
    }

    // additive-expression' -> term' {addop term'}
    private ExpressionNode additiveExpressionPrime(ExpressionNode leftExpr) throws IOException {
        ExpressionNode left = (leftExpr != null) ? 
                              termPrime(leftExpr) : 
                              termPrime(null);
        int lineNum = left.getLineNum();
        while (currentToken.getType() == TokenType.PLUS || 
               currentToken.getType() == TokenType.MINUS) {
            AddOpType operator = (currentToken.getType() == TokenType.PLUS) ? 
                                AddOpType.PLUS : AddOpType.MINUS;
            advance();
            
            ExpressionNode right = termPrime(null);
            left = new SimpleExpressionNode(lineNum, left, operator, right);
        }
        return left;
    }

    // term' -> factor {mulop factor}
    private ExpressionNode termPrime(ExpressionNode leftExpr) throws IOException {
        ExpressionNode left = (leftExpr != null) ? 
                              leftExpr : 
                              factor();
        int lineNum = left.getLineNum();
        while (currentToken.getType() == TokenType.TIMES || 
               currentToken.getType() == TokenType.OVER) {
            MulOpType operator = (currentToken.getType() == TokenType.TIMES) ? MulOpType.TIMES : MulOpType.DIVIDE;
            advance();
            ExpressionNode right = factor();
            left = new TermNode(lineNum, left, operator, right);
        }
        return left;
    }

    // factor -> (expression) | var | call | NUM
private ExpressionNode factor() throws IOException {
    int lineNum = currentToken.getLineNo();
    if (currentToken.getType() == TokenType.LPAREN) {
        advance();
        ExpressionNode expr = expression();
        match(TokenType.RPAREN);
        return expr;
    } 
    else if (currentToken.getType() == TokenType.NUM) {
        try {
            int value = Integer.parseInt(currentToken.getValue().trim());
            advance();
            return new NumberNode(lineNum, value);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number: '" + currentToken.getValue() + "'");
            reportError("Invalid number format: " + currentToken.getValue());
            advance();
            return new NumberNode(lineNum, 0);
        }
    }
    else if (currentToken.getType() == TokenType.ID) {
        String id = currentToken.getValue();
        advance();
        if (currentToken.getType() == TokenType.LBRACK) {
            advance();
            ExpressionNode indexExpr = expression();
            match(TokenType.RBRACK);
            return new VarExpressionNode(lineNum, id, indexExpr);
        }
        else if (currentToken.getType() == TokenType.LPAREN) {
            CallNode callNode = new CallNode(lineNum, id);
            advance();
            if (currentToken.getType() != TokenType.RPAREN) {
                argList(callNode);
            }
            match(TokenType.RPAREN);
            return callNode;
        }
        return new VarExpressionNode(lineNum, id);
    }
    
    reportError("Invalid factor");
    return new NumberNode(lineNum, 0);
}

    // args -> arg-list | ε
    private void args(CallNode callNode) throws IOException {
        if (currentToken.getType() == TokenType.RPAREN) {
            return;
        }
        argList(callNode);
    }

    // arg-list -> expression{, expression}
    private void argList(CallNode callNode) throws IOException {
        do {
            ExpressionNode arg = expression();
            callNode.addArgument(arg);
            if (currentToken.getType() != TokenType.COMMA) {
                break;
            }
            advance();
        } while (true);
    }
}