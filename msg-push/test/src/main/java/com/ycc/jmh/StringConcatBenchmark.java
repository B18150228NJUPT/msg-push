package com.ycc.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime) // 测试平均时间
@OutputTimeUnit(TimeUnit.NANOSECONDS) // 输出时间单位为纳秒
@Warmup(iterations = 3, time = 1) // 预热3轮，每轮1秒
@Measurement(iterations = 5, time = 1) // 测量5轮，每轮1秒
@Fork(2) //  forks 2个进程进行测试
@State(Scope.Thread) // 每个线程一个实例
public class StringConcatBenchmark {

    private String a = "a";
    private String b = "b";
    private String c = "c";

    @Benchmark
    public String testStringPlus() {
        return a + b + c;
    }

    @Benchmark
    public String testStringBuilder() {
        return new StringBuilder().append(a).append(b).append(c).toString();
    }

    @Benchmark
    public String testStringBuffer() {
        return new StringBuffer().append(a).append(b).append(c).toString();
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(StringConcatBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}
