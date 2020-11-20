package xyz.ethyr.downloader.impl;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import xyz.ethyr.downloader.Downloader;
import xyz.ethyr.util.ExecutorUtil;
import xyz.ethyr.util.FileUtil;

public class NHentaiDownloader extends Downloader {

  private static final String MAIN_URL = "https://nhentai.to/g/%s";
  private static final String VIEW_URL = "https://nhentai.to/g/%s/%s";
  private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

  private String doujinshiId;

  public NHentaiDownloader(File dir, Scanner scanner) {
    super(dir);
    System.out.print("Doujinshi [ID/Link]: ");
    String input = scanner.next();
    if (input.startsWith("https://nhentai.to/g/") || input.startsWith("nhentai.to/g/")) {
      doujinshiId = input.split("/g/")[1];
    } else {
      doujinshiId = input;
    }
  }

  @Override
  public void downloadImages() {
    ExecutorUtil.submit(() -> {
      try {
        Document element = Jsoup.connect(String.format(MAIN_URL, this.doujinshiId)).get();
        String doujinshiName = element.body().getElementById("info").getElementsByTag("h1")
            .text();
        int doujinshiPages = Integer
            .parseInt(element.body().getElementsContainingOwnText("pages").text().split(" ")[0]);
        System.out.println("Doujinshi Name: " + doujinshiName);
        System.out.println("Doujinshi Pages: " + doujinshiPages);

        File file = new File(this.dir, FileUtil.replace(doujinshiName));
        FileUtil.deleteAndCreateDirectory(file);
        for (int doujinshiPage = 1; doujinshiPage != doujinshiPages + 1; doujinshiPage++) {
          System.out.print(
              "Downloading " + doujinshiPage + "/" + doujinshiPages + " (" + ((doujinshiPage * 100)
                  / doujinshiPages) + "%)\r");

          try {
            String imageUrl = Jsoup
                .connect(String.format(VIEW_URL, this.doujinshiId, doujinshiPage))
                .get().body().getElementById("image-container").select("img").attr("src");
            URLConnection connection = new URL(imageUrl).openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            Files
                .copy(connection.getInputStream(),
                    Paths.get(file.getPath(), doujinshiPage + ".jpg"));
          } catch (Exception e) {
          }
        }
      } catch (Exception e) {
      }
    });
  }
}
