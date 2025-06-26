package com.cobra;

import com.cobra.cli.CLI;
import java.io.IOException;

public class Jobra {
    public static void main(String[] args) {
        try {
            CLI.run(args);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
} 
