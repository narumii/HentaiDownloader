package xyz.ethyr.downloader.impl;

import java.io.File;
import java.net.URL;
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

@Deprecated
//TODO: FIX THIS SHIT
public class DanBooruDownloader extends Downloader {

  private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
  private static final String URL = "https://danbooru.donmai.us/posts.xml?limit=%s&page=%s&tag=%s";

  private final List<Site> urls = new ArrayList<>();
  private final RegexInfo ratings;
  private final RegexInfo blacklistedTags;
  private final String[] tags;

  private Elements gelbooruElement;

  public DanBooruDownloader(File dir, Scanner scanner) {
    super(dir);

    System.out.print("Image tags: ");
    tags = scanner.nextLine().split(" ");

    System.out.print("Blacklisted tags: ");
    blacklistedTags = RegexParser.parse(scanner.nextLine());

    System.out.print("Ratings (s q e): ");
    ratings = RegexParser.parse(scanner.nextLine());

    System.out.print("Amount: ");
    int amount = scanner.nextInt();

    Map<Integer, Integer> pages = SiteUtil.parsePages(amount, 1000);
    urls.addAll(SiteUtil.createSites(pages, URL, tags));
  }

  @Override
  public void downloadImages() {
    int[] index = {0};
    ExecutorUtil.submit(() -> urls.forEach(site -> {
      try {
        File file = FileUtil.createFile(dir, Arrays.toString(tags) + blacklistedTags.getString(" - "));
        if (!file.exists()) {
          file.mkdirs();
        }

        gelbooruElement = Jsoup.connect(site.getUrl()).get().getElementsByTag("post").clone();
        for (int i = 0; i < site.getAmount(); i++) {
          System.out.printf("Downloading: Page: %s/%s, Image: %s/%s - (%s%s)\r",
              index[0] + 1, urls.size(), i + 1, site.getAmount(),
              ((i + 1) * 100) / site.getAmount(), "%");

          Image image = getImage(i);
          if (image == null) {
            continue;
          }

          URLConnection connection = new URL(image.getDownloadURL()).openConnection();
          connection.setRequestProperty("User-Agent", USER_AGENT);
          String[] extension = image.getDownloadURL().split("\\.");
          Files
              .copy(connection.getInputStream(),
                  Paths.get(file.getPath(),
                      "db_" + image.getName() + "." + extension[extension.length - 1]));
        }
        index[0]++;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }));
  }

  private Image getImage(int image) {
    try {
      Element post = gelbooruElement.get(image);
      String tags = post.attr("tag-string");
      String rating = post.attr("rating");
      if (ratings.getPattern().matcher(rating).matches() && !(!blacklistedTags.getString().isEmpty()
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
