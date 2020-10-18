package xyz.ethyr.util;

import java.io.File;

public final class FileUtil {

  public static String replace(final String string) {
    return string
        .replace("[", "")
        .replace("]", "")
        .replace(".", "")
        .replace("<", "")
        .replace(">", "")
        .replace(":", "")
        .replace("\\", "")
        .replace("\"", "")
        .replace("|", "")
        .replace("?", "")
        .replace("*", "");
  }

  public static void deleteAndCreateDirectory(final File file) {
      if (file.exists()) {
          deleteDirectory(file);
      }

    file.mkdir();
  }

  private static void deleteDirectory(final File file) {
    for (final File file1 : file.listFiles()) {
        if (file1.isDirectory()) {
            deleteDirectory(file1);
        } else {
            file1.delete();
        }
    }

    file.delete();
  }
}
