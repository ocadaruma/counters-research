package com.mayreh.bench.accuracy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.mayreh.counter.CMCounter;
import com.mayreh.counter.ExactCounter;

public class AccuracyBenchmark {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: " + AccuracyBenchmark.class.getName() +
                               " /path/to/simplewiki.xml <epsilon for CM sketch> <delta for CM sketch>");
            System.exit(1);
        }

        ExactCounter exactCounter = new ExactCounter();
        CMCounter cmCounter = new CMCounter(
                Double.parseDouble(args[1]),
                Double.parseDouble(args[2]));

        int[] count = new int[1];
        tokenize(args[0]).forEach(token -> {
            byte[] input = token.getBytes(StandardCharsets.UTF_8);
            exactCounter.incrementAndGet(input, 1);
            cmCounter.incrementAndGet(input, 1);
            count[0]++;
        });

        System.out.printf(" Total item count : %d\n", count[0]);
        System.out.printf("Unique item count : %d\n", exactCounter.table().size());
    }

    // Just split input text into words by white space characters
    private static Stream<String> tokenize(String path) throws IOException {
        return new BufferedReader(new FileReader(path))
                .lines()
                .flatMap(line -> Arrays.stream(WHITESPACE.split(line)))
                .filter(line -> !line.isEmpty());
    }
}
