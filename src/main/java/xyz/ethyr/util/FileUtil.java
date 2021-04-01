package xyz.ethyr.util;

import java.io.File;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;

public final class FileUtil {

  private static final char[] chars = "QqWwEeRrTtYyUuIiOoPpAaSsDdFfGgHhJjKkLlZzXxCcVvBbNnMm"
      .toCharArray();

  public static File createFile(File dir, String name) {
    return new File(dir, replace(name));
  }

  //na chuj mam zwracac mkdirs?
  public static void deleteAndCreateDirectory(File file) {
    if (file.exists()) {
      deleteDirectory(file);
    }

    file.mkdirs();
  }

  public static String generateRandomString(int length) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      stringBuilder.append(chars[ThreadLocalRandom.current().nextInt(chars.length)]);
    }
    return stringBuilder.toString();
  }

  public static Path computePath(File dir, String file, String extension) {
    return Path.of(dir.getAbsolutePath(), file + "." + extension);
  }

  public static void saveImage(Path path, URLConnection connection) {
    if (connection == null) {
      return;
    }

    try (InputStream stream = connection.getInputStream()) {
      Files.copy(stream, path);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //Tak to java 11 i nie uzyjemy apache
  private static String replace(String string) {
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
