package com.ycc.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(2)
@State(Scope.Benchmark)
public class MapPerformanceBenchmark {

    // 测试数据大小
    private static final int DATA_SIZE = 10_000;
    // 用于生成随机键
    private Random random;
    // 键数组
    private Integer[] keys;
    
    // 待测试的Map实例
    private Hashtable<Integer, String> hashtable;
    private ConcurrentHashMap<Integer, String> concurrentHashMap;

    // 初始化测试数据
    @Setup(Level.Trial)
    public void setup() {
        random = new Random(42); // 固定种子，保证测试可重复
        keys = new Integer[DATA_SIZE];
        
        // 初始化键数组
        for (int i = 0; i < DATA_SIZE; i++) {
            keys[i] = i;
        }
        
        // 初始化Hashtable
        hashtable = new Hashtable<>();
        for (int i = 0; i < DATA_SIZE; i++) {
            hashtable.put(keys[i], "value-" + i);
        }
        
        // 初始化ConcurrentHashMap
        concurrentHashMap = new ConcurrentHashMap<>();
        for (int i = 0; i < DATA_SIZE; i++) {
            concurrentHashMap.put(keys[i], "value-" + i);
        }
    }
    
    // 随机获取一个键
    private Integer getRandomKey() {
        return keys[random.nextInt(DATA_SIZE)];
    }
    
    // Hashtable读取测试
    @Benchmark
    public String testHashtableRead() {
        return hashtable.get(getRandomKey());
    }
    
    // Hashtable写入测试
    @Benchmark
    public void testHashtableWrite() {
        Integer key = getRandomKey();
        hashtable.put(key, "new-value-" + key);
    }
    
    // Hashtable读写混合测试
    @Benchmark
    public String testHashtableReadWrite() {
        Integer key = getRandomKey();
        if (random.nextBoolean()) {
            return hashtable.get(key);
        } else {
            hashtable.put(key, "new-value-" + key);
            return null;
        }
    }
    
    // ConcurrentHashMap读取测试
    @Benchmark
    public String testConcurrentHashMapRead() {
        return concurrentHashMap.get(getRandomKey());
    }
    
    // ConcurrentHashMap写入测试
    @Benchmark
    public void testConcurrentHashMapWrite() {
        Integer key = getRandomKey();
        concurrentHashMap.put(key, "new-value-" + key);
    }
    
    // ConcurrentHashMap读写混合测试
    @Benchmark
    public String testConcurrentHashMapReadWrite() {
        Integer key = getRandomKey();
        if (random.nextBoolean()) {
            return concurrentHashMap.get(key);
        } else {
            concurrentHashMap.put(key, "new-value-" + key);
            return null;
        }
    }
    
    // 多线程环境下的测试
    @Group("hashtableMt")
    @GroupThreads(4)
    @Benchmark
    public String hashtableMultiThreadRead() {
        return hashtable.get(getRandomKey());
    }
    
    @Group("hashtableMt")
    @GroupThreads(1)
    @Benchmark
    public void hashtableMultiThreadWrite() {
        Integer key = getRandomKey();
        hashtable.put(key, "new-value-" + key);
    }
    
    @Group("concurrentMt")
    @GroupThreads(4)
    @Benchmark
    public String concurrentMultiThreadRead() {
        return concurrentHashMap.get(getRandomKey());
    }
    
    @Group("concurrentMt")
    @GroupThreads(1)
    @Benchmark
    public void concurrentMultiThreadWrite() {
        Integer key = getRandomKey();
        concurrentHashMap.put(key, "new-value-" + key);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(MapPerformanceBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();
        new Runner(options).run();
    }
}
    