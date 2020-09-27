package xyz.ethyr.downloader.impl;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.select.Elements;
import xyz.ethyr.downloader.Downloader;
import xyz.ethyr.util.ExecutorUtil;

public class GelBooruDownloader extends Downloader {

  private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
  private static final String URL =
      "https://gelbooru.com/index.php?page=dapi&s=post&q=index&pid=" + "%s" + "&limit=" + "%s"
          + "&tags=" + "%s"
          + "&api_key=16d7195f94cd43f7680c2310ec6788f05a6a6e06fbaad1f0e6c55fd284c57f5a&user_id=629393";
  private final List<GelBooruSite> urls = new ArrayList<>();
  private final List<String> tags = new ArrayList<>();
  private final List<String> blockedTags = new ArrayList<>();
  private final List<String> ratings;
  private Elements gelbooruElement;
  private final int amount;

  public GelBooruDownloader(final File dir, final Scanner scanner) {
    super(dir);

    System.out.print("Image tags: ");
    for (String s : scanner.nextLine().split(" ")) {
        if (s.startsWith("-")) {
            blockedTags.add(s.replace("_", ""));
        } else {
            tags.add(s);
        }
    }

    System.out.print("Ratings (s q): ");
    ratings = Arrays.asList(scanner.nextLine().split(" "));

    System.out.print("Amount: ");
    amount = scanner.nextInt();

    final Map<Integer, Integer> imagePages = new HashMap<>();
    final int pages = amount / 1000;
    IntStream.range(0, pages + 1).forEach(page -> {
        if (pages == page) {
            imagePages.put(page, amount - ((page) * 1000));
        } else {
            imagePages.put(page, 1000);
        }
    });

    imagePages.forEach(
        (page, images) -> urls.add(new GelBooruSite(String.format(URL, page, images, StringUtil.join(
            Arrays.asList(tags.toArray()), "+")), page, images)));
  }

  @Override
  public void downloadImages() {
    ExecutorUtil.submit(() -> urls.forEach(site -> {
      try {
        final File file = new File(this.dir, tags.toString() + " + " + blockedTags.toString());
          if (!file.exists()) {
              file.mkdirs();
          }

        gelbooruElement = Jsoup.connect(site.getUrl()).get().getElementsByTag("post").clone();
        for (int i = 0; i < site.getAmount(); i++) {
          final Image image = getImage();
            if (image == null) {
                continue;
            }

          final URLConnection connection = new URL(image.getDownloadURL()).openConnection();
          connection.setRequestProperty("User-Agent", USER_AGENT);
          final String extension = image.getDownloadURL().split("\\.")[3];
          Files
              .copy(connection.getInputStream(),
                  Paths.get(file.getPath(), image.getName() + "." + extension));
        }
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }));
  }

  private Image getImage() {
    return null;
  }

  @AllArgsConstructor
  @Getter
  private class Image {

    private final String downloadURL;
    private final String name;
  }

  @AllArgsConstructor
  @Getter
  private class GelBooruSite {

    private final String url;
    private final int page;
    private final int amount;
  }
}
