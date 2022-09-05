package cafe.ethyr.hentaidl.executor;

import cafe.ethyr.hentaidl.helper.PropertiesHelper;

public final class ExecutorHelper {

    private static final RateLimitedPool JOIN_POOL;
    private static final RateLimitedPool SLAVE_JOIN_POOL;
    private static int THREADS = Integer.parseInt(String.valueOf(PropertiesHelper.getOrDefault("downloading_threads", 0)));
    private static int SLAVE_THREADS = Integer.parseInt(String.valueOf(PropertiesHelper.getOrDefault("slave_downloading_threads", 0)));

    static {
        if (THREADS <= 0)
            THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() * 2) - 2;

        if (SLAVE_THREADS <= 0)
            SLAVE_THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);

        JOIN_POOL = new RateLimitedPool(THREADS);
        SLAVE_JOIN_POOL = new RateLimitedPool(SLAVE_THREADS);
    }

    public static void submit(Runnable runnable) {
        JOIN_POOL.execute(runnable);
    }

    public static void slaveSubmit(Runnable runnable) {
        SLAVE_JOIN_POOL.execute(runnable);
    }

    public static void limitSubmit(Runnable runnable) {
        JOIN_POOL.rateLimitExecute(runnable);
    }

    public static void limitSlaveSubmit(Runnable runnable) {
        SLAVE_JOIN_POOL.rateLimitExecute(runnable);
    }

    public static RateLimitedPool getJoinPool() {
        return JOIN_POOL;
    }

    public static RateLimitedPool getSlaveJoinPool() {
        return SLAVE_JOIN_POOL;
    }

    public static int getThreads() {
        return THREADS;
    }

    public static int getSlaveThreads() {
        return SLAVE_THREADS;
    }
}
