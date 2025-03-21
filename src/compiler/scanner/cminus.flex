package compiler.scanner;

%%

%public
%class cminusScanner
%implements scanner
%unicode
%line
%column
%type Token

%{
    private StringBuilder tokenString = new StringBuilder();
    
    private Token createToken(TokenType type, String value) {
        return new Token(type, value, yyline + 1);
    }
%}

/* States */
%state COMMENT

/* Regular Definitions */
WhiteSpace = [ \t\r\n]+
Digit      = [0-9]
NonZero    = [1-9]
Letter     = [a-zA-Z]
Identifier = {Letter}({Letter}|{Digit})*
Number     = {NonZero}{Digit}*|0
InvalidNum = 0{Digit}+

%%

<YYINITIAL> {
    /* Whitespace */
    {WhiteSpace}           {  }

    /* Comments */
    "/*"                    { yybegin(COMMENT); }

    /* Keywords */
    "else"                  { return createToken(TokenType.ELSE, yytext()); }
    "if"                    { return createToken(TokenType.IF, yytext()); }
    "int"                   { return createToken(TokenType.INT, yytext()); }
    "return"                { return createToken(TokenType.RETURN, yytext()); }
    "void"                  { return createToken(TokenType.VOID, yytext()); }
    "while"                 { return createToken(TokenType.WHILE, yytext()); }

    /* Numbers */
    {InvalidNum}            { return createToken(TokenType.ERROR, yytext()); }
    {Number}                { return createToken(TokenType.NUM, yytext()); }

    /* Identifiers */
    {Identifier}           { return createToken(TokenType.ID, yytext()); }

    /* Operators */
    "+"                     { return createToken(TokenType.PLUS, yytext()); }
    "-"                     { return createToken(TokenType.MINUS, yytext()); }
    "*"                     { return createToken(TokenType.TIMES, yytext()); }
    "/"                     { return createToken(TokenType.OVER, yytext()); }
    "<="                    { return createToken(TokenType.LTE, yytext()); }
    "<"                     { return createToken(TokenType.LT, yytext()); }
    ">="                    { return createToken(TokenType.GTE, yytext()); }
    ">"                     { return createToken(TokenType.GT, yytext()); }
    "=="                    { return createToken(TokenType.EQ, yytext()); }
    "!="                    { return createToken(TokenType.NEQ, yytext()); }
    "="                     { return createToken(TokenType.ASSIGN, yytext()); }

    /* Delimiters */
    ";"                     { return createToken(TokenType.SEMI, yytext()); }
    ","                     { return createToken(TokenType.COMMA, yytext()); }
    "("                     { return createToken(TokenType.LPAREN, yytext()); }
    ")"                     { return createToken(TokenType.RPAREN, yytext()); }
    "["                     { return createToken(TokenType.LBRACK, yytext()); }
    "]"                     { return createToken(TokenType.RBRACK, yytext()); }
    "{"                     { return createToken(TokenType.LBRACE, yytext()); }
    "}"                     { return createToken(TokenType.RBRACE, yytext()); }

    /* Invalid characters */
    [^]                    { return createToken(TokenType.ERROR, yytext()); }

}

<COMMENT> {
    "*/"                   { yybegin(YYINITIAL); }
    [^*\n]+               { }
    "*"                    { }
    \n                     { }
    <<EOF>>               { return createToken(TokenType.ERROR, "Unterminated comment"); }
}

<<EOF>>                   { return createToken(TokenType.ENDFILE, ""); }
