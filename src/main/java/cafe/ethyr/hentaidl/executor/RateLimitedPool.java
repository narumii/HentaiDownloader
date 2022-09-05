package cafe.ethyr.hentaidl.executor;

import java.time.Duration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

//It's stupid i know, but it may work
public class RateLimitedPool extends ForkJoinPool {

    private final Queue<Runnable> runnables = new LinkedList<>();

    private int maxRequests;
    private Duration duration;
    private boolean created;

    public RateLimitedPool() {
    }

    public RateLimitedPool(int parallelism) {
        super(parallelism);
    }

    public RateLimitedPool(int parallelism, ForkJoinWorkerThreadFactory factory, Thread.UncaughtExceptionHandler handler, boolean asyncMode) {
        super(parallelism, factory, handler, asyncMode);
    }

    public RateLimitedPool(int parallelism, ForkJoinWorkerThreadFactory factory, Thread.UncaughtExceptionHandler handler, boolean asyncMode, int corePoolSize, int maximumPoolSize, int minimumRunnable, Predicate<? super ForkJoinPool> saturate, long keepAliveTime, TimeUnit unit) {
        super(parallelism, factory, handler, asyncMode, corePoolSize, maximumPoolSize, minimumRunnable, saturate, keepAliveTime, unit);
    }

    public RateLimitedPool init() {
        return init(maxRequests, duration);
    }

    public RateLimitedPool init(int maxRequests, Duration duration) {
        if (!created) {
            Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
                if (runnables.isEmpty())
                    return;

                execute(runnables.poll());
            }, 0, duration.toMillis() / maxRequests, TimeUnit.MILLISECONDS);
            created = true;
        }

        return this;
    }

    public void rateLimitExecute(Runnable runnable) {
        runnables.add(runnable);
    }

    public RateLimitedPool maxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
        return this;
    }

    public RateLimitedPool duration(Duration duration) {
        this.duration = duration;
        return this;
    }
}
