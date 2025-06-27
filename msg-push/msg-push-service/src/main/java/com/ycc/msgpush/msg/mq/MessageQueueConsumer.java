//package com.ycc.msgpush.msg.mq;
//
//import io.hippo.Hippo4j;
//import io.hippo.core.client.MetricReporter;
//import io.hippo.metric.Metric;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ThreadPoolExecutor;
//
////cn.hippo4j.springboot.starter.config.DynamicThreadPoolAutoConfiguration
////org.springframework.boot.diagnostics.FailureAnalyzer=cn.hippo4j.springboot.starter.core.ConfigEmptyAnalyzer
//
//public class MessageQueueConsumer {
//    private static final int THREAD_POOL_SIZE = 10;
//    private ExecutorService executorService;
//    private Hippo4j hippo4j;
//
//    public MessageQueueConsumer() {
//        // 创建固定大小的线程池
//        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
//
//        // 初始化 Hippo4j
//        hippo4j = new Hippo4j();
//    }
//
//    public void startConsuming() {
//        // 启动消费任务
//        for (int i = 0; i < THREAD_POOL_SIZE; i++) {
//            executorService.submit(new MessageConsumerTask());
//        }
//
//        // 启动 Hippo4j 监控
//        hippo4j.start();
//    }
//
//    public void stopConsuming() {
//        // 关闭线程池
//        executorService.shutdown();
//
//        // 停止 Hippo4j 监控
//        hippo4j.stop();
//    }
//
//    // 消息消费任务
//    private class MessageConsumerTask implements Runnable {
//        @Override
//        public void run() {
//            // 模拟消息消费
//            while (!Thread.currentThread().isInterrupted()) {
//                try {
//                    // 模拟消息处理
//                    Thread.sleep(1000); // 假设消息处理耗时 1 秒
//
//                    // 获取线程池的活跃线程数
//                    int activeThreads = ((ThreadPoolExecutor) executorService).getActiveCount();
//
//                    // 上报活跃线程数给 Hippo4j
//                    MetricReporter.report(Metric.builder()
//                            .name("ActiveThreads")
//                            .value(activeThreads)
//                            .build());
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        MessageQueueConsumer consumer = new MessageQueueConsumer();
//        consumer.startConsuming();
//
//        // 等待一段时间后停止消费
//        try {
//            Thread.sleep(60000); // 消费 60 秒
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        consumer.stopConsuming();
//    }
//}
