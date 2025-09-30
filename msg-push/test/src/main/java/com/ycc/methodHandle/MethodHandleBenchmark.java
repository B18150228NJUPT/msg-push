package com.ycc.methodHandle;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

/**
 * 对比 Hashtable 与 ConcurrentHashMap 的读写性能
 * 运行示例：
 * mvn clean package
 * java -jar target/benchmarks.jar MapBenchmark -wi 5 -i 5 -f 1 -t 4
 */
@BenchmarkMode(Mode.Throughput)          // 吞吐量，ops/us
@OutputTimeUnit(java.util.concurrent.TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class MethodHandleBenchmark {

    /* -------------------- 参数 -------------------- */

    @Param({"1000000"})
    private int size;                  // 初始键值对数量

    @Param({"get", "MethodHandle", "reflect"})
    private String mapType;            // 测试目标

    Target target = new Target("Charlie");
    MethodHandle mh;
    Method method;

    @Setup
    public void setup() throws IllegalAccessException, NoSuchMethodException {
        // 注意：默认lookup无法访问私有方法，需通过目标类内部的lookup或提升权限
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(Target.class, MethodHandles.lookup());

        // 定义MethodType：返回值String，参数String（secret方法的签名）
        MethodType mt = MethodType.methodType(String.class, String.class);

        // 查找私有方法"greet"
        mh = lookup.findVirtual(Target.class, "greet", mt);

        method = Target.class.getMethod("greet", String.class);
    }

    /* -------------------- 基准方法 -------------------- */

    @Benchmark
    @Group("g")
    @GroupThreads(2)                  // 每个线程既读又写
    public void readWrite(Blackhole bh) throws Throwable {
        if ("MethodHandle".equals(mapType)) {
            // 调用
            bh.consume(mh.invoke(target, "MethodHandle is powerful!"));
        } else if ("get".equals(mapType)) {
            bh.consume(target.greet("MethodHandle is powerful!"));
        } else {
            // 反射调用方法
            bh.consume(method.invoke(target, "MethodHandle is powerful!"));
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(MethodHandleBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}