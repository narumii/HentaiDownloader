package xyz.ethyr.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ExecutorUtil {

  public static void submit(final Runnable runnable) {
    ExecutorService executor = null;
    try {
      executor = Executors.newSingleThreadExecutor();
      executor.submit(runnable);
    } finally {
      if (executor != null) {
        executor.shutdown();
      }
    }
  }
}
