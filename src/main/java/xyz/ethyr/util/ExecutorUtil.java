package xyz.ethyr.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ExecutorUtil {

  private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

  public static void submit(Runnable runnable) {
    EXECUTOR_SERVICE.submit(runnable);
  }
}
