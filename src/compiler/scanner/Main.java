package compiler.scanner;

import java.io.*;

// Reads a the source file and writes the token listing to an output file
public class Main {
    public static void main(String[] args) {
        // Checks to make sure that the required arguments are provided
        if (args.length < 2) {
            System.out.println("Usage: java Main <input-file> <output-file>");
            System.exit(1);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(args[1]))) {
            Scanner scanner = new Scanner(args[0]);
            Token token;

            writer.println("TOKEN LISTING FOR FILE: " + args[0]);
            writer.println("Line\tType\t\tLexeme");
            writer.println("----------------------------------------");

            while (true) {
                token = scanner.getToken();
                writer.printf("%-4d\t%-12s\t%s%n",
                        token.getLineNo(),
                        token.getType(),
                        token.getLexeme());

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
