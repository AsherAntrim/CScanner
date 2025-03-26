package compiler.parser;

import java.io.IOException;

/**
 * Interface for a parser that can analyze source code and generate an AST.
 */
public interface Parser {
    /**
     * @throws IOException
     */
    void parse() throws IOException;

    /**
     * @param outputFile
     * @throws IOException
     */
    void printTree(String outputFile) throws IOException;
}