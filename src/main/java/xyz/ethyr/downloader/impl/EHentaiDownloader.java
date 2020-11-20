package xyz.ethyr.downloader.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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

  private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

  private List<String> viewUrls = new ArrayList<>();
  private List<String> fileUrls = new ArrayList<>();

  private String link;
  private boolean all;
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
    ExecutorUtil.submit(() -> {
      try {
        Document element = Jsoup.connect(String.format(link, 0))
            .cookie("nw", "1")
            .get();
        String name = element.getElementById("gn").getElementsByTag("h1").text();

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
          System.out.print(
              "Downloading " + (i + 1) + "/" + (all ? images : 1) + " (" + (((i + 1) * 100)
                  / (all ? images : 1)) + "%)\r");

          try {
            URLConnection connection = new URL(fileUrls.get(i)).openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            Files
                .copy(connection.getInputStream(),
                    Paths.get(file.getPath(), i + FileUtil.replace(name) + ".jpg"));
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
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