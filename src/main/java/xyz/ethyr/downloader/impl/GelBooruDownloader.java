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
import xyz.ethyr.parser.RegexParser;
import xyz.ethyr.parser.RegexParser.ParserObject;
import xyz.ethyr.util.ExecutorUtil;
import xyz.ethyr.util.FileUtil;
import xyz.ethyr.util.SiteUtil;

public class GelBooruDownloader extends Downloader {

  private static final RegexParser REGEX_PARSER = new RegexParser();
  private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
  private static final String URL =
      "https://gelbooru.com/index.php?page=dapi&s=post&q=index&limit=%s&pid=%s"
          + "&tags=" + "%s"
          + "&api_key=16d7195f94cd43f7680c2310ec6788f05a6a6e06fbaad1f0e6c55fd284c57f5a&user_id=629393";

  private List<Site> urls = new ArrayList<>();
  private int amount;

  private Elements gelbooruElement;

  private ParserObject ratings;
  private ParserObject blacklistedTags;
  private String[] tags;

  public GelBooruDownloader(File dir, Scanner scanner) {
    super(dir);

    System.out.print("Image tags: ");
    tags = scanner.nextLine().split(" ");

    System.out.print("Blacklisted tags: ");
    blacklistedTags = REGEX_PARSER.parse(scanner.nextLine());

    System.out.print("Ratings (s q e): ");
    ratings = REGEX_PARSER.parse(scanner.nextLine());

    System.out.print("Amount: ");
    amount = scanner.nextInt();

    Map<Integer, Integer> pages = SiteUtil.parsePages(amount, 1000);
    urls.addAll(SiteUtil.createSites(pages, URL, tags));
  }

  @Override
  public void downloadImages() {
    ExecutorUtil.submit(() -> urls.forEach(site -> {
      try {
        File file = new File(this.dir,
            FileUtil.replace(Arrays.toString(tags) + " -" + blacklistedTags.getString()));
        if (!file.exists()) {
          file.mkdirs();
        }

        gelbooruElement = Jsoup.connect(site.getUrl()).get().getElementsByTag("post").clone();
        for (int i = 0; i < site.getAmount(); i++) {
          System.out.print(
              "Downloading " + (i + 1) + "/" + site.getAmount() + " (" + (((i + 1) * 100)
                  / site.getAmount()) + "%)\r");

          Image image = getImage(i);
          if (image == null) {
            continue;
          }

          URLConnection connection = new URL(image.getDownloadURL()).openConnection();
          connection.setRequestProperty("User-Agent", USER_AGENT);
          String extension = image.getDownloadURL().split("\\.")[3];
          Files
              .copy(connection.getInputStream(),
                  Paths.get(file.getPath(), "gb_" + image.getName() + "." + extension));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }));
  }

  private Image getImage(int image) {
    try {
      Element post = gelbooruElement.get(image);
      String tags = post.attr("tags");
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
