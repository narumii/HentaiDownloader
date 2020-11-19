package xyz.ethyr.booru;

public class Image {

  private final String downloadURL;
  private final String name;

  public Image(String downloadURL, String name) {
    this.downloadURL = downloadURL;
    this.name = name;
  }

  public String getDownloadURL() {
    return downloadURL;
  }

  public String getName() {
    return name;
  }
}
