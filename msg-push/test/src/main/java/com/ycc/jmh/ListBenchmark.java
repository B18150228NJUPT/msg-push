package com.ycc.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

@BenchmarkMode(Mode.Throughput)          // 吞吐量，ops/us
@OutputTimeUnit(java.util.concurrent.TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class ListBenchmark {

    @Param({"synchronizedList", "CopyOnWriteArrayList"})
    private String mapType;            // 测试目标


    @Param({"0.1", "0.9"})
    private double writeRatio;

    List list;

    int size = 10000;
    int mask = size - 1;

    @Setup
    public void setup() {
        if ("synchronizedList".equals(mapType)) {
            list = Collections.synchronizedList(new ArrayList<>());
        } else {
            list = new CopyOnWriteArrayList<>();
        }

        for (int i = 0; i < size; i++) {
            list.add(i);
        }

    }

    @Benchmark
    @Group("g")
    @GroupThreads(2)                  // 每个线程既读又写
    public void readWrite(Blackhole bh) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int key = r.nextInt() & mask;

        if (r.nextDouble() < writeRatio) {
            list.add(key);
        } else {
            bh.consume(list.get(key));
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(ListBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}
