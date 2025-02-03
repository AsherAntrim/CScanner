package compiler.scanner;

// The types of tokens that we could run into from the book in C- language
public enum TokenType {
    // Keywords
    ELSE, IF, INT, RETURN, VOID, WHILE,

    // Special symbols
    PLUS, MINUS, TIMES, OVER, LT, GT, LTE, GTE, EQ, NEQ, ASSIGN,
    SEMI, COMMA, LPAREN, RPAREN, LBRACK, RBRACK, LBRACE, RBRACE,

    // Regular tokens
    ID, NUM,

    // Special tokens
    ERROR, ENDFILE
}
