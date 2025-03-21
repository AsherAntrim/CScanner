package compiler.scanner;

import java.io.IOException;

public interface scanner {
    Token getToken() throws IOException;
    // public Token yylex() throws IOException;
}