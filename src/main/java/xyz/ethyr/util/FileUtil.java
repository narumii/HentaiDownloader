package xyz.ethyr.util;

import java.io.File;

public final class FileUtil {

  public static File createFile(File dir, String name) {
    return new File(dir, replace(name));
  }

  //XD
  public static String replace(String string) {
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

  public static void deleteAndCreateDirectory(File file) {
    if (file.exists()) {
      deleteDirectory(file);
    }

    file.mkdirs();
  }

  private static void deleteDirectory(File file) {
    for (File parentFile : file.listFiles()) {
      if (parentFile.isDirectory()) {
        deleteDirectory(parentFile);
      } else {
        parentFile.delete();
      }
    }
    file.delete();
  }
}
