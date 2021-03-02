package xyz.ethyr.downloader;

import java.io.File;

public abstract class Downloader {

  protected File dir;
  protected boolean downloading;

  public Downloader(File dir) {
    this.dir = dir;
    if (!dir.exists()) {
      dir.mkdir();
    }
  }

  public abstract void downloadImages();

  public boolean isDownloading() {
    return downloading;
  }

  public void setDownloading(boolean downloading) {
    this.downloading = downloading;
  }
}
