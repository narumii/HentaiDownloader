package xyz.ethyr.downloader.impl;

import java.io.File;
import java.util.Scanner;
import xyz.ethyr.downloader.Downloader;
import xyz.ethyr.util.ExecutorUtil;
import xyz.ethyr.util.FileUtil;
import xyz.ethyr.util.SiteUtil;

public class NekosLifeDownloader extends Downloader {

  private static final String URL = "https://nekos.life/api/v2/img/%s";
  private static final String[] ENDPOINTS = new String[]{"femdom", "tickle", "classic", "ngif",
      "erofeet", "meow", "erok", "poke", "les", "v3", "hololewd", "nekoapi_v3.1", "lewdk", "keta",
      "feetg", "nsfw_neko_gif", "eroyuri", "kiss", "8ball", "kuni", "tits", "pussy_jpg", "cum_jpg",
      "pussy", "lewdkemo", "lizard", "slap", "lewd", "cum", "cuddle", "spank", "smallboobs",
      "goose", "Random_hentai_gif", "avatar", "fox_girl", "nsfw_avatar", "hug", "gecg", "boobs",
      "pat", "feet", "smug", "kemonomimi", "solog", "holo", "wallpaper", "bj", "woof", "yuri",
      "trap", "anal", "baka", "blowjob", "holoero", "feed", "neko", "gasm", "hentai", "futanari",
      "ero", "solo", "waifu", "pwankg", "eron", "erokemo"};

  private final String tag;
  private final int amount;

  public NekosLifeDownloader(File dir, Scanner scanner) {
    super(dir);

    System.out.print("Tag: ");
    tag = scanner.next();
    if (!checkTag(tag)) {
      System.out.println("Available tags: " + String.join(", ", ENDPOINTS));
      System.exit(0);
    }

    System.out.print("Amount: ");
    amount = scanner.nextInt();
  }

  @Override
  public void downloadImages() {
    setDownloading(true);
    ExecutorUtil.submit(() -> {
      try {
        File file = FileUtil.createFile(dir, tag);
        if (!file.exists()) {
          file.mkdirs();
        }

        for (int i = 0; i < amount; i++) {
          System.out.printf("Downloading | Image: %s/%s - (%s%s)\r",
              i + 1, amount, (((i + 1) * 100) / amount), "%");

          String fileUrl = SiteUtil.toJson(String.format(URL, tag)).getString("url");
          FileUtil.saveImage(FileUtil.computePath(file, "nl_" + FileUtil.generateRandomString(15),
              SiteUtil.getExtension(fileUrl)), SiteUtil.openConnection(fileUrl));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.out.printf("Downloaded %s images with %s tag\r", amount, tag);
      setDownloading(false);
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
