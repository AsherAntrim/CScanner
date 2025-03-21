package compiler.scanner;

import java.io.*;

/**
 * This class serves as the start point for the C Scanner program, handling
 * the scanning of tokens from an input source file and writing them to an
 * output file.
 *
 * @author Asher Antrim & Ethan Emery
 * @version 1.0
 *          File: Main.java
 *          Created: 31-Jan-2025
 *          ©Copyright Cedarville University, its Computer Science faculty, and
 *          the authors. All rights reserved.
 *
 *          Description: This class reads a source file, tokenizes it using the
 *          Scanner class, and writes the resulting token list to an output
 *          file. Each token output includes its line number, type, and value.
 *          The program requires two command-line arguments: the input source
 *          file path and the output file path.
 */

// Reads a the source file and writes the token listing to an output file
public class Main {
    public static void main(String[] args) {
        // Checks to make sure that the required arguments are provided
        if (args.length < 2) {
            System.out.println("Usage: java Main <input-file> <output-file>");
            System.exit(1);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(args[1]))) {
            scanner scanner = new cminus(args[0]);
            Token token;

            // cminus scanner = new cminus(args[0]);
            // Token token;

            writer.println("TOKEN LISTING FOR FILE: " + args[0]);
            writer.println("Line\tType\t\tValue");
            writer.println("----------------------------------------");

            while (true) {
                token = scanner.getToken();
                writer.printf("%-4d\t%-12s\t%s%n",
                        token.getLineNo(),
                        token.getType(),
                        token.getValue());

                if (token.getType() == TokenType.ENDFILE) {
                    break;
                }
            }

            writer.println("\nScanning completed successfully.");
            System.out.println("Scanning completed. Output written to " + args[1]);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
