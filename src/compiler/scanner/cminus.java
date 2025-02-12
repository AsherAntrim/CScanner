package compiler.scanner;

import java.io.*;
import java.util.*;

/**
 * Implements a scanner for the C- programming language
 * using a Deterministic Finite Automaton (DFA) approach.
 *
 * @author Asher Antrim & Ethan Emery
 * @version 1.0
 *          File: Scanner.java
 *          Created: 31-Jan-2025
 *          ©Copyright Cedarville University, its Computer Science faculty, and
 *          the
 *          authors. All rights reserved.
 *
 *          Description: This scanner implements lexical analysis for C- source
 *          code.
 *          It reads input character by character, recognizing tokens based on
 *          DFA states.
 *          Features include:
 *          - Reserved word recognition
 *          - Comment handling
 *          - Number and identifier tokenization
 *          - Special symbol and operator detection
 *          The scanner maintains line number tracking for error reporting and
 *          supports file input with buffered reading.
 * 
 */

public class cminus implements scanner { // Changed class name and added implements clause

    private static final int MAXTOKENLEN = 40;

    private BufferedReader reader;
    private String currentLine;
    private int linePos;
    private int lineNo;
    private boolean EOF_flag;
    private String tokenString;

    // States of the DFA
    private enum State {
        START, INLT, INGT, INEQ, INNOT, INSLASH,
        INCOMMENT, INCOMMENTSTAR, INNUM, INID, DONE
    }

    // Reserved words
    private static final Map<String, TokenType> reservedWords;
    static {
        reservedWords = new HashMap<>();
        reservedWords.put("else", TokenType.ELSE);
        reservedWords.put("if", TokenType.IF);
        reservedWords.put("int", TokenType.INT);
        reservedWords.put("return", TokenType.RETURN);
        reservedWords.put("void", TokenType.VOID);
        reservedWords.put("while", TokenType.WHILE);
    }

    // Scanner Constructor - opens the input file
    public cminus(String inputFile) throws FileNotFoundException { // Changed constructor name to cminus
        reader = new BufferedReader(new FileReader(inputFile));
        currentLine = "";
        linePos = 0;
        lineNo = 0;
        EOF_flag = false;
        tokenString = "";
    }

    // Gets the next char in the input file
    private int getNextChar() throws IOException {
        if (linePos >= currentLine.length()) {
            lineNo++;
            String line = reader.readLine();
            if (line != null) {
                currentLine = line + "\n";
                linePos = 0;
                return currentLine.charAt(linePos++);
            } else {
                EOF_flag = true;
                return -1;
            }
        }
        return currentLine.charAt(linePos++);
    }

    // Puts back the last char read from the input file if it does not match the
    // current state
    private void ungetNextChar() {
        if (!EOF_flag && linePos > 0) {
            linePos--;
        }
    }

