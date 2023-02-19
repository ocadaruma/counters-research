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

import com.mayreh.counter.CMCounter;
import com.mayreh.counter.ExactCounter;

@BenchmarkMode(Mode.Throughput)
@Threads(1)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CounterBenchmark {
    @Benchmark
    public void exact(BenchmarkState state) {
        for (byte[] data : state.data) {
            state.exactCounter.incrementAndGet(data, 1);
        }
    }

    @Benchmark
    public void countMin(BenchmarkState state) {
        for (byte[] data : state.data) {
            state.cmCounter.incrementAndGet(data, 1);
        }
    }

    @Benchmark
    public void conservativeUpdateCountMin(BenchmarkState state) {
        for (byte[] data : state.data) {
            state.cmCuCounter.incrementAndGet(data, 1);
        }
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param("")
        public String path;
        @Param("0")
        public double cmEpsilon;
        @Param("0")
        public double cmDelta;

        private List<byte[]> data;
        private ExactCounter exactCounter;
        private CMCounter cmCounter;
        private CMCounter cmCuCounter;

        @Setup(Level.Trial)
        public void setup() throws Exception {
            data = new ArrayList<>();
            try (BufferedReader r = new BufferedReader(new FileReader(path))) {
                String line;
                while ((line = r.readLine()) != null) {
                    data.add(line.getBytes(StandardCharsets.UTF_8));
                }
            }
            exactCounter = new ExactCounter();
            cmCounter = new CMCounter(cmEpsilon, cmDelta, false);
            cmCuCounter = new CMCounter(cmEpsilon, cmDelta, true);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: " + CounterBenchmark.class.getName() +
                               " /path/to/file.dat <epsilon for CM sketch> <delta for CM sketch>");
            System.exit(1);
        }
        String path = args[0];
        int count = (int)Files.lines(Paths.get(path)).count();
        System.out.println("Experimental data count: " + count);

        Options opts = new OptionsBuilder()
                .include(CounterBenchmark.class.getSimpleName())
                .operationsPerInvocation(count)
                .param("path", path)
                .param("cmEpsilon", args[1])
                .param("cmDelta", args[2])
                .build();
        new Runner(opts).run();
    }
}
