package xyz.ethyr.downloader;

import java.io.File;

public abstract class Downloader {

  protected final File dir;

  public Downloader(File dir) {
    this.dir = dir;
    if (!dir.exists()) {
      dir.mkdir();
    }
  }

  public abstract void downloadImages();
}
