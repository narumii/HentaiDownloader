package xyz.ethyr.downloader.impl;

import java.io.File;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.apache.commons.lang3.RandomStringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xyz.ethyr.booru.Image;
import xyz.ethyr.booru.Site;
import xyz.ethyr.downloader.Downloader;
import xyz.ethyr.util.RegexParser;
import xyz.ethyr.util.RegexParser.RegexInfo;
import xyz.ethyr.util.ExecutorUtil;
import xyz.ethyr.util.FileUtil;
import xyz.ethyr.util.SiteUtil;

public class Rule34Downloader extends Downloader {

  private static final String URL = "https://rule34.xxx/index.php?page=dapi&s=post&q=index&limit=%s&pid=%s&tags=%s";

  private final List<Site> urls = new ArrayList<>();
  private final int amount;
  private final RegexInfo ratings;
  private final RegexInfo blacklistedTags;
  private final String[] tags;

  private Elements ruleElement;

  public Rule34Downloader(File dir, Scanner scanner) {
    super(dir);

    System.out.print("Image tags: ");
    tags = scanner.nextLine().split(" ");

    System.out.print("Blacklisted tags: ");
    blacklistedTags = RegexParser.parse(scanner.nextLine());

    System.out.print("Ratings (s q e): ");
    ratings = RegexParser.parse(scanner.nextLine());

    System.out.print("Amount: ");
    amount = scanner.nextInt();

    Map<Integer, Integer> pages = SiteUtil.parsePages(amount, 100);
    urls.addAll(SiteUtil.createSites(pages, URL, tags));
  }

  @Override
  public void downloadImages() {
    setDownloading(true);
    ExecutorUtil.submit(() -> {
      for (int j = 0; j < urls.size(); j++) {
        try {
          Site site = urls.get(j);
          File file = FileUtil.createFile(dir, Arrays.toString(tags) + blacklistedTags.getString(" - "));
          if (!file.exists()) {
            file.mkdirs();
          }

          ruleElement = Jsoup.connect(site.getUrl()).get().getElementsByTag("post").clone();
          for (int i = 0; i < site.getAmount(); i++) {
            System.out.printf("Downloading | Page: %s/%s, Image: %s/%s - (%s%s)\r",
                j + 1, urls.size(), i + 1, site.getAmount(), ((i + 1) * 100) / site.getAmount(),
                "%");

            Image image = getImage(i);
            if (image == null) {
              continue;
            }

            URLConnection connection = SiteUtil.openConnection(image.getDownloadURL());
            if (connection != null) {
              String[] extension = image.getDownloadURL().split("\\.");
              Files.copy(connection.getInputStream(),
                  Paths.get(file.getPath(),
                      "r34_" + image.getName() + "." + extension[extension.length - 1]));
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      System.out.printf("Downloaded %s images with %s %s\r", amount, String.join(", ", tags),
          tags.length > 1 ? "tags" : "tag");
      setDownloading(false);
    });
  }

  private Image getImage(int image) {
    try {
      Element post = ruleElement.get(image);
      String tags = post.attr("tags");
      String rating = post.attr("rating");
      if (ratings.getPattern().matcher(rating).matches() && !(!blacklistedTags.getString().isEmpty()
          && blacklistedTags.getPattern().matcher(tags).find())) {
        return new Image(post.attr("file_url"),
            image + "_" + RandomStringUtils.randomAlphabetic(15));
      }
      return null;
    } catch (Exception e) {
      return null;
    }
  }
}
