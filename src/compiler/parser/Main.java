package compiler.parser;

import java.io.IOException;

/**
 * Main class to run the C- parser on input files.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java compiler.parser.Main <input-file> <output-file>");
            System.exit(1);
        }

        try {
            Parser parser = new CMinusParser(args[0]);

            parser.parse();

            parser.printTree(args[1]);

            System.out.println("Parsing completed. AST written to " + args[1]);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}