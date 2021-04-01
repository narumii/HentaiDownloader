package xyz.ethyr;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;
import xyz.ethyr.downloader.Downloader;
import xyz.ethyr.downloader.DownloaderFactory;
import xyz.ethyr.downloader.DownloaderType;

public class Bootstrap {

  public static void main(String... args) throws IOException {
    Optional<Downloader> downloaderOptional = Optional.empty();
    Scanner scanner = new Scanner(System.in);
    System.out.println("HentaiDownloader created by narumi ( https://github.com/narumii )");
    System.out.println("Supported sites: " + Arrays.toString(DownloaderType.values()) + "\n");

    do {
      if (downloaderOptional.isPresent() && downloaderOptional.get().isDownloading()) {
        continue;
      }

      System.out.println("\n------------------------------------");

      System.out.print("Site name: \r");
      String name = scanner.next();

      System.out.print("Downloading threads: \r");
      int threads = scanner.nextInt(); //TODO: IN FUTURE

      System.out.print("Images dir: \r");
      String dir = scanner.next();

      System.out.println();
      scanner.nextLine(); //JAVA IS THE BEST, NO DOUBT

      downloaderOptional = DownloaderFactory.fetch(name, new File(dir), scanner);
      downloaderOptional.ifPresentOrElse(Downloader::downloadImages, () -> {
        System.out.printf("No downloader by name %s was found", name);
        scanner.reset();
      });
    } while (true);
  }
}
