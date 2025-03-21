package compiler.parser;

import java.io.IOException;

/**
 * Interface for a parser that can analyze source code and generate an AST.
 */
public interface Parser {
    /**
     * Parse the input source and construct an Abstract Syntax Tree.
     * 
     * @throws IOException If an I/O error occurs during parsing
     */
    void parse() throws IOException;

    /**
     * Print the generated Abstract Syntax Tree to the specified file.
     * 
     * @param outputFile Path to write the AST output
     * @throws IOException If an I/O error occurs during writing
     */
    void printTree(String outputFile) throws IOException;
}