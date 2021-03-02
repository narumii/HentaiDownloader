package xyz.ethyr.downloader;

import java.io.File;
import java.util.Optional;
import java.util.Scanner;

public final class DownloaderFactory {

  public static Optional<Downloader> create(String name, File dir, Scanner scanner) {
    return DownloaderType.getByName(name).map(type -> create0(type, dir, scanner));
  }

  private static Downloader create0(DownloaderType type, File dir, Scanner scanner) {
    try {
      return (Downloader) type.getConstructor().invoke(dir, scanner);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      return null;
    }
  }
}
