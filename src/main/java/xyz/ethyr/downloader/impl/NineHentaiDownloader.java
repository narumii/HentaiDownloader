package xyz.ethyr.downloader.impl;

import java.io.File;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import xyz.ethyr.downloader.Downloader;
import xyz.ethyr.util.ExecutorUtil;
import xyz.ethyr.util.FileUtil;
import xyz.ethyr.util.SiteUtil;

public class NineHentaiDownloader extends Downloader {

  private static final String MAIN_URL = "https://9hentai.com/g/%s";
  private static final String DOWNLOAD_URL = "https://cdn.9hentai.com/images/%s/%s.jpg";

  private final String doujinshiId;
  private String doujinshiName;

  public NineHentaiDownloader(File dir, Scanner scanner) {
    super(dir);
    System.out.print("Doujinshi [ID/Link]: ");
    String input = scanner.next();
    if (input.startsWith("https://9hentai") || input.startsWith("9hentai")) {
      doujinshiId = input.split("/g/")[1].replace("/", "");
    } else {
      doujinshiId = input;
    }
  }

  @Override
  public void downloadImages() {
    setDownloading(true);
    ExecutorUtil.submit(() -> {
      try {
        Document element = Jsoup.connect(String.format(MAIN_URL, this.doujinshiId)).get();
        doujinshiName = element.body().getElementById("info").getElementsByTag("h1")
            .text();
        int doujinshiPages = Integer
            .parseInt(element.body().getElementsContainingOwnText("pages").text().split(" ")[0]);

        System.out.println("Doujinshi Name: " + doujinshiName);
        System.out.println("Doujinshi Pages: " + doujinshiPages);

        File file = FileUtil.createFile(dir, doujinshiName);
        FileUtil.deleteAndCreateDirectory(file);
        for (int doujinshiPage = 1; doujinshiPage != doujinshiPages + 1; doujinshiPage++) {
          System.out.printf("Downloading | Page: %s/%s - (%s%s)\r",
              doujinshiPage, doujinshiPages, ((doujinshiPage * 100) / doujinshiPages), "%");

          URLConnection connection = SiteUtil
              .openConnection(String.format(DOWNLOAD_URL, this.doujinshiId, doujinshiPage));
          if (connection != null) {
            Files.copy(connection.getInputStream(),
                Paths.get(file.getPath(), doujinshiPage + ".jpg"));
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.out.printf("Downloaded %s doujinshi\r", doujinshiName);
      setDownloading(false);
    });
  }
}
