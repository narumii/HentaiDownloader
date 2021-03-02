package xyz.ethyr.downloader;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

public class DownloaderManager {

  public Downloader newDownloader(String name, File dir, Scanner scanner) throws Exception {
    Class<?> downloaderClass = getDownloader(name);
    if (downloaderClass == null) {
      return null;
    }

    return (Downloader) downloaderClass.getDeclaredConstructors()[0].newInstance(dir, scanner);
  }

  public Class<?> getDownloader(String clazz) {
    Optional<DownloaderEnum> downloader = DownloaderEnum.getByName(clazz);
    if (downloader.isPresent()) {
      return downloader.get().getClazz();
    }

    System.out.println("Downloader not found, available downloaders: " + Arrays.toString(
        DownloaderEnum.values()));
    return null;
  }
}
