package com.ycc.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@BenchmarkMode(Mode.Throughput)          // 吞吐量，ops/us
@OutputTimeUnit(java.util.concurrent.TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class ListBenchmark2 {

    @Param({"ArrayList", "LinkedList"})
    private String mapType;            // 测试目标


    @Param({"0.1", "0.9"})
    private double writeRatio;

    List list;

    @Param({"100", "1000", "10000", "100000"})
    int size;

    @Setup
    public void setup() {
        if ("ArrayList".equals(mapType)) {
            list = new ArrayList<>();
        } else {
            list = new LinkedList<>();
        }

        for (int i = 0; i < size; i++) {
            list.add(i);
        }

    }

    @Benchmark
    @Group("g")
    @GroupThreads(2)                  // 每个线程既读又写
    public void readWrite(Blackhole bh) {
        list.forEach(bh::consume);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(ListBenchmark2.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}
