package com.mayreh.bench;

import java.io.PrintWriter;
import java.util.UUID;

public class GenerateData {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: " + GenerateData.class.getName() + " /path/to/file.dat 10000000");
            System.exit(1);
        }

        String path = args[0];
        int count = Integer.parseInt(args[1]);

        try (PrintWriter w = new PrintWriter(path)) {
            for (int i = 0; i < count; i++) {
                w.println(UUID.randomUUID());
            }
        }
    }
}
