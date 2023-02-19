package com.mayreh.bench.accuracy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.mayreh.counter.CMCounter;
import com.mayreh.counter.ExactCounter;

public class AccuracyBenchmark {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: " + AccuracyBenchmark.class.getName() +
                               " /path/to/simplewiki.xml <epsilon for CM sketch> <delta for CM sketch> [--conservative]");
            System.exit(1);
        }
        boolean conservative = args.length >= 4 && "--conservative".equals(args[3]);

        double cmEpsilon = Double.parseDouble(args[1]);
        double cmDelta = Double.parseDouble(args[2]);
        ExactCounter exactCounter = new ExactCounter();
        CMCounter cmCounter = new CMCounter(cmEpsilon, cmDelta, conservative);

        int[] count = new int[1];
        tokenize(args[0]).forEach(token -> {
            byte[] input = token.getBytes(StandardCharsets.UTF_8);
            exactCounter.incrementAndGet(input, 1);
            cmCounter.incrementAndGet(input, 1);
            count[0]++;
        });

        System.out.printf(" Total item count : %d\n", count[0]);
        System.out.printf("Unique item count : %d\n", exactCounter.table().size());
        System.out.printf("   CountMin    Nε : %f\n", cmEpsilon * count[0]);
        System.out.printf("   CountMin   1-δ : %f\n", 1 - cmDelta);
        System.out.printf("   CountMin depth : %d\n", cmCounter.depth());
        System.out.printf("   CountMin width : %d\n", cmCounter.width());
        System.out.printf("================================\n");

        int histogramBuckets = 10;
        int maxFrequency = exactCounter
                .table()
                .values()
                .stream()
                .mapToInt(Integer::intValue)
                .max()
                .getAsInt();

        int bucketBound = Math.max(maxFrequency / histogramBuckets, 1);

        Iterator<Entry<ByteBuffer, Integer>> entries =
                exactCounter.table()
                            .entrySet()
                            .stream()
                            .sorted(Entry.comparingByValue())
                            .iterator();

        double rmse = 0;
        int maxError = 0;
        int minError = Integer.MAX_VALUE;
        int numKeys = 0;
        int currentBound = bucketBound;
        int numKeysExceedErrorBound = 0;
        System.out.printf("   frequency | num keys  | max error | min error | rmse\n");
        while (entries.hasNext()) {
            Entry<ByteBuffer, Integer> entry = entries.next();

            int exact = entry.getValue();
            int cm = cmCounter.get(entry.getKey().array());

            // count min estimation always gteq exact
            int error = cm - exact;

            maxError = Math.max(maxError, error);
            minError = Math.min(minError, error);
            numKeys++;
            rmse += Math.pow(error, 2);

            if (error > cmEpsilon * count[0]) {
                numKeysExceedErrorBound++;
            }

            // last of current bucket bound
            if (exact >= currentBound || !entries.hasNext()) {
                if (!entries.hasNext()) {
                    currentBound = maxFrequency;
                }
                System.out.printf("<=%10d |%10d |%10d |%10d |%f\n",
                                  currentBound,
                                  numKeys,
                                  maxError,
                                  minError,
                                  Math.sqrt(rmse / numKeys));
                currentBound += bucketBound;
                numKeys = 0;
                maxError = 0;
                minError = Integer.MAX_VALUE;
                rmse = 0;
            }
        }

        System.out.printf("================================\n");
        System.out.printf("   Keys with error > Nε : %d\n", numKeysExceedErrorBound);
        System.out.printf("           Conform rate : %f\n", 1 - (double)numKeysExceedErrorBound / exactCounter.table().size());
    }

    // Just split input text into words by white space characters
    private static Stream<String> tokenize(String path) throws IOException {
        return new BufferedReader(new FileReader(path))
                .lines()
                .flatMap(line -> Arrays.stream(WHITESPACE.split(line)))
                .filter(line -> !line.isEmpty());
    }
}
