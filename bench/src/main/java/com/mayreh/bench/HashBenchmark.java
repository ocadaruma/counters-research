package com.mayreh.bench;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.mayreh.hash.HashFamilies;
import com.mayreh.hash.Hasher;

@BenchmarkMode(Mode.Throughput)
@Threads(1)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class HashBenchmark {
    @Benchmark
    public void xx3(BenchmarkState state) {
        Hasher hasher = HashFamilies.XX3.get();
        int bitMask = (1 << 20) - 1;
        for (byte[] data : state.data) {
            hasher.hash(data, bitMask);
        }
    }

    @Benchmark
    public void murmur3(BenchmarkState state) {
        Hasher hasher = HashFamilies.Murmur3.get();
        int bitMask = (1 << 20) - 1;
        for (byte[] data : state.data) {
            hasher.hash(data, bitMask);
        }
    }

    @Benchmark
    public void wy3(BenchmarkState state) {
        Hasher hasher = HashFamilies.WY3.get();
        int bitMask = (1 << 20) - 1;
        for (byte[] data : state.data) {
            hasher.hash(data, bitMask);
        }
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param("")
        public String path;
        private List<byte[]> data;

        @Setup(Level.Trial)
        public void setup() throws Exception {
            data = new ArrayList<>();
            try (BufferedReader r = new BufferedReader(new FileReader(path))) {
                String line;
                while ((line = r.readLine()) != null) {
                    data.add(line.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: " + HashBenchmark.class.getName() + " /path/to/file.dat");
            System.exit(1);
        }
        String path = args[0];
        int count = (int)Files.lines(Paths.get(path)).count();
        System.out.println("Experimental data count: " + count);

        Options opts = new OptionsBuilder()
                .include(HashBenchmark.class.getSimpleName())
                .operationsPerInvocation(count)
                .param("path", path)
                .build();
        new Runner(opts).run();
    }
}
