import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by _chenqiang on 2017/6/21.
 * 定时器工具类
 */
public class TimerManager {


    ScheduledExecutorService scheduledExecutorService;

    private TimerManager() {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(
                Runtime.getRuntime().availableProcessors(),
                new TimerThreadFactory());
    }

    private static TimerManager timerManager = new TimerManager();

    public static TimerManager instance() {
        return timerManager;
    }

    /**
     * @param runnable 执行任务
     * @param delay    延迟毫秒值
     */
    public ScheduledFuture<?> schedule(Runnable runnable, long delay) {
        return this.scheduledExecutorService.schedule(new DelegateRun(runnable), delay, TimeUnit.MILLISECONDS);
    }

    /**
     * @param command      执行任务
     * @param initialDelay 初始延迟
     * @param period       上一个任务执行开始后再经过多长时间执行
     * @return scheduledFuture.cancel(true)打断任务
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period) {
        return this.scheduledExecutorService.scheduleAtFixedRate(
                new DelegateRun(command), initialDelay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * @param command      执行任务
     * @param initialDelay 初始延迟
     * @param period       上一个任务执行结束后再经过多长时间执行
     * @return scheduledFuture.cancel(true)打断任务
     */
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long period) {
        return this.scheduledExecutorService.scheduleWithFixedDelay(
                new DelegateRun(command), initialDelay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * @param command      执行任务
     * @param initialDelay 初始延迟
     * @param period       上一个任务执行开始后再经过多长时间执行
     * @param count        执行次数
     * @return scheduledFuture.cancel(true)打断任务
     */
    public void scheduleWithFixedRateAndFixedCount(Runnable command, long initialDelay, long period, int count) {
        FixedCountDelegateRun fixedCountDelegateRun = new FixedCountDelegateRun(command, count);
        ScheduledFuture<?> scheduledFuture = this.scheduledExecutorService.scheduleAtFixedRate(fixedCountDelegateRun, initialDelay, period, TimeUnit.MILLISECONDS);
        fixedCountDelegateRun.setScheduledFuture(scheduledFuture);
    }


    public static class DelegateRun implements Runnable {

        final Runnable runnable;

        public DelegateRun(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            long t = System.currentTimeMillis();
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            t = System.currentTimeMillis() - t;

            if (t > 50) {
                System.err.println(" timeout:" + runnable + " time:" + t + "ms");
            }
        }
    }

    public static class FixedCountDelegateRun extends DelegateRun {

        final int count;
        ScheduledFuture<?> scheduledFuture;
        int currentCount;

        public FixedCountDelegateRun(Runnable runnable, int count) {
            super(runnable);
            this.count = count;
        }

        public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        @Override
        public void run() {
            super.run();
            currentCount++;
            if (currentCount >= count && scheduledFuture != null) {
                scheduledFuture.cancel(true);
            }
        }
    }

    static class TimerThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        TimerThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "timer-pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
