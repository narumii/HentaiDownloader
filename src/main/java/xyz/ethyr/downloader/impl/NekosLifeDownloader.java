package xyz.ethyr.downloader.impl;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import org.apache.commons.lang3.RandomStringUtils;
import xyz.ethyr.downloader.Downloader;
import xyz.ethyr.util.ExecutorUtil;
import xyz.ethyr.util.SiteUtil;

public class NekosLifeDownloader extends Downloader {

  private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
  private static final String URL = "https://nekos.life/api/v2/img/%s";
  private static final String[] ENDPOINTS = new String[]{"femdom", "tickle", "classic", "ngif",
      "erofeet", "meow", "erok", "poke", "les", "v3", "hololewd", "nekoapi_v3.1", "lewdk", "keta",
      "feetg", "nsfw_neko_gif", "eroyuri", "kiss", "8ball", "kuni", "tits", "pussy_jpg", "cum_jpg",
      "pussy", "lewdkemo", "lizard", "slap", "lewd", "cum", "cuddle", "spank", "smallboobs",
      "goose", "Random_hentai_gif", "avatar", "fox_girl", "nsfw_avatar", "hug", "gecg", "boobs",
      "pat", "feet", "smug", "kemonomimi", "solog", "holo", "wallpaper", "bj", "woof", "yuri",
      "trap", "anal", "baka", "blowjob", "holoero", "feed", "neko", "gasm", "hentai", "futanari",
      "ero", "solo", "waifu", "pwankg", "eron", "erokemo"};

  private String tag;
  private int amount;

  public NekosLifeDownloader(File dir, Scanner scanner) {
    super(dir);

    System.out.print("Tag: ");
    tag = scanner.next();
    if (checkTag(tag)) {
      System.out.println("Available tags: " + String.join(", ", ENDPOINTS));
      System.exit(0);
    }

    System.out.print("Amount: ");
    amount = scanner.nextInt();
  }

  @Override
  public void downloadImages() {
    ExecutorUtil.submit(() -> {
      try {
        File file = new File(this.dir, tag);
        if (!file.exists()) {
          file.mkdirs();
        }

        for (int i = 0; i < amount; i++) {
          System.out.print(
              "Downloading " + (i + 1) + "/" + amount + " (" + (((i + 1) * 100)
                  / amount) + "%)\r");

          String fileUrl = SiteUtil.toJson(String.format(URL, tag)).getString("url");
          URLConnection connection = new URL(fileUrl).openConnection();
          connection.setRequestProperty("User-Agent", USER_AGENT);
          Files
              .copy(connection.getInputStream(),
                  Paths.get(file.getPath(),
                      "nl_" + RandomStringUtils.randomAlphabetic(15) + "." + fileUrl
                          .split("\\.")[3]));
        }
      } catch (final Exception e) {
      }
    });
  }

  private boolean checkTag(String string) {
    for (String endpoint : ENDPOINTS) {
      if (string.equalsIgnoreCase(endpoint)) {
        return true;
      }
    }
    return false;
  }
}
