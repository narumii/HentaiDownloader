package xyz.ethyr;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import xyz.ethyr.downloader.Downloader;
import xyz.ethyr.downloader.DownloaderEnum;
import xyz.ethyr.downloader.DownloaderManager;

public class Bootstrap {

  private static final DownloaderManager manager = new DownloaderManager();
  private static Downloader downloader;

  public static void main(String... args) throws Exception {
    Scanner scanner = new Scanner(System.in);
    System.out.println("HentaiDownloader created by narumi ( https://github.com/narumii )");
    System.out.println("Supported sites: " + Arrays.toString(DownloaderEnum.values()) + "\n");

    do {
      if (downloader != null && downloader.isDownloading()) {
        continue;
      }

      System.out.println("\n------------------------------------");

      System.out.print("Site name: \r");
      String name = scanner.next();

      System.out.print("Downloading threads: \r");
      int threads = scanner.nextInt();

      System.out.print("Images dir: \r");
      String dir = scanner.next();

      System.out.println();
      scanner.nextLine(); //JAVA THE BEST NO DOUBT

      downloader = manager.newDownloader(name, new File(dir), scanner);
      downloader.downloadImages();

      System.out.println();
      scanner.nextLine(); //JAVA THE BEST NO DOUBT
    } while (true);
  }
}
