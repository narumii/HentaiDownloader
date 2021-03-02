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

public class NHentaiDownloader extends Downloader {

  private static final String MAIN_URL = "https://nhentai.net/g/%s";
  private static final String VIEW_URL = "https://nhentai.net/g/%s/%s";

  private final String doujinshiId;
  private String doujinshiName;

  public NHentaiDownloader(File dir, Scanner scanner) {
    super(dir);
    System.out.print("Doujinshi [ID/Link]: ");
    String input = scanner.next();
    if (input.startsWith("https://nhentai") || input.startsWith("nhentai")) {
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
            .parseInt(element.body().getElementsByClass("name").last().ownText());

        System.out.println("Doujinshi Name: " + doujinshiName);
        System.out.println("Doujinshi Pages: " + doujinshiPages);

        File file = FileUtil.createFile(dir, doujinshiName);
        FileUtil.deleteAndCreateDirectory(file);
        for (int doujinshiPage = 1; doujinshiPage != doujinshiPages + 1; doujinshiPage++) {
          System.out.printf("Downloading | Page: %s/%s - (%s%s)\r",
              doujinshiPage, doujinshiPages, ((doujinshiPage * 100) / doujinshiPages), "%");

          String imageUrl = Jsoup
              .connect(String.format(VIEW_URL, this.doujinshiId, doujinshiPage))
              .get().body().getElementById("image-container").select("img").attr("src");

          URLConnection connection = SiteUtil.openConnection(imageUrl);
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
