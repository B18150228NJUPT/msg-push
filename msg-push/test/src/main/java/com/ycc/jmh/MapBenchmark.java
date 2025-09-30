package com.ycc.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 对比 Hashtable 与 ConcurrentHashMap 的读写性能
 * 运行示例：
 *   mvn clean package
 *   java -jar target/benchmarks.jar MapBenchmark -wi 5 -i 5 -f 1 -t 4
 */
@BenchmarkMode(Mode.Throughput)          // 吞吐量，ops/us
@OutputTimeUnit(java.util.concurrent.TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class MapBenchmark {

    /* -------------------- 参数 -------------------- */

    @Param({"100", "1000", "10000"})
    private int size;                  // 初始键值对数量

    @Param({"Hashtable", "ConcurrentHashMap"})
    private String mapType;            // 测试目标

    @Param({"0.1", "0.5", "0.9", "1.0"})
    private double writeRatio;         // 写比例：0.1=10%写 90%读，1.0=100%写

    /* -------------------- 状态 -------------------- */

    private Map<Integer, Integer> map;
    private int mask;                  // 用于随机 key

    @Setup
    public void setup() {
        // 创建 Map
        if ("Hashtable".equals(mapType)) {
            map = new Hashtable<>();
        } else {
            map = new ConcurrentHashMap<>();
        }

        // 预填充
        for (int i = 0; i < size; i++) {
            map.put(i, i);
        }
        mask = size - 1;               // 保证 key 落在已有区间内
    }

    /* -------------------- 基准方法 -------------------- */

    @Benchmark
    @Group("g")
    @GroupThreads(2)                  // 每个线程既读又写
    public void readWrite(Blackhole bh) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int key = r.nextInt() & mask;

        if (r.nextDouble() < writeRatio) {
            map.put(key, r.nextInt());
        } else {
            bh.consume(map.get(key));
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(MapBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}