package compiler.scanner;

/**
 * Enumerates all possible token types recognized by the C- language.
 * Provides a complete set of tokens for lexical analysis.
 *
 * @author Asher Antrim & Ethan Emery
 * @version 1.0
 *          File: TokenType.java
 *          Created: 31-Jan-2025
 *          ©Copyright Cedarville University, its Computer Science faculty, and
 *          the
 *          authors. All rights reserved.
 *
 *          Description: This enum defines the complete set of tokens for the C-
 *          language:
 *          - Keywords (if, else, while, etc.)
 *          - Special symbols and operators (+, -, *, /, <, >, etc.)
 *          - Regular tokens (identifiers, numbers)
 *          - Special handling tokens (error, EoF)
 *          Used by the Scanner and Token classes for lexical analysis.
 */

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
