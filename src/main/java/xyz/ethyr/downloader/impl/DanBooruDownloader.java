package xyz.ethyr.downloader.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xyz.ethyr.booru.Image;
import xyz.ethyr.booru.Site;
import xyz.ethyr.downloader.Downloader;
import xyz.ethyr.util.ExecutorUtil;
import xyz.ethyr.util.FileUtil;
import xyz.ethyr.util.RegexParser;
import xyz.ethyr.util.RegexParser.RegexInfo;
import xyz.ethyr.util.SiteUtil;

public class DanBooruDownloader extends Downloader {

  private static final String URL = "https://danbooru.donmai.us/posts.xml?limit=%s&page=%s&tag=%s";

  private final List<Site> urls = new ArrayList<>();
  private final int amount;
  private final RegexInfo ratings;
  private final RegexInfo blacklistedTags;
  private final String[] tags;

  public DanBooruDownloader(File dir, Scanner scanner) {
    super(dir);

    System.out.print("Image tags: ");
    tags = scanner.nextLine().split(" ");

    System.out.print("Blacklisted tags: ");
    blacklistedTags = RegexParser.parse(scanner.nextLine());

    System.out.print("Ratings (s q e): ");
    ratings = RegexParser.parse(scanner.nextLine());

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

          Elements elements = Jsoup.connect(site.getUrl()).execute().parse()
              .getElementsByTag("post");
          for (int i = 0; i < site.getAmount(); i++) {
            System.out.printf("Downloading | Page: %s/%s, Image: %s/%s - (%s%s)\r",
                j + 1, urls.size(), i + 1, site.getAmount(), ((i + 1) * 100) / site.getAmount(),
                "%");

            getImage("db_" + i, i, elements).ifPresent(image -> FileUtil.saveImage(
                FileUtil.computePath(file, image.getName(),
                    SiteUtil.getExtension(image.getDownloadURL())),
                SiteUtil.openConnection(image.getDownloadURL())));
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

  private Optional<Image> getImage(String fileName, int position, Elements elements) {
    try {
      Element post = elements.get(position);

      //B1G API
      if (!post.select("file-url").text().contains("http")) {
        return Optional.empty();
      }

      String tags = post.select("tag-string").text();
      String rating = post.select("rating").text();
      if (ratings.getPattern().matcher(rating).matches() && !(!blacklistedTags.getString().isEmpty()
          && blacklistedTags.getPattern().matcher(tags).find())) {
        return Optional.of(new Image(post.select("file-url").text(),
            fileName + "_" + FileUtil.generateRandomString(10)));
      }
    } catch (Exception e) {
    }
    return Optional.empty();
  }
}
