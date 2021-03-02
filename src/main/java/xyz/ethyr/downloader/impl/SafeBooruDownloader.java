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
import xyz.ethyr.parser.RegexParser;
import xyz.ethyr.parser.RegexParser.ParsedObject;
import xyz.ethyr.util.ExecutorUtil;
import xyz.ethyr.util.FileUtil;
import xyz.ethyr.util.SiteUtil;

public class SafeBooruDownloader extends Downloader {

  private static final RegexParser REGEX_PARSER = new RegexParser();
  private static final String URL = "https://safebooru.org/index.php?page=dapi&s=post&q=index&limit=%s&pid=%s&tags=%s";

  private final List<Site> urls = new ArrayList<>();
  private final int amount;

  private Elements gelbooruElement;

  private final ParsedObject blacklistedTags;
  private final String[] tags;

  public SafeBooruDownloader(File dir, Scanner scanner) {
    super(dir);

    System.out.print("Image tags: ");
    tags = scanner.nextLine().split(" ");

    System.out.print("Blacklisted tags: ");
    blacklistedTags = REGEX_PARSER.parse(scanner.nextLine());

    System.out.print("Amount: ");
    amount = scanner.nextInt();

    Map<Integer, Integer> pages = SiteUtil.parsePages(amount, 1000);
    urls.addAll(SiteUtil.createSites(pages, URL, tags));
  }

  @Override
  public void downloadImages() {
    setDownloading(true);
    ExecutorUtil.submit(() -> {
      for (int j = 0; j < urls.size(); j++) {
        try {
          Site site = urls.get(j);
          File file = FileUtil
              .createFile(dir, Arrays.toString(tags) + blacklistedTags.getString(" - "));
          if (!file.exists()) {
            file.mkdirs();
          }

          gelbooruElement = Jsoup.connect(site.getUrl()).get().getElementsByTag("post").clone();
          for (int i = 0; i < site.getAmount(); i++) {
            System.out.print(String.format("Downloading | Page: %s/%s, Image: %s/%s - (%s%s)\r",
                j + 1, urls.size(), i + 1, site.getAmount(), ((i + 1) * 100) / site.getAmount(),
                "%"));

            Image image = getImage(i);
            if (image == null) {
              continue;
            }

            URLConnection connection = SiteUtil.openConnection(image.getDownloadURL());
            if (connection != null) {
              String[] extension = image.getDownloadURL().split("\\.");
              Files.copy(connection.getInputStream(),
                  Paths.get(file.getPath(),
                      "sb_" + image.getName() + "." + extension[extension.length - 1]));
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      System.out.print(String
          .format("Downloaded %s images with %s %s\r", amount, String.join(", ", tags),
              tags.length > 1 ? "tags" : "tag"));
      setDownloading(false);
    });
  }

  private Image getImage(int image) {
    try {
      Element post = gelbooruElement.get(image);
      String tags = post.attr("tags");
      if (!(!blacklistedTags.getString().isEmpty()
          && blacklistedTags.getPattern().matcher(tags).find())) {
        return new Image(post.attr("file_url"),
            image + "_" + RandomStringUtils.randomAlphabetic(10));
      }
      return null;
    } catch (Exception e) {
      return null;
    }
  }
}
