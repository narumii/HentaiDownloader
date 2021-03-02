package xyz.ethyr.downloader.impl;

import java.io.File;
import java.util.Scanner;
import xyz.ethyr.downloader.Downloader;

@Deprecated
public class LolisLifeDownloader extends Downloader {

  private static final String URL = "https://api.lolis.life/random?category=%s";
  private static final String[] ENDPOINTS = new String[]{"neko", "futa", "kawaii", "lewd", "slave",
      "pat", "monster"};

  private final String tag;
  private final int amount;

  public LolisLifeDownloader(File dir, Scanner scanner) {
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
    //TODO: LOLIS.LIFE [*]
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
