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

public class NineHentaiDownloader extends Downloader {

  private static final String MAIN_URL = "https://9hentai.com/g/%s";
  private static final String DOWNLOAD_URL = "https://cdn.9hentai.com/images/%s/%s.jpg";
  private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

  private final String doujinshiId;

  public NineHentaiDownloader(final File dir, final Scanner scanner) {
    super(dir);
    System.out.print("Doujinshi ID: ");
    this.doujinshiId = String.valueOf(scanner.nextInt());
  }

  @Override
  public void downloadImages() {
    ExecutorUtil.submit(() -> {
      try {
        final Document element = Jsoup.connect(String.format(MAIN_URL, this.doujinshiId)).get();
        final String doujinshiName = element.body().getElementById("info").getElementsByTag("h1")
            .text();
        final int doujinshiPages = Integer
            .parseInt(element.body().getElementsContainingOwnText("pages").text().split(" ")[0]);
        System.out.println("Doujinshi Name: " + doujinshiName);
        System.out.println("Doujinshi Pages: " + doujinshiPages);

        final File file = new File(this.dir, FileUtil.replace(doujinshiName));
        FileUtil.deleteAndCreateDirectory(file);
        for (int doujinshiPage = 1; doujinshiPage != doujinshiPages + 1; doujinshiPage++) {
          System.out.print(
              "Downloading " + doujinshiPage + "/" + doujinshiPages + " (" + ((doujinshiPage * 100)
                  / doujinshiPages) + "%)\r");

          try {
            final URLConnection connection = new URL(
                String.format(DOWNLOAD_URL, this.doujinshiId, doujinshiPage)).openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            Files
                .copy(connection.getInputStream(),
                    Paths.get(file.getPath(), doujinshiPage + ".jpg"));
          }catch (final Exception e) {}
        }
      } catch (final Exception e) {
      }
    });
  }//67871 67673
}
