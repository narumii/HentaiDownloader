package cafe.ethyr.hentaidl.helper;

import java.util.concurrent.ForkJoinPool;

public final class ExecutorHelper {

    private static final ForkJoinPool JOIN_POOL;
    private static final ForkJoinPool SLAVE_JOIN_POOL;
    private static int THREADS = Integer.parseInt(String.valueOf(PropertiesHelper.getOrDefault("downloading_threads", 0)));
    private static int SLAVE_THREADS = Integer.parseInt(String.valueOf(PropertiesHelper.getOrDefault("slave_downloading_threads", 0)));

    static {
        if (THREADS <= 0)
            THREADS = Runtime.getRuntime().availableProcessors() * 2;

        if (SLAVE_THREADS <= 0)
            SLAVE_THREADS = Runtime.getRuntime().availableProcessors() > 2 ? Runtime.getRuntime().availableProcessors() / 2 : 1;

        JOIN_POOL = new ForkJoinPool(THREADS);
        SLAVE_JOIN_POOL = new ForkJoinPool(SLAVE_THREADS);
    }

    public static void submit(Runnable runnable) {
        JOIN_POOL.execute(runnable);
    }

    public static void slaveSubmit(Runnable runnable) {
        SLAVE_JOIN_POOL.execute(runnable);
    }

    public static ForkJoinPool getJoinPool() {
        return JOIN_POOL;
    }

    public static ForkJoinPool getSlaveJoinPool() {
        return SLAVE_JOIN_POOL;
    }

    public static int getThreads() {
        return THREADS;
    }

    public static int getSlaveThreads() {
        return SLAVE_THREADS;
    }
}
