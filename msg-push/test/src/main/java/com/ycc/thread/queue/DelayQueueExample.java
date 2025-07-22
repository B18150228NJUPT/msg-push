package com.ycc.thread.queue;

import java.util.concurrent.*;

// 实现 Delayed 接口的任务类
class DelayedTask implements Delayed {
    private final String name;             // 任务名称
    private final long startTime;          // 任务开始时间（毫秒）

    // 构造器：接收延迟时间（秒）和任务名称
    public DelayedTask(long delaySeconds, String name) {
        this.name = name;
        // 当前时间 + 延迟时间 = 任务开始时间
        this.startTime = System.currentTimeMillis() + (delaySeconds * 1000);
    }

    // 获取剩余延迟时间（实现 Delayed 接口）
    @Override
    public long getDelay(TimeUnit unit) {
        long diff = startTime - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    // 比较任务优先级（实现 Delayed 接口）
    @Override
    public int compareTo(Delayed other) {
        return Long.compare(this.startTime, ((DelayedTask) other).startTime);
    }

    // 任务执行方法
    public void execute() {
        System.out.println("执行任务 [" + name + "] at " + System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "DelayedTask{" +
                "name='" + name + '\'' +
                ", startTime=" + startTime +
                '}';
    }
}

public class DelayQueueExample {
    public static void main(String[] args) throws InterruptedException {
        // 创建 DelayQueue 实例
        DelayQueue<DelayedTask> delayQueue = new DelayQueue<>();

        // 创建并添加多个延迟任务
        delayQueue.put(new DelayedTask(5, "任务A"));  // 5秒后执行
        delayQueue.put(new DelayedTask(3, "任务B"));  // 3秒后执行
        delayQueue.put(new DelayedTask(8, "任务C"));  // 8秒后执行

        System.out.println("所有任务已加入队列，当前时间：" + System.currentTimeMillis());

        // 创建一个线程来处理队列中的任务
        Thread workerThread = new Thread(() -> {
            try {
                while (true) {
                    // take() 方法会阻塞直到有任务到期
                    DelayedTask task = delayQueue.take();
                    task.execute();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 启动工作线程
        workerThread.start();

        // 主线程休眠一段时间后退出
        Thread.sleep(10000);
        workerThread.interrupt();  // 终止工作线程
        System.out.println("程序结束");
    }
}