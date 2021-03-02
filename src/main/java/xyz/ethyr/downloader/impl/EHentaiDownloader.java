package xyz.ethyr.downloader.impl;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import xyz.ethyr.downloader.Downloader;
import xyz.ethyr.util.ExecutorUtil;
import xyz.ethyr.util.FileUtil;
import xyz.ethyr.util.SiteUtil;

public class EHentaiDownloader extends Downloader {

  private final List<String> viewUrls = new ArrayList<>();
  private final List<String> fileUrls = new ArrayList<>();
  private final boolean all;
  private String link;
  private String name;
  private int pages;

  public EHentaiDownloader(File dir, Scanner scanner) {
    super(dir);
    System.out.print("Link: ");
    link = scanner.next();

    System.out.print("All images [true/false]: ");
    all = scanner.nextBoolean();

    if (!link.startsWith("https://e-hentai.org/g/") && link.startsWith("e-hentai.org/g/")) {
      System.out.println("Invalid e-hentai link");
      System.exit(0);
    }

    link += link.endsWith("/") ? "?p=%s" : "/p=%s";
  }

  @Override
  public void downloadImages() {
    setDownloading(true);
    ExecutorUtil.submit(() -> {
      try {
        Document element = Jsoup.connect(String.format(link, 0))
            .cookie("nw", "1")
            .get();
        name = element.getElementById("gn").getElementsByTag("h1").text();

        int images = Integer
            .parseInt(element.body().getElementsContainingOwnText("pages").text().split(" ")[0]);
        pages = SiteUtil.parsePages(images, 40).size();

        System.out.println("Name: " + name);
        System.out.println("Images: " + images);
        System.out.println("Pages: " + pages);

        createViewUrls(element);
        createFileUrls();

        File file = new File(this.dir, FileUtil.replace(name));
        FileUtil.deleteAndCreateDirectory(file);

        for (int i = 0; i < fileUrls.size(); i++) {
          System.out.print(String.format("Downloading | Image: %s/%s - (%s%s)\r",
              i + 1, all ? images : 1, ((i + 1) * 100) / (all ? images : 1), "%"));

          URLConnection connection = SiteUtil.openConnection(fileUrls.get(i));
          if (connection != null) {
            Files.copy(connection.getInputStream(),
                Paths.get(file.getPath(), i + FileUtil.replace(name) + ".jpg"));
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.out.print(String.format("Downloaded %s\r", name));
      setDownloading(false);
    });
  }

  private void createViewUrls(Document document) throws IOException {
    if (all) {
      document.getElementsByClass("gdtm")
          .forEach(element -> viewUrls.add(element.select("a").attr("href")));

      for (int i = 1; i < pages; i++) {
        document = Jsoup
            .connect(String.format(link, i))
            .cookie("nw", "1")
            .get();
        document.getElementsByClass("gdtm")
            .forEach(element -> viewUrls.add(element.select("a").attr("href")));
      }
    } else {
      viewUrls.add(document.getElementsByClass("gdtm").first().select("a").attr("href"));
    }
  }

  private void createFileUrls() throws IOException {
    for (String viewUrl : viewUrls) {
      Document image = Jsoup.connect(viewUrl).get();
      fileUrls.add(image.getElementById("img").attr("src"));
    }
  }
}