    // Retrieves the next character and thenm tries to assign it a token type
    // It will keep getting chars till it can assign them a token type
    // Once it finds a token type, it will get to the DONE state and return the
    // token
    public Token getToken() throws IOException {
        StringBuilder tokenBuilder = new StringBuilder();
        State state = State.START;
        TokenType currentToken = null;

        while (state != State.DONE) {
            int c = getNextChar();
            boolean save = true;

            switch (state) {
                case DONE:
                    break;
                case START:
                    if (Character.isDigit(c)) {
                        if (c == '0') {
                            int nextChar = getNextChar();
                            if (Character.isDigit(nextChar)) {
                                tokenString += (char) c;
                                tokenString += (char) nextChar;
                                currentToken = TokenType.ERROR;
                                state = State.DONE;
                            } else {
                                ungetNextChar();
                                state = State.INNUM;
                            }
                        } else {
                            state = State.INNUM;
                        }
                    } else if (Character.isLetter(c)) {
                        state = State.INID;
                    } else if (Character.isWhitespace(c)) {
                        save = false;
                    } else if (c == '<') {
                        state = State.INLT;
                    } else if (c == '>') {
                        state = State.INGT;
                    } else if (c == '=') {
                        state = State.INEQ;
                    } else if (c == '!') {
                        state = State.INNOT;
                    } else if (c == '/') {
                        save = false;
                        state = State.INSLASH;
                    } else {
                        state = State.DONE;
                        switch (c) {
                            case -1:
                                save = false;
                                currentToken = TokenType.ENDFILE;
                                break;
                            case '+':
                                currentToken = TokenType.PLUS;
                                break;
                            case '-':
                                currentToken = TokenType.MINUS;
                                break;
                            case '*':
                                currentToken = TokenType.TIMES;
                                break;
                            case ';':
                                currentToken = TokenType.SEMI;
                                break;
                            case ',':
                                currentToken = TokenType.COMMA;
                                break;
                            case '(':
                                currentToken = TokenType.LPAREN;
                                break;
                            case ')':
                                currentToken = TokenType.RPAREN;
                                break;
                            case '[':
                                currentToken = TokenType.LBRACK;
                                break;
                            case ']':
                                currentToken = TokenType.RBRACK;
                                break;
                            case '{':
                                currentToken = TokenType.LBRACE;
                                break;
                            case '}':
                                currentToken = TokenType.RBRACE;
                                break;
                            default:
                                currentToken = TokenType.ERROR;
                                break;
                        }
                    }
                    break;

                case INSLASH:
                    if (c == '*') {
                        state = State.INCOMMENT;
                        save = false;
                    } else {
                        state = State.DONE;
                        ungetNextChar();
                        currentToken = TokenType.OVER;
                    }
                    break;

                case INCOMMENT:
                    save = false;
                    if (c == -1) {
                        currentToken = TokenType.ERROR;
                        state = State.DONE;
                    } else if (c == '*') {
                        state = State.INCOMMENTSTAR;
                    }
                    break;
                case INCOMMENTSTAR:
                    save = false;
                    if (c == -1) {
                        currentToken = TokenType.ERROR;
                        state = State.DONE;
                    } else if (c == '/') {
                        state = State.START;
                    } else if (c != '*') {
                        state = State.INCOMMENT;
                    }
                    break;

                case INLT:
                    state = State.DONE;
                    if (c == '=') {
                        currentToken = TokenType.LTE;
                    } else {
                        ungetNextChar();
                        currentToken = TokenType.LT;
                    }
                    break;

                case INGT:
                    state = State.DONE;
                    if (c == '=') {
                        currentToken = TokenType.GTE;
                    } else {
                        ungetNextChar();
                        currentToken = TokenType.GT;
                    }
                    break;

                case INEQ:
                    state = State.DONE;
                    if (c == '=') {
                        currentToken = TokenType.EQ;
                    } else {
                        ungetNextChar();
                        currentToken = TokenType.ASSIGN;
                    }
                    break;

                case INNOT:
                    state = State.DONE;
                    if (c == '=') {
                        currentToken = TokenType.NEQ;
                    } else {
                        ungetNextChar();
                        save = false;
                        currentToken = TokenType.ERROR;
                    }
                    break;

                case INNUM:
                    if (Character.isLetter(c)) {
                        tokenString += (char) c;
                        currentToken = TokenType.ERROR;
                        state = State.DONE;
                    } else if (!Character.isDigit(c)) {
                        ungetNextChar();
                        save = false;
                        state = State.DONE;
                        currentToken = TokenType.NUM;
                    }
                    break;

                case INID:
                    if (!Character.isLetterOrDigit(c)) {
                        ungetNextChar();
                        save = false;
                        state = State.DONE;
                        currentToken = TokenType.ID;
                    }
                    break;
            }

            if (save && tokenBuilder.length() < MAXTOKENLEN) {
                tokenBuilder.append((char) c);
            }

            if (state == State.DONE) {
                tokenString = tokenBuilder.toString();
                if (currentToken == TokenType.ID) {
                    currentToken = reservedWords.getOrDefault(tokenString, TokenType.ID);
                }
            }
        }

        return new Token(currentToken, tokenString, lineNo);
    }
